<template>
  <div class="voice-recognition">
    <!-- 页面标题 -->
    <h1>语音识别</h1>

    <!-- 触发 Modal 的按钮 -->
    <el-button type="primary" @click="openModal">打开语音识别对话框</el-button>

    <!-- Modal 组件 -->
    <VoiceRecognitionModal v-model="modalVisible" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import VoiceRecognitionModal from './voiceRecognitionModal.vue';

export default defineComponent({
  components: {
    VoiceRecognitionModal
  },
  setup() {
    // 控制 Modal 显示状态
    const modalVisible = ref(false);

    // 打开 Modal 的方法
    const openModal = () => {
      console.log('Before open:', modalVisible.value);
      modalVisible.value = true;
      console.log('After open:', modalVisible.value);
    };

    return {
      modalVisible,
      openModal,
    };
  },
});
</script>

<style scoped>
.voice-recognition {
  padding: 20px;
}
</style>
