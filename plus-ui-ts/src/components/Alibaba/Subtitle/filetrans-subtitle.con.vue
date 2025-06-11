<template>
  <a-modal v-model:open="open" title="Basic Modal" @ok="handleOk">
    <p>Some contents...</p>
    <p>Some contents...</p>
    <p>Some contents...</p>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';

const open = ref(false);

const showModal = () => {
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
