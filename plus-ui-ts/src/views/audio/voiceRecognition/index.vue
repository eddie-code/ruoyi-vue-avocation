<template>
  <div>
    <a-button type="primary" @click="showModal">开始上传音频</a-button>
    <FiletransUpload ref="filetransSubtitleCom"></FiletransUpload>
  </div>
</template>

<script setup>
import FiletransUpload from './filetrans-upload.vue';
import { ref } from 'vue';

const filetransSubtitleCom = ref();

const showModal = () => {
  console.log('Button clicked'); // 调试信息
  if (filetransSubtitleCom.value) {
    filetransSubtitleCom.value.showModal();
    console.log('showModal 在 FiletransUpload 上调用'); // 调试信息
  } else {
    console.error('filetransSubtitleCom 未定义'); // 错误信息
  }
};
</script>
