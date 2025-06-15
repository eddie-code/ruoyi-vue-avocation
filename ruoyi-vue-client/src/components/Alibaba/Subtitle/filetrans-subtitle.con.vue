<!--
组件功能说明
    主要功能：
        显示字幕生成模态框
        分页展示字幕列表（时间段+文本）
        支持下载SRT和TXT两种格式的字幕文件
        特殊标记第一项和最后一项的时间
    核心方法：
        showModal: 打开模态框并加载数据
        loadData: 加载字幕数据
        handleDownloadSrt: 下载SRT格式字幕
        handleDownloadTxt: 下载TXT格式字幕
        convertSrtToTxt: SRT转TXT格式转换
    特色功能：
        时间格式转换（毫秒转HH:mm:ss）
        首尾时间项特殊样式标记
        完善的错误处理和加载状态
        详细的日志输出便于调试
    使用方式：
        父组件通过ref调用showModal方法打开模态框
        内部自动处理数据加载和分页
        提供两种格式的字幕下载
-->
<template>
  <!-- 字幕生成模态框 -->
  <a-modal
    v-model:open="open"
    title="生成字幕"
    width="80%"
    :footer="null"
    centered
    :modalStyle="{
      top: '20px',
      right: '20px',  // 添加右侧间距
      left: '20px',   // 添加左侧间距
      transform: 'none', // 移除transform
      margin: '0 auto', // 水平居中
      maxWidth: 'calc(100% - 40px)' // 防止溢出
    }"
  >
    <!-- 下载按钮区域 -->
    <div style="margin-bottom: 16px; text-align: right;">
      <!-- 下载SRT字幕按钮 -->
      <a-button
        type="primary"
        @click="handleDownloadSrt"
        :loading="downloadSrtLoading"
        style="margin-right: 8px;"
      >
        下载SRT字幕
      </a-button>
      <!-- 下载TXT字幕按钮 -->
      <a-button
        type="primary"
        @click="handleDownloadTxt"
        :loading="downloadLoading"
      >
        下载TXT字幕
      </a-button>
    </div>

    <!-- 字幕表格展示区域 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      :loading="loading"
      @change="handleTableChange"
      bordered
    >
      <!-- 开始时间 | 结束时间 | 字幕 -->
      <!--      <template #bodyCell="{ column, record }">-->
      <!--        <template v-if="column.dataIndex === 'begin'">-->
      <!--          {{ formatTime(record.begin) }}-->
      <!--        </template>-->
      <!--        <template v-else-if="column.dataIndex === 'end'">-->
      <!--          {{ formatTime(record.end) }}-->
      <!--        </template>-->
      <!--        <template v-else-if="column.dataIndex === 'text'">-->
      <!--          {{ record.text }}-->
      <!--        </template>-->
      <!--      </template>-->
      <!-- 时间段 | 字幕 -->
      <!--      <template #bodyCell="{ column, record }">-->
      <!--        <template v-if="column.key === 'timeRange'">-->
      <!--          {{ formatTime(record.begin) }} - {{ formatTime(record.end) }}-->
      <!--        </template>-->
      <!--        <template v-else-if="column.dataIndex === 'text'">-->
      <!--          {{ record.text }}-->
      <!--        </template>-->
      <!--      </template>-->
      <!-- 时间段（增加判断第一个时间与最后一个时间，添加加粗红色的样式） | 字幕 -->
      <!-- 表格单元格自定义渲染 -->
      <template #bodyCell="{ column, record, index }">
        <!-- 时间段列渲染 -->
        <template v-if="column.key === 'timeRange'">
          <!-- 开始时间（如果是第一页第一项则加粗红色显示） -->
          <span :style="pagination.current === 1 && index === 0 ? 'color: red; font-weight: bold' : ''">
            {{ formatTime(record.begin) }}
          </span>
          -
          <!-- 结束时间（如果是最后一页最后一项则加粗红色显示） -->
          <span :style="isLastPageLastItem(index) ? 'color: red; font-weight: bold' : ''">
            {{ formatTime(record.end) }}
          </span>
        </template>
        <!-- 字幕文本列渲染 -->
        <template v-else-if="column.dataIndex === 'text'">
          {{ record.text }}
        </template>
      </template>
    </a-table>
  </a-modal>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {listFiletransSubtitle, genSubtitle} from '@/api/audio/filetrans';
import type {TableProps} from 'ant-design-vue';
import {message} from 'ant-design-vue';
import request from '@/utils/request';

/**
 * 表格列配置
 * 注释掉的代码是旧版的两列配置（开始时间、结束时间分开）
 */
// const columns = [
//   {
//     title: '开始时间',
//     dataIndex: 'begin',
//     key: 'begin',
//     width: 120,
//   },
//   {
//     title: '结束时间',
//     dataIndex: 'end',
//     key: 'end',
//     width: 120,
//   },
//   {
//     title: '字幕',
//     dataIndex: 'text',
//     key: 'text',
//   },
// ];

/**
 * 当前使用的表格列配置（时间段合并为一列）
 */
const columns = [
  {
    title: '时间段',
    key: 'timeRange',
    width: 250, // 适当增加宽度
  },
  {
    title: '字幕',
    dataIndex: 'text',
    key: 'text',
  },
];

// 模态框显示状态
const open = ref(false);
// 当前处理的文件转换数据
const filetrans = ref<Record<string, any>>({});
// 字幕数据列表
const dataList = ref<any[]>([]);
// 表格加载状态
const loading = ref(false);
// TXT下载加载状态
const downloadLoading = ref(false);
// SRT下载加载状态
const downloadSrtLoading = ref(false);

/**
 * 分页配置
 */
const pagination = reactive({
  current: 1,                   // 当前页码
  pageSize: 10,                 // 每页条数
  total: 0,                     // 总条数
  showSizeChanger: true,        // 显示每页条数选择器
  pageSizeOptions: ['10', '20', '50'], // 可选的每页条数
});

/**
 * 将毫秒转换为时间格式 (HH:mm:ss)
 * @param milliseconds 毫秒数
 * @returns 格式化后的时间字符串
 */
const formatTime = (milliseconds: number): string => {
  const totalSeconds = Math.floor(milliseconds / 1000);
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  const ms = milliseconds % 1000;

  // 修改后（去掉毫秒）
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  // 修改前（包含毫秒）
  // return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}.${ms.toString().padStart(3, '0')}`;
};

/**
 * 判断当前项是否是最后一页的最后一项
 * @param index 当前项的索引
 * @returns 是否是最后一页的最后一项
 */
const isLastPageLastItem = (index: number): boolean => {
  // 计算总页数
  const totalPage = Math.ceil(pagination.total / pagination.pageSize);

  // 判断条件：
  // 1. 当前页是最后一页
  // 2. 当前项是当前页的最后一项
  // 3. 总记录数不为0（避免空数据时出错）
  return pagination.current === totalPage &&
    index === dataList.value.length - 1 &&
    pagination.total > 0;
};

/**
 * 显示模态框
 * @param _filetrans 文件转换数据
 */
const showModal = (_filetrans: any) => {
  filetrans.value = _filetrans;
  open.value = true;

  // 重置分页并加载数据
  pagination.current = 1;
  loadData();
};

/**
 * 加载字幕数据
 */
const loadData = async () => {
  if (!filetrans.value?.id) {
    console.warn('无法加载字幕：缺少文件转换ID');
    return;
  }

  try {
    loading.value = true;
    const params = {
      filetransId: filetrans.value.id,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    };

    console.log('请求参数:', params);
    const data = await listFiletransSubtitle(params);
    console.log('接口业务数据:', data);

    if (data && data.rows) {
      // 确保数据正确赋值
      dataList.value = data.rows || [];
      pagination.total = data.total || 0;

      console.log('加载的数据:', dataList.value);
      console.log('分页信息:', pagination);
    } else {
      console.error('接口返回的业务数据无效:', data);
    }
  } catch (error: any) {
    console.error('加载字幕失败:', error);
    if (error.response) {
      console.error('HTTP错误:', error.response.status, error.response.data);
    }
  } finally {
    loading.value = false;
  }
};

/**
 * 处理表格分页变化
 * @param pag 分页参数
 */
const handleTableChange: TableProps['onChange'] = (pag) => {
  pagination.current = pag.current!;
  pagination.pageSize = pag.pageSize!;
  loadData();
};

/**
 * 下载TXT字幕文件
 */
const handleDownloadTxt = async () => {
  if (!filetrans.value?.id) {
    message.error('无法下载字幕：缺少文件转换ID');
    return;
  }

  try {
    downloadLoading.value = true;

    console.log('调用生成字幕接口，参数:', {filetransId: filetrans.value.id});
    const response = await genSubtitle({
      filetransId: filetrans.value.id
    });

    // 直接使用响应数据作为 URL
    const srtUrl = response.data;
    console.log('获取到SRT文件URL:', srtUrl);

    // 验证 URL 格式
    if (typeof srtUrl === 'string' && srtUrl.startsWith('http')) {
      const txtContent = await convertSrtToTxt(srtUrl);
      downloadTxtFile(txtContent, `subtitle_${filetrans.value.id}.txt`);
      message.success('字幕下载成功');
    } else {
      console.error('无效的响应数据:', srtUrl);
      message.error('生成字幕失败: 无效的响应格式');
    }
  } catch (error: any) {
    console.error('下载字幕失败:', error);

    let errorMsg = '下载字幕失败';
    if (error.response) {
      const responseData = error.response.data;
      errorMsg = responseData?.msg || responseData?.message ||
        `HTTP ${error.response.status}: ${error.response.statusText}`;
    } else if (error.message) {
      errorMsg = error.message;
    }

    message.error('生成字幕失败: ' + errorMsg);
  } finally {
    downloadLoading.value = false;
  }
};

/**
 * 将SRT格式转换为TXT格式
 * @param srtUrl SRT文件URL
 * @returns 转换后的TXT内容
 */
const convertSrtToTxt = async (srtUrl: string): Promise<string> => {
  try {
    const response = await fetch(srtUrl);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const srtContent = await response.text();
    console.log('原始SRT内容:', srtContent);

    let txtContent = '';

    // 处理多种格式的SRT文件
    const blocks = srtContent.trim().split(/(?:\r?\n){2,}/); // 使用两个或更多换行符分割块

    for (const block of blocks) {
      if (!block.trim()) continue; // 跳过空块

      const lines = block.split(/\r?\n/).filter(line => line.trim() !== '');

      // 有效的字幕块至少包含序号、时间行和文本
      if (lines.length >= 3) {
        // 检查第一行是否是数字（序号）
        const isNumbered = !isNaN(parseInt(lines[0]));

        // 检查第二行是否是时间格式（HH:mm:ss,SSS --> HH:mm:ss,SSS）
        const isTimeFormat = /^\d{2}:\d{2}:\d{2},\d{3}\s*-->\s*\d{2}:\d{2}:\d{2},\d{3}$/.test(lines[1]);

        if (isNumbered && isTimeFormat) {
          // 从第三行开始是文本（可能有多行）
          const textLines = lines.slice(2);

          // 处理多行文本（合并为一个段落）
          const paragraph = textLines.join(' ').trim();

          if (paragraph) {
            txtContent += paragraph + '\n\n';
          }
        } else {
          console.warn('无效的字幕块:', block);
        }
      }
    }

    return txtContent.trim();
  } catch (error) {
    console.error('转换字幕失败:', error);
    throw new Error('字幕转换失败: ' + error.message);
  }
};

/**
 * 下载TXT文件
 * @param content 文件内容
 * @param filename 文件名
 */
const downloadTxtFile = (content: string, filename: string) => {
  const blob = new Blob([content], {type: 'text/plain'});
  const url = URL.createObjectURL(blob);

  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();

  // 清理
  setTimeout(() => {
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }, 100);
};

/**
 * 下载SRT字幕文件
 */
const handleDownloadSrt = async () => {
  if (!filetrans.value?.id) {
    message.error('无法下载字幕：缺少文件转换ID');
    return;
  }

  try {
    downloadSrtLoading.value = true;

    console.log('调用生成字幕接口（用于SRT），参数:', {filetransId: filetrans.value.id});
    const response = await genSubtitle({
      filetransId: filetrans.value.id
    });

    // 直接使用响应数据作为URL
    const srtUrl = response.data;
    console.log('获取到SRT文件URL（用于下载）:', srtUrl);

    // 验证URL格式
    if (typeof srtUrl === 'string' && srtUrl.startsWith('http')) {
      // 直接下载SRT文件
      downloadFile(srtUrl, `subtitle_${filetrans.value.id}.srt`);
      message.success('SRT字幕下载成功');
    } else {
      console.error('无效的响应数据:', srtUrl);
      message.error('生成字幕失败: 无效的响应格式');
    }
  } catch (error: any) {
    console.error('下载SRT字幕失败:', error);

    let errorMsg = '下载SRT字幕失败';
    if (error.response) {
      const responseData = error.response.data;
      errorMsg = responseData?.msg || responseData?.message ||
        `HTTP ${error.response.status}: ${error.response.statusText}`;
    } else if (error.message) {
      errorMsg = error.message;
    }

    message.error('下载SRT字幕失败: ' + errorMsg);
  } finally {
    downloadSrtLoading.value = false;
  }
};

/**
 * 通用文件下载函数
 * @param url 文件URL
 * @param filename 下载的文件名
 */
const downloadFile = (url: string, filename: string) => {
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();

  // 清理
  setTimeout(() => {
    document.body.removeChild(a);
  }, 100);
};

// 暴露方法给父组件
defineExpose({
  showModal
});
</script>
