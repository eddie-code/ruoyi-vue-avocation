<template>
  <a-modal v-model:open="open" title="Basic Modal" @ok="handleOk">
    <a-button type="primary" size="large" @click="handleClick">
      <span><UploadOutlined /> 选择者音频</span>
    </a-button>
    <input type="file"
           style="display: none"
           ref="fileUploadCom"
           accept=".mp3,.wav,.m4a"
           @change="uploadFile"
    />
    <p>Some contents...</p>
    <p>Some contents...</p>
    <p>Some contents...</p>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { notification } from 'ant-design-vue';

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
  console.log('选择文件逻辑');
  fileUploadCom.value.value = "";
  fileUploadCom.value.click();
};

// ----------- 上传文件 -----------
const uploadFile = () => {
  console.log('上传文件逻辑');
  const file = fileUploadCom.value.files[0];
  console.log(file)

  // 音频的文件最大为500M
  let max = 500 * 1024 * 1024;
  // let max = 1 * 1024; // 测试用
  let size = file.size;
  if (size > max){
    // [Notification 通知提醒框] https://2x.antdv.com/components/notification-cn
    notification['warning']({
      message: '系统提示',
      description: '文件大小超过最大限制, 最大为500M'
    });
    return;
  }

};

// 处理按钮点击事件
const handleClick = () => {
  showModal();
  selectFile();
};

// 使用 defineExpose 向外部暴露指定的数据和方法
defineExpose({
  showModal
});
</script>
