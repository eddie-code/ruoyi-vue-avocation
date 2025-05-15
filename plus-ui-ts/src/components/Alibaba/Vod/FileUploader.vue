<template>
  <div>
    <!-- 文件上传输入框，隐藏显示，支持上传 .mp3, .wav, .m4a 格式的文件 -->
    <input type="file" style="display: none" ref="fileUploadCom" accept=".mp3,.wav,.m4a" @change="handleFileChange" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { notification } from 'ant-design-vue';
import { getCredentials } from '@/api/audio/voiceRecognition/filetrans-upload';
import { FileTransUploadForm } from '@/api/audio/voiceRecognition/types';
import md5 from 'js-md5';

// 文件上传输入框的引用
const fileUploadCom = ref<HTMLInputElement | null>(null);

// 上传凭证、上传地址和视频ID
let uploadAuth: string;
let uploadAddress: string;
let videoId: string;

/**
 * 阿里云上传器实例
 * 配置了用户ID、分片大小、并行上传数量、重试次数等参数
 * 并定义了上传开始、成功、失败、进度、凭证过期等回调函数
 * https://help.aliyun.com/zh/vod/developer-reference/upload-sdk-for-javascript?spm=a2c4g.11186623.help-menu-29932.d_4_1_6_1_1.3a6b485cEgJijk#34b59711afp0q
 */
const uploader = new AliyunUpload.Vod({
  //userID，必填，只需有值即可。
  userId: '122',
  //分片大小默认1 MB (1048576)，不能小于100 KB
  partSize: 104858,
  //并行上传分片个数，默认5
  parallel: 5,
  //网络原因失败时，重新上传次数，默认为3
  retryCount: 3,
  //网络原因失败时，重新上传间隔时间，默认为2秒
  retryDuration: 2,
  //是否上报上传日志到视频点播，默认为true
  enableUploadProgress: true,
  //开始上传
  onUploadstarted(uploadInfo) {
    console.log('文件上传开始:', uploadInfo.file.name);
    uploader.setUploadAuthAndAddress(uploadInfo, uploadAuth, uploadAddress, videoId);
  },
  //文件上传成功
  onUploadSucceed(uploadInfo) {
    console.log('文件上传成功: ' + uploadInfo.file.name + ', endpoint:' + uploadInfo.endpoint + ', bucket:' + uploadInfo.bucket + ', object:' + uploadInfo.object);
    const fileUrl = uploadInfo.endpoint.replace('https://', 'https://' + uploadInfo.bucket + '.') + '/' + uploadInfo.object;
    console.log('文件地址:', fileUrl);
    emit('upload-success', fileUrl);
  },
  //文件上传失败
  onUploadFailed(uploadInfo, code, message) {
    console.log('文件上传失败:', uploadInfo.file.name, 'code:', code, 'message:', message);
    emit('upload-failed', { code, message });
  },
  //文件上传进度，单位：字节
  onUploadProgress(uploadInfo, totalSize, loadedPercent) {
    console.log('文件上传进度:', uploadInfo.file.name, 'percent:', Math.ceil(loadedPercent * 100) + '%');
    emit('upload-progress', loadedPercent);
  },
  //上传凭证超时
  onUploadTokenExpired(uploadInfo) {
    console.log('上传凭证超时');
    uploader.resumeUploadWithAuth(uploadAuth);
  },
  //全部文件上传结束
  onUploadEnd(uploadInfo) {
    console.log('文件上传结束');
    if (fileUploadCom.value) {
      fileUploadCom.value.value = '';
    }
  }
});

// 定义事件发射器，用于通知父组件上传状态
const emit = defineEmits(['upload-success', 'upload-failed', 'upload-progress']);

/**
 * 处理文件选择变化的函数
 * 检查文件是否存在，文件大小是否超过限制，获取上传凭证并开始上传
 */
const handleFileChange = () => {
  if (!fileUploadCom.value || !fileUploadCom.value.files || fileUploadCom.value.files.length === 0) {
    console.error('未选择文件');
    return;
  }

  const file = fileUploadCom.value.files[0];
  const max = 500 * 1024 * 1024;
  if (file.size > max) {
    notification['warning']({
      message: '系统提示',
      description: '文件大小超过最大限制, 最大为500M'
    });
    return;
  }

  // 使用 md5.64 生成文件的唯一标识符
  const hash32 = md5(file.name + file.type + file.size + file.lastModified);
  const key = hash32.substring(0, 16);
  const params: FileTransUploadForm = { name: file.name, key: key };

  // 获取上传凭证并开始上传
  getCredentials(params)
    .then((response) => {
      if (response.code === 200) {
        const { uploadAuth: auth, uploadAddress: address, videoId: id, fileUrl } = response.data;
        // 根据和后端约定返回fileUrl字段：表示已经上传过的文件就返回此字段
        if (fileUrl) {
          console.log('文件已上传过, 地址：', fileUrl);
          emit('upload-success', fileUrl);
        } else {
          console.log('获取上传凭证成功：', response.data);
          uploadAuth = auth;
          uploadAddress = address;
          videoId = id;
          // 调用阿里云的Vod SDK方法
          uploader.addFile(file, null, null, null, null);
          uploader.startUpload();
        }
      } else {
        notification['error']({
          message: '系统提示',
          description: response.msg || '上传文件失败'
        });
        emit('upload-failed', { code: response.code, message: response.msg });
      }
    })
    .catch((error) => {
      console.error('获取上传凭证失败：', error);
      notification['error']({
        message: '系统提示',
        description: '获取上传凭证失败'
      });
      emit('upload-failed', { code: -1, message: '获取上传凭证失败' });
    });
};

/**
 * 触发文件选择对话框
 */
const selectFile = () => {
  if (fileUploadCom.value) {
    fileUploadCom.value.value = '';
    fileUploadCom.value.click();
  }
};

// 暴露 selectFile 方法给父组件调用
defineExpose({
  selectFile
});
</script>
