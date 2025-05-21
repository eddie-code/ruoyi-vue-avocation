<template>
  <!-- 文件上传模态框 -->
  <a-modal v-model:open="open" title="" @ok="pay" ok-text="结算" cancel-text="取消">
    <!-- 文件选择触发按钮 -->
    <a-button type="primary" size="large" @click="handleClick">
      <span>选择音频文件</span>
    </a-button>

    <p></p>

    <!-- 更新组件事件监听 -->
    <FileUploader
      ref="fileUploader"
      @upload-success="handleUploadSuccess"
      @upload-failed="handleUploadFailed"
      @amount-calculated="handleAmountCalculated"
    />

    <!-- 上传状态显示 -->
    <p>
      已选择文件：{{ fileName }} <span v-show="calAmount !== '0.00'">，金额：<b style="color: red; font-size: 18px">{{calAmount}}</b> &nbsp;元</span>
    </p>
    <p>
      <a-progress :percent="uploadPercent" />
    </p>
    <p>
      音频语言：
      <a-select v-model:value="lang" style="width: 120px">
        <a-select-option v-for="o in FILETRANS_LANG_ARRAY" :value="o.code">{{o.desc}}</a-select-option>
      </a-select>
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
import { isEmpty } from 'radash';


// 添加金额格式化方法
const formatAmount = (amount: string) => {
  return parseFloat(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};


/**
 * 控制模态框显示/隐藏的状态
 */
const open = ref(false);

const FILETRANS_LANG_ARRAY = ref(window.FILETRANS_LANG_ARRAY)

const lang = ref<string>(''); // 语言选择状态

/**
 * 文件上传组件引用（包含类型提示）
 */
const fileUploader = ref<FileUploaderExpose>();

const calAmount = ref('0.00'); // 金额响应式变量

/**
 * 处理金额计算结果（增强类型安全）
 */
const handleAmountCalculated = (amount: string | number) => {
  try {
    const numericAmount = typeof amount === 'string' ? parseFloat(amount) : amount;
    // 确保返回标准的两位小数格式
    calAmount.value = isNaN(numericAmount) || numericAmount === 0
      ? '0.00'  // 明确处理0值情况
      : numericAmount.toFixed(2);
  } catch {
    calAmount.value = '0.00';
  }
};

/**
 * 重置金额显示（增强可靠性）
 */
const resetAmount = () => {
  calAmount.value = '0.00';
};

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
  resetAmount(); // 每次打开模态框时重置金额
};

/**
 * 处理模态框结算按钮
 * @param e 事件对象
 */
const pay = (e: MouseEvent) => {

  // 合并 filetrans 和 lang 到新对象
  const mergedData = {
    ...fileUploader.value?.filetrans, // 展开原始 filetrans 数据
    lang: lang.value                  // 添加当前选择的语言
  };

  console.log('准备结算：', JSON.stringify(mergedData));

  // 检查音频地址
  if (isEmpty(fileUploader.value?.filetrans?.audioAddr)) {
    notification.error({
      message: '系统提示',
      description: "请先上传音频文件",
    });
    return;
  }

  // 检查语言选择（lang是本地响应式变量）
  if (isEmpty(lang.value)) {
    notification.error({
      message: '系统提示',
      description: "请选择音频语言",
    });
    return;
  }

  // 检查金额（使用已计算的金额值）
  if (calAmount.value === '0.00') {
    notification.error({
      message: '系统提示',
      description: "金额不能为0",
    });
    return;
  }
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
  calAmount.value = '0.00'; // 保持字符串类型一致性
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
