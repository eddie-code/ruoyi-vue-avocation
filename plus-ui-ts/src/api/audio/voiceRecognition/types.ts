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
 * 支付金额
 */
export interface PayForm {
  name: string | number;
  percent: number;
  amount: number;
  lang: string | number;
  audio: string | number;
  fileSign: string | number;
  vod: string | number;
  channel: string | number;
}

/**
 * 文件需需要字段（前端交互）
 */
export interface FileItem {
  /**
   * 当前上传的文件名
   */
  name: string;

  /**
   * 上传进度百分比（0-100）
   */
  percent: number;
  /**
   * 原音语言
   */
  lang: string;
  /**
   * 上传音频文件
   */
  audioAddr: string;
  /**
   * md5 加密过的key
   */
  fileSign: string;
  /**
   * videoId
   */
  vod: string;
}

/**
 * 文件上传组件暴露的方法和属性（前端交互）
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
  filetrans: FileItem;

  /**
   * 触发下单功能
   */
  handlePay: () => void;
}

// 扩展事件类型声明
declare module 'vue' {
  interface ComponentCustomProperties {
    $emit: (event: 'amount-calculated', amount: string | number) => void;
  }
}
