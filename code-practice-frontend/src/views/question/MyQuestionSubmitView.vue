<template>
  <div id="MyQuestionSolutionView">
    <a-space direction="vertical" fill size="medium">
      <a-space wrap align="center" :size="8">
        <a-input
          v-model="filterForm.questionTitle"
          allow-clear
          placeholder="Задача (название)"
          style="width: 220px"
          @press-enter="applyFilters"
        />
        <a-select
          v-model="filterForm.language"
          placeholder="Язык"
          style="width: 140px"
        >
          <a-option value="all">Все языки</a-option>
          <a-option value="java">java</a-option>
        </a-select>
        <a-select
          v-model="filterForm.status"
          placeholder="Статус"
          style="width: 200px"
        >
          <a-option value="all">Все статусы</a-option>
          <a-option :value="0">Ожидание</a-option>
          <a-option :value="1">Выполняется</a-option>
          <a-option :value="2">Успешно</a-option>
          <a-option :value="3">Ошибка</a-option>
          <a-option :value="4">Ошибка компиляции</a-option>
        </a-select>
        <a-select
          v-model="filterForm.sortField"
          placeholder="Сортировка"
          style="width: 260px"
        >
          <a-option value="createTime">Время отправки</a-option>
          <a-option value="updateTime">Время обновления</a-option>
          <a-option value="language">Язык</a-option>
          <a-option value="status">Статус</a-option>
          <a-option value="questionId">Задача (id)</a-option>
        </a-select>
        <a-select v-model="filterForm.sortOrder" style="width: 160px">
          <a-option value="descend">По убыванию</a-option>
          <a-option value="ascend">По возрастанию</a-option>
        </a-select>
        <a-button type="primary" @click="applyFilters">Применить</a-button>
        <a-button @click="resetFilters">Сброс</a-button>
      </a-space>

      <a-table
        :columns="columns"
        :data="dataList"
        :bordered="{ wrapper: true, cell: true }"
        :scroll="{ x: 1280 }"
        :pagination="{
          showTotal: true,
          showPageSize: true,
          pageSize: query.pageSize,
          current: query.current,
          total: Number(total),
          pageSizeOptions: [10, 20, 50, 100],
        }"
        row-key="id"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #empty>
          <a-empty description="Нет отправок" />
        </template>

        <template #questionVO="{ record }">
          <a-link
            v-if="record.questionVO?.id != null && record.questionVO?.id !== ''"
            :href="'/view/question/' + record.questionVO.id"
          >
            {{ record.questionVO.title || "Задача" }}
          </a-link>
          <span v-else class="text-muted">{{
            record.questionId != null ? `id ${record.questionId}` : "—"
          }}</span>
        </template>

        <template #courses="{ record }">
          <span v-if="!courseTitlesForRow(record).length" class="text-muted"
            >—</span
          >
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
              <a-tag
                v-if="courseTitlesForRow(record).length > 2"
                size="small"
                color="gray"
                >+{{ courseTitlesForRow(record).length - 2 }}</a-tag
              >
            </span>
          </a-tooltip>
        </template>

        <template #judgeInfo="{ record }">
          <a-tooltip
            v-if="record.judgeInfo?.message"
            :content="record.judgeInfo.message"
            position="top"
          >
            <span class="submit-judge-ellipsis-wrap">
              <a-tag
                :color="
                  record.judgeInfo.message === 'Accepted' ? 'green' : 'red'
                "
                class="submit-judge-tag"
              >
                <span class="submit-judge-tag-text">{{
                  record.judgeInfo.message
                }}</span>
              </a-tag>
            </span>
          </a-tooltip>
          <span v-else class="text-muted">—</span>
        </template>

        <template #time="{ record }">
          {{
            record.judgeInfo?.time != null ? `${record.judgeInfo.time} ms` : "—"
          }}
        </template>

        <template #memory="{ record }">
          <template v-if="record.judgeInfo?.memory != null">
            {{ (Number(record.judgeInfo.memory) / 1024 / 1024).toFixed(1) }} MB
          </template>
          <template v-else>—</template>
        </template>

        <template #statusStr="{ record }">
          {{ record.statusStr ?? "—" }}
        </template>

        <template #createTime="{ record }">
          {{
            record.createTime
              ? moment(record.createTime).format("YYYY-MM-DD HH:mm")
              : "—"
          }}
        </template>

        <template #reviewIndicator="{ record }">
          <a-button type="text" size="small" @click="openReview(record)">
            Комментарии
          </a-button>
        </template>
      </a-table>
    </a-space>

    <a-drawer
      v-model:visible="reviewDrawerVisible"
      :width="900"
      title="Комментарии code review"
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
              :handle-change="() => {}"
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
                <a-space
                  direction="vertical"
                  style="width: 100%; padding: 8px 0"
                  fill
                >
                  <a-space align="center" justify="space-between">
                    <div>
                      <a-tag size="small" v-if="item.lineNumber !== null">
                        Строка {{ item.lineNumber }}
                      </a-tag>
                    </div>
                    <a-tag
                      size="small"
                      :color="
                        item.status === 'RESOLVED' ? 'green' : 'orangered'
                      "
                    >
                      {{ item.status }}
                    </a-tag>
                  </a-space>
                  <div>{{ item.commentText }}</div>
                  <a-divider style="margin: 8px 0" />
                </a-space>
              </template>
            </a-list>
            <div v-else>Комментариев к этой отправке пока нет.</div>
          </a-col>
        </a-row>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, watch, withDefaults } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitQueryRequest,
  type QuestionSubmitVO,
} from "../../../generated";
import { Message } from "@arco-design/web-vue";
import moment from "moment";
import axios from "axios";
import { useUserStore } from "@/stores/user";
import CodeEditor from "@/components/CodeEditor.vue";

interface Props {
  questionId?: string | number;
}
const props = withDefaults(defineProps<Props>(), {
  questionId: undefined,
});

const userStore = useUserStore();

function defaultFilterForm() {
  return {
    questionTitle: "" as string,
    language: "all" as string,
    status: "all" as string | number,
    sortField: "createTime" as string,
    sortOrder: "descend" as string,
  };
}

const filterForm = ref(defaultFilterForm());

const query = ref({
  current: 1,
  pageSize: 10,
  questionTitleKeyword: "" as string,
  language: undefined as string | undefined,
  status: undefined as number | undefined,
  sortField: "createTime",
  sortOrder: "descend",
});

const dataList = ref<QuestionSubmitVO[]>([]);
const total = ref(0);

const reviewDrawerVisible = ref(false);
const currentSubmissionId = ref<string | null>(null);
const reviewComments = ref<
  { lineNumber: number | null; status: string; commentText: string }[]
>([]);
const currentCode = ref("");
const currentLanguage = ref("java");

const courseTitlesForRow = (record: QuestionSubmitVO) =>
  record.courseTitles ?? [];

const buildRequest = (): QuestionSubmitQueryRequest => {
  const uid = userStore.loginUser?.id;
  const q = query.value;
  const body: QuestionSubmitQueryRequest = {
    current: q.current,
    pageSize: q.pageSize,
    userId: uid as never,
    questionId: props.questionId as never,
    sortField: q.sortField,
    sortOrder: q.sortOrder,
  };
  if (q.questionTitleKeyword) {
    body.questionTitleKeyword = q.questionTitleKeyword;
  }
  if (q.language) {
    body.language = q.language;
  }
  if (q.status != null) {
    body.status = q.status;
  }
  return body;
};

const loadData = async () => {
  const uid = userStore.loginUser?.id;
  if (!uid) {
    return;
  }
  const res = await QuestionControllerService.listQuestionSubmitByPage2(
    buildRequest()
  );
  if (res.code === 0 && res.data) {
    dataList.value = (res.data.records ?? []) as QuestionSubmitVO[];
    total.value = Number(res.data.total ?? 0);
  } else {
    Message.error("Ошибка загрузки: " + (res as { message?: string }).message);
  }
};

watch(
  () => userStore.loginUser?.id,
  (id) => {
    if (id) {
      loadData();
    } else {
      dataList.value = [];
      total.value = 0;
    }
  },
  { immediate: true }
);

watch(
  () => props.questionId,
  () => {
    query.value = { ...query.value, current: 1 };
    loadData();
  }
);

const columns = [
  { title: "Задача", slotName: "questionVO", width: 240 },
  { title: "Курсы", slotName: "courses", width: 200 },
  { title: "Язык", dataIndex: "language", width: 88 },
  { title: "Статус", slotName: "statusStr", width: 120 },
  { title: "Время", slotName: "time", width: 100 },
  { title: "Память", slotName: "memory", width: 100 },
  { title: "Результат", slotName: "judgeInfo", width: 160 },
  { title: "Дата", slotName: "createTime", width: 150 },
  {
    title: "Code review",
    slotName: "reviewIndicator",
    width: 158,
    fixed: "right" as const,
  },
];

const applyFilters = () => {
  const f = filterForm.value;
  query.value = {
    ...query.value,
    current: 1,
    questionTitleKeyword: f.questionTitle.trim(),
    language: f.language === "all" ? undefined : f.language,
    status: f.status === "all" ? undefined : Number(f.status),
    sortField: f.sortField,
    sortOrder: f.sortOrder,
  };
  loadData();
};

const resetFilters = () => {
  filterForm.value = defaultFilterForm();
  query.value = {
    current: 1,
    pageSize: query.value.pageSize,
    questionTitleKeyword: "",
    language: undefined,
    status: undefined,
    sortField: "createTime",
    sortOrder: "descend",
  };
  loadData();
};

const onPageChange = (page: number) => {
  query.value = { ...query.value, current: page };
  loadData();
};

const onPageSizeChange = (size: number) => {
  query.value = { ...query.value, pageSize: size, current: 1 };
  loadData();
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
</script>

<style scoped>
#MyQuestionSolutionView {
  max-width: 1280px;
  margin: 0 auto;
}
.text-muted {
  color: var(--color-text-3);
}
.submit-courses-cell {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 2px;
  max-width: 190px;
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
