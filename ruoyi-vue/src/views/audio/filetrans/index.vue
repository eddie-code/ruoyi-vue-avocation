<template>
  <!-- 主容器 -->
  <div class="p-2">
    <!-- 搜索区域动画过渡 -->
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter"
                :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">

          <el-alert title='温馨提示' type='warning'
                    closable effect='light'
                    show-icon close-text='关闭'
                    description='为保障用户数据不泄露，用户上传的音频将在10天后系统自动删除。'
                    style="margin-bottom: 16px;"
          />

          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item>
              <!-- 操作按钮组 -->
              <el-button v-hasPermi="['web:vod:get-upload-auth']" type="primary" icon="UploadFilled" @click="showModal"
                         style="margin-right: 12px">上传音频
              </el-button>
              <FiletransUpload ref="filetransUploadView" @after-pay="handleAfterPay"></FiletransUpload>
              <FiletransSubtitle ref="filetransSubtitleCom"></FiletransSubtitle>
              <el-button icon="Refresh" @click="resetQuery">刷新列表</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 主内容区域 -->
    <el-card shadow="never">
      <!-- 文件列表表格 -->
      <el-table v-loading="loading" :data="filetransList" @selection-change="handleSelectionChange">
        <el-table-column label="id" align="center" prop="id" v-if="false"/>
        <el-table-column label="文件名称" align="center" prop="name"/>
        <el-table-column label="支付状态" align="center" prop="payStatus">
          <template #default="scope">
            {{ payStatusMap[scope.row.payStatus] || scope.row.payStatus }}
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            {{ statusMap[scope.row.status] || scope.row.status }}
          </template>
        </el-table-column>
        <el-table-column label="音频语言" align="center" prop="lang">
          <template #default="scope">
            {{ langMap[scope.row.lang] || scope.row.lang }}
          </template>
        </el-table-column>
        <el-table-column label="音频时长" align="center" prop="second">
          <template #default="scope">
            {{ formatDuration(scope.row.second) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <!-- 字幕查看按钮（仅当状态为SS时显示） -->
            <el-tooltip v-if="scope.row.status === 'SS'" content="查看字幕" placement="top">
              <el-button link v-hasPermi="['web:filetransSubtitle:list']" type="primary" icon="Document"
                         @click="showSubtitleModal(scope.row)"></el-button>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination v-show="total > 0" :total="total"
                  v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize"
                  @pagination="getList"/>
    </el-card>

    <!-- 编辑对话框（保留但未使用） -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="filetransFormRef" :model="form" :rules="rules" label-width="80px">
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup name="Filetrans" lang="ts">
import {listFiletrans} from '@/api/audio/filetrans';
import {FiletransVO, FiletransQuery, FiletransForm} from '@/api/audio/filetrans/types';
import FiletransUpload from '../voiceRecognition/filetrans-upload.vue';
import FiletransSubtitle from '@/components/Alibaba/Subtitle/filetrans-subtitle.con.vue';

// 获取组件实例
const {proxy} = getCurrentInstance() as ComponentInternalInstance;

/**
 * 响应式数据定义
 */
const filetransList = ref<FiletransVO[]>([]);  // 文件列表数据
const buttonLoading = ref(false);             // 按钮加载状态
const loading = ref(true);                    // 表格加载状态
const showSearch = ref(true);                 // 是否显示搜索区域
const ids = ref<Array<string | number>>([]);  // 选中项ID数组
const single = ref(true);                     // 是否单选
const multiple = ref(true);                   // 是否多选
const total = ref(0);                         // 总条数

// 表单引用
const queryFormRef = ref<ElFormInstance>();
const filetransFormRef = ref<ElFormInstance>();

// 组件引用
const filetransUploadView = ref();
const filetransSubtitleCom = ref();

/**
 * 显示上传模态框
 */
const showModal = () => {
  console.log('Button clicked');
  if (filetransUploadView.value) {
    filetransUploadView.value.showModal();
    console.log('showModal 在 FiletransUpload 上调用');
  } else {
    console.error('filetransUploadCom 未定义');
  }
};

/**
 * 显示字幕模态框
 * @param row 当前行数据
 */
const showSubtitleModal = (row: FiletransVO) => {
  console.log('查看字幕', row.id);
  if (filetransSubtitleCom.value) {
    filetransSubtitleCom.value.showModal(row);
    console.log('showSubtitleModal 在 filetransSubtitleCom 上调用');
  } else {
    console.error('filetransSubtitleCom 未定义');
  }
};

// 对话框配置
const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

// 表单初始数据
const initFormData: FiletransForm = {}

// 页面数据管理
const data = reactive<PageData<FiletransForm, FiletransQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    params: {}
  },
  rules: {}
});

// 解构数据
const {queryParams, form, rules} = toRefs(data);

/**
 * 查询文件列表
 */
const getList = async () => {
  loading.value = true;
  const res = await listFiletrans(queryParams.value);
  filetransList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/**
 * 取消操作
 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/**
 * 重置表单
 */
const reset = () => {
  form.value = {...initFormData};
  filetransFormRef.value?.resetFields();
}

/**
 * 查询操作
 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/**
 * 重置查询
 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/**
 * 表格选中项变化
 * @param selection 选中项数组
 */
const handleSelectionChange = (selection: FiletransVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/**
 * 支付结果处理
 * @param status 支付状态
 * @param refresh 是否刷新列表
 */
const handleAfterPay = (status: string, refresh: boolean = false) => {
  if (status === 'S') {
    ElNotification({
      title: '支付宝支付提示',
      message: "支付成功，感谢您的使用！",
      type: 'success',
      duration: 3000
    });
    if (refresh) {
      console.log("支付后执行列表刷新...")
      getList(); // 支付成功后刷新列表
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
 * 导出操作
 */
const handleExport = () => {
  proxy?.download('audio/filetrans/export', {
    ...queryParams.value
  }, `filetrans_${new Date().getTime()}.xlsx`)
}

/**
 * 计算支付状态映射
 */
const payStatusMap = computed(() => {
  return Object.values(window.FILETRANS_PAY_STATUS).reduce((acc, item) => {
    acc[item.code] = item.desc;
    return acc;
  }, {} as Record<string, string>);
});

/**
 * 计算状态映射
 */
const statusMap = computed(() => {
  return Object.values(window.FILETRANS_STATUS).reduce((acc, item) => {
    acc[item.code] = item.desc;
    return acc;
  }, {} as Record<string, string>);
});

/**
 * 计算语言映射
 */
const langMap = computed(() => {
  return Object.values(window.FILETRANS_LANG).reduce((acc, item) => {
    acc[item.code] = item.desc;
    return acc;
  }, {} as Record<string, string>);
});

/**
 * 格式化音频时长
 * @param seconds 总秒数
 * @returns 格式化后的时间字符串 (HH:mm:ss)
 */
const formatDuration = (seconds) => {
  if (!seconds && seconds !== 0) return '-';

  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = Math.floor(seconds % 60);

  // 使用 padStart 确保两位数显示
  const formattedHours = String(hours).padStart(2, '0');
  const formattedMinutes = String(minutes).padStart(2, '0');
  const formattedSeconds = String(secs).padStart(2, '0');

  return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
};

// 组件挂载后获取列表数据
onMounted(() => {
  getList();
});
</script>
