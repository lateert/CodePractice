<template>
  <div id="addQuestionView">
    <h2>{{ route.name }}</h2>
    <a-form
      :model="form"
      :style="{ width: '1000px' }"
      @submit="handleSubmit"
      label-align="left"
    >
      <a-form-item field="title" label="Название">
        <a-input v-model="form.title" placeholder="Введите название" />
      </a-form-item>
      <a-form-item
        field="tags"
        label="Теги"
        extra="Введите тег и нажмите Enter или перейдите к другому полю — тег добавится в список."
      >
        <a-input-tag
          v-model="form.tags"
          v-model:input-value="tagInputDraft"
          :retain-input-value="{ blur: true }"
          unique-value
          allow-clear
          placeholder="Например: loop"
          @blur="commitPendingTag"
        />
      </a-form-item>
      <a-form-item field="content" label="Условие задачи">
        <MdEditor
          :value="form.content"
          :handle-change="onContentChange"
          style="min-width: 800px"
        />
      </a-form-item>
      <a-form-item field="answer" label="Ответ">
        <MdEditor
          :value="form.answer"
          :handle-change="onAnswerChange"
          style="min-width: 800px"
        />
      </a-form-item>
      <a-form-item label="Настройки задачи" :content-flex="false" :merge-props="false">
        <a-space direction="vertical" fill>
          <a-form-item field="judgeConfig.memoryLimit" label="Ограничение памяти">
            <a-input-number
              v-model="form.judgeConfig.memoryLimit"
              placeholder="Введите лимит памяти"
              mode="button"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.stackLimit" label="Ограничение стека">
            <a-input-number
              v-model="form.judgeConfig.stackLimit"
              placeholder="Введите лимит стека"
              mode="button"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.timeLimit" label="Ограничение времени">
            <a-input-number
              v-model="form.judgeConfig.timeLimit"
              placeholder="Введите лимит времени"
              mode="button"
              size="large"
            />
          </a-form-item>
        </a-space>
      </a-form-item>
      <a-form-item
        label="Тестовые случаи"
        :content-flex="false"
        :merge-props="false"
      >
        <a-form-item
          v-for="(judgeCaseItem, index) of form.judgeCase"
          :key="index"
          no-style
        >
          <a-space direction="vertical" style="min-width: 480px">
            <a-form-item
              :field="`form.judgeCase[${index}].input`"
              :label="`Ввод-${index}`"
              :key="index"
            >
              <a-input
                v-model="judgeCaseItem.input"
                placeholder="Введите входные данные"
              />
            </a-form-item>
            <a-form-item
              :field="`form.judgeCase[${index}].output`"
              :label="`Вывод-${index}`"
              :key="index"
            >
              <a-input
                v-model="judgeCaseItem.output"
                placeholder="Введите ожидаемый вывод"
              />
            </a-form-item>
            <a-button status="danger" @click="handleDelete(index)"
              >Удалить</a-button
            >
          </a-space>
        </a-form-item>
        <div style="margin-top: 32px">
          <a-button type="outline" status="success" @click="handleAdd"
            >Добавить</a-button
          >
        </div>
      </a-form-item>

      <a-form-item>
        <a-button html-type="submit" type="primary">Сохранить</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import { Message } from "@arco-design/web-vue";
import { useRoute } from "vue-router";

const route = useRoute();
const updatePage = route.path.includes("update");

const form = reactive({
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    memoryLimit: 1000,
    stackLimit: 1000,
    timeLimit: 1000,
  },
  tags: [] as string[],
  title: "",
});

/** Черновик в поле a-input-tag; без этого при blur Arco сбрасывает текст, не добавляя тег */
const tagInputDraft = ref("");

const commitPendingTag = () => {
  const t = (tagInputDraft.value ?? "").trim();
  if (!t) return;
  if (form.tags.includes(t)) {
    tagInputDraft.value = "";
    return;
  }
  form.tags.push(t);
  tagInputDraft.value = "";
};

/** Загрузка задачи при редактировании (query-параметр id). */
const loadData = async () => {
  const raw = route.query.id;
  if (!raw) {
    return;
  }
  const idStr = String(Array.isArray(raw) ? raw[0] : raw);
  const res = await QuestionControllerService.getQuestionById2(idStr);
  if (res.code === 0) {
    Object.assign(form, res.data);
    form.judgeCase = JSON.parse(res.data.judgeCase);
    form.judgeConfig = JSON.parse(res.data.judgeConfig);
    form.tags = JSON.parse(res.data.tags);
  } else {
    Message.error("Ошибка загрузки: " + res.message);
  }
};

onMounted(() => {
  loadData();
});

const handleSubmit = async () => {
  // создание или обновление по маршруту
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestion(form);
    console.log(res);
    if (res.code === 0) {
      Message.success("Изменения сохранены");
    } else {
      Message.error("Ошибка сохранения: " + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestion2(form);
    console.log(res);
    if (res.code === 0) {
      Message.success("Задача создана");
    } else {
      Message.error("Ошибка создания: " + res.message);
    }
  }
};

/** Добавить строку тест-кейса проверки. */
const handleAdd = () => {
  form.judgeCase.push({
    output: "",
    input: "",
  });
};
/** Удалить тест-кейс проверки по индексу. */
const handleDelete = (index: number) => {
  form.judgeCase.splice(index, 1);
};

const onContentChange = (value: string) => {
  form.content = value;
};

const onAnswerChange = (value: string) => {
  form.answer = value;
};
</script>
