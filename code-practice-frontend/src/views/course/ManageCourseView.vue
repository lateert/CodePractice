<template>
  <div id="manageCourseView">
    <a-space direction="vertical" fill size="medium">
      <div class="manage-course-toolbar">
        <a-button type="primary" status="success" @click="showAddModal">Создать курс</a-button>
        <div class="manage-course-toolbar__filters">
          <a-space wrap align="center" :size="8">
            <a-input
              v-model="filterForm.title"
              allow-clear
              placeholder="Название (частичное совпадение)"
              style="width: 260px"
              @press-enter="applyFilters"
            />
            <a-select v-model="filterForm.status" placeholder="Статус" style="width: 200px">
              <a-option value="all">Все статусы</a-option>
              <a-option value="published">Опубликован</a-option>
              <a-option value="draft">Черновик</a-option>
            </a-select>
            <a-button type="primary" @click="applyFilters">Применить</a-button>
            <a-button @click="resetFilters">Сброс</a-button>
          </a-space>
        </div>
      </div>
      <a-table
        :columns="columns"
        :data="dataList"
        :bordered="{ wrapper: true, cell: true }"
        :pagination="{
          showTotal: true,
          showPageSize: true,
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total,
          pageSizeOptions: [10, 20, 50, 100],
        }"
        :scroll="{ x: 920 }"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #empty>
          <a-empty description="Нет данных" />
        </template>
        <template #description="{ record }">
          <a-tooltip v-if="record.description" :content="record.description" position="top">
            <span class="manage-course-desc-cell">{{ record.description }}</span>
          </a-tooltip>
          <span v-else class="manage-course-desc-cell text-muted">—</span>
        </template>
        <template #optional="{ record }">
          <a-space :size="6" :wrap="false">
            <a-button type="primary" size="small" @click="doEdit(record)">Изменить</a-button>
            <a-button
              size="small"
              :status="isPublishedRecord(record) ? 'warning' : 'success'"
              @click="togglePublish(record)"
            >
              {{ isPublishedRecord(record) ? "Закрыть доступ" : "Опубликовать" }}
            </a-button>
            <a-dropdown trigger="click" @select="(v) => onRowExtraSelect(v, record)">
              <a-button size="small">Ещё</a-button>
              <template #content>
                <a-doption value="tasks">Задачи курса</a-doption>
                <a-doption value="delete">Удалить</a-doption>
              </template>
            </a-dropdown>
          </a-space>
        </template>
        <template #publishStatus="{ record }">
          <a-tag :color="isPublishedRecord(record) ? 'green' : 'gray'">
            {{ isPublishedRecord(record) ? "Опубликован" : "Черновик" }}
          </a-tag>
        </template>
      </a-table>
    </a-space>

    <!-- Создать курс -->
    <a-modal
      v-model:visible="addModalVisible"
      title="Создать курс"
      ok-text="Создать"
      cancel-text="Отмена"
      :on-before-ok="submitAdd"
      @cancel="addModalVisible = false"
    >
      <a-form :model="addForm" layout="vertical">
        <a-form-item label="Название" required>
          <a-input v-model="addForm.title" placeholder="Название курса" />
        </a-form-item>
        <a-form-item label="Описание">
          <a-textarea v-model="addForm.description" placeholder="Описание (Markdown)" :auto-size="{ minRows: 3 }" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Редактировать курс -->
    <a-modal
      v-model:visible="editModalVisible"
      title="Редактировать курс"
      ok-text="Сохранить"
      cancel-text="Отмена"
      :on-before-ok="submitEdit"
      @cancel="editModalVisible = false"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="Название" required>
          <a-input v-model="editForm.title" placeholder="Название курса" />
        </a-form-item>
        <a-form-item label="Описание">
          <a-textarea v-model="editForm.description" placeholder="Описание (Markdown)" :auto-size="{ minRows: 3 }" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Задачи курса: список + добавить задачу -->
    <a-modal
      v-model:visible="tasksModalVisible"
      title="Задачи курса"
      width="720px"
      :footer="false"
      unmount-on-close
      @close="closeTasksModal"
    >
      <template v-if="currentCourse">
        <a-space direction="vertical" fill style="margin-bottom: 16px">
          <a-space>
            <span>Добавить задачу:</span>
            <a-select
              v-model="bindQuestionId"
              placeholder="Выберите задачу"
              allow-search
              style="width: 320px"
              :loading="questionsLoading"
              :filter-option="filterQuestionOption"
              :field-names="{ value: 'id', label: 'title' }"
            >
              <template #empty>
                <a-empty description="Нет данных" />
              </template>
              <a-option v-for="q in availableQuestions" :key="q.id" :value="q.id" :label="q.title" />
            </a-select>
            <a-button type="primary" :disabled="!bindQuestionId" :loading="bindLoading" @click="bindQuestion">
              Добавить
            </a-button>
          </a-space>
        </a-space>
        <a-spin :loading="courseQuestionsLoading">
          <a-table
            :columns="taskColumns"
            :data="courseQuestionList"
            :pagination="false"
            :bordered="{ wrapper: true, cell: true }"
            size="small"
          />
        </a-spin>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { Message, Modal } from "@arco-design/web-vue";

interface Course {
  id: number | string;
  title: string;
  description?: string;
  isPublished?: number | boolean | string;
}

const dataList = ref<Course[]>([]);
const total = ref(0);
const searchParams = ref({ pageSize: 10, current: 1 });
const filterForm = ref({
  title: "",
  status: "all" as "all" | "published" | "draft",
});

const addModalVisible = ref(false);
const editModalVisible = ref(false);
const addForm = ref({ title: "", description: "" });
const editForm = ref<{ id?: number | string; title: string; description: string }>({
  title: "",
  description: "",
});

const api = async (url: string, options?: RequestInit) => {
  const res = await fetch(url, { credentials: "include", ...options });
  return res.json();
};

const listQueryString = () => {
  const { current, pageSize } = searchParams.value;
  const params = new URLSearchParams();
  params.set("current", String(current));
  params.set("pageSize", String(pageSize));
  params.set("mineOnly", "1");
  const t = filterForm.value.title.trim();
  if (t) params.set("title", t);
  if (filterForm.value.status === "published") params.set("isPublished", "1");
  if (filterForm.value.status === "draft") params.set("isPublished", "0");
  return params.toString();
};

const loadData = async () => {
  try {
    const data = await api(`/api/course/list/page?${listQueryString()}`);
    if (data.code === 0 && data.data) {
      dataList.value = data.data.records || [];
      total.value = data.data.total ?? 0;
    } else {
      Message.error(data.message ?? "Ошибка загрузки курсов");
    }
  } catch {
    Message.error("Не удалось загрузить курсы");
  }
};

const applyFilters = () => {
  searchParams.value = { ...searchParams.value, current: 1 };
  loadData();
};

const resetFilters = () => {
  filterForm.value = { title: "", status: "all" };
  searchParams.value = { ...searchParams.value, current: 1 };
  loadData();
};

const isPublishedRecord = (record: Course) => {
  const p = record.isPublished;
  return p === 1 || p === true || p === "1";
};

const onRowExtraSelect = (value: string | number, record: Course) => {
  const v = String(value);
  if (v === "tasks") openTasksModal(record);
  else if (v === "delete") confirmDelete(record);
};

const showAddModal = () => {
  addForm.value = { title: "", description: "" };
  addModalVisible.value = true;
};

const submitAdd = async () => {
  if (!addForm.value.title?.trim()) {
    Message.warning("Введите название курса");
    return false;
  }
  try {
    const data = await api("/api/course/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        title: addForm.value.title.trim(),
        description: addForm.value.description?.trim() || undefined,
      }),
    });
    if (data.code === 0) {
      Message.success("Курс создан");
      loadData();
      return true;
    }
    Message.error(data.message ?? "Ошибка создания курса");
    return false;
  } catch {
    Message.error("Не удалось создать курс");
    return false;
  }
};

const doEdit = (record: Course) => {
  editForm.value = {
    id: record.id,
    title: record.title || "",
    description: record.description || "",
  };
  editModalVisible.value = true;
};

const submitEdit = async () => {
  if (!editForm.value.id || !editForm.value.title?.trim()) {
    Message.warning("Введите название курса");
    return false;
  }
  try {
    const data = await api("/api/course/update", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        id: editForm.value.id,
        title: editForm.value.title.trim(),
        description: editForm.value.description?.trim() || undefined,
      }),
    });
    if (data.code === 0) {
      Message.success("Курс обновлён");
      loadData();
      return true;
    }
    Message.error(data.message ?? "Ошибка обновления курса");
    return false;
  } catch {
    Message.error("Не удалось обновить курс");
    return false;
  }
};

const doDelete = async (record: Course) => {
  try {
    const data = await api("/api/course/delete", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id: record.id }),
    });
    if (data.code === 0) {
      Message.success("Курс удалён");
      loadData();
    } else {
      Message.error(data.message ?? "Ошибка удаления курса");
    }
  } catch {
    Message.error("Не удалось удалить курс");
  }
};

const confirmDelete = (record: Course) => {
  Modal.confirm({
    title: "Подтверждение удаления",
    content: "Вы уверены, что хотите удалить курс?",
    okText: "Удалить",
    cancelText: "Отмена",
    modalClass: "delete-confirm-modal",
    okButtonProps: { status: "danger" },
    simple: true,
    onOk: () => doDelete(record),
  });
};

const onPageChange = (page: number) => {
  searchParams.value = { ...searchParams.value, current: page };
  loadData();
};

const onPageSizeChange = (size: number) => {
  searchParams.value = { ...searchParams.value, pageSize: size, current: 1 };
  loadData();
};

const togglePublish = async (record: Course) => {
  try {
    const published = !isPublishedRecord(record);
    const data = await api("/api/course/publish", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        courseId: record.id,
        published,
      }),
    });
    if (data.code === 0) {
      Message.success(published ? "Курс опубликован" : "Доступ к курсу закрыт");
      loadData();
    } else {
      Message.error(data.message ?? "Ошибка изменения статуса публикации");
    }
  } catch {
    Message.error("Не удалось изменить статус публикации");
  }
};

const columns = [
  { title: "Название", dataIndex: "title", width: 200 },
  { title: "Статус", slotName: "publishStatus", width: 130 },
  { title: "Описание", dataIndex: "description", slotName: "description", width: 305 },
  { title: "Действия", slotName: "optional", fixed: "right" as const, width: 275 },
];

// Задачи курса
const currentCourse = ref<Course | null>(null);
const tasksModalVisible = ref(false);
const courseQuestionList = ref<{ id: string; title: string; position?: number }[]>([]);
const courseQuestionsLoading = ref(false);
const bindQuestionId = ref<string | undefined>(undefined);
const bindLoading = ref(false);
const questionsLoading = ref(false);
const allQuestionsList = ref<{ id: string; title: string }[]>([]);

const qid = (v: unknown) => (v === undefined || v === null ? "" : String(v));

const filterQuestionOption = (inputValue: string, option: { label?: string }) => {
  if (!inputValue) return true;
  return (option.label ?? "").toLowerCase().includes(inputValue.toLowerCase());
};

const taskColumns = [
  { title: "Позиция", dataIndex: "position", width: 80 },
  { title: "Задача", dataIndex: "title" },
];

const openTasksModal = async (course: Course) => {
  currentCourse.value = course;
  tasksModalVisible.value = true;
  bindQuestionId.value = undefined;
  await loadCourseQuestions();
  await loadAllQuestions();
};

const closeTasksModal = () => {
  currentCourse.value = null;
  courseQuestionList.value = [];
};

const loadCourseQuestions = async () => {
  if (!currentCourse.value) return;
  courseQuestionsLoading.value = true;
  try {
    const data = await api(`/api/course/question/list?courseId=${currentCourse.value.id}`);
    if (data.code === 0 && Array.isArray(data.data)) {
      courseQuestionList.value = (data.data as { id: unknown; title: string; position?: number }[]).map((q, i) => ({
        id: qid(q.id),
        title: q.title,
        position: (q as { position?: number }).position ?? i + 1,
      }));
    } else {
      courseQuestionList.value = [];
    }
  } catch {
    courseQuestionList.value = [];
  } finally {
    courseQuestionsLoading.value = false;
  }
};

const loadAllQuestions = async () => {
  questionsLoading.value = true;
  try {
    /** API /question/list/page: pageSize не больше 100 */
    const pageSize = 100;
    const merged: { id: string; title: string }[] = [];
    let current = 1;
    for (;;) {
      const data = await api("/api/question/list/page", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          current,
          pageSize,
          sortField: "createTime",
          sortOrder: "descend",
        }),
      });
      if (data.code !== 0 || !data.data?.records) {
        if (current === 1) {
          Message.error(data.message ?? "Не удалось загрузить список задач");
        }
        break;
      }
      const chunk = data.data.records as { id: unknown; title?: string }[];
      for (const q of chunk) {
        merged.push({ id: qid(q.id), title: q.title || "" });
      }
      if (chunk.length < pageSize) break;
      current += 1;
      if (current > 100) break;
    }
    allQuestionsList.value = merged;
  } catch {
    allQuestionsList.value = [];
    Message.error("Ошибка запроса списка задач");
  } finally {
    questionsLoading.value = false;
  }
};

const courseQuestionIds = computed(() => new Set(courseQuestionList.value.map((q) => qid(q.id))));
const availableQuestions = computed(() =>
  allQuestionsList.value.filter((q) => !courseQuestionIds.value.has(qid(q.id)))
);

const bindQuestion = async () => {
  if (!currentCourse.value || !bindQuestionId.value) return;
  bindLoading.value = true;
  try {
    const data = await api("/api/course/question/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
        courseId: currentCourse.value.id,
        questionId: bindQuestionId.value,
      }),
    });
    if (data.code === 0) {
      Message.success("Задача добавлена в курс");
      bindQuestionId.value = undefined;
      loadCourseQuestions();
      loadAllQuestions();
    } else {
      Message.error(data.message ?? "Ошибка добавления задачи");
    }
  } catch {
    Message.error("Не удалось добавить задачу");
  } finally {
    bindLoading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.manage-course-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 16px;
}

.manage-course-toolbar__filters {
  flex: 1 1 auto;
  min-width: 0;
}

.manage-course-desc-cell {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  max-width: 305px;
  line-height: 1.4;
  word-break: break-word;
}

.text-muted {
  color: var(--color-text-3);
}
</style>
