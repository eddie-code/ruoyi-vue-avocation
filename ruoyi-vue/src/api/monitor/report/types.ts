export interface CacheVO {
  commandStats: Array<{ name: string; value: string }>;
  dbSize: number;
  info: { [key: string]: string };
}

// 新增统计数据接口
export interface StatisticVO {
  onlineCount: number; // 在线人数
  registerCount: number; // 注册人数
  orderCount: number; // 订单数
  orderAmount: number; // 订单金额
  filetransCount: number; // 语音识别次数
  filetransSecond: number; // 语音识别时长
}
