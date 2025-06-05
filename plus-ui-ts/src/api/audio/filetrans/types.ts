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



