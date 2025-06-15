<template>
  <div>
    <!-- 隐藏的文件输入框 -->
    <input
      type="file"
      style="display: none"
      ref="fileUploadCom"
      accept=".mp3,.wav,.m4a"
      @change="handleFileChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { notification } from 'ant-design-vue';
import { getCredentialsApi, calculateAmountApi, payApi } from '@/api/audio/voiceRecognition/filetrans-upload';
import md5 from 'js-md5';
import { PayForm } from '@/api/audio/voiceRecognition/types';

// ======================== DOM引用 ========================
/**
 * 文件上传输入框的DOM引用
 */
const fileUploadCom = ref<HTMLInputElement | null>(null);

// ======================== 变量声明 ========================
/**
 * 阿里云上传凭证信息
 * @type {string} uploadAuth - 上传授权凭证
 * @type {string} uploadAddress - 上传地址
 * @type {string} videoId - 视频资源ID
 */
let uploadAuth: string;
let uploadAddress: string;
let videoId: string;

/**
 * 文件上传状态（响应式）
 * @property {string} name - 文件名
 * @property {number} percent - 上传进度百分比
 * @property {string} lang - 音频语言
 * @property {string} audioAddr - 音频地址
 * @property {string} fileSign - 文件签名
 * @property {string} vod - 视频点播ID
 * @property {string} channel - 支付渠道(A:支付宝)
 */
const filetrans = ref({
  name: '',
  percent: 0,
  lang: "",
  audioAddr: "",
  fileSign: "",
  vod: "",
  channel: "A" // 默认值, 支付宝
});

/**
 * 金额响应式变量
 */
const amount = ref<number>(0);

// ======================== 阿里云上传器配置 ========================
/**
 * 阿里云VOD上传实例
 * @see https://help.aliyun.com/document_detail/51992.html
 */
const uploader = new AliyunUpload.Vod({
  // 必填配置
  userId: '122', // 用户ID，只需有值即可
  partSize: 104858, // 分片大小(默认1MB)，不能小于100KB
  parallel: 5, // 并行上传分片个数
  retryCount: 3, // 失败重试次数
  retryDuration: 2, // 重试间隔(秒)
  enableUploadProgress: true, // 是否上报上传日志

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
    // resetFileTrans(); // 上传完成后自动重置(已注释)
  }
});

// ======================== 事件定义 ========================
/**
 * 组件事件定义
 * @event upload-success - 上传成功事件
 * @event upload-failed - 上传失败事件
 * @event upload-progress - 上传进度事件
 * @event amount-calculated - 金额计算完成事件
 * @event pay-success - 支付成功事件
 * @event trigger-alipay - 触发支付宝支付事件
 */
const emit = defineEmits([
  'upload-success',
  'upload-failed',
  'upload-progress',
  'amount-calculated',
  'pay-success',
  'trigger-alipay'
]);

// ======================== 核心方法 ========================
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
    vod: "",
    channel: "A"
  };
  if (fileUploadCom.value) {
    fileUploadCom.value.value = '';
  }
  console.log('上传状态已重置');
};

/**
 * 金额计算接口
 * @param {string} videoId - 视频ID
 */
const calculateAmount = async (videoId: string) => {
  calculateAmountApi(videoId)
    .then((response: any) => {
      if (response.code === 200) {
        console.log('金额接口返回:', response.data);
        amount.value = response.data;
        emit('amount-calculated', response.data);
      } else {
        throw new Error(response.msg || '金额计算失败');
      }
    })
    .catch((error: any) => {
      console.error('请求或处理失败:', error);
      notification.warning({
        message: '费用计算失败',
        description: error.message || '无法获取预估费用'
      });
      emit('amount-calculated', '0.00');
    });
};

/**
 * 支付处理方法
 */
const handlePay = () => {
  if (!filetrans.value.vod) {
    notification.error({ message: '支付失败', description: '未获取到视频ID' });
    return;
  }

  const payData: PayForm = {
    name: filetrans.value.name,
    percent: filetrans.value.percent,
    amount: amount.value,
    lang: filetrans.value.lang,
    audio: filetrans.value.audioAddr,
    fileSign: filetrans.value.fileSign,
    vod: filetrans.value.vod,
    channel: filetrans.value.channel
  };

  console.log('下单请求数据:', payData);

  payApi(payData)
    .then(response => {
      console.log('支付响应:', response);
      if (response.code === 200) {
        notification.success({
          message: '系统提示',
          description: '下单成功, 订单号：' + response.data.orderNo
        });
        emit('pay-success');

        // 支付宝支付渠道处理
        if (filetrans.value.channel === "A") {
          emit('trigger-alipay', {
            amount: amount.value,
            qrcode: response.data.channelResult,
            orderNo: response.data.orderNo
          });
        }
      }
    })
    .catch(error => {
      console.error('支付错误:', error);
      notification.error({
        message: '系统提示',
        description: error.message || '下单失败'
      });
    });
};

/**
 * 处理文件选择变化
 */
const handleFileChange = () => {
  if (!fileUploadCom.value?.files?.length) {
    console.error('未选择文件');
    return;
  }

  const file = fileUploadCom.value.files[0];
  const maxSize = 500 * 1024 * 1024; // 500MB限制

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
    vod: "",
    channel: ""
  };

  // 生成文件唯一标识(MD5)
  const fileHash = md5(file.name + file.type + file.size + file.lastModified);
  const fileKey = fileHash.substring(0, 16);
  filetrans.value.fileSign = fileKey;

  // 获取上传凭证
  getCredentialsApi({ name: file.name, key: fileKey })
    .then((response) => {
      if (response.code !== 200) throw new Error(response.msg);

      // 已上传过的文件处理
      if (response.data.fileUrl) {
        console.log('文件已上传过:', response.data.fileUrl);
        filetrans.value.percent = 100;
        emit('upload-success', response.data.fileUrl);
        videoId = response.data.videoId;
        calculateAmount(videoId);
        filetrans.value.audioAddr = response.data.fileUrl;
        filetrans.value.vod = videoId;
        return;
      }

      // 新文件上传流程
      console.log('新文件开始上传:', response.data);
      uploadAuth = response.data.uploadAuth;
      uploadAddress = response.data.uploadAddress;
      videoId = response.data.videoId;
      filetrans.value.vod = videoId;
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

// ======================== 暴露方法 ========================
/**
 * 向父组件暴露的方法
 * @method selectFile - 触发文件选择
 * @method resetFileTrans - 重置上传状态
 * @method filetrans - 上传状态对象
 * @method handlePay - 支付处理方法
 */
defineExpose({
  selectFile,
  resetFileTrans,
  filetrans,
  handlePay
});
</script>
