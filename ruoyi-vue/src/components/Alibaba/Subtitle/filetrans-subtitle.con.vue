<!-- 字幕生成模态框 - Element Plus 版本 -->
<template>
  <el-dialog
    v-model="open"
    title="生成字幕"
    width="80%"
    custom-class="centered-dialog"
  >
    <!-- 下载按钮区域 -->
    <div style="margin-bottom: 16px; text-align: right;">
      <!-- 下载SRT字幕按钮 -->
      <el-button
        v-hasPermi="['web:filetransSubtitle:genSubtitle']"
        type="primary"
        @click="handleDownloadSrt"
        :loading="downloadSrtLoading"
        style="margin-right: 8px;"
      >
        下载SRT字幕
      </el-button>
      <!-- 下载TXT字幕按钮 -->
      <el-button
        v-hasPermi="['web:filetransSubtitle:genText']"
        type="primary"
        @click="handleDownloadTxt"
        :loading="downloadLoading"
      >
        下载TXT字幕
      </el-button>
    </div>

    <!-- 字幕表格展示区域 -->
    <el-table
      :data="dataList"
      v-loading="loading"
      border
      style="width: 100%"
    >
      <!-- 时间段列 -->
      <el-table-column prop="timeRange" label="时间段" width="250">
        <template #default="{ row, $index }">
          <!-- 开始时间（如果是第一页第一项则加粗红色显示） -->
          <span :style="pagination.current === 1 && $index === 0 ? 'color: red; font-weight: bold' : ''">
            {{ formatTime(row.begin) }}
          </span>
          -
          <!-- 结束时间（如果是最后一页最后一项则加粗红色显示） -->
          <span :style="isLastPageLastItem($index) ? 'color: red; font-weight: bold' : ''">
            {{ formatTime(row.end) }}
          </span>
        </template>
      </el-table-column>

      <!-- 字幕文本列 -->
      <el-table-column prop="text" label="字幕" />
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { listFiletransSubtitle, genSubtitle } from '@/api/audio/filetrans';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

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
});

/**
 * 将毫秒转换为时间格式 (HH:mm:ss)
 */
const formatTime = (milliseconds: number): string => {
  const totalSeconds = Math.floor(milliseconds / 1000);
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
};

/**
 * 判断当前项是否是最后一页的最后一项
 */
const isLastPageLastItem = (index: number): boolean => {
  const totalPage = Math.ceil(pagination.total / pagination.pageSize);
  return pagination.current === totalPage &&
    index === dataList.value.length - 1 &&
    pagination.total > 0;
};

/**
 * 显示模态框
 */
const showModal = (_filetrans: any) => {
  filetrans.value = _filetrans;
  open.value = true;
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

    const data = await listFiletransSubtitle(params);
    if (data && data.rows) {
      dataList.value = data.rows || [];
      pagination.total = data.total || 0;
    } else {
      console.error('接口返回的业务数据无效:', data);
    }
  } catch (error: any) {
    console.error('加载字幕失败:', error);
    ElMessage.error('加载字幕失败');
  } finally {
    loading.value = false;
  }
};

/**
 * 处理每页条数变化
 */
const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.current = 1;
  loadData();
};

/**
 * 处理页码变化
 */
const handleCurrentChange = (page: number) => {
  pagination.current = page;
  loadData();
};

/**
 * 下载TXT字幕文件
 */
const handleDownloadTxt = async () => {
  if (!filetrans.value?.id) {
    ElMessage.error('无法下载字幕：缺少文件转换ID');
    return;
  }

  try {
    downloadLoading.value = true;
    const response = await genSubtitle({ filetransId: filetrans.value.id });
    const srtUrl = response.data;

    if (typeof srtUrl === 'string' && srtUrl.startsWith('http')) {
      const txtContent = await convertSrtToTxt(srtUrl);
      downloadTxtFile(txtContent, `subtitle_${filetrans.value.id}.txt`);
      ElMessage.success('字幕下载成功');
    } else {
      console.error('无效的响应数据:', srtUrl);
      ElMessage.error('生成字幕失败: 无效的响应格式');
    }
  } catch (error: any) {
    console.error('下载字幕失败:', error);
    ElMessage.error('下载字幕失败');
  } finally {
    downloadLoading.value = false;
  }
};

/**
 * 将SRT格式转换为TXT格式
 */
const convertSrtToTxt = async (srtUrl: string): Promise<string> => {
  try {
    const response = await fetch(srtUrl);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const srtContent = await response.text();
    let txtContent = '';
    const blocks = srtContent.trim().split(/(?:\r?\n){2,}/);

    for (const block of blocks) {
      if (!block.trim()) continue;
      const lines = block.split(/\r?\n/).filter(line => line.trim() !== '');

      if (lines.length >= 3) {
        const isNumbered = !isNaN(parseInt(lines[0]));
        const isTimeFormat = /^\d{2}:\d{2}:\d{2},\d{3}\s*-->\s*\d{2}:\d{2}:\d{2},\d{3}$/.test(lines[1]);

        if (isNumbered && isTimeFormat) {
          const paragraph = lines.slice(2).join(' ').trim();
          if (paragraph) txtContent += paragraph + '\n\n';
        }
      }
    }
    return txtContent.trim();
  } catch (error: any) {
    throw new Error('字幕转换失败: ' + error.message);
  }
};

/**
 * 下载TXT文件
 */
const downloadTxtFile = (content: string, filename: string) => {
  const blob = new Blob([content], {type: 'text/plain'});
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
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
    ElMessage.error('无法下载字幕：缺少文件转换ID');
    return;
  }

  try {
    downloadSrtLoading.value = true;
    const response = await genSubtitle({ filetransId: filetrans.value.id });
    const srtUrl = response.data;

    if (typeof srtUrl === 'string' && srtUrl.startsWith('http')) {
      downloadFile(srtUrl, `subtitle_${filetrans.value.id}.srt`);
      ElMessage.success('SRT字幕下载成功');
    } else {
      console.error('无效的响应数据:', srtUrl);
      ElMessage.error('生成字幕失败: 无效的响应格式');
    }
  } catch (error: any) {
    console.error('下载SRT字幕失败:', error);
    ElMessage.error('下载SRT字幕失败');
  } finally {
    downloadSrtLoading.value = false;
  }
};

/**
 * 通用文件下载函数
 */
const downloadFile = (url: string, filename: string) => {
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  setTimeout(() => document.body.removeChild(a), 100);
};

defineExpose({ showModal });
</script>

<style>
.centered-dialog {
  margin: 20px auto !important;
  max-width: calc(100% - 40px);
}

.pagination-container {
  display: flex;
  justify-content: flex-end; /* 分页组件右对齐 */
  margin-top: 20px; /* 与上方数据列表的间距 */
}
</style>
