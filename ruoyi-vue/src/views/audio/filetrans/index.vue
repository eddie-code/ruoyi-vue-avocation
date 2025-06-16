<template>
  <!-- 主容器 -->
  <div class="p-2">
    <!-- 搜索区域动画过渡 -->
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter"
                :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item>
              <!-- 操作按钮组 -->
              <el-button v-hasPermi="['web:vod:get-upload-auth']" type="primary" icon="UploadFilled" @click="showModal">上传音频</el-button>
              <FiletransUpload ref="filetransUploadView"></FiletransUpload>
              <FiletransSubtitle ref="filetransSubtitleCom"></FiletransSubtitle>
              <el-button icon="Refresh" @click="resetQuery">刷新列表</el-button>

              <!-- 保留的原注释代码 -->
              <!--
              <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['audio:filetrans:add']">新增</el-button>
              <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['audio:filetrans:edit']">修改</el-button>
              <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['audio:filetrans:remove']">删除</el-button>
              <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['audio:filetrans:export']">导出</el-button>
              <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
              -->
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 主内容区域 -->
    <el-card shadow="never">
      <!-- 保留的原注释代码 -->
      <!--
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['audio:filetrans:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['audio:filetrans:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['audio:filetrans:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['audio:filetrans:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>
      -->

      <!-- 文件列表表格 -->
      <el-table v-loading="loading" :data="filetransList" @selection-change="handleSelectionChange">
        <!-- 保留的原注释代码 -->
        <!-- <el-table-column type="selection" width="55" align="center" /> -->

        <el-table-column label="id" align="center" prop="id" v-if="false" />
        <el-table-column label="文件名称" align="center" prop="name" />
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
            <!-- 保留的原注释代码 -->
            <!--
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['audio:filetrans:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['audio:filetrans:remove']"></el-button>
            </el-tooltip>
            -->

            <!-- 字幕查看按钮（仅当状态为SS时显示） -->
            <el-tooltip v-if="scope.row.status === 'SS'" content="查看字幕" placement="top">
              <el-button link v-hasPermi="['web:filetransSubtitle:list']" type="primary" icon="Document" @click="showSubtitleModal(scope.row)"></el-button>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination v-show="total > 0" :total="total"
                  v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize"
                  @pagination="getList" />
    </el-card>

    <!-- 编辑对话框（保留但未使用） -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="filetransFormRef" :model="form" :rules="rules" label-width="80px">
      </el-form>

      <!-- 保留的原注释代码 -->
      <!--
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
      -->
    </el-dialog>
  </div>
</template>

<script setup name="Filetrans" lang="ts">
// 保留的原注释代码
// import { listFiletrans, getFiletrans, delFiletrans, addFiletrans, updateFiletrans } from '@/api/audio/filetrans';

// 导入必要的模块和组件
import { listFiletrans } from '@/api/audio/filetrans';
import { FiletransVO, FiletransQuery, FiletransForm } from '@/api/audio/filetrans/types';
import FiletransUpload from '../voiceRecognition/filetrans-upload.vue';
import FiletransSubtitle from '@/components/Alibaba/Subtitle/filetrans-subtitle.con.vue';

// 获取组件实例
const { proxy } = getCurrentInstance() as ComponentInternalInstance;

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
  console.log('Button clicked'); // 调试信息
  if (filetransUploadView.value) {
    filetransUploadView.value.showModal();
    console.log('showModal 在 FiletransUpload 上调用'); // 调试信息
  } else {
    console.error('filetransUploadCom 未定义'); // 错误信息
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
    console.log('showSubtitleModal 在 filetransSubtitleCom 上调用'); // 调试信息
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
const initFormData: FiletransForm = {
}

// 页面数据管理
const data = reactive<PageData<FiletransForm, FiletransQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    params: {
    }
  },
  rules: {
  }
});

// 解构数据
const { queryParams, form, rules } = toRefs(data);

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

// 保留的原注释代码
// /** 新增按钮操作 */
// const handleAdd = () => {
//   reset();
//   dialog.visible = true;
//   dialog.title = "添加语音识别";
// }
//
// /** 修改按钮操作 */
// const handleUpdate = async (row?: FiletransVO) => {
//   reset();
//   const _id = row?.id || ids.value[0]
//   const res = await getFiletrans(_id);
//   Object.assign(form.value, res.data);
//   dialog.visible = true;
//   dialog.title = "修改语音识别";
// }
//
// /** 提交按钮 */
// const submitForm = () => {
//   filetransFormRef.value?.validate(async (valid: boolean) => {
//     if (valid) {
//       buttonLoading.value = true;
//       if (form.value.id) {
//         await updateFiletrans(form.value).finally(() =>  buttonLoading.value = false);
//       } else {
//         await addFiletrans(form.value).finally(() =>  buttonLoading.value = false);
//       }
//       proxy?.$modal.msgSuccess("操作成功");
//       dialog.visible = false;
//       await getList();
//     }
//   });
// }
//
// /** 删除按钮操作 */
// const handleDelete = async (row?: FiletransVO) => {
//   const _ids = row?.id || ids.value;
//   await proxy?.$modal.confirm('是否确认删除语音识别编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
//   await delFiletrans(_ids);
//   proxy?.$modal.msgSuccess("删除成功");
//   await getList();
// }

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
