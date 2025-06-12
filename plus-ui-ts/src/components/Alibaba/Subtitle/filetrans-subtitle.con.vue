<template>
  <a-modal v-model:open="open" title="生成字幕" @ok="handleOk">
    <p>{{ filetrans }}</p>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';

const open = ref(false);
const filetrans = ref(false);
filetrans.value = {};

const showModal = (_filetrans) => {
  filetrans.value = _filetrans;
  open.value = true;
};

const handleOk = e => {
  console.log(e);
  open.value = false;
};

// 暴露方法给父组件
defineExpose({
  showModal
});
</script>
