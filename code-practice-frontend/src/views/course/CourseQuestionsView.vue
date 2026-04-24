<template>
  <div id="courseQuestionsView">
    <a-space direction="vertical" fill>
      <a-card>
        <template #title>Задачи курса: {{ courseTitle }}</template>
        <div class="course-description">{{ courseDescription || "—" }}</div>
      </a-card>

      <a-space>
        <a-input
          v-model="taskKeyword"
          allow-clear
          style="width: 280px"
          placeholder="Поиск по названию задачи"
        />
        <a-select v-model="taskSort" style="width: 220px" placeholder="Сортировка">
          <a-option value="positionAsc">По порядку в курсе</a-option>
          <a-option value="idAsc">ID по возрастанию</a-option>
          <a-option value="idDesc">ID по убыванию</a-option>
          <a-option value="titleAsc">Название А-Я</a-option>
          <a-option value="titleDesc">Название Я-А</a-option>
        </a-select>
      </a-space>

      <a-table :columns="questionColumns" :data="filteredQuestions" :pagination="false">
        <template #empty>
          <a-empty description="В этом курсе пока нет задач" />
        </template>
        <template #tags="{ record }">
          <a-space wrap>
            <a-tag v-for="(tag, index) of (record.tags || [])" :key="index" color="green">{{ tag }}</a-tag>
          </a-space>
        </template>
        <template #acceptedRate="{ record }">
          <span :class="{ acceptedRate: true }">
            {{
              `${
                record.submitNum
                  ? Number.parseFloat(((record.acceptedNum / record.submitNum) * 100) as string).toFixed(2)
                  : "0.00"
              }%`
            }}
          </span>
          <span> ( {{ `${record.acceptedNum || 0}/${record.submitNum || 0}` }} ) </span>
        </template>
        <template #optional="{ record }">
          <a-button type="primary" @click="openQuestion(record.id)">Решить</a-button>
        </template>
      </a-table>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";

interface Course {
  id: string;
  title: string;
  description?: string;
}

interface CourseQuestion {
  id: string;
  title: string;
  tags?: string[];
  submitNum?: number;
  acceptedNum?: number;
}

const route = useRoute();
const router = useRouter();
const courseId = String(route.params.id || "");

const courseTitle = ref("Курс");
const courseDescription = ref("");
const questions = ref<CourseQuestion[]>([]);
const taskKeyword = ref("");
const taskSort = ref("positionAsc");

const api = async (url: string, options?: RequestInit) => {
  const res = await fetch(url, { credentials: "include", ...options });
  return res.json();
};

const loadCourseInfo = async () => {
  const res = await api("/api/course/enroll/my");
  if (res.code === 0 && Array.isArray(res.data)) {
    const course = (res.data as Course[]).find((c) => String(c.id) === courseId);
    if (course) {
      courseTitle.value = course.title;
      courseDescription.value = course.description || "";
    }
  }
};

const loadCourseQuestions = async () => {
  const res = await api(`/api/course/question/list?courseId=${courseId}`);
  if (res.code === 0 && Array.isArray(res.data)) {
    questions.value = res.data || [];
  } else {
    Message.error(res.message ?? "Не удалось загрузить задачи курса");
  }
};

const filteredQuestions = computed(() => {
  const keyword = taskKeyword.value.trim().toLowerCase();
  let list = [...questions.value];
  if (keyword) {
    list = list.filter((q) => (q.title || "").toLowerCase().includes(keyword));
  }
  switch (taskSort.value) {
    case "idAsc":
      list.sort((a, b) =>
        String(a.id || "").localeCompare(String(b.id || ""), undefined, { numeric: true })
      );
      break;
    case "idDesc":
      list.sort((a, b) =>
        String(b.id || "").localeCompare(String(a.id || ""), undefined, { numeric: true })
      );
      break;
    case "titleAsc":
      list.sort((a, b) => (a.title || "").localeCompare(b.title || ""));
      break;
    case "titleDesc":
      list.sort((a, b) => (b.title || "").localeCompare(a.title || ""));
      break;
    default:
      break;
  }
  return list;
});

const questionColumns = [
  { title: "Название", dataIndex: "title" },
  { title: "Теги", slotName: "tags" },
  { title: "Прошло", slotName: "acceptedRate", width: 180 },
  { title: "Действия", slotName: "optional", width: 120 },
];

const openQuestion = (questionId: string) => {
  router.push(`/view/question/${questionId}`);
};

onMounted(async () => {
  if (!courseId || courseId === "undefined" || courseId === "null") {
    Message.error("Некорректный id курса");
    router.push("/");
    return;
  }
  await loadCourseInfo();
  await loadCourseQuestions();
});
</script>

<style scoped>
#courseQuestionsView {
  max-width: 1280px;
  margin: 0 auto;
}

.course-description {
  color: #555;
}

.acceptedRate {
  font-size: 24px;
  color: darkorange;
}
</style>
