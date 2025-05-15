<template>
  <a-modal v-model:open="open" title="Basic Modal" @ok="handleOk">
    <a-button type="primary" size="large" @click="handleClick">
      <span><UploadOutlined /> 选择者音频</span>
    </a-button>
    <input type="file" style="display: none" ref="fileUploadCom" accept=".mp3,.wav,.m4a" @change="uploadFile" />
    <p>Some contents...</p>
    <p>Some contents...</p>
    <p>Some contents...</p>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { notification } from 'ant-design-vue';
import { getCredentials } from '@/api/audio/voiceRecognition/filetrans-upload';
import { FileTransUploadForm } from '@/api/audio/voiceRecognition/types';
import md5 from 'js-md5';

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
  fileUploadCom.value.value = '';
  fileUploadCom.value.click();
};

// ----------- 上传文件 -----------
const uploadFile = () => {
  console.log('上传文件逻辑');
  const file = fileUploadCom.value.files[0];
  /**
   * file打印
   *
   * lastModified * : * 1585927010477
   * lastModifiedDate * : * Fri Apr 03 2020 23:16:50 GMT+0800 (中国标准时间) {}
   * name * : * "爱猪乖.mp3"
   * size * : * 1547283
   * type * : * "audio/mpeg"
   * webkitRelativePath * : * ""
   */
  console.log(file);

  // 音频的文件最大为500M
  let max = 500 * 1024 * 1024;
  // let max = 1 * 1024; // 测试用
  let size = file.size;
  if (size > max) {
    // [Notification 通知提醒框] https://2x.antdv.com/components/notification-cn
    notification['warning']({
      message: '系统提示',
      description: '文件大小超过最大限制, 最大为500M'
    });
    return;
  }

  // ----------- 调用后端接口获取上传凭证 -----------
  // 计算文件的 MD5 哈希值（截取前 16 位作为 key）
  const hash32 = md5(file.name + file.type + file.size + file.lastModified);
  const key = hash32.substring(0, 16);
  // 调用 getCredentials 方法
  const params: FileTransUploadForm = { name: file.name, key: key };
  getCredentials(params)
    .then(response => {
      console.log('完整响应对象：', response); // 打印完整的响应对象
      if (response.code === 200) { // 根据 code 判断是否成功
        console.log('获取上传凭证成功：', response.data);
      } else {
        notification['error']({
          message: '系统提示',
          description: response.msg || '上传文件失败' // 显示后端返回的错误信息
        });
      }
    })
    .catch(error => {
      console.error('获取上传凭证失败：', error);
      notification['error']({
        message: '系统提示',
        description: '获取上传凭证失败'
      });
    });


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
