<template>
  <div class="statistic-container">
    <el-alert v-if="loading" type="info" title="数据加载中..." show-icon />
    <el-alert v-if="error" type="error" :title="error" show-icon />

    <el-row :gutter="16" v-if="!loading && !error">
      <el-col :span="4" v-for="item in statisticData" :key="item.label">
        <div class="statistic-card">
          <el-statistic :value="item.value">
            <template #title>
              <div style="display: inline-flex; align-items: center">
                {{ item.label }}
                <el-tooltip
                  v-if="item.tooltip"
                  effect="dark"
                  :content="item.tooltip"
                  placement="top"
                >
                  <el-icon style="margin-left: 4px" :size="12">
                    <Warning />
                  </el-icon>
                </el-tooltip>
              </div>
            </template>
          </el-statistic>
          <div class="statistic-footer" v-if="item.showGrowth">
            <div class="footer-item">
              <span>较昨日</span>
              <span :class="getGrowthRateClass(item.growthRate)">
                {{ formatGrowthRate(item.growthRate, item.yesterdayValue) }}
                <el-icon v-if="item.growthRate !== null">
                  <CaretTop v-if="item.growthRate > 0" />
                  <CaretBottom v-if="item.growthRate < 0" />
                  <Minus v-if="item.growthRate === 0" />
                </el-icon>
              </span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <statistic-charts v-if="!loading && !error" :statisticData="statisticDataObj" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { queryStatistic } from '@/api/monitor/report';
import StatisticCharts from './statistic-charts.vue';
import { Warning, CaretTop, CaretBottom, Minus } from '@element-plus/icons-vue';

export default defineComponent({
  name: 'StatisticIndex',
  components: {
    StatisticCharts,
    Warning,
    CaretTop,
    CaretBottom,
    Minus
  },
  setup() {
    const statisticData = ref([]);
    const statisticDataObj = ref({});
    const loading = ref(true);
    const error = ref('');

    // 获取昨天的日期字符串，格式为 "MM-DD"
    const getYesterdayDate = () => {
      const date = new Date();
      date.setDate(date.getDate() - 1);
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${month}-${day}`;
    };

    // 从列表中查找昨天的数据
    const findYesterdayData = (list, currentValue) => {
      const yesterdayDate = getYesterdayDate();
      const yesterdayItem = list.find(item => item.date === yesterdayDate);

      // 如果没有昨天的数据记录
      if (!yesterdayItem) {
        return { growthRate: null, yesterdayValue: null };
      }

      // 处理字符串类型的数字
      const yesterdayValue = typeof yesterdayItem.num === 'string'
        ? parseFloat(yesterdayItem.num)
        : yesterdayItem.num;

      const current = typeof currentValue === 'string'
        ? parseFloat(currentValue)
        : currentValue;

      // 如果昨日数据为0
      if (yesterdayValue === 0) {
        // 当前也为0，显示无变化
        if (current === 0) return { growthRate: 0, yesterdayValue: 0 };
        // 当前不为0，显示新增
        return { growthRate: 100, yesterdayValue: 0 };
      }

      // 正常计算增长率
      return {
        growthRate: ((current - yesterdayValue) / yesterdayValue) * 100,
        yesterdayValue
      };
    };

    // 格式化增长率显示
    const formatGrowthRate = (rate, yesterdayValue) => {
      if (rate === null) return '无数据';
      if (yesterdayValue === 0 && rate === 100) return '新增';
      if (rate === 0) return '无变化';
      return `${rate > 0 ? '+' : ''}${rate.toFixed(2)}%`;
    };

    // 根据增长率返回样式类
    const getGrowthRateClass = (rate) => {
      if (rate === null) return '';
      if (rate === 0) return 'gray';
      return rate > 0 ? 'green' : 'red';
    };

    const fetchStatistic = async () => {
      try {
        loading.value = true;
        error.value = '';

        const response = await queryStatistic();
        console.log('API响应数据:', response);

        const data = response.data || {};
        statisticDataObj.value = data;

        statisticData.value = [
          {
            label: '在线人数',
            value: data.onlineCount || 0,
            growthRate: null,
            yesterdayValue: null,
            showGrowth: false,
            tooltip: '当前在线用户数量'
          },
          {
            label: '注册人数',
            value: data.registerCount || 0,
            ...findYesterdayData(data.registerCountList, data.registerCount),
            showGrowth: true,
            tooltip: '系统总注册用户数'
          },
          {
            label: '订单数',
            value: data.orderCount || 0,
            ...findYesterdayData(data.orderCountList, data.orderCount),
            showGrowth: true,
            tooltip: '今日订单总数'
          },
          {
            label: '订单金额',
            value: formatAmount(data.orderAmount) || '0.00',
            ...findYesterdayData(data.orderAmountList, data.orderAmount),
            showGrowth: true,
            tooltip: '今日订单总金额'
          },
          {
            label: '语音识别次数',
            value: data.filetransCount || 0,
            ...findYesterdayData(data.filetransCountList, data.filetransCount),
            showGrowth: true,
            tooltip: '今日语音识别总次数'
          },
          {
            label: '语音识别时长',
            value: formatDuration(data.filetransSecond || 0),
            ...findYesterdayData(data.filetransSecondList, data.filetransSecond),
            showGrowth: true,
            tooltip: '今日语音识别总时长'
          },
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
      error,
      formatGrowthRate,
      getGrowthRateClass
    };
  },
});
</script>

<style scoped>
.statistic-container {
  padding: 20px;
}

.statistic-card {
  position: relative;
  height: 100%;
  padding: 20px;
  border-radius: 4px;
  background-color: var(--el-bg-color-overlay);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.statistic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-top: 16px;
}

.statistic-footer .footer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistic-footer .footer-item span:last-child {
  display: inline-flex;
  align-items: center;
  margin-left: 4px;
}

.green {
  color: var(--el-color-success);
}
.red {
  color: var(--el-color-error);
}
.gray {
  color: var(--el-text-color-secondary);
}
</style>
