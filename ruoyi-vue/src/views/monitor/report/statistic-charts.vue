<template>
  <div class="charts-container">
    <!-- 注册人数柱状图 -->
    <div class="chart-item" @click="showChartDialog('register')">
      <div ref="registerChart" class="chart-container"></div>
    </div>

    <!-- 语音识别次数柱状图 -->
    <div class="chart-item" @click="showChartDialog('filetransCount')">
      <div ref="filetransCountChart" class="chart-container"></div>
    </div>

    <!-- 语音识别时长柱状图 -->
    <div class="chart-item" @click="showChartDialog('filetransSecond')">
      <div ref="filetransSecondChart" class="chart-container"></div>
    </div>

    <!-- 订单数柱状图 -->
    <div class="chart-item" @click="showChartDialog('orderCount')">
      <div ref="orderCountChart" class="chart-container"></div>
    </div>

    <!-- 订单金额柱状图 -->
    <div class="chart-item" @click="showChartDialog('orderAmount')">
      <div ref="orderAmountChart" class="chart-container"></div>
    </div>

    <!-- 弹出模态框 -->
    <el-dialog v-model="dialogVisible" title="图表详情" width="80%">
      <div ref="dialogChart" class="dialog-chart"></div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { defineComponent, ref, onMounted, watch, onBeforeUnmount, nextTick } from 'vue';

export default defineComponent({
  name: 'StatisticCharts',
  props: {
    statisticData: {
      type: Object,
      required: true,
      default: () => ({})
    }
  },
  setup(props) {
    // 创建图表引用
    const registerChart = ref(null);
    const filetransCountChart = ref(null);
    const filetransSecondChart = ref(null);
    const orderCountChart = ref(null);
    const orderAmountChart = ref(null);

    // 弹出模态框相关
    const dialogVisible = ref(false);
    const dialogChart = ref(null);
    let dialogChartInstance = null;
    const activeChartType = ref(null);

    // 存储图表实例
    let registerChartInstance = null;
    let filetransCountChartInstance = null;
    let filetransSecondChartInstance = null;
    let orderCountChartInstance = null;
    let orderAmountChartInstance = null;

    // 显示弹出模态框
    const showChartDialog = (chartType) => {
      // 设置当前激活的图表类型
      activeChartType.value = chartType;
      // 打开弹出模态框
      dialogVisible.value = true;
      // 在下次 DOM 更新循环结束之后执行延迟回调
      nextTick(() => {
        // 渲染弹出模态框中的图表
        renderDialogChart();
      });
    };

    // 渲染弹出模态框中的图表
    const renderDialogChart = () => {
      if (!dialogChart.value) return; // 如果对话框图表容器不存在，直接返回

      if (dialogChartInstance) {
        // 如果已有图表实例，先销毁它
        dialogChartInstance.dispose();
      }

      // 初始化 ECharts 实例
      dialogChartInstance = echarts.init(dialogChart.value);

      // 获取当前激活图表类型的数据和标题
      const data = getDataByType(activeChartType.value);
      const title = getTitleByType(activeChartType.value);

      // 获取图表配置选项（大图显示全部30天数据）
      const option = getOption(data, title, true);
      // 应用配置选项到图表实例
      dialogChartInstance.setOption(option);
    };

    // 根据图表类型获取数据
    const getDataByType = (chartType) => {
      switch (chartType) {
        case 'register':
          return props.statisticData.registerCountList || [];
        case 'filetransCount':
          return props.statisticData.filetransCountList || [];
        case 'filetransSecond':
          return props.statisticData.filetransSecondList || [];
        case 'orderCount':
          return props.statisticData.orderCountList || [];
        case 'orderAmount':
          return props.statisticData.orderAmountList || [];
        default:
          return [];
      }
    };

    // 根据图表类型获取标题
    const getTitleByType = (chartType) => {
      switch (chartType) {
        case 'register':
          return '近30天注册人数';
        case 'filetransCount':
          return '近30天语音识别次数';
        case 'filetransSecond':
          return '近30天语音识别时长(秒)';
        case 'orderCount':
          return '近30天订单数';
        case 'orderAmount':
          return '近30天订单金额';
        default:
          return '';
      }
    };

    // 获取图表配置选项
    const getOption = (data, title, isDialog = false) => {
      // 小图只显示最近7天的数据
      let displayData = data;
      if (!isDialog && data.length > 7) {
        displayData = data.slice(-7);
      }

      const dates = displayData.map(item => item.date || '');
      const values = displayData.map(item => item.num || 0);

      return {
        title: {
          text: title,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            return `${params[0].name}<br/>${params[0].seriesName}: ${params[0].value}`;
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        dataZoom: isDialog ? [
          {
            type: 'inside',
            start: 0,
            end: 100
          },
          {
            type: 'slider',
            show: true,
            start: 0,
            end: 100,
            handleStyle: {
              color: '#1890ff'
            }
          }
        ] : [],
        xAxis: {
          type: 'category',
          name: '日期',
          nameLocation: 'end',
          data: dates,
          axisLabel: {
            interval: 0,
            rotate: isDialog ? 45 : 0 // 小图不需要旋转
          }
        },
        yAxis: {
          type: 'value',
          name: '数量'
        },
        series: [{
          name: title,
          type: 'bar',
          data: values,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#2378f7' },
                { offset: 0.7, color: '#2378f7' },
                { offset: 1, color: '#83bff6' }
              ])
            }
          }
        }]
      };
    };

    // 初始化普通图表
    const initChart = (chartInstanceRef, chartData, chartTitle) => {
      if (!chartInstanceRef.value) return;

      const chartInstance = echarts.init(chartInstanceRef.value);
      // 小图使用7天数据
      const option = getOption(chartData, chartTitle, false);
      chartInstance.setOption(option);
      return chartInstance;
    };

    // 渲染所有普通图表
    const renderCharts = () => {
      registerChartInstance = initChart(
        registerChart,
        props.statisticData.registerCountList,
        '近30天注册人数'
      );

      filetransCountChartInstance = initChart(
        filetransCountChart,
        props.statisticData.filetransCountList,
        '近30天语音识别次数'
      );

      filetransSecondChartInstance = initChart(
        filetransSecondChart,
        props.statisticData.filetransSecondList,
        '近30天语音识别时长(秒)'
      );

      orderCountChartInstance = initChart(
        orderCountChart,
        props.statisticData.orderCountList,
        '近30天订单数'
      );

      orderAmountChartInstance = initChart(
        orderAmountChart,
        props.statisticData.orderAmountList,
        '近30天订单金额'
      );
    };

    // 监听数据变化
    watch(() => props.statisticData, (newVal) => {
      if (newVal) {
        renderCharts();
      }
    }, { deep: true, immediate: true });

    // 组件挂载时渲染图表
    onMounted(() => {
      renderCharts();
    });

    // 组件卸载前销毁图表实例
    onBeforeUnmount(() => {
      if (registerChartInstance) registerChartInstance.dispose();
      if (filetransCountChartInstance) filetransCountChartInstance.dispose();
      if (filetransSecondChartInstance) filetransSecondChartInstance.dispose();
      if (orderCountChartInstance) orderCountChartInstance.dispose();
      if (orderAmountChartInstance) orderAmountChartInstance.dispose();
      if (dialogChartInstance) dialogChartInstance.dispose();
    });

    return {
      registerChart,
      filetransCountChart,
      filetransSecondChart,
      orderCountChart,
      orderAmountChart,
      dialogVisible,
      dialogChart,
      showChartDialog
    };
  }
});
</script>

<style scoped>
.charts-container {
  margin-top: 30px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(30%, 1fr));
  gap: 20px;
}

.chart-item {
  margin-bottom: 30px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.dialog-chart {
  width: 100%;
  height: 600px;
}
</style>
