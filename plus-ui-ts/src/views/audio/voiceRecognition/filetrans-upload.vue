<template>
  <!-- 模态框组件，用于显示一个包含文件上传功能的弹窗 -->
  <a-modal v-model:open="open" title="Basic Modal" @ok="handleOk">
    <!-- 主按钮，点击后触发文件选择操作 -->
    <a-button type="primary" size="large" @click="handleClick">
      <span><UploadOutlined /> 选择者音频</span>
    </a-button>
    <!-- 组件：文件上传组件，用于处理文件上传逻辑 -->
    <FileUploader ref="fileUploader" @upload-success="handleUploadSuccess" @upload-failed="handleUploadFailed" />
    <!-- 模态框内容占位符 -->
    <p>Some contents...</p>
    <p>Some contents...</p>
    <p>Some contents...</p>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { notification } from 'ant-design-vue';
import FileUploader from '@/components/Alibaba/Vod/FileUploader.vue';

/**
 * 控制模态框的显示与隐藏
 */
const open = ref(false);

/**
 * 文件上传组件的引用
 */
const fileUploader = ref();

/**
 * 显示模态框
 */
const showModal = () => {
  open.value = true;
};

/**
 * 处理模态框确认按钮点击事件
 * @param e - 事件对象
 */
const handleOk = (e) => {
  console.log(e);
  open.value = false;
};

/**
 * 处理主按钮点击事件，显示模态框并触发文件选择操作
 */
const handleClick = () => {
  showModal();
  fileUploader.value.selectFile();
};

/**
 * 处理文件上传成功事件
 * @param fileUrl - 上传成功的文件地址
 */
const handleUploadSuccess = (fileUrl: string) => {
  notification['success']({
    message: '系统提示',
    description: '文件上传成功'
  });
};

/**
 * 处理文件上传失败事件
 * @param code - 错误码
 * @param message - 错误信息
 */
const handleUploadFailed = ({ code, message }: { code: number; message: string }) => {
  console.error('文件上传失败，code:', code, 'message:', message);
  notification['error']({
    message: '系统提示',
    description: message || '文件上传失败'
  });
};

/**
 * 暴露 showModal 方法，供外部调用
 */
defineExpose({
  showModal
});
</script>
