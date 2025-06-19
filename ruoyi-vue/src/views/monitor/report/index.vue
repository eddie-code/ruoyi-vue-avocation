<template>
  <div class="statistic-container">
    <!-- 添加加载状态提示 -->
    <el-alert v-if="loading" type="info" title="数据加载中..." show-icon />
    <el-alert v-if="error" type="error" :title="error" show-icon />

    <!-- 使用 div 和 Flexbox 布局 -->
    <div class="statistic-row">
      <div v-for="item in statisticData" :key="item.label" class="statistic-item">
        <span class="label">{{ item.label }}</span>
        <span class="value">{{ item.value }}</span>
      </div>
    </div>

    <!-- 新增的图表组件 -->
    <statistic-charts v-if="!loading && !error" :statisticData="statisticDataObj" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { queryStatistic } from '@/api/monitor/report';
import StatisticCharts from './statistic-charts.vue'; // 引入图表组件

export default defineComponent({
  name: 'StatisticIndex',
  components: {
    StatisticCharts // 注册组件
  },
  setup() {
    const statisticData = ref([]);
    const statisticDataObj = ref({}); // 用于存储整个统计数据对象
    const loading = ref(true);
    const error = ref('');

    const fetchStatistic = async () => {
      try {
        loading.value = true;
        error.value = '';

        const response = await queryStatistic();
        console.log('API响应数据:', response); // 添加调试日志

        // 确保数据结构正确
        const data = response.data || {};

        // 存储整个数据对象，用于图表
        statisticDataObj.value = data;

        // 更新顶部统计数据
        statisticData.value = [
          { label: '在线人数', value: data.onlineCount || 0 },
          { label: '注册人数', value: data.registerCount || 0 },
          { label: '订单数', value: data.orderCount || 0 },
          { label: '订单金额', value: formatAmount(data.orderAmount) || '0.00' },
          { label: '语音识别次数', value: data.filetransCount || 0 },
          { label: '语音识别时长', value: formatDuration(data.filetransSecond || 0) },
        ];
      } catch (err) {
        console.error('获取统计数据失败:', err);
        error.value = `数据加载失败: ${err.message || '未知错误'}`;
      } finally {
        loading.value = false;
      }
    };

    // 格式化语音识别时长
    const formatDuration = (seconds) => {
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;

      return [
        hours.toString().padStart(2, '0'),
        minutes.toString().padStart(2, '0'),
        secs.toString().padStart(2, '0')
      ].join(':');
    };

    // 格式化金额
    const formatAmount = (amount) => {
      if (!amount) return '0.00';
      return parseFloat(amount).toFixed(2);
    };

    onMounted(() => {
      fetchStatistic();
    });

    return {
      statisticData,
      statisticDataObj,
      loading,
      error
    };
  },
});
</script>

<style scoped>
.statistic-container {
  padding: 20px;
}

.statistic-row {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 平均分配每个统计项 */
  border-radius: 4px;
  padding: 10px 20px; /* 内边距 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

.statistic-item {
  display: flex;
  flex-direction: column; /* 使 label 和 value 垂直排列 */
  align-items: center; /* 水平居中对齐 */
  width: calc(100% / 6); /* 每个统计项占总宽度的1/6 */
}

.label {
  font-size: 14px; /* 标签字体大小 */
  margin-bottom: 5px; /* label 和 value 之间的间距 */
}

.value {
  font-size: 20px; /* 数值字体大小 */
  font-weight: bold; /* 数值加粗 */
}
</style>
