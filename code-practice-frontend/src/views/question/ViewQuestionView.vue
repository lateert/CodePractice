<template>
  <div id="viewQuestionView">
    <a-row :gutter="[24, 24]">
      <a-col :md="12" :xs="24">
        <a-tabs
          default-active-key="question"
          v-model:active-key="activeKey"
          lazy-load
          type="card-gutter"
          :editable="true"
          @delete="handleDelete"
          auto-switch
          animation
        >
          <a-tab-pane :closable="false" key="question" title="Описание">
            <a-card
              v-if="question"
              :title="question.title"
              style="height: 75vh; overflow: auto"
            >
              <a-descriptions
                title="Условия проверки"
                :column="{ xs: 1, md: 3, lg: 4 }"
              >
                <a-descriptions-item label="Ограничение по времени">
                  <a-tag>{{ question.judgeConfig.timeLimit }}</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="Ограничение по памяти">
                  <a-tag>{{ question.judgeConfig.memoryLimit }}</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="Ограничение стека">
                  <a-tag>{{ question.judgeConfig.stackLimit }}</a-tag>
                </a-descriptions-item>
              </a-descriptions>
              <MdViewer :value="question.content || ''" />
              <template #extra>
                <a-space wrap>
                  <a-tag
                    v-for="(tag, index) of question.tags"
                    :key="index"
                    color="green"
                    >{{ tag }}</a-tag
                  >
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>
          <a-tab-pane :closable="false" key="solution" title="Разбор">
            <QuestionsSolutionView
              @doAddTab="openSolutionTab"
              :questionId="props.id"
            />
          </a-tab-pane>
          <a-tab-pane :closable="false" key="record" title="Мои отправки">
            <MyQuestionSubmitView :question-id="props.id" />
          </a-tab-pane>
          <a-tab-pane
            v-for="item of data"
            :key="String(item.key)"
            :title="tabTitle(item)"
            closable
          >
            <div style="margin: 12px">
              <div style="height: 72vh; overflow: auto">
                <a-space direction="vertical" fill>
                  <span style="font-size: 16px; font-weight: bold">{{
                    item.title
                  }}</span>
                  <a-space>
                    <a-avatar :image-url="item.userVO?.userAvatar || defaultUserAvatar" />
                    <a-space direction="vertical" fill>
                      <span style="font-size: 16px">{{
                        item.userVO?.userName ?? "—"
                      }}</span>
                      <span
                        v-if="formatDateYMD(item.createTime)"
                        style="font-size: 10px; color: rgb(128, 128, 128)"
                      >
                        <icon-calendar />&nbsp;&nbsp;{{
                          formatDateYMD(item.createTime)
                        }}</span
                      >
                    </a-space>
                  </a-space>
                  <a-space wrap>
                    <a-tag
                      v-for="(tag, index) of JSON.parse(item.tags)"
                      :key="index"
                      >{{ tag }}</a-tag
                    >
                  </a-space>
                </a-space>

                <MdViewer :value="item?.content || ''" />
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </a-col>
      <a-col :md="12" :xs="24">
        <a-space direction="vertical" fill>
          <a-space>
            <a-button type="primary" @click="doRunCode"> Запустить </a-button>
            <a-button type="primary" status="success" @click="doSubmit">
              Отправить
            </a-button>
          </a-space>
          <CodeEditor
            :value="form.code"
            :language="form.language"
            :handle-change="onCodeChange"
          />
        </a-space>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, withDefaults, ref, defineProps } from "vue";
import {
  QuestionControllerService,
  QuestionVO,
} from "../../../generated";
import { Message } from "@arco-design/web-vue";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import MyQuestionSubmitView from "@/views/question/MyQuestionSubmitView.vue";
import QuestionsSolutionView from "@/views/question/QuestionsSolutionView.vue";
import type { QuestionSolution } from "../../../generated";
import { formatDateYMD } from "@/utils/formatDate";

type SolutionTab = QuestionSolution & {
  key: number | string;
  title: string;
  content?: string;
};

const data = ref<SolutionTab[]>([]);

const activeKey = ref("question");

/** Картинка по умолчанию, если в профиле нет userAvatar (тот же источник, что в списке разборов). */
const defaultUserAvatar =
  "//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/a8c8cdb109cb051163646151a4a5083b.png~tplv-uwbnlip3yd-webp.webp";

function tabTitle(item: { title?: string; solutionId?: number }) {
  const t = (item.title ?? "").trim();
  return t || (item.solutionId != null ? `Разбор #${item.solutionId}` : "Разбор");
}

interface Props {
  id: string;
}

const form = ref({
  language: "java" as const,
  code: "",
  questionId: "",
});

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

const question = ref<QuestionVO>();

/** Загрузка задачи по id из маршрута. */
const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoById2(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
    getCodeTemplate(form.value.language);
  } else {
    Message.error("Ошибка загрузки: " + res.message);
  }
};

const getCodeTemplate = async (language: string) => {
  const codeRes = await QuestionControllerService.getCodeTemplate({
    title: question.value?.title as any,
    language: language as any,
  });
  if (codeRes.code === 0) {
    onCodeChange(codeRes.data);
  }
};

onMounted(() => {
  loadData();
});

const doSubmit = async () => {
  if (!question.value?.id) {
    return;
  }
  try {
    const res = await QuestionControllerService.doQuestionSubmit2({
      ...form.value,
      questionId: question.value?.id,
    });
    if (res.code === 0) {
      Message.success("Отправлено");
      return;
    }
    Message.error("Ошибка отправки: " + res.message);
  } catch (error: any) {
    Message.error("Ошибка отправки: сервер вернул Not Found/ошибку API");
  }
};

const doRunCode = async () => {
  console.log(222);
};

const onCodeChange = (value: string) => {
  form.value.code = value;
  console.log("value--->", form.value.code);
};

function openSolutionTab(questionSolution: QuestionSolution) {
  const sid = questionSolution.solutionId;
  if (sid != null && data.value.some((d) => d.key === sid)) {
    activeKey.value = String(sid);
    return;
  }
  const title =
    (questionSolution.title ?? "").trim() ||
    (sid != null ? `Разбор #${sid}` : "Разбор");
  data.value = data.value.concat({
    key: sid as number | string,
    title,
    content: questionSolution.solution,
    ...questionSolution,
  } as SolutionTab);
  activeKey.value = String(sid ?? title);
}
const handleDelete = (key: string | number) => {
  data.value = data.value.filter((item) => String(item.key) !== String(key));
  activeKey.value = "solution";
};
</script>

<style>
#viewQuestionView {
  max-width: 1400px;
  margin: 0 auto;
}
#viewQuestionView .arco-space-horizontal .arco-space-item {
  margin-bottom: 0 !important;
}
</style>
