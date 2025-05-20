/**
 * 文件上传相关类型定义
 */

/**
 * 文件上传表单参数（传递给后端的参数）
 */
export interface FileTransUploadForm {
  /**
   * 文件名（支持字符串或数字类型）
   */
  name: string | number;

  /**
   * 文件唯一标识键（支持字符串或数字类型）
   */
  key: string | number;
}

/**
 * 文件上传进度信息
 */
export interface FileTrans {
  /**
   * 当前上传的文件名
   */
  name: string;

  /**
   * 上传进度百分比（0-100）
   */
  percent: number;
}

/**
 * 文件上传组件暴露的方法和属性
 */
export interface FileUploaderExpose {
  /**
   * 触发文件选择对话框
   */
  selectFile: () => void;

  /**
   * 重置上传状态（清除文件名和进度）
   */
  resetFileTrans: () => void;

  /**
   * 当前上传文件的状态信息
   */
  filetrans: FileTrans;
}

// 扩展事件类型声明
declare module 'vue' {
  interface ComponentCustomProperties {
    $emit: (
      event: 'amount-calculated',
      amount: string | number
    ) => void;
  }
}

// 新增金额计算接口响应类型
export interface CalAmountResponse {
  code: number;
  msg: string;
  data: string | number;
}
