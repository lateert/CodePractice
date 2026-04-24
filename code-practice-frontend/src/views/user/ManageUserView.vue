<template>
  <div id="manageUserView">
    <a-space direction="vertical" fill>
      <a-space align="center" wrap :size="10">
        <a-button @click="showAddModal" type="primary" status="success"
          >Добавить пользователя</a-button
        >
        <a-input
          v-model="filterForm.userAccount"
          placeholder="Логин (частичное совпадение)"
          allow-clear
          style="width: 220px"
          @press-enter="applyFilters"
        />
        <a-input
          v-model="filterForm.userName"
          placeholder="Имя (частичное совпадение)"
          allow-clear
          style="width: 220px"
          @press-enter="applyFilters"
        />
        <a-input
          v-model="filterForm.idQuery"
          placeholder="ID (точное совпадение)"
          allow-clear
          style="width: 200px"
          @press-enter="applyFilters"
        />
        <a-select
          v-model="filterForm.userRole"
          placeholder="Роль"
          style="width: 200px"
        >
          <a-option value="all" label="Все роли">Все роли</a-option>
          <a-option value="user">Студент</a-option>
          <a-option value="teacher">Преподаватель</a-option>
          <a-option value="admin">Администратор</a-option>
          <a-option value="ban">Заблокирован</a-option>
        </a-select>
        <a-select
          v-model="filterForm.sortField"
          placeholder="Сортировка"
          style="width: 220px"
        >
          <a-option value="createTime">Дата создания</a-option>
          <a-option value="id">ID</a-option>
          <a-option value="userAccount">Логин</a-option>
          <a-option value="userName">Имя</a-option>
          <a-option value="userRole">Роль</a-option>
        </a-select>
        <a-select v-model="filterForm.sortOrder" style="width: 160px">
          <a-option value="ascend">По возрастанию</a-option>
          <a-option value="descend">По убыванию</a-option>
        </a-select>
        <a-button type="primary" @click="applyFilters">Применить</a-button>
        <a-button @click="resetFilters">Сброс</a-button>
      </a-space>
      <a-table
        :columns="columns"
        :data="dataList"
        :bordered="{ wrapper: true, cell: true }"
        :pagination="{
          showTotal: true,
          showPageSize: true,
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total: Number(total),
          pageSizeOptions: [10, 20, 50, 100],
        }"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #createTime="{ record }">
          {{ record.createTime ? formatDate(record.createTime) : "" }}
        </template>
        <template #optional="{ record }">
          <a-space>
            <a-button @click="doEdit(record)" type="primary">Изменить</a-button>
            <a-button @click="confirmDelete(record)" status="danger"
              >Удалить</a-button
            >
          </a-space>
        </template>
      </a-table>
    </a-space>

    <a-modal
      v-model:visible="addModalVisible"
      title="Добавить пользователя"
      ok-text="Сохранить"
      cancel-text="Отмена"
      :on-before-ok="submitAdd"
      @cancel="addModalVisible = false"
    >
      <a-form :model="addForm" layout="vertical">
        <a-form-item label="Логин" required>
          <a-input v-model="addForm.userAccount" placeholder="Логин" />
        </a-form-item>
        <a-form-item label="Имя">
          <a-input v-model="addForm.userName" placeholder="Имя" />
        </a-form-item>
        <a-form-item label="Пароль">
          <a-input-password
            v-model="addForm.userPassword"
            placeholder="Пароль (по умолчанию 12345678)"
          />
        </a-form-item>
        <a-form-item label="Роль">
          <a-select v-model="addForm.userRole" placeholder="Роль">
            <a-option value="user">Студент</a-option>
            <a-option value="teacher">Преподаватель</a-option>
            <a-option value="admin">Администратор</a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="editModalVisible"
      title="Редактировать пользователя"
      ok-text="Сохранить"
      cancel-text="Отмена"
      :on-before-ok="submitEdit"
      @cancel="editModalVisible = false"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="Имя">
          <a-input v-model="editForm.userName" placeholder="Имя" />
        </a-form-item>
        <a-form-item label="Роль">
          <a-select v-model="editForm.userRole" placeholder="Роль">
            <a-option value="user">Студент</a-option>
            <a-option value="teacher">Преподаватель</a-option>
            <a-option value="admin">Администратор</a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { User, UserControllerService } from "../../../generated";
import type { UserAddRequest, UserUpdateRequest } from "../../../generated";
import { Message, Modal } from "@arco-design/web-vue";

const dataList = ref<User[]>([]);
const total = ref(0);

function defaultFilterForm() {
  return {
    userAccount: "" as string,
    userName: "" as string,
    idQuery: "" as string,
    userRole: "all" as string,
    sortField: "createTime",
    sortOrder: "descend",
  };
}

/** Snowflake ID не помещается в JS number без потери точности — в API уходит строка. */
function parseUserIdQuery(raw: string): string | undefined {
  const t = raw.trim();
  if (!t) return undefined;
  if (!/^\d+$/.test(t)) return undefined;
  const normalized = t.replace(/^0+/, "") || "0";
  if (normalized === "0") return undefined;
  return normalized;
}

const filterForm = ref(defaultFilterForm());

const searchParams = ref({
  pageSize: 10,
  current: 1,
  userAccount: undefined as string | undefined,
  userName: undefined as string | undefined,
  id: undefined as string | undefined,
  userRole: undefined as string | undefined,
  sortField: "createTime",
  sortOrder: "descend",
});
const addModalVisible = ref(false);
const editModalVisible = ref(false);
const addForm = ref<{
  userAccount: string;
  userName: string;
  userPassword: string;
  userRole: string;
}>({
  userAccount: "",
  userName: "",
  userPassword: "",
  userRole: "user",
});
const editForm = ref<{ id?: number; userName: string; userRole: string }>({
  userName: "",
  userRole: "user",
});

function formatDate(s: string) {
  if (!s) return "";
  const d = new Date(s);
  return d.toISOString().slice(0, 10);
}

const loadData = async () => {
  const res = await UserControllerService.listUserByPage(
    searchParams.value
  );
  if (res.code === 0 && res.data) {
    dataList.value = res.data.records ?? [];
    total.value = Number(res.data.total ?? 0);
  } else {
    Message.error("Ошибка загрузки: " + (res as any).message);
  }
};

watchEffect(() => {
  loadData();
});

const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "Логин", dataIndex: "userAccount" },
  { title: "Имя", dataIndex: "userName" },
  { title: "Роль", dataIndex: "userRole" },
  { title: "Создан", slotName: "createTime" },
  {
    title: "Действия",
    slotName: "optional",
    width: 160,
    fixed: "right" as const,
  },
];

const showAddModal = () => {
  addForm.value = {
    userAccount: "",
    userName: "",
    userPassword: "",
    userRole: "user",
  };
  addModalVisible.value = true;
};

const submitAdd = async () => {
  if (!addForm.value.userAccount?.trim()) {
    Message.error("Введите логин");
    return false;
  }
  const body: UserAddRequest & { userPassword?: string } = {
    userAccount: addForm.value.userAccount.trim(),
    userName: addForm.value.userName?.trim() || undefined,
    userRole: addForm.value.userRole || "user",
  };
  if (addForm.value.userPassword?.trim()) {
    body.userPassword = addForm.value.userPassword.trim();
  }
  const res = await UserControllerService.addUser(body);
  if (res.code === 0) {
    Message.success("Пользователь добавлен");
    loadData();
    return true;
  }
  Message.error("Ошибка: " + (res as any).message);
  return false;
};

const doEdit = (record: User) => {
  editForm.value = {
    id: record.id,
    userName: record.userName ?? "",
    userRole: record.userRole ?? "user",
  };
  editModalVisible.value = true;
};

const submitEdit = async () => {
  if (!editForm.value.id) return false;
  const body: UserUpdateRequest = {
    id: editForm.value.id,
    userName: editForm.value.userName || undefined,
    userRole: editForm.value.userRole || "user",
  };
  const res = await UserControllerService.updateUser(body);
  if (res.code === 0) {
    Message.success("Изменения сохранены");
    loadData();
    return true;
  }
  Message.error("Ошибка: " + (res as any).message);
  return false;
};

const doDelete = async (record: User) => {
  if (!record.id) return;
  const res = await UserControllerService.deleteUser({
    id: record.id,
  });
  if (res.code === 0) {
    Message.success("Пользователь удалён");
    loadData();
  } else {
    Message.error("Ошибка удаления: " + (res as any).message);
  }
};

const confirmDelete = (record: User) => {
  Modal.confirm({
    title: "Подтверждение удаления",
    content: "Вы уверены, что хотите удалить пользователя?",
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
};

const onPageSizeChange = (size: number) => {
  searchParams.value = { ...searchParams.value, pageSize: size, current: 1 };
};

const applyFilters = () => {
  const role = filterForm.value.userRole;
  searchParams.value = {
    ...searchParams.value,
    current: 1,
    userAccount: filterForm.value.userAccount?.trim() || undefined,
    userName: filterForm.value.userName?.trim() || undefined,
    id: parseUserIdQuery(filterForm.value.idQuery),
    userRole: !role || role === "all" ? undefined : role,
    sortField: filterForm.value.sortField,
    sortOrder: filterForm.value.sortOrder,
  };
};

const resetFilters = () => {
  filterForm.value = defaultFilterForm();
  searchParams.value = {
    pageSize: searchParams.value.pageSize,
    current: 1,
    userAccount: undefined,
    userName: undefined,
    id: undefined,
    userRole: undefined,
    sortField: "createTime",
    sortOrder: "descend",
  };
};
</script>

<style scoped>
#manageUserView {
}
</style>
