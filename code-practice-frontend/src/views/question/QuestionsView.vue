<template>
  <div id="questionsView">
    <template v-if="isStudent">
      <a-space direction="vertical" fill>
        <h3>Ваши курсы</h3>
        <a-space wrap align="center" :size="8">
          <a-input
            v-model="studentCourseFilter.keyword"
            allow-clear
            style="width: 300px"
            placeholder="Название или фрагмент описания"
            @press-enter="applyStudentCourseFilters"
          />
          <a-select
            v-model="studentCourseFilter.sortField"
            placeholder="Сортировка"
            style="width: 220px"
          >
            <a-option value="updateTime">Дата обновления</a-option>
            <a-option value="createTime">Дата создания</a-option>
            <a-option value="title">Название</a-option>
          </a-select>
          <a-select
            v-model="studentCourseFilter.sortOrder"
            style="width: 170px"
          >
            <a-option value="ascend">По возрастанию</a-option>
            <a-option value="descend">По убыванию</a-option>
          </a-select>
          <a-button type="primary" @click="applyStudentCourseFilters"
            >Применить</a-button
          >
          <a-button @click="resetStudentCourseFilters">Сброс</a-button>
        </a-space>
        <a-table
          :columns="courseColumns"
          :data="displayMyCourses"
          :pagination="false"
          :bordered="{ wrapper: true, cell: true }"
          @row-click="onCourseRowClick"
        >
          <template #empty>
            <a-empty
              :description="
                myCourses.length
                  ? 'Нет курсов по текущему поиску'
                  : 'Вы не записаны ни на один курс'
              "
            />
          </template>
        </a-table>
      </a-space>
    </template>

    <template v-else>
      <a-space direction="vertical" fill size="medium">
        <div class="teacher-home-intro">
          <h3 class="teacher-home-title">Задачи</h3>
        </div>

        <div class="teacher-home-toolbar">
          <a-space wrap align="center" :size="8">
            <a-input
              v-model="form.title"
              allow-clear
              placeholder="Название (частичное совпадение)"
              style="width: 240px"
              @press-enter="applyFilters"
            />
            <a-input
              v-model="form.tagKeyword"
              allow-clear
              placeholder="Тег (подстрока в JSON-тегах)"
              style="width: 220px"
              @press-enter="applyFilters"
            />
            <a-select
              v-model="form.courseFilter"
              allow-search
              allow-clear
              placeholder="Курс"
              style="width: 260px"
              @clear="form.courseFilter = 'all'"
            >
              <a-option value="all" label="Все курсы">Все курсы</a-option>
              <a-option value="none" label="Без курса">Без курса</a-option>
              <a-option
                v-for="c in courseOptions"
                :key="c.id"
                :value="String(c.id)"
                :label="c.title"
              >
                {{ c.title }}
              </a-option>
            </a-select>
            <a-select v-model="form.sortField" style="width: 240px">
              <a-option value="createTime">Сортировка: дата создания</a-option>
              <a-option value="title">Сортировка: название</a-option>
              <a-option value="submitNum">Сортировка: отправок</a-option>
              <a-option value="acceptedNum">Сортировка: прошло (AC)</a-option>
            </a-select>
            <a-select v-model="form.sortOrder" style="width: 140px">
              <a-option value="ascend">По возрастанию</a-option>
              <a-option value="descend">По убыванию</a-option>
            </a-select>
            <a-button type="primary" @click="applyFilters">Применить</a-button>
            <a-button @click="resetFilters">Сброс</a-button>
          </a-space>
        </div>

        <a-table
          :columns="teacherColumns"
          :data="dataList"
          :bordered="{ wrapper: true, cell: true }"
          :scroll="{ x: 1100 }"
          :pagination="{
            showTotal: true,
            showPageSize: true,
            pageSize: form.pageSize,
            current: currentPage,
            total: Number(total),
            pageSizeOptions: [10, 20, 50, 100],
          }"
          row-key="id"
          @page-change="onPageChange"
          @page-size-change="onPageSizeChange"
        >
          <template #empty>
            <a-empty description="Нет задач по выбранным условиям" />
          </template>
          <template #tags="{ record }">
            <template
              v-for="w in [{ id: qidKey(record), arr: parseTags(record.tags) }]"
              :key="w.id"
            >
              <span v-if="!w.arr.length" class="text-muted">—</span>
              <a-tooltip v-else :content="w.arr.join(', ')" position="top">
                <span class="question-tags-compact">
                  <a-tag
                    v-for="(tag, index) in w.arr.slice(0, 4)"
                    :key="index"
                    color="green"
                    size="small"
                    style="margin-right: 4px"
                    >{{ tag }}</a-tag
                  >
                  <a-tag v-if="w.arr.length > 4" size="small" color="gray"
                    >+{{ w.arr.length - 4 }}</a-tag
                  >
                </span>
              </a-tooltip>
            </template>
          </template>
          <template #courses="{ record }">
            <span v-if="!courseListForRow(record).length" class="text-muted"
              >—</span
            >
            <a-tooltip
              v-else
              :content="
                courseListForRow(record)
                  .map((c) => c.title)
                  .join(', ')
              "
              position="top"
            >
              <span class="course-tags-compact">
                <a-tag
                  v-for="c in courseListForRow(record).slice(0, 2)"
                  :key="c.id"
                  color="arcoblue"
                  size="small"
                  style="margin-right: 4px"
                  >{{ c.title }}</a-tag
                >
                <a-tag
                  v-if="courseListForRow(record).length > 2"
                  size="small"
                  color="gray"
                  >+{{ courseListForRow(record).length - 2 }}</a-tag
                >
              </span>
            </a-tooltip>
          </template>
          <template #acceptedRate="{ record }">
            <span class="accepted-rate">
              {{
                record.submitNum
                  ? (
                      (100 * (record.acceptedNum ?? 0)) /
                      record.submitNum
                    ).toFixed(2)
                  : "0.00"
              }}%
            </span>
            <span class="accepted-fraction">
              ({{ record.acceptedNum ?? 0 }}/{{ record.submitNum ?? 0 }})</span
            >
          </template>
          <template #createTime="{ record }">
            {{
              record.createTime
                ? moment(record.createTime).format("YYYY-MM-DD")
                : "—"
            }}
          </template>
          <template #optional="{ record }">
            <a-button type="primary" @click="doQuestionPage(record)"
              >Решить</a-button
            >
          </template>
        </a-table>
      </a-space>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { QuestionControllerService } from "../../../generated";
import type { QuestionQueryRequest, QuestionVO } from "../../../generated";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import moment from "moment";
import { useUserStore } from "@/stores/user";
import accessEnum from "@/access/accessEnum";

interface Course {
  id: string;
  title: string;
  description?: string;
  createTime?: string;
  updateTime?: string;
}

type CourseLite = { id: string; title: string };
type QuestionRow = QuestionVO & { tags?: unknown };

const qidKey = (r: { id?: string | number }) =>
  r.id === undefined || r.id === null ? "" : String(r.id);

const parseTags = (tags: unknown): string[] => {
  let list: string[] = [];

  if (tags == null || tags === "") return [];

  if (Array.isArray(tags)) {
    list = tags.map((x) => String(x).trim()).filter(Boolean);
  } else if (typeof tags === "string") {
    const s = tags.trim();
    if (!s) return [];
    try {
      const parsed = JSON.parse(s) as unknown;
      if (Array.isArray(parsed)) {
        list = parsed.map((x) => String(x).trim()).filter(Boolean);
      }
    } catch {
      list = s
        .split(/[\s,;]+/)
        .map((t) => t.trim())
        .filter(Boolean);
    }
  } else {
    return [];
  }

  if (list.length === 1 && /\s/.test(list[0])) {
    const parts = list[0].split(/\s+/).filter(Boolean);
    if (parts.length > 1) list = parts;
  }

  return list;
};

const dataList = ref<QuestionRow[]>([]);
const total = ref(0);
const userStore = useUserStore();
const currentPage = ref(1);

const form = ref({
  title: "",
  tagKeyword: "",
  courseFilter: "all" as string,
  sortField: "createTime",
  sortOrder: "descend",
  pageSize: 10,
});

const coursesByQuestionId = ref<Record<string, CourseLite[]>>({});
const courseOptions = ref<CourseLite[]>([]);

const courseListForRow = (r: QuestionRow) =>
  coursesByQuestionId.value[qidKey(r)] ?? [];

const myCourses = ref<Course[]>([]);

function defaultStudentCourseFilter() {
  return {
    keyword: "" as string,
    sortField: "updateTime" as string,
    sortOrder: "descend" as string,
  };
}

const studentCourseFilter = ref(defaultStudentCourseFilter());
const appliedStudentCourse = ref(defaultStudentCourseFilter());

const isStudent = computed(
  () => userStore.loginUser?.userRole === accessEnum.USER
);

const api = async (
  url: string,
  options?: { method?: string; headers?: Record<string, string>; body?: string }
) => {
  const res = await fetch(url, { credentials: "include", ...options });
  return res.json();
};

const buildQuery = (): QuestionQueryRequest => {
  const f = form.value;
  const body: QuestionQueryRequest = {
    current: currentPage.value,
    pageSize: f.pageSize,
    sortField: f.sortField,
    sortOrder: f.sortOrder,
  };
  if (f.title.trim()) body.title = f.title.trim();
  if (f.tagKeyword.trim()) body.tagKeyword = f.tagKeyword.trim();
  const courseSel =
    f.courseFilter == null || f.courseFilter === ""
      ? "all"
      : String(f.courseFilter);
  if (courseSel === "none") body.onlyWithoutCourse = true;
  else if (courseSel !== "all" && /^\d+$/.test(courseSel)) {
    (body as Record<string, unknown>).courseId = courseSel;
  }
  return body;
};

const loadCoursesForQuestions = async (ids: string[]) => {
  if (ids.length === 0) {
    coursesByQuestionId.value = {};
    return;
  }
  try {
    const res = await fetch(
      `/api/course/question/courses-by-questions?questionIds=${ids.join(",")}`,
      { credentials: "include" }
    );
    const data = await res.json();
    if (data.code === 0 && data.data) {
      const raw = data.data as Record<string, CourseLite[]>;
      const next: Record<string, CourseLite[]> = {};
      for (const id of ids) {
        const arr = raw[id] ?? raw[String(id)];
        next[id] = Array.isArray(arr) ? arr : [];
      }
      coursesByQuestionId.value = next;
    } else {
      coursesByQuestionId.value = {};
    }
  } catch {
    coursesByQuestionId.value = {};
  }
};

const loadCourseDropdown = async () => {
  try {
    const res = await fetch("/api/course/list/page?current=1&pageSize=100", {
      credentials: "include",
    });
    const json = await res.json();
    if (json.code === 0 && json.data?.records) {
      courseOptions.value = json.data.records.map(
        (c: { id: string | number; title: string }) => ({
          id: String(c.id),
          title: c.title,
        })
      );
    }
  } catch {
    courseOptions.value = [];
  }
};

const loadData = async () => {
  if (isStudent.value) {
    return;
  }
  const res = await QuestionControllerService.listQuestionVoByPage2(
    buildQuery()
  );
  if (res.code === 0 && res.data) {
    dataList.value = (res.data.records || []) as QuestionRow[];
    total.value = Number(res.data.total ?? 0);
    const ids = (res.data.records || [])
      .map((r) => qidKey(r as QuestionRow))
      .filter(Boolean);
    await loadCoursesForQuestions(ids);
  } else {
    Message.error("Ошибка загрузки: " + (res as { message?: string }).message);
  }
};

const applyFilters = () => {
  currentPage.value = 1;
  loadData();
};

const resetFilters = () => {
  form.value = {
    title: "",
    tagKeyword: "",
    courseFilter: "all",
    sortField: "createTime",
    sortOrder: "descend",
    pageSize: form.value.pageSize,
  };
  currentPage.value = 1;
  loadData();
};

const onPageChange = (page: number) => {
  currentPage.value = page;
  loadData();
};

const onPageSizeChange = (size: number) => {
  form.value.pageSize = size;
  currentPage.value = 1;
  loadData();
};

const teacherColumns = [
  {
    title: "Название",
    dataIndex: "title",
    width: 220,
    ellipsis: true,
    tooltip: true,
  },
  { title: "Теги", slotName: "tags", width: 200 },
  { title: "Курсы", slotName: "courses", width: 200 },
  { title: "Прошло", slotName: "acceptedRate", width: 130 },
  { title: "Создано", slotName: "createTime", width: 110 },
  {
    title: "Действия",
    slotName: "optional",
    fixed: "right" as const,
    width: 120,
  },
];

const courseColumns = [
  { title: "Курс", dataIndex: "title", width: 320 },
  { title: "Описание", dataIndex: "description" },
];
const router = useRouter();

const doQuestionPage = (question: QuestionRow) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

const courseTimeMs = (s?: string) => {
  if (!s) return 0;
  const t = new Date(s).getTime();
  return Number.isFinite(t) ? t : 0;
};

const displayMyCourses = computed(() => {
  const applied = appliedStudentCourse.value;
  let list = myCourses.value.slice();
  const kw = applied.keyword.trim().toLowerCase();
  if (kw) {
    list = list.filter(
      (c) =>
        (c.title || "").toLowerCase().includes(kw) ||
        (c.description || "").toLowerCase().includes(kw)
    );
  }
  const asc = applied.sortOrder === "ascend";
  const m = asc ? 1 : -1;
  const sf = applied.sortField;
  list.sort((a, b) => {
    if (sf === "title") {
      return (
        m *
        (a.title || "").localeCompare(b.title || "", "ru", {
          sensitivity: "base",
        })
      );
    }
    if (sf === "createTime") {
      return m * (courseTimeMs(a.createTime) - courseTimeMs(b.createTime));
    }
    if (sf === "updateTime") {
      return m * (courseTimeMs(a.updateTime) - courseTimeMs(b.updateTime));
    }
    return 0;
  });
  return list;
});

const applyStudentCourseFilters = () => {
  const f = studentCourseFilter.value;
  appliedStudentCourse.value = {
    keyword: f.keyword.trim(),
    sortField: f.sortField,
    sortOrder: f.sortOrder,
  };
};

const resetStudentCourseFilters = () => {
  studentCourseFilter.value = defaultStudentCourseFilter();
  appliedStudentCourse.value = defaultStudentCourseFilter();
};

const loadMyCourses = async () => {
  const res = await api("/api/course/enroll/my");
  if (res.code === 0 && Array.isArray(res.data)) {
    myCourses.value = (res.data as Course[]).map((c) => ({
      ...c,
      id: String(c.id),
    }));
  } else {
    Message.error(res.message ?? "Не удалось загрузить курсы");
  }
};

const onCourseRowClick = async (record: Course) => {
  await router.push(`/course/${record.id}/questions`);
};

onMounted(async () => {
  if (isStudent.value) {
    await loadMyCourses();
  } else {
    await loadCourseDropdown();
    await loadData();
  }
});
</script>

<style scoped>
#questionsView {
  max-width: 1280px;
  margin: 0 auto;
}

.teacher-home-intro {
  margin-bottom: 4px;
}

.teacher-home-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.teacher-home-toolbar {
  padding-bottom: 4px;
}

.text-muted {
  color: var(--color-text-3);
}

.course-tags-compact,
.question-tags-compact {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 2px;
  max-width: 100%;
}

.accepted-rate {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-warning-6);
}

.accepted-fraction {
  font-size: 13px;
  color: var(--color-text-2);
}
</style>
