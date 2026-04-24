<template>
  <div id="manageQuestionView">
    <a-space direction="vertical" fill size="medium">
      <div class="manage-question-toolbar">
        <a-space align="center" :size="10" class="manage-question-toolbar__actions">
          <a-button type="primary" status="success" @click="doCreate">Создать</a-button>
          <a-switch v-model="compactList" type="round" size="small">
            <template #checked>Компактно</template>
            <template #unchecked>Подробно</template>
          </a-switch>
          <a-button size="small" @click="exportCsvPage">CSV (страница)</a-button>
        </a-space>
        <div class="manage-question-toolbar__filters">
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
              placeholder="Тег (подстрока)"
              style="width: 180px"
              @press-enter="applyFilters"
            />
            <a-select
              v-model="form.courseFilter"
              allow-search
              allow-clear
              placeholder="Курс — поиск по названию"
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
            <a-select v-model="form.sortField" class="manage-question-sort-select">
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
      </div>

      <a-table
        :columns="columns"
        :data="dataList"
        :bordered="{ wrapper: true, cell: true }"
        :pagination="{
          showTotal: true,
          showPageSize: true,
          pageSize: form.pageSize,
          current: currentPage,
          total,
          pageSizeOptions: [10, 20, 50, 100],
        }"
        :scroll="scroll"
        row-key="id"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #empty>
          <a-empty description="Нет задач" />
        </template>
        <template #tags="{ record }">
          <template v-for="tagArr in [parseTags(record.tags)]" :key="qidKey(record)">
            <span v-if="!tagArr.length" class="text-muted">—</span>
            <a-tooltip v-else :content="tagArr.join(', ')" position="top">
              <span class="question-tags-compact">
                <a-tag
                  v-for="(tag, index) in tagArr.slice(0, 4)"
                  :key="index"
                  color="green"
                  size="small"
                  style="margin-right: 4px"
                  >{{ tag }}</a-tag
                >
                <a-tag v-if="tagArr.length > 4" size="small" color="gray"
                  >+{{ tagArr.length - 4 }}</a-tag
                >
              </span>
            </a-tooltip>
          </template>
        </template>
        <template #createTime="{ record }">
          {{ record.createTime ? moment(record.createTime).format("YYYY-MM-DD") : "—" }}
        </template>
        <template #courses="{ record }">
          <span v-if="!courseListForRow(record).length" class="text-muted">—</span>
          <a-tooltip
            v-else
            :content="courseListForRow(record).map((c) => c.title).join(', ')"
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
        <template #optional="{ record }">
          <a-space>
            <a-button type="primary" @click="doUpdate(record)">Изменить</a-button>
            <a-button status="danger" @click="confirmDelete(record)">Удалить</a-button>
          </a-space>
        </template>
      </a-table>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { Question, QuestionControllerService } from "../../../generated";
import type { QuestionQueryRequest } from "../../../generated";
import { Message, Modal } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import moment from "moment/moment";

type QuestionRow = Question & { userName?: string };
/** id курса/задачи как строка — snowflake > Number.MAX_SAFE_INTEGER */
type CourseLite = { id: string; title: string };

const qidKey = (r: { id?: string | number }) =>
  r.id === undefined || r.id === null ? "" : String(r.id);

const courseListForRow = (r: QuestionRow) => coursesByQuestionId.value[qidKey(r)] ?? [];

const dataList = ref<QuestionRow[]>([]);
const total = ref(0);
const currentPage = ref(1);
const compactList = ref(true);

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

/** Нормализует теги: JSON-массив в строке, массив с API, легаси — одна строка с пробелами. */
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
      list = s.split(/[\s,;]+/).map((t) => t.trim()).filter(Boolean);
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
    f.courseFilter == null || f.courseFilter === "" ? "all" : String(f.courseFilter);
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

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionByPage(buildQuery());
  if (res.code === 0 && res.data) {
    dataList.value = (res.data.records || []) as QuestionRow[];
    total.value = res.data.total ?? 0;
    const ids = (res.data.records || []).map((r) => qidKey(r)).filter(Boolean);
    await loadCoursesForQuestions(ids);
  } else {
    Message.error("Ошибка загрузки: " + (res as { message?: string }).message);
  }
};

const loadCourseDropdown = async () => {
  try {
    const res = await fetch("/api/course/list/page?current=1&pageSize=100", {
      credentials: "include",
    });
    const json = await res.json();
    if (json.code === 0 && json.data?.records) {
      courseOptions.value = json.data.records.map((c: { id: string | number; title: string }) => ({
        id: String(c.id),
        title: c.title,
      }));
    }
  } catch {
    courseOptions.value = [];
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

watch(
  () => [form.value.sortField, form.value.sortOrder] as const,
  () => {
    currentPage.value = 1;
    loadData();
  }
);

onMounted(() => {
  loadCourseDropdown();
  loadData();
});

const scroll = computed(() => ({
  x: compactList.value ? 1240 : 2040,
}));

const baseColumns = [
  { title: "Название", dataIndex: "title", width: 200 },
  { title: "Теги", slotName: "tags", width: 220 },
  { title: "Отправок", dataIndex: "submitNum", width: 90 },
  { title: "Прошло", dataIndex: "acceptedNum", width: 80 },
  { title: "Курсы", slotName: "courses", width: 200 },
  { title: "Автор", dataIndex: "userName", width: 120 },
  { title: "Создано", slotName: "createTime", width: 110 },
  { title: "Действия", slotName: "optional", fixed: "right" as const, width: 200 },
];

const heavyColumns = [
  { title: "Условие", dataIndex: "content", ellipsis: true, tooltip: true, width: 200 },
  { title: "Ответ", dataIndex: "answer", ellipsis: true, tooltip: true, width: 160 },
  { title: "Настройки", dataIndex: "judgeConfig", ellipsis: true, tooltip: true, width: 120 },
  { title: "Тесты", dataIndex: "judgeCase", ellipsis: true, tooltip: true, width: 120 },
];

const columns = computed(() => {
  if (compactList.value) {
    return baseColumns;
  }
  const c = [...baseColumns];
  c.splice(1, 0, ...heavyColumns);
  return c;
});

const escapeCsv = (v: string) => {
  if (v.includes('"') || v.includes(",") || v.includes("\n")) {
    return `"${v.replace(/"/g, '""')}"`;
  }
  return v;
};

const exportCsvPage = () => {
  const rows: string[][] = [];
  const header = [
    "id",
    "title",
    "tags",
    "submitNum",
    "acceptedNum",
    "courses",
    "userName",
    "createTime",
  ];
  rows.push(header);
  for (const r of dataList.value) {
    const tags = parseTags(r.tags).join("; ");
    const cnames = (coursesByQuestionId.value[qidKey(r)] || []).map((c) => c.title).join("; ");
    rows.push([
      String(r.id ?? ""),
      r.title ?? "",
      tags,
      String(r.submitNum ?? ""),
      String(r.acceptedNum ?? ""),
      cnames,
      r.userName ?? "",
      r.createTime ? moment(r.createTime).format("YYYY-MM-DD") : "",
    ]);
  }
  const csv = rows.map((line) => line.map((c) => escapeCsv(c)).join(",")).join("\n");
  const blob = new Blob(["\ufeff" + csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `questions-page-${currentPage.value}.csv`;
  a.click();
  URL.revokeObjectURL(url);
};

const doDelete = async (question: Question) => {
  const res = await QuestionControllerService.deleteQuestion({
    id: question.id as any,
  });
  if (res.code === 0) {
    Message.success("Удалено");
    loadData();
  } else {
    Message.error("Ошибка удаления: " + (res as { message?: string }).message);
  }
};

const confirmDelete = (question: Question) => {
  Modal.confirm({
    title: "Подтверждение удаления",
    content: "Вы уверены, что хотите удалить задачу?",
    okText: "Удалить",
    cancelText: "Отмена",
    modalClass: "delete-confirm-modal",
    okButtonProps: { status: "danger" },
    simple: true,
    onOk: () => doDelete(question),
  });
};

const router = useRouter();
const doUpdate = (question: { id?: string | number }) => {
  router.push({
    path: "/question/update",
    query: { id: String(question.id ?? "") },
  });
};

const doCreate = () => {
  router.push({ path: "/question/add" });
};
</script>

<style scoped>
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

.manage-question-sort-select {
  min-width: 320px;
  width: 320px;
}

.manage-question-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 16px;
}

.manage-question-toolbar__actions {
  flex-shrink: 0;
}

.manage-question-toolbar__filters {
  flex: 1 1 auto;
  min-width: 0;
}
</style>
