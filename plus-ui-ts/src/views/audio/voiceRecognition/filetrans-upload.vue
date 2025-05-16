<template>
  <!-- 文件上传模态框 -->
  <a-modal v-model:open="open" title="文件上传" @ok="handleOk">
    <!-- 文件选择触发按钮 -->
    <a-button type="primary" size="large" @click="handleClick">
      <span><UploadOutlined /> 选择音频文件</span>
    </a-button>

    <!-- 文件上传组件 -->
    <FileUploader
      ref="fileUploader"
      @upload-success="handleUploadSuccess"
      @upload-failed="handleUploadFailed"
    />

    <!-- 上传状态显示 -->
    <p>已选择文件：{{ fileName }}</p>
    <p>
      <a-progress :percent="uploadPercent" />
    </p>

    <!-- 模态框其他内容 -->
    <p>支持格式：.mp3, .wav, .m4a，最大500MB</p>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { notification } from 'ant-design-vue';
import FileUploader from '@/components/Alibaba/Vod/FileUploader.vue';
import { FileUploaderExpose } from '@/api/audio/voiceRecognition/types';

/**
 * 控制模态框显示/隐藏的状态
 */
const open = ref(false);

/**
 * 文件上传组件引用（包含类型提示）
 */
const fileUploader = ref<FileUploaderExpose>();

/**
 * 计算属性：当前选择的文件名（未选择时显示默认文本）
 */
const fileName = computed(() => fileUploader.value?.filetrans?.name || '未选择文件');

/**
 * 计算属性：当前上传进度百分比（默认0）
 */
const uploadPercent = computed(() => fileUploader.value?.filetrans?.percent || 0);

/**
 * 显示模态框并重置上传状态
 */
const showModal = () => {
  open.value = true;
  fileUploader.value?.resetFileTrans(); // 每次打开时重置状态
};

/**
 * 处理模态框确认按钮
 * @param e 事件对象
 */
const handleOk = (e: MouseEvent) => {
  console.log('模态框确认', e);
  open.value = false;
};

/**
 * 处理文件选择按钮点击
 * 先显示模态框，然后触发文件选择
 */
const handleClick = () => {
  showModal();
  fileUploader.value?.selectFile();
};

/**
 * 处理上传成功事件
 * @param fileUrl 上传成功的文件URL
 */
const handleUploadSuccess = (fileUrl: string) => {
  notification.success({
    message: '上传成功',
    description: '文件已上传至：' + fileUrl
  });
};

/**
 * 处理上传失败事件
 * @param code 错误码
 * @param message 错误信息
 */
const handleUploadFailed = ({ code, message }: { code: number; message: string }) => {
  notification.error({
    message: `上传失败 (${code})`,
    description: message || '未知错误'
  });
};

// 暴露给父组件的方法
defineExpose({ showModal });
</script>
