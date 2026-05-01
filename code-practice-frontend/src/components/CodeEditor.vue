<template>
  <div
    id="code-editor"
    ref="codeEditorRef"
    style="min-height: 600px; height: 70vh"
  />
</template>

<script setup lang="ts">
import * as monaco from "monaco-editor";
import { onMounted, ref, toRaw, withDefaults, defineProps, watch } from "vue";

const codeEditorRef = ref();
const codeEditor = ref();
let editor: Partial<monaco.editor.IStandaloneCodeEditor> = {};

interface Props {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}

/**
 * Начальное значение компонента
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  language: () => "java",
  handleChange: (v: string) => {
    console.log(v);
  },
});

watch(
  () => props.language,
  () => {
    if (codeEditor.value) {
      monaco.editor.setModelLanguage(
        toRaw(codeEditor.value).getModel(),
        props.language
      );
    }
  }
);

/** Данные для отображения */
watch(
  () => props.value,
  (val) => {
    if (codeEditor.value) {
      const value =
        toRaw(codeEditor.value).getValue && toRaw(codeEditor.value).getValue();
      if (val !== value) {
        toRaw(codeEditor.value).setValue &&
          toRaw(codeEditor.value).setValue(toRaw(val) || toRaw(""));
      }
    }
  }
);

onMounted(() => {
  if (!codeEditorRef.value) {
    return;
  }
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    automaticLayout: true,
    colorDecorators: true,
    minimap: {
      enabled: true,
    },
    readOnly: false,
    theme: "vs-dark",
  });

  // Обработка изменения содержимого
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue());
  });
});
</script>

<style scoped></style>
