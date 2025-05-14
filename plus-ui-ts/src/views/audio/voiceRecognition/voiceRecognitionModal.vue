<template>
  <el-dialog title="语音识别对话框" :visible="visible" @update:visible="handleVisibleChange" width="30%">
    <!-- 对话框内容 -->
    <div>
      <p>这里是语音识别的具体内容</p>
      <!-- 可以添加更多的表单元素、提示信息等 -->
    </div>

    <!-- 对话框底部按钮 -->
    <span slot="footer" class="dialog-footer">
      <el-button @click="closeModal">取 消</el-button>
      <el-button type="primary" @click="confirmAction">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';

export default defineComponent({
  props: {
    visible: {
      type: Boolean as PropType<boolean>,
      required: true,
    },
  },
  emits: ['update:visible'],
  setup(props, { emit }) {
    // 关闭 Modal 的方法
    const closeModal = () => {
      console.log('Before emit:', props.visible);
      emit('update:visible', false);
      console.log('After emit:', props.visible);
    };

    // 确认操作的方法
    const confirmAction = () => {
      // 这里可以添加确认操作的逻辑
      console.log('确认操作');
      closeModal();
    };

    // 处理 visible 变化的回调
    const handleVisibleChange = (value: boolean) => {
      console.log('Visible changed:', value);
    };

    return {
      closeModal,
      confirmAction,
      handleVisibleChange,
    };
  },
});
</script>

<style scoped>
/* 可以根据需要添加具体的样式 */
</style>
