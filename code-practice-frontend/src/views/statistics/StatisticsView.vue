<template>
  <div id="statisticsView">
    <a-space direction="vertical" fill :size="18">
      <div>
        <template v-if="isAdmin">
          <h2>Системная статистика</h2>
          <a-spin :loading="loading">
            <a-row :gutter="[12, 12]">
              <a-col :xs="24" :sm="12" :md="8" :lg="6">
                <a-card class="kpi-card">
                  <div class="stat-title">Курсов</div>
                  <div class="stat-value">{{ summary.courseCount }}</div>
                </a-card>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8" :lg="6">
                <a-card class="kpi-card">
                  <div class="stat-title">Задач</div>
                  <div class="stat-value">{{ summary.questionCount }}</div>
                </a-card>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8" :lg="6">
                <a-card class="kpi-card">
                  <div class="stat-title">Всего отправок</div>
                  <div class="stat-value">{{ summary.submissionCount }}</div>
                </a-card>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8" :lg="6">
                <a-card class="kpi-card">
                  <div class="stat-title">Успешных отправок</div>
                  <div class="stat-value">{{ summary.acceptedCount }}</div>
                </a-card>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8" :lg="6">
                <a-card class="kpi-card">
                  <div class="stat-title">Успешность</div>
                  <div class="stat-value">{{ summaryAcceptanceRate }}%</div>
                </a-card>
              </a-col>
            </a-row>
          </a-spin>
        </template>
      </div>

      <div>
        <h2>Отчёт по успеваемости</h2>
        <a-card class="report-card">
          <a-space direction="vertical" fill :size="12">
            <a-space wrap>
              <span>Курс:</span>
              <a-select
                v-model="selectedCourseId"
                placeholder="Выберите курс"
                allow-clear
                allow-search
                style="width: 320px"
                :loading="coursesLoading"
                @change="onCourseSelect"
              >
                <template #empty>
                  <a-empty description="Нет курсов" />
                </template>
                <a-option v-for="c in courseList" :key="c.id" :value="c.id">
                  {{ c.title }}
                </a-option>
              </a-select>
              <a-input
                v-model="studentKeyword"
                allow-clear
                style="width: 260px"
                placeholder="Поиск студента"
              />
              <a-input
                v-model="questionKeyword"
                allow-clear
                style="width: 260px"
                placeholder="Поиск задачи"
              />
            </a-space>

            <a-space wrap>
              <a-checkbox v-model="onlyWithSubmissions">Только с попытками</a-checkbox>
              <a-checkbox v-model="onlyWithoutAccepted">Только без успешных</a-checkbox>
              <a-checkbox v-model="onlyHardQuestions">Только сложные задачи (&lt; 50%)</a-checkbox>
            </a-space>

            <a-space v-if="courseProgress && !progressLoading" wrap>
              <a-tag color="arcoblue">Курс: {{ courseProgress.courseTitle }}</a-tag>
              <a-tag color="green">Студентов: {{ courseSummary.studentCount }}</a-tag>
              <a-tag color="orangered">Задач: {{ courseSummary.questionCount }}</a-tag>
              <a-tag color="purple">Отправок: {{ courseSummary.submitTotal }}</a-tag>
              <a-tag color="green">Успешных: {{ courseSummary.acceptedTotal }}</a-tag>
              <a-tag color="goldenrod">Успешность: {{ courseSummary.successRate }}%</a-tag>
            </a-space>

            <a-spin :loading="progressLoading">
              <a-table
                v-if="progressColumns.length > 0"
                :columns="progressColumns"
                :data="progressData"
                :pagination="false"
                :bordered="{ wrapper: true, cell: true }"
                class="progress-table"
              >
                <template #empty>
                  <a-empty description="Нет записанных студентов на курс" />
                </template>
              </a-table>
              <a-empty
                v-else-if="selectedCourseId && !progressLoading"
                description="Нет записанных студентов или задач в курсе"
              />
              <a-empty
                v-else-if="!progressLoading"
                description="Выберите курс для просмотра отчёта"
              />
            </a-spin>
          </a-space>
        </a-card>
      </div>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { Message } from "@arco-design/web-vue";
import { useUserStore } from "@/stores/user";
import accessEnum from "@/access/accessEnum";

const loading = ref(true);
const summary = ref({
  courseCount: 0,
  questionCount: 0,
  submissionCount: 0,
  acceptedCount: 0,
});
const userStore = useUserStore();
const isAdmin = computed(
  () => userStore.loginUser?.userRole === accessEnum.ADMIN
);
const summaryAcceptanceRate = computed(() => {
  if (!summary.value.submissionCount) return "0.00";
  return ((summary.value.acceptedCount / summary.value.submissionCount) * 100).toFixed(2);
});

const loadData = async () => {
  loading.value = true;
  try {
    const res = await fetch("/api/statistics/summary", {
      method: "GET",
      credentials: "include",
    });
    const data = await res.json();
    if (data.code === 0 && data.data) {
      const d = data.data;
      summary.value = {
        courseCount: Number(d.courseCount) || 0,
        questionCount: Number(d.questionCount) || 0,
        submissionCount: Number(d.submissionCount) || 0,
        acceptedCount: Number(d.acceptedCount) || 0,
      };
    } else {
      Message.error(data.message ?? "Ошибка загрузки статистики");
    }
  } catch (e) {
    Message.error("Не удалось загрузить статистику");
  } finally {
    loading.value = false;
  }
};

interface CourseItem {
  id: number;
  title: string;
}
const courseList = ref<CourseItem[]>([]);
const coursesLoading = ref(false);
const selectedCourseId = ref<number | undefined>(undefined);
const progressLoading = ref(false);
interface QuestionProgressItem {
  questionId: number;
  questionTitle: string;
  submitCount: number;
  acceptedCount: number;
}
interface StudentProgress {
  userId: number;
  userAccount: string;
  userName?: string;
  submitTotal?: number;
  acceptedTotal?: number;
  successRate?: number;
  questionProgress: QuestionProgressItem[];
}
interface CourseSummary {
  studentCount: number;
  questionCount: number;
  submitTotal: number;
  acceptedTotal: number;
  successRate: number;
}
interface QuestionSummary {
  questionId: number;
  questionTitle: string;
  submitTotal: number;
  acceptedTotal: number;
  successRate: number;
  attemptedStudents: number;
  solvedStudents: number;
}
interface CourseProgress {
  courseId: number;
  courseTitle: string;
  courseSummary?: CourseSummary;
  courseQuestions?: QuestionProgressItem[];
  questionSummaries?: QuestionSummary[];
  students: StudentProgress[];
}
const courseProgress = ref<CourseProgress | null>(null);
const studentKeyword = ref("");
const questionKeyword = ref("");
const onlyWithSubmissions = ref(false);
const onlyWithoutAccepted = ref(false);
const onlyHardQuestions = ref(false);

const cleanProgressUserName = (raw: string | undefined | null): string => {
  const t = (raw ?? "").trim();
  if (!t || t.toLowerCase() === "null") {
    return "";
  }
  return t;
};

const courseSummary = computed(() => {
  const s = courseProgress.value?.courseSummary;
  return {
    studentCount: Number(s?.studentCount ?? courseProgress.value?.students?.length ?? 0),
    questionCount: Number(s?.questionCount ?? courseProgress.value?.courseQuestions?.length ?? 0),
    submitTotal: Number(s?.submitTotal ?? 0),
    acceptedTotal: Number(s?.acceptedTotal ?? 0),
    successRate: Number(s?.successRate ?? 0).toFixed(2),
  };
});

const loadCourses = async () => {
  coursesLoading.value = true;
  try {
    const res = await fetch("/api/course/list/page?current=1&pageSize=100", { method: "GET", credentials: "include" });
    const data = await res.json();
    if (data.code === 0 && data.data?.records) {
      courseList.value = (data.data.records as { id: number; title: string }[]).map((r) => ({ id: r.id, title: r.title || "" }));
    }
  } catch {
    Message.error("Не удалось загрузить список курсов");
  } finally {
    coursesLoading.value = false;
  }
};

const onCourseSelect = async (courseId: number | undefined) => {
  if (!courseId) {
    courseProgress.value = null;
    studentKeyword.value = "";
    questionKeyword.value = "";
    onlyWithSubmissions.value = false;
    onlyWithoutAccepted.value = false;
    onlyHardQuestions.value = false;
    return;
  }
  progressLoading.value = true;
  courseProgress.value = null;
  try {
    const res = await fetch(`/api/statistics/progress/course/${courseId}`, { method: "GET", credentials: "include" });
    const data = await res.json();
    if (data.code === 0 && data.data) {
      courseProgress.value = data.data as CourseProgress;
    } else {
      Message.error(data.message ?? "Ошибка загрузки отчёта");
    }
  } catch {
    Message.error("Не удалось загрузить отчёт по курсу");
  } finally {
    progressLoading.value = false;
  }
};

const filteredQuestionIds = computed(() => {
  const p = courseProgress.value;
  if (!p) return [] as string[];
  const keyword = questionKeyword.value.trim().toLowerCase();
  const summaries = p.questionSummaries || [];
  const idsByKeyword = new Set<string>();
  const idsByHardness = new Set<string>();

  if (!keyword) {
    (p.courseQuestions || []).forEach((q) => idsByKeyword.add(String(q.questionId)));
  } else {
    (p.courseQuestions || []).forEach((q) => {
      if ((q.questionTitle || "").toLowerCase().includes(keyword)) {
        idsByKeyword.add(String(q.questionId));
      }
    });
  }

  if (!onlyHardQuestions.value) {
    (p.courseQuestions || []).forEach((q) => idsByHardness.add(String(q.questionId)));
  } else {
    summaries.forEach((s) => {
      if (Number(s.successRate || 0) < 50) {
        idsByHardness.add(String(s.questionId));
      }
    });
  }

  return Array.from(idsByKeyword).filter((id) => idsByHardness.has(id));
});

const filteredStudents = computed(() => {
  const p = courseProgress.value;
  if (!p?.students) return [] as StudentProgress[];
  const keyword = studentKeyword.value.trim().toLowerCase();
  return p.students.filter((s) => {
    const submitTotal = Number(s.submitTotal ?? 0);
    const acceptedTotal = Number(s.acceptedTotal ?? 0);
    if (onlyWithSubmissions.value && submitTotal <= 0) return false;
    if (onlyWithoutAccepted.value && acceptedTotal > 0) return false;
    if (!keyword) return true;
    return (
      (s.userAccount || "").toLowerCase().includes(keyword) ||
      cleanProgressUserName(s.userName).toLowerCase().includes(keyword)
    );
  });
});

const progressColumns = computed(() => {
  const p = courseProgress.value;
  if (!p) return [];
  const questions = (p.courseQuestions || []).filter((q) =>
    filteredQuestionIds.value.includes(String(q.questionId))
  );
  if (!questions.length) return [];
  const cols: { title: string; dataIndex: string; width?: number }[] = [
    { title: "Студент", dataIndex: "student", width: 220 },
    { title: "Всего отправок", dataIndex: "totalSubmit", width: 130 },
    { title: "Успешных", dataIndex: "totalAccepted", width: 110 },
    { title: "Успешность", dataIndex: "totalRate", width: 110 },
  ];
  questions.forEach((q, i) => {
    cols.push({
      title: q.questionTitle || `Задача ${i + 1}`,
      dataIndex: `q_${q.questionId}`,
      width: 220,
    });
  });
  return cols;
});

const progressData = computed(() => {
  const questionIdSet = new Set<string>(filteredQuestionIds.value);
  return filteredStudents.value.map((s) => {
    const byQuestion = new Map<string, QuestionProgressItem>();
    (s.questionProgress || []).forEach((q) => byQuestion.set(String(q.questionId), q));
    const totalSubmit = Number(s.submitTotal ?? 0);
    const totalAccepted = Number(s.acceptedTotal ?? 0);
    const totalRate = totalSubmit ? `${((totalAccepted / totalSubmit) * 100).toFixed(2)}%` : "0.00%";
    const displayName = cleanProgressUserName(s.userName);
    const row: Record<string, string | number> = {
      student: displayName ? `${displayName} (${s.userAccount ?? ""})` : (s.userAccount ?? ""),
      totalSubmit,
      totalAccepted,
      totalRate,
    };
    questionIdSet.forEach((qid: string) => {
      const q = byQuestion.get(qid);
      row[`q_${qid}`] = `${q?.acceptedCount ?? 0}/${q?.submitCount ?? 0}`;
    });
    return row;
  });
});

onMounted(() => {
  loadData();
  loadCourses();
});
</script>

<style scoped>
#statisticsView {
  max-width: 1400px;
  margin: 0 auto;
}
.stat-title {
  color: var(--color-text-3);
  font-size: 14px;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
}
.kpi-card {
  min-height: 110px;
}
.report-card {
  border-radius: 12px;
}
.progress-table :deep(.arco-table-th) {
  white-space: nowrap;
}
.progress-table {
  width: 100%;
}
.progress-table :deep(.arco-table-container) {
  width: 100%;
}
</style>

