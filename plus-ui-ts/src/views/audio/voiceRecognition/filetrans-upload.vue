<template>
  <!-- 文件上传模态框 -->
  <a-modal v-model:open="open" title="" @ok="pay" ok-text="结算" cancel-text="取消">
    <!-- 文件选择触发按钮 -->
    <a-button type="primary" size="large" @click="handleClick">
      <span>选择音频文件</span>
    </a-button>

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
        ，金额：<b style="color: red; font-size: 18px">{{calAmount}}</b> &nbsp;元
      </span>
    </p>

    <!-- 上传进度展示 -->
    <p>
      <a-progress :percent="uploadPercent" />
    </p>

    <!-- 语言选择 -->
    <p>
      音频语言：
      <a-select v-model:value="lang" style="width: 120px">
        <a-select-option v-for="o in FILETRANS_LANG_ARRAY" :value="o.code">{{o.desc}}</a-select-option>
      </a-select>
    </p>

    <!-- 支付方式选择 -->
    <p>
      支付方式：
      <a-radio-group name="radioGroup" v-model:value="channel">
        <a-radio value="A"><img src="/image/alipay.jpg" alt="支付宝" style="height: 50px;"/></a-radio>
        <a-radio value="W"><img src="/image/wechatpay.jpg" alt="微信" style="height: 100px;"/></a-radio>
      </a-radio-group>
    </p>

    <!-- 支付宝支付组件 -->
    <AlipayCom ref="alipayCom" @after-pay="handleAfterPay" />

    <!-- 文件格式提示 -->
    <p>支持格式：.mp3, .wav, .m4a，最大500MB</p>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { notification } from 'ant-design-vue';
import FiletransUploadCom from '@/components/Alibaba/Vod/filetrans-upload-com.vue';
import { FileUploaderExpose } from '@/api/audio/voiceRecognition/types';
import { isEmpty } from 'radash';
import AlipayCom from '@/components/Alibaba/OrderInfo/alipay-com.vue';

/**
 * 响应式数据
 */
const open = ref(false); // 模态框显示状态
const channel = ref<string>('A'); // 支付方式，默认支付宝
const lang = ref<string>(''); // 选择的语言
const calAmount = ref('0.00'); // 计算金额
const fileUploader = ref<FileUploaderExpose>(); // 文件上传组件引用
const alipayCom = ref<InstanceType<typeof AlipayCom>>(); // 支付宝组件引用
const FILETRANS_LANG_ARRAY = ref(window.FILETRANS_LANG_ARRAY); // 语言选项数组

/**
 * 计算属性
 */
// 当前选择的文件名
const fileName = computed(() => fileUploader.value?.filetrans?.name || '未选择文件');
// 当前上传进度
const uploadPercent = computed(() => fileUploader.value?.filetrans?.percent || 0);

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
  notification.success({
    message: '上传成功',
    description: '文件已上传至：' + fileUrl
  });
};

/**
 * 上传失败处理
 * @param param0 错误对象 { code: number; message: string }
 */
const handleUploadFailed = ({ code, message }: { code: number; message: string }) => {
  notification.error({
    message: `上传失败 (${code})`,
    description: message || '未知错误'
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
 */
const handleAfterPay = (status: string) => {
  if (status === 'S') {
    notification['success']({
      message: '支付宝支付提示',
      description: "支付成功，感谢您的使用！",
    });
    open.value = false;
  } else {
    notification['error']({
      message: '支付宝支付失败',
      description: "支付失败！请重新发起支付！",
    });
  }
};

/**
 * 结算按钮处理
 * @param e 鼠标事件
 */
const pay = (e: MouseEvent) => {
  console.log('处理模态框结算按钮', e);

  // 同步语言和支付方式到上传组件
  if (fileUploader.value) {
    fileUploader.value.filetrans.lang = lang.value;
    fileUploader.value.filetrans.channel = channel.value;
  }

  const mergedData = {
    ...fileUploader.value?.filetrans,
    lang: lang.value
  };

  console.log('准备结算：', JSON.stringify(mergedData));

  // 验证音频地址
  if (isEmpty(fileUploader.value?.filetrans?.audioAddr)) {
    notification.error({
      message: '系统提示',
      description: "请先上传音频文件",
    });
    return;
  }

  // 验证语言选择
  if (isEmpty(lang.value)) {
    notification.error({
      message: '系统提示',
      description: "请选择音频语言",
    });
    return;
  }

  // 验证金额
  if (calAmount.value === '0.00' || calAmount.value === '0') {
    notification.error({
      message: '系统提示',
      description: "金额不能为0",
    });
    return;
  }

  // 执行支付
  fileUploader.value?.handlePay();
};

// 暴露方法给父组件
defineExpose({ showModal });
</script>
