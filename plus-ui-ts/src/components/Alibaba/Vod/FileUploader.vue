<template>
  <div>
    <!-- 隐藏的文件输入框 -->
    <input type="file" style="display: none" ref="fileUploadCom" accept=".mp3,.wav,.m4a" @change="handleFileChange" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { notification } from 'ant-design-vue';
import { getCredentials } from '@/api/audio/voiceRecognition/filetrans-upload';
import md5 from 'js-md5';
import request from '@/utils/request'; // 新增引入request

/**
 * 文件输入框DOM引用
 */
const fileUploadCom = ref<HTMLInputElement | null>(null);

/**
 * 阿里云上传凭证信息
 */
let uploadAuth: string;
let uploadAddress: string;
let videoId: string;

/**
 * 文件上传状态（响应式）
 */
const filetrans = ref({
  name: '',
  percent: 0,
  lang: "",
  audioAddr: "",
  fileSign: "",
  vod: ""
});

/**
 * 重置上传状态
 * 1. 清空文件名和进度
 * 2. 重置文件输入框
 */
const resetFileTrans = () => {
  filetrans.value = {
    name: '',
    percent: 0,
    lang: "",
    audioAddr: "",
    fileSign: "",
    vod: ""
  };
  if (fileUploadCom.value) {
    fileUploadCom.value.value = '';
  }

  console.log('上传状态已重置');
};

/**
 * 阿里云VOD上传实例
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

  // 上传开始回调
  onUploadstarted(uploadInfo) {
    console.log('开始上传:', uploadInfo.file.name);
    uploader.setUploadAuthAndAddress(uploadInfo, uploadAuth, uploadAddress, videoId);
  },

  // 上传成功回调
  onUploadSucceed(uploadInfo) {
    const fileUrl = uploadInfo.endpoint.replace('https://', 'https://' + uploadInfo.bucket + '.') + '/' + uploadInfo.object;
    console.log('上传成功:', fileUrl);
    emit('upload-success', fileUrl);
    // 关键修复：上传成功后再触发金额计算
    calculateAmount(videoId); // 确保使用最新的videoId
    filetrans.value.audioAddr = fileUrl;
  },

  // 上传失败回调
  onUploadFailed(uploadInfo, code, message) {
    console.log('上传失败:', uploadInfo.file.name, 'code:', code, 'message:', message);
    emit('upload-failed', { code, message });
  },

  // 上传进度回调
  onUploadProgress(uploadInfo, totalSize, loadedPercent) {
    const percent = Math.ceil(loadedPercent * 100);
    filetrans.value.percent = percent;
    console.log('文件上传进度:', uploadInfo.file.name, 'percent:', percent + '%');
    emit('upload-progress', loadedPercent);
  },

  // 凭证过期回调
  onUploadTokenExpired() {
    console.log('凭证过期，尝试续期');
    uploader.resumeUploadWithAuth(uploadAuth);
  },

  // 上传结束回调
  onUploadEnd() {
    console.log('上传流程结束');
    // resetFileTrans(); // 上传完成后自动重置
  }
});

/**
 * 新增：金额计算接口
 * @param videoId 视频ID
 */
const calculateAmount = async (videoId: string) => {
  try {
    const response = await request({
      url: `/web/vod/cal-amount/${videoId}`,
      method: 'get'
    });

    if (response.code === 200) {
      emit('amount-calculated', response.data); // 触发金额事件
    } else {
      throw new Error(response.msg || '金额计算失败');
    }
  } catch (error: any) {
    console.error('金额计算错误:', error);
    notification.warning({
      message: '费用计算失败',
      description: error.message || '无法获取预估费用'
    });
    emit('amount-calculated', '0.00'); // 失败时重置金额
  }
};

// 更新事件定义
const emit = defineEmits([
  'upload-success',
  'upload-failed',
  'upload-progress',
  'amount-calculated' // 新增金额事件
]);

/**
 * 处理文件选择变化
 */
const handleFileChange = () => {
  if (!fileUploadCom.value?.files?.length) {
    console.error('未选择文件');
    return;
  }

  const file = fileUploadCom.value.files[0];
  const maxSize = 500 * 1024 * 1024; // 500MB

  // 文件大小校验
  if (file.size > maxSize) {
    notification.warning({
      message: '文件过大',
      description: '最大支持500MB的文件'
    });
    return;
  }

  // 更新上传状态
  filetrans.value = {
    name: file.name,
    percent: 0,
    lang: "",
    audioAddr: "",
    fileSign: "",
    vod: ""
  };

  // 生成文件唯一标识
  const fileHash = md5(file.name + file.type + file.size + file.lastModified);
  const fileKey = fileHash.substring(0, 16);
  filetrans.value.fileSign = fileKey;

  // 获取上传凭证
  getCredentials({ name: file.name, key: fileKey })
    .then((response) => {
      if (response.code !== 200) throw new Error(response.msg);

      // 已上传过的文件直接返回URL
      if (response.data.fileUrl) {
        console.log('文件已上传过:', response.data.fileUrl);
        filetrans.value.percent = 100;
        emit('upload-success', response.data.fileUrl);
        videoId = response.data.videoId; // 确保videoId更新
        // 关键新增：立即触发预计算（可选）
        calculateAmount(videoId);
        filetrans.value.audioAddr = response.data.fileUrl;
        filetrans.value.vod = videoId;
        return;
      }

      // 使用阿里云SDK方法新文件开始上传
      console.log('新文件开始上传:', response.data);
      uploadAuth = response.data.uploadAuth;
      uploadAddress = response.data.uploadAddress;
      videoId = response.data.videoId; // 确保videoId更新
      filetrans.value.vod = videoId;
      // 关键新增：立即触发预计算（可选）
      calculateAmount(videoId);
      uploader.addFile(file);
      uploader.startUpload();
    })
    .catch((error) => {
      console.error('获取凭证失败:', error);
      notification.error({
        message: '上传失败',
        description: error.message || '无法获取上传凭证'
      });
      emit('upload-failed', {
        code: -1,
        message: error.message || '获取上传凭证失败'
      });
    });
};

/**
 * 触发文件选择对话框
 */
const selectFile = () => {
  if (fileUploadCom.value) {
    fileUploadCom.value.value = ''; // 清除之前的选择
    fileUploadCom.value.click();
  }
};

// 暴露给父组件的方法和属性
defineExpose({
  selectFile,
  resetFileTrans,
  filetrans
});
</script>
