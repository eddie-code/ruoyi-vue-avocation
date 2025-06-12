:plus-ui-ts/src/components/Alibaba/Subtitle/filetrans-subtitle.con.vue
<template>
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
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.key === 'timeRange'">
          <span :style="pagination.current === 1 && index === 0 ? 'color: red; font-weight: bold' : ''">
            {{ formatTime(record.begin) }}
          </span>
          -
          <span :style="isLastPageLastItem(index) ? 'color: red; font-weight: bold' : ''">
             {{ formatTime(record.end) }}
         </span>
        </template>
        <template v-else-if="column.dataIndex === 'text'">
          {{ record.text }}
        </template>
      </template>

    </a-table>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { listFiletransSubtitle } from '@/api/audio/filetrans';
import type { TableProps } from 'ant-design-vue';

// 表格列定义 - 更新为匹配后端字段
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


const open = ref(false);
const filetrans = ref<Record<string, any>>({});
const dataList = ref<any[]>([]); // 改为 any[] 或定义匹配的类型
const loading = ref(false);

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
});

// 毫秒转换为时间格式 (HH:mm:ss.SSS)
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

// 在 formatTime 函数下方添加
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

// 显示模态框
const showModal = (_filetrans: any) => {
  filetrans.value = _filetrans;
  open.value = true;

  // 重置分页并加载数据
  pagination.current = 1;
  loadData();
};

// 加载字幕数据
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

// 处理分页变化
const handleTableChange: TableProps['onChange'] = (pag) => {
  pagination.current = pag.current!;
  pagination.pageSize = pag.pageSize!;
  loadData();
};

// 暴露方法给父组件
defineExpose({
  showModal
});
</script>
