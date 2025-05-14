<template>
  <a-modal v-model:open="open" title="Basic Modal" @ok="handleOk">
    <a-button type="primary" size="large" @click="handleClick">
      <span><UploadOutlined /> 选择者音频</span>
    </a-button>
    <input type="file" style="display: none" ref="fileUploadCom"/>
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

const handleOk = (e) => {
  console.log(e);
  open.value = false;
};

// ----------- 选择文件 -----------
const fileUploadCom = ref();
const selectFile = () => {
  console.log('此处为文件选择逻辑');
  fileUploadCom.value.click();
};

// 处理按钮点击事件
const handleClick = () => {
  showModal();
  selectFile();
};

// 使用 defineExpose 向外部暴露指定的数据和方法
defineExpose({
  showModal,
});
</script>
