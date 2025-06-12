export interface FiletransVO {
  /**
   * id
   */
  id: string | number;

  /**
   * 会员ID
   */
  memberId: string | number;

  /**
   * 文件名称
   */
  name: string;

  /**
   * 音频文件时长|秒
   */
  second: number;

  /**
   * 金额|元，second*单价
   */
  amount: number;

  /**
   * 文件链接
   */
  audio: string;

  /**
   * 文件签名md5
   */
  fileSign: string;

  /**
   * 支付状态|枚举[FiletransPayStatusEnum];
   */
  payStatus: string;

  /**
   * 识别状态|枚举[FiletransStatusEnum];
   */
  status: string;

  /**
   * 音频语言|枚举[FiletransLangEnum]
   */
  lang: string;

  /**
   * VOD|videoId
   */
  vod: string;

  /**
   * 任务ID
   */
  taskId: string | number;

  /**
   * 转换状态码
   */
  transStatusCode: number;

  /**
   * 转换状态说明
   */
  transStatusText: string;

  /**
   * 转换时间|开始转换的时间
   */
  transTime: string;

  /**
   * 完成时间|录音文件识别完成的时间
   */
  solveTime: string;

}

export interface FiletransForm extends BaseEntity {
  /**
   * id
   */
  id?: string | number;

  /**
   * 会员ID
   */
  memberId?: string | number;

  /**
   * 文件名称
   */
  name?: string;

  /**
   * 音频文件时长|秒
   */
  second?: number;

  /**
   * 金额|元，second*单价
   */
  amount?: number;

  /**
   * 文件链接
   */
  audio?: string;

  /**
   * 文件签名md5
   */
  fileSign?: string;

  /**
   * 支付状态|枚举[FiletransPayStatusEnum];
   */
  payStatus?: string;

  /**
   * 识别状态|枚举[FiletransStatusEnum];
   */
  status?: string;

  /**
   * 音频语言|枚举[FiletransLangEnum]
   */
  lang?: string;

  /**
   * VOD|videoId
   */
  vod?: string;

  /**
   * 任务ID
   */
  taskId?: string | number;

  /**
   * 转换状态码
   */
  transStatusCode?: number;

  /**
   * 转换状态说明
   */
  transStatusText?: string;

  /**
   * 转换时间|开始转换的时间
   */
  transTime?: string;

  /**
   * 完成时间|录音文件识别完成的时间
   */
  solveTime?: string;

}

export interface FiletransQuery extends PageQuery {

  /**
   * 会员ID
   */
  memberId?: string | number;

  /**
   * 文件名称
   */
  name?: string;

  /**
   * 音频文件时长|秒
   */
  second?: number;

  /**
   * 金额|元，second*单价
   */
  amount?: number;

  /**
   * 文件链接
   */
  audio?: string;

  /**
   * 文件签名md5
   */
  fileSign?: string;

  /**
   * 支付状态|枚举[FiletransPayStatusEnum];
   */
  payStatus?: string;

  /**
   * 识别状态|枚举[FiletransStatusEnum];
   */
  status?: string;

  /**
   * 音频语言|枚举[FiletransLangEnum]
   */
  lang?: string;

  /**
   * VOD|videoId
   */
  vod?: string;

  /**
   * 任务ID
   */
  taskId?: string | number;

  /**
   * 转换状态码
   */
  transStatusCode?: number;

  /**
   * 转换状态说明
   */
  transStatusText?: string;

  /**
   * 转换时间|开始转换的时间
   */
  transTime?: string;

  /**
   * 完成时间|录音文件识别完成的时间
   */
  solveTime?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}


// 新增字幕列表接口 =============================================
export interface FiletransSubtitleQuery {
  /** 文件转换ID */
  filetransId: string | number;
  /** 页码 */
  pageNum?: number;
  /** 每页大小 */
  pageSize?: number;
}

export interface SubtitleItem {
  /** 开始时间 */
  startTime: string;
  /** 结束时间 */
  endTime: string;
  /** 字幕内容 */
  subtitle: string;
}

// 修正为后端实际返回结构
export interface TableDataInfo<T> {
  /** 状态码 */
  code: number;
  /** 消息 */
  msg: string;
  /** 总记录数 */
  total: number;
  /** 列表数据 */
  rows: T[];
}
