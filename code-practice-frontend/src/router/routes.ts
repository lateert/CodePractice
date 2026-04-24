import { RouteRecordRaw } from "vue-router";
import NoAuthView from "@/views/NoAuthView.vue";
import accessEnum from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import OAuthGithubCallbackView from "@/views/user/OAuthGithubCallbackView.vue";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import ManageUserView from "@/views/user/ManageUserView.vue";
import QuestionsView from "@/views/question/QuestionsView.vue";
import QuestionSubmitView from "@/views/question/QuestionSubmitView.vue";
import MyQuestionSubmitView from "@/views/question/MyQuestionSubmitView.vue";
import ViewQuestionView from "@/views/question/ViewQuestionView.vue";
import StatisticsView from "@/views/statistics/StatisticsView.vue";
import ManageCourseView from "@/views/course/ManageCourseView.vue";
import CourseCatalogView from "@/views/course/CourseCatalogView.vue";
import CourseQuestionsView from "@/views/course/CourseQuestionsView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "Пользователь",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "Вход",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "Регистрация",
        component: UserRegisterView,
      },
      {
        path: "/user/oauth/github/callback",
        name: "GitHub OAuth Callback",
        component: OAuthGithubCallbackView,
      },
    ],
    meta: {
      hidden: true,
    },
  },
  {
    path: "/",
    name: "Главная",
    component: QuestionsView,
  },
  {
    path: "/questions_submit",
    name: "Отправки решений",
    component: QuestionSubmitView,
    meta: {
      access: accessEnum.ADMIN_OR_TEACHER,
    },
  },
  {
    path: "/my/submits",
    name: "Мои отправки",
    component: MyQuestionSubmitView,
    meta: {
      access: accessEnum.USER,
      /** Только студенты: пункт меню и прямой заход недоступны преподавателю/админу */
      studentMenuOnly: true,
    },
  },
  {
    path: "/courses",
    name: "Курсы",
    component: CourseCatalogView,
    meta: {
      access: accessEnum.USER,
      studentMenuOnly: true,
    },
  },
  {
    path: "/course/:id/questions",
    name: "Задачи курса",
    component: CourseQuestionsView,
    meta: {
      access: accessEnum.USER,
      hidden: true,
    },
  },
  {
    path: "/question/add",
    name: "Создать задачу",
    component: AddQuestionView,
    meta: {
      access: accessEnum.ADMIN_OR_TEACHER,
      hidden: true,
    },
  },
  {
    path: "/question/update",
    name: "Редактировать задачу",
    component: AddQuestionView,
    meta: {
      access: accessEnum.ADMIN_OR_TEACHER,
      hidden: true,
    },
  },
  {
    path: "/question/manage",
    name: "Управление задачами",
    component: ManageQuestionView,
    meta: {
      access: accessEnum.ADMIN_OR_TEACHER,
    },
  },
  {
    path: "/course/manage",
    name: "Управление курсами",
    component: ManageCourseView,
    meta: {
      access: accessEnum.ADMIN_OR_TEACHER,
    },
  },
  {
    path: "/user/manage",
    name: "Управление пользователями",
    component: ManageUserView,
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    path: "/statistics",
    name: "Статистика",
    component: StatisticsView,
    meta: {
      access: accessEnum.ADMIN_OR_TEACHER,
    },
  },
  {
    path: "/view/question/:id",
    name: "Решение задачи",
    component: ViewQuestionView,
    props: true,
    meta: {
      access: accessEnum.USER,
      hidden: true,
    },
  },
  {
    path: "/noAuth",
    name: "Нет доступа",
    component: NoAuthView,
    meta: {
      hidden: true,
    },
  },
];
