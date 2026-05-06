<template>
  <div id="courseCatalogView">
    <a-space direction="vertical" fill>
      <a-space align="center" wrap :size="10">
        <a-input
          v-model="filterForm.keyword"
          placeholder="Название или фрагмент описания"
          allow-clear
          style="width: 280px"
          @press-enter="applyFilters"
        />
        <a-select
          v-model="filterForm.sortField"
          placeholder="Сортировка"
          style="width: 220px"
        >
          <a-option value="updateTime">Дата обновления</a-option>
          <a-option value="createTime">Дата создания</a-option>
          <a-option value="title">Название</a-option>
        </a-select>
        <a-select v-model="filterForm.sortOrder" style="width: 170px">
          <a-option value="ascend">По возрастанию</a-option>
          <a-option value="descend">По убыванию</a-option>
        </a-select>
        <a-button type="primary" @click="applyFilters">Применить</a-button>
        <a-button @click="resetFilters">Сброс</a-button>
      </a-space>
      <a-table
        :columns="courseColumns"
        :data="courseList"
        :pagination="{
          pageSize: query.pageSize,
          current: query.current,
          total: Number(total),
          showTotal: true,
          showPageSize: true,
          pageSizeOptions: [10, 20, 50],
        }"
        :bordered="{ wrapper: true, cell: true }"
        row-key="id"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #empty>
          <a-empty description="Нет доступных курсов" />
        </template>
        <template #description="{ record }">
          <a-typography-paragraph :ellipsis="{ rows: 2 }">
            {{ record.description || "—" }}
          </a-typography-paragraph>
        </template>
        <template #actions="{ record }">
          <template v-if="isMyCourse(record)">
            <a-space :size="8" wrap>
              <span class="own-course-label">Ваш курс</span>
              <a-button
                v-if="canManageCourses"
                type="outline"
                size="small"
                @click="goManageCourses"
              >
                Управление
              </a-button>
            </a-space>
          </template>
          <a-button
            v-else
            size="small"
            :status="isEnrolled(record.id) ? 'warning' : 'success'"
            @click="toggleEnrollment(record)"
          >
            {{ isEnrolled(record.id) ? "Отписаться" : "Записаться" }}
          </a-button>
        </template>
      </a-table>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import accessEnum from "@/access/accessEnum";

interface Course {
  id: string | number;
  title: string;
  description?: string;
  authorId?: string | number;
}

function defaultFilterForm() {
  return {
    keyword: "" as string,
    sortField: "updateTime" as string,
    sortOrder: "descend" as string,
  };
}

const router = useRouter();
const userStore = useUserStore();

const courseList = ref<Course[]>([]);
const enrolledCourseIds = ref<Set<string>>(new Set());
const total = ref(0);

const canManageCourses = computed(() => {
  const r = userStore.loginUser?.userRole;
  return r === accessEnum.ADMIN || r === accessEnum.TEACHER;
});

const isMyCourse = (record: Course) => {
  const uid = userStore.loginUser?.id;
  if (uid == null || record.authorId == null) return false;
  return String(uid) === String(record.authorId);
};

const goManageCourses = () => {
  router.push("/course/manage");
};

const filterForm = ref(defaultFilterForm());

const query = ref({
  current: 1,
  pageSize: 10,
  keyword: "" as string,
  sortField: "updateTime",
  sortOrder: "descend",
});

const api = async (
  url: string,
  options?: { method?: string; headers?: Record<string, string>; body?: string }
) => {
  const res = await fetch(url, { credentials: "include", ...options });
  return res.json();
};

const buildPublicListUrl = () => {
  const q = query.value;
  const p = new URLSearchParams();
  p.set("current", String(q.current));
  p.set("pageSize", String(q.pageSize));
  const kw = q.keyword.trim();
  if (kw) {
    p.set("keyword", kw);
  }
  p.set("sortField", q.sortField);
  p.set("sortOrder", q.sortOrder);
  return `/api/course/list/public?${p.toString()}`;
};

const loadPublicCourses = async () => {
  const res = await api(buildPublicListUrl());
  if (res.code === 0 && res.data) {
    courseList.value = res.data.records || [];
    total.value = Number(res.data.total ?? 0);
  } else {
    Message.error(res.message ?? "Не удалось загрузить курсы");
  }
};

const loadMyEnrollments = async () => {
  const res = await api("/api/course/enroll/my");
  if (res.code === 0 && Array.isArray(res.data)) {
    enrolledCourseIds.value = new Set(
      (res.data as Course[]).map((c) => String(c.id))
    );
  }
};

const isEnrolled = (courseId: string | number) =>
  enrolledCourseIds.value.has(String(courseId));

const toggleEnrollment = async (course: Course) => {
  const enrolled = isEnrolled(course.id);
  const url = enrolled ? "/api/course/enroll/cancel" : "/api/course/enroll";
  const res = await api(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ courseId: course.id }),
  });
  if (res.code === 0) {
    Message.success(
      enrolled ? "Вы отписались от курса" : "Вы записались на курс"
    );
    await loadMyEnrollments();
  } else {
    Message.error(res.message ?? "Операция не выполнена");
  }
};

const courseColumns = [
  { title: "Курс", dataIndex: "title", width: 280 },
  { title: "Описание", dataIndex: "description", slotName: "description" },
  { title: "Действия", slotName: "actions", width: 220 },
];

const applyFilters = () => {
  const f = filterForm.value;
  query.value = {
    ...query.value,
    current: 1,
    keyword: f.keyword.trim(),
    sortField: f.sortField,
    sortOrder: f.sortOrder,
  };
  loadPublicCourses();
};

const resetFilters = () => {
  filterForm.value = defaultFilterForm();
  query.value = {
    current: 1,
    pageSize: query.value.pageSize,
    keyword: "",
    sortField: "updateTime",
    sortOrder: "descend",
  };
  loadPublicCourses();
};

const onPageChange = (page: number) => {
  query.value = { ...query.value, current: page };
  loadPublicCourses();
};

const onPageSizeChange = (size: number) => {
  query.value = { ...query.value, pageSize: size, current: 1 };
  loadPublicCourses();
};

onMounted(async () => {
  if (!userStore.loginUser?.id) {
    await userStore.getLoginUser();
  }
  await loadPublicCourses();
  await loadMyEnrollments();
});
</script>

<style scoped>
#courseCatalogView {
}

.own-course-label {
  font-size: 13px;
  color: var(--color-text-2);
}
</style>
