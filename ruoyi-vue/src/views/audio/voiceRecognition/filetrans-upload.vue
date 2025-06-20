<template>
  <!-- 文件上传模态框 - Element Plus 版本 -->
  <el-dialog
    v-model="open"
    title="文件上传"
    :before-close="handleCancel"
    custom-class="file-upload-dialog"
    width="500px"
  >
    <!-- 文件选择触发按钮 -->
    <el-space>
      <el-button type="primary" @click="handleClick">
        <span>选择音频文件</span>
      </el-button>
      <el-button type="primary" @click="handleDemoClick" :loading="uploadLoading">
        <span>没有音频? 使用示例音频</span>
      </el-button>
    </el-space>

    <p></p>

    <!-- 文件上传组件 -->
    <FiletransUploadCom
      ref="fileUploader"
      @upload-success="handleUploadSuccess"
      @upload-failed="handleUploadFailed"
      @amount-calculated="handleAmountCalculated"
      @trigger-alipay="handleTriggerAlipay"
    />

    <!-- 文件及金额信息展示 -->
    <p>
      已选择文件：{{ fileName }}
      <span v-show="calAmount !== '0.00'">
        ，金额：<b style="color: red; font-size: 18px">{{ calAmount }}</b> &nbsp;元
      </span>
    </p>

    <!-- 上传进度展示 -->
    <p>
      <el-progress :percentage="uploadPercent"/>
    </p>

    <!-- 语言选择 -->
    <p>
      音频语言：
      <el-select v-model="lang" style="width: 120px">
        <el-option
          v-for="o in FILETRANS_LANG_ARRAY"
          :key="o.code"
          :label="o.desc"
          :value="o.code"
        />
      </el-select>
    </p>

    <!-- 支付方式选择 -->
    <p>
      支付方式：
      <el-radio-group v-model="channel">
        <el-radio value="A">
          <img src="/image/alipay.png" alt="支付宝"/>
        </el-radio>
        <el-radio value="W" disabled class="disabled-option">
          <img src="/image/wechatpay.png" alt="微信"/>
        </el-radio>
      </el-radio-group>
    </p>

    <!-- 支付宝支付组件 -->
    <AlipayCom ref="alipayCom" @after-pay="handleAfterPay"/>

    <!-- 文件格式提示 -->
    <p>支持格式：.mp3, .wav, .m4a，最大500MB</p>

    <!-- 自定义底部按钮 -->
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="pay">
          结算
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import {ref, computed} from 'vue';
import {ElNotification} from 'element-plus';
import FiletransUploadCom from '@/components/Alibaba/Vod/filetrans-upload-com.vue';
import {FileUploaderExpose} from '@/api/audio/voiceRecognition/types';
import {isEmpty} from 'radash';
import AlipayCom from '@/components/Alibaba/OrderInfo/alipay-com.vue';
import {getDemoAudioApi} from '@/api/audio/voiceRecognition/filetrans-upload';
import md5 from "js-md5";

const emit = defineEmits(['after-pay']); // 支付后自动刷新

/**
 * 响应式数据
 */
const open = ref(false); // 模态框显示状态
const channel = ref('A'); // 支付方式，默认支付宝
const lang = ref(''); // 选择的语言
const calAmount = ref('0.00'); // 计算金额
const fileUploader = ref<FileUploaderExpose>(); // 文件上传组件引用
const alipayCom = ref<InstanceType<typeof AlipayCom>>(); // 支付宝组件引用
const FILETRANS_LANG_ARRAY = ref(window.FILETRANS_LANG_ARRAY); // 语言选项数组
const uploadLoading = ref(false); // 示例音频加载状态

/**
 * 计算属性
 */
// 当前选择的文件名
const fileName = computed(() => fileUploader.value?.filetrans?.name || '未选择文件');
// 当前上传进度
const uploadPercent = computed(() => fileUploader.value?.filetrans?.percent || 0);

/**
 * 示例音频按钮点击处理
 */
const handleDemoClick = async () => {
  try {
    uploadLoading.value = true;
    const {data} = await getDemoAudioApi();
    // 设置上传组件状态
    if (fileUploader.value) {
      fileUploader.value.filetrans = {
        ...fileUploader.value.filetrans,
        name: data.name,
        audioAddr: data.audio,
        vod: data.vid,
        percent: 100,
        channel: 'A',
        duration: data.duration,
        amount: Number(data.amount),
        fileSign: data.key || md5(data.name),
        lang: data.lang
      };
    }

    console.log('示例音频按钮点击处理:', fileUploader.value.filetrans);

    // 音频选择
    lang.value = data.lang;

    // 计算金额
    // console.log('原始金额:', data.amount);
    //handleAmountCalculated(data.amount);
    // console.log('处理后:', {
    //   original: data.amount,
    //   displayed: calAmount.value
    // });

    calAmount.value = Number(data.amount).toFixed(2);

    ElNotification({
      title: '示例音频加载成功',
      message: '已加载示例音频文件',
      type: 'success',
      duration: 2000
    });
  } catch (error) {
    ElNotification({
      title: '示例音频加载失败',
      message: error.message || '加载示例音频失败',
      type: 'error',
      duration: 5000
    });
  } finally {
    uploadLoading.value = false;
  }
};

/**
 * 金额格式化方法
 * @param amount 原始金额
 * @returns 格式化后的金额字符串
 */
const formatAmount = (amount: string) => {
  return parseFloat(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

/**
 * 重置金额显示
 */
const resetAmount = () => {
  calAmount.value = '0.00';
};

/**
 * 显示模态框
 */
const showModal = () => {
  open.value = true;
  fileUploader.value?.resetFileTrans(); // 重置上传状态
  resetAmount(); // 重置金额
  lang.value = ''; // 重置语言选择
};

/**
 * 文件选择按钮点击事件
 */
const handleClick = () => {
  showModal();
  fileUploader.value?.selectFile(); // 触发文件选择
};

/**
 * 处理金额计算结果
 * @param amount 金额数值或字符串
 */
const handleAmountCalculated = (amount: string | number) => {
  console.log("handleAmountCalculated === ", amount)
  try {
    const numericAmount = typeof amount === 'string' ? parseFloat(amount) : amount;
    calAmount.value = isNaN(numericAmount) || numericAmount === 0
      ? '0.00'
      : numericAmount.toFixed(2);
  } catch {
    calAmount.value = '0.00';
  }
};

/**
 * 上传成功处理
 * @param fileUrl 上传成功的文件URL
 */
const handleUploadSuccess = (fileUrl: string) => {
  calAmount.value = '0.00';
  ElNotification({
    title: '上传成功',
    message: '文件已上传至：' + fileUrl,
    type: 'success',
    duration: 3000
  });
};

/**
 * 上传失败处理
 * @param param0 错误对象 { code: number; message: string }
 */
const handleUploadFailed = ({code, message}: { code: number; message: string }) => {
  ElNotification({
    title: `上传失败 (${code})`,
    message: message || '未知错误',
    type: 'error',
    duration: 5000
  });
};

/**
 * 支付宝支付触发
 * @param payInfo 支付信息
 */
const handleTriggerAlipay = (payInfo: any) => {
  alipayCom.value?.handleOpen({
    amount: payInfo.amount,
    desc: "语音识别结算",
    qrcode: payInfo.qrcode,
    orderNo: payInfo.orderNo
  });
};

/**
 * 支付结果处理
 * @param status 支付状态
 * @param refresh 是否刷新列表
 */
const handleAfterPay = (status: string, refresh: boolean = false) => {
  // console.log('filetrans-upload收到支付状态:', status, refresh)
  if (status === 'S') {
    ElNotification({
      title: '支付宝支付提示',
      message: "支付成功，感谢您的使用！",
      type: 'success',
      duration: 3000
    });
    emit('after-pay', status, refresh); // 将事件传递给父组件
    if (refresh) {
      open.value = false; // 支付成功后关闭弹窗
    }
  } else {
    ElNotification({
      title: '支付宝支付失败',
      message: "支付失败！请重新发起支付！",
      type: 'error',
      duration: 5000
    });
  }
};

/**
 * 结算按钮处理
 */
const pay = () => {
  // 验证金额
  if (parseFloat(calAmount.value) < 0.01) {
    ElNotification.error('金额不能低于0.01元');
    return;
  }
  // 同步语言和支付方式到上传组件
  if (fileUploader.value) {
    fileUploader.value.filetrans.lang = lang.value;
    fileUploader.value.filetrans.channel = channel.value;
  }

  console.log("pay === ", fileUploader.value.filetrans);

  const mergedData = {
    ...fileUploader.value?.filetrans,
    lang: lang.value
  };

  // 验证音频地址
  if (isEmpty(fileUploader.value?.filetrans?.audioAddr)) {
    ElNotification({
      title: '系统提示',
      message: "请先上传音频文件",
      type: 'error',
      duration: 3000
    });
    return;
  }

  // 验证语言选择
  if (isEmpty(lang.value)) {
    ElNotification({
      title: '系统提示',
      message: "请选择音频语言",
      type: 'error',
      duration: 3000
    });
    return;
  }

  // 验证金额
  if (calAmount.value === '0.00' || calAmount.value === '0') {
    ElNotification({
      title: '系统提示',
      message: "金额不能为0",
      type: 'error',
      duration: 3000
    });
    return;
  }

  // 执行支付
  fileUploader.value?.handlePay();
};

/**
 * 取消按钮处理
 */
const handleCancel = () => {
  open.value = false;
};

// 暴露方法给父组件
defineExpose({showModal});
</script>

<style>
.file-upload-dialog .el-dialog__body {
  padding: 10px 15px; /* 缩小内边距 */
}

/* 缩小支付方式图片尺寸 */
.el-radio-group img {
  height: auto !important; /* 移除固定高度 */
  max-height: 80px; /* 设置最大高度约束 */
}

.disabled {
  filter: grayscale(80%);
}
</style>
