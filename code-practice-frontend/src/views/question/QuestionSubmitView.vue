<template>
  <div id="QuestionSubmitView">
    <a-space direction="vertical" fill size="medium">
      <div class="submit-toolbar">
        <div class="submit-toolbar__filters">
          <a-space wrap align="center" :size="8">
            <a-select
              v-model="filterForm.courseId"
              allow-search
              placeholder="Курс"
              style="width: 220px"
              :filter-option="filterCourseOption"
            >
              <a-option value="all" label="Все курсы">Все курсы</a-option>
              <a-option
                v-for="c in courseOptions"
                :key="c.id"
                :value="String(c.id)"
                :label="c.title"
              >
                {{ c.title }}
              </a-option>
            </a-select>
            <a-input
              v-model="filterForm.questionTitle"
              allow-clear
              placeholder="Задача (название)"
              style="width: 200px"
              @press-enter="applyFilters"
            />
            <a-input
              v-model="filterForm.userName"
              allow-clear
              placeholder="Пользователь (логин)"
              style="width: 200px"
              @press-enter="applyFilters"
            />
            <a-select v-model="filterForm.language" placeholder="Язык" style="width: 140px">
              <a-option value="all">Все языки</a-option>
              <a-option value="java">java</a-option>
            </a-select>
            <a-select v-model="filterForm.status" placeholder="Статус" style="width: 200px">
              <a-option value="all">Все статусы</a-option>
              <a-option :value="0">Ожидание</a-option>
              <a-option :value="1">Выполняется</a-option>
              <a-option :value="2">Успешно</a-option>
              <a-option :value="3">Ошибка</a-option>
              <a-option :value="4">Ошибка компиляции</a-option>
            </a-select>
            <a-select v-model="filterForm.sortField" class="submit-sort-select" @change="onSortOrOrderChange">
              <a-option value="createTime">Сортировка: время</a-option>
              <a-option value="updateTime">Сортировка: обновление</a-option>
              <a-option value="language">Сортировка: язык</a-option>
              <a-option value="status">Сортировка: статус</a-option>
              <a-option value="questionId">Сортировка: задача (id)</a-option>
              <a-option value="userId">Сортировка: пользователь (id)</a-option>
            </a-select>
            <a-select v-model="filterForm.sortOrder" style="width: 150px" @change="onSortOrOrderChange">
              <a-option value="descend">По убыванию</a-option>
              <a-option value="ascend">По возрастанию</a-option>
            </a-select>
            <a-button type="primary" @click="applyFilters">Применить</a-button>
            <a-button @click="resetFilters">Сброс</a-button>
          </a-space>
        </div>
      </div>

      <a-table
        :ref="tableRef"
        :columns="columns"
        :data="dataList"
        :bordered="{ wrapper: true, cell: true }"
        :scroll="{ x: 1180 }"
        :pagination="{
          showTotal: true,
          showPageSize: true,
          current: pageState.current,
          pageSize: pageState.pageSize,
          total,
          pageSizeOptions: [10, 20, 50, 100],
        }"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #empty>
          <a-empty description="Нет отправок" />
        </template>
        <template #judgeInfo="{ record }">
          <a-tooltip
            v-if="record.judgeInfo?.message"
            :content="record.judgeInfo.message"
            position="top"
          >
            <span class="submit-judge-ellipsis-wrap">
              <a-tag
                :color="record.judgeInfo.message === 'Accepted' ? 'green' : 'red'"
                class="submit-judge-tag"
              >
                <span class="submit-judge-tag-text">{{ record.judgeInfo.message }}</span>
              </a-tag>
            </span>
          </a-tooltip>
          <span v-else class="text-muted">—</span>
        </template>

        <template #statusStr="{ record }">
          {{ record.statusStr ?? "—" }}
        </template>

        <template #courses="{ record }">
          <span v-if="!courseTitlesForRow(record).length" class="text-muted">—</span>
          <a-tooltip
            v-else
            :content="courseTitlesForRow(record).join(', ')"
            position="top"
          >
            <span class="submit-courses-cell">
              <a-tag
                v-for="(t, i) in courseTitlesForRow(record).slice(0, 2)"
                :key="i"
                size="small"
                color="arcoblue"
                style="margin-right: 4px"
                >{{ t }}</a-tag
              >
              <a-tag v-if="courseTitlesForRow(record).length > 2" size="small" color="gray"
                >+{{ courseTitlesForRow(record).length - 2 }}</a-tag
              >
            </span>
          </a-tooltip>
        </template>

        <template #questionVO="{ record }">
          <a-link
            v-if="record.questionVO?.id != null && record.questionVO?.id !== ''"
            :href="'/view/question/' + record.questionVO.id"
          >
            {{ record.questionVO.title || "Задача" }}
          </a-link>
          <span v-else class="text-muted">{{ record.questionId != null ? `id ${record.questionId}` : "—" }}</span>
        </template>

        <template #createTime="{ record }">
          {{ record.createTime ? moment(record.createTime).format("YYYY-MM-DD HH:mm") : "—" }}
        </template>
        <template #reviewActions="{ record }">
          <a-button type="text" size="small" @click="openReview(record)"> Комментарии </a-button>
        </template>
      </a-table>
    </a-space>

    <a-drawer
      v-model:visible="reviewDrawerVisible"
      :width="'100vw'"
      title="Code review"
      :footer="false"
    >
      <div v-if="currentSubmissionId">
        <p style="margin-bottom: 8px">
          Отправка:
          <b>{{ currentSubmissionId }}</b>
        </p>
        <a-row :gutter="16">
          <a-col :span="14">
            <CodeEditor
              :value="currentCode"
              :language="currentLanguage"
              :handle-change="noopChange"
            />
          </a-col>
          <a-col :span="10">
            <a-list
              v-if="reviewComments.length"
              :data="reviewComments"
              :bordered="false"
              size="small"
            >
              <template #item="{ item }">
                <a-space direction="vertical" style="width: 100%; padding: 8px 0" fill>
                  <a-space align="center" justify="space-between">
                    <div>
                      <a-tag v-if="item.lineNumber !== null" size="small"> Строка {{ item.lineNumber }} </a-tag>
                    </div>
                    <a-tag size="small" :color="item.status === 'RESOLVED' ? 'green' : 'orangered'">
                      {{ item.status }}
                    </a-tag>
                  </a-space>
                  <div>{{ item.commentText }}</div>
                  <a-button type="text" size="mini" @click="toggleStatus(item)">
                    {{
                      item.status === "RESOLVED" ? "Открыть снова" : "Пометить как решено"
                    }}
                  </a-button>
                  <a-divider style="margin: 8px 0" />
                </a-space>
              </template>
            </a-list>
            <div v-else style="margin: 8px 0 16px 0">Комментариев пока нет.</div>

            <a-form :model="newComment" layout="vertical">
              <a-form-item label="Строка (необязательно)">
                <a-input-number
                  v-model="newComment.lineNumber"
                  :min="1"
                  style="width: 100%"
                  allow-clear
                />
              </a-form-item>
              <a-form-item label="Комментарий">
                <a-textarea
                  v-model="newComment.commentText"
                  :auto-size="{ minRows: 3, maxRows: 6 }"
                  placeholder="Введите текст комментария..."
                />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="submitComment"> Добавить комментарий </a-button>
              </a-form-item>
            </a-form>
          </a-col>
        </a-row>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { QuestionControllerService } from "../../../generated";
import type { QuestionSubmitQueryRequest } from "../../../generated";
import type { QuestionSubmitVO } from "../../../generated";
import { Message } from "@arco-design/web-vue";
import moment from "moment";
import axios from "axios";
import CodeEditor from "@/components/CodeEditor.vue";

type CourseLite = { id: string; title: string };

const dataList = ref<QuestionSubmitVO[]>([]);
const tableRef = ref();
const total = ref(0);

const pageState = ref({ current: 1, pageSize: 10 });

const filterForm = ref({
  courseId: "all" as string,
  questionTitle: "",
  userName: "",
  language: "all",
  status: "all" as "all" | number,
  sortField: "createTime",
  sortOrder: "descend",
});

const courseOptions = ref<CourseLite[]>([]);

const route = useRoute();
const router = useRouter();

const reviewDrawerVisible = ref(false);
const currentSubmissionId = ref<string | null>(null);
const reviewComments = ref<any[]>([]);
const newComment = ref<{
  lineNumber: number | null;
  commentText: string;
}>({
  lineNumber: null,
  commentText: "",
});

const currentCode = ref("");
const currentLanguage = ref("java");

const courseTitlesForRow = (r: QuestionSubmitVO) => r.courseTitles ?? [];

const filterCourseOption = (inputValue: string, option: { label?: string }) => {
  if (!inputValue) return true;
  return (option.label ?? "").toLowerCase().includes(inputValue.toLowerCase());
};

const buildApiBody = (): QuestionSubmitQueryRequest => {
  const f = filterForm.value;
  const p = pageState.value;
  const body: QuestionSubmitQueryRequest = {
    current: p.current,
    pageSize: p.pageSize,
    sortField: f.sortField,
    sortOrder: f.sortOrder,
  };
  if (f.courseId !== "all" && f.courseId && /^\d+$/.test(f.courseId)) {
    body.courseId = Number(f.courseId);
  }
  if (f.questionTitle.trim()) body.questionTitleKeyword = f.questionTitle.trim();
  if (f.userName.trim()) body.userNameKeyword = f.userName.trim();
  if (f.language !== "all") body.language = f.language;
  if (f.status !== "all") body.status = f.status as number;
  return body;
};

const queryFromState = () => {
  const f = filterForm.value;
  const p = pageState.value;
  const q: Record<string, string> = {};
  if (f.courseId !== "all") q.courseId = f.courseId;
  if (f.questionTitle.trim()) q.qTitle = f.questionTitle.trim();
  if (f.userName.trim()) q.user = f.userName.trim();
  if (f.language !== "all") q.lang = f.language;
  if (f.status !== "all") q.st = String(f.status);
  q.sort = f.sortField;
  q.order = f.sortOrder;
  q.page = String(p.current);
  q.size = String(p.pageSize);
  return q;
};

const readStateFromQuery = () => {
  const q = route.query;
  const f = filterForm.value;
  const p = pageState.value;
  f.courseId = typeof q.courseId === "string" && q.courseId ? q.courseId : "all";
  f.questionTitle = typeof q.qTitle === "string" ? q.qTitle : "";
  f.userName = typeof q.user === "string" ? q.user : "";
  f.language = typeof q.lang === "string" && ["java"].includes(q.lang) ? q.lang : "all";
  if (q.st === "0" || q.st === "1" || q.st === "2" || q.st === "3" || q.st === "4") {
    f.status = Number(q.st);
  } else {
    f.status = "all";
  }
  f.sortField =
    typeof q.sort === "string" &&
    ["createTime", "updateTime", "language", "status", "questionId", "userId"].includes(q.sort)
      ? q.sort
      : "createTime";
  f.sortOrder = q.order === "ascend" ? "ascend" : "descend";
  p.current = Math.max(1, parseInt(String(q.page || "1"), 10) || 1);
  p.pageSize = Math.min(100, Math.max(1, parseInt(String(q.size || "10"), 10) || 10));
};

const syncUrl = () => {
  router.replace({ path: route.path, query: queryFromState() });
};

const loadCourseOptions = async () => {
  try {
    const res = await fetch("/api/course/list/page?current=1&pageSize=100", { credentials: "include" });
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

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionSubmitByPage2(buildApiBody());
  if (res.code === 0 && res.data) {
    dataList.value = (res.data.records ?? []) as QuestionSubmitVO[];
    total.value = res.data.total ?? 0;
  } else {
    Message.error("Ошибка загрузки: " + (res as { message?: string }).message);
  }
};

const onSortOrOrderChange = () => {
  pageState.value.current = 1;
  syncUrl();
  loadData();
};

const applyFilters = () => {
  pageState.value.current = 1;
  syncUrl();
  loadData();
};

const resetFilters = () => {
  filterForm.value = {
    courseId: "all",
    questionTitle: "",
    userName: "",
    language: "all",
    status: "all",
    sortField: "createTime",
    sortOrder: "descend",
  };
  pageState.value = { current: 1, pageSize: 10 };
  syncUrl();
  loadData();
};

const onPageChange = (page: number) => {
  pageState.value.current = page;
  syncUrl();
  loadData();
};

const onPageSizeChange = (size: number) => {
  pageState.value.pageSize = size;
  pageState.value.current = 1;
  syncUrl();
  loadData();
};

onMounted(() => {
  readStateFromQuery();
  loadCourseOptions();
  loadData();
});

const noopChange = () => {
  // read-only
};

const openReview = (record: QuestionSubmitVO) => {
  currentSubmissionId.value = record.id != null ? String(record.id) : null;
  currentCode.value = record.code || "";
  currentLanguage.value = record.language || "java";
  reviewDrawerVisible.value = true;
  fetchComments();
};

const fetchComments = async () => {
  if (!currentSubmissionId.value) {
    return;
  }
  try {
    const res = await axios.get("/api/review/comment/list", {
      params: {
        submissionId: currentSubmissionId.value,
      },
    });
    if (res.data.code === 0) {
      reviewComments.value = res.data.data || [];
    } else {
      Message.error("Ошибка загрузки комментариев: " + res.data.message);
    }
  } catch {
    Message.error("Ошибка запроса комментариев");
  }
};

const submitComment = async () => {
  if (!currentSubmissionId.value) {
    return;
  }
  if (!newComment.value.commentText.trim()) {
    Message.warning("Введите текст комментария");
    return;
  }
  try {
    const res = await axios.post("/api/review/comment/add", {
      submissionId: currentSubmissionId.value,
      lineNumber: newComment.value.lineNumber,
      commentText: newComment.value.commentText,
    });
    if (res.data.code === 0) {
      Message.success("Комментарий добавлен");
      newComment.value.commentText = "";
      fetchComments();
    } else {
      Message.error("Ошибка добавления: " + res.data.message);
    }
  } catch {
    Message.error("Ошибка запроса при добавлении");
  }
};

const toggleStatus = async (item: any) => {
  const newStatus = item.status === "RESOLVED" ? "OPEN" : "RESOLVED";
  try {
    const res = await axios.post("/api/review/comment/status", {
      id: item.id,
      status: newStatus,
    });
    if (res.data.code === 0) {
      item.status = newStatus;
      Message.success("Статус обновлён");
    } else {
      Message.error("Ошибка обновления статуса: " + res.data.message);
    }
  } catch {
    Message.error("Ошибка запроса при обновлении статуса");
  }
};

const columns = [
  { title: "Язык", dataIndex: "language", width: 88 },
  { title: "Статус", slotName: "statusStr", width: 120 },
  { title: "Результат", slotName: "judgeInfo", width: 160 },
  { title: "Курсы", slotName: "courses", width: 220 },
  { title: "Задача", slotName: "questionVO", width: 220 },
  { title: "Пользователь", dataIndex: "userName", width: 130 },
  { title: "Время", slotName: "createTime", width: 150 },
  { title: "Code review", slotName: "reviewActions", width: 148, fixed: "right" as const },
];
</script>

<style scoped>
#QuestionSubmitView {
  max-width: 1400px;
  margin: 0 auto;
}

.submit-toolbar__filters {
  flex: 1 1 auto;
  min-width: 0;
}

.submit-sort-select {
  min-width: 260px;
  width: 260px;
}

.submit-courses-cell {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 2px;
  max-width: 210px;
}

.text-muted {
  color: var(--color-text-3);
}

.submit-judge-ellipsis-wrap {
  display: inline-block;
  max-width: 150px;
  vertical-align: middle;
}

.submit-judge-tag {
  max-width: 100%;
  vertical-align: top;
  box-sizing: border-box;
}

.submit-judge-tag-text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
  min-width: 0;
}
</style>
