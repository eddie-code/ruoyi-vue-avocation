import request from '@/utils/request';
import { FileTransUploadForm, PayForm, DemoAudioResponse } from '@/api/audio/voiceRecognition/types';

/**
 * 调用后端接口获取上传凭证
 * @param data
 */
export const getCredentialsApi = (data: FileTransUploadForm) => {
  // 调用 request 方法发送请求
  return request({
    url: '/web/vod/get-upload-auth',
    method: 'post',
    data: data
  });
};

/**
 * 新增：计算金额接口
 * @param videoId 视频ID
 */
export const calculateAmountApi = (videoId: string) => {
  return request({
    url: `/web/vod/cal-amount/${videoId}`,
    method: 'get'
  });
};

/**
 *
 * @param data
 */
export const payApi = (data: PayForm) => {
  // 调用 request 方法发送请求
  return request({
    url: '/web/filetrans/pay',
    method: 'post',
    data: data
  });
};

/**
 * 新增订单状态查询接口
 * @param orderNo
 */
export const queryOrderStatusApi = (orderNo: string) => {
  return request({
    url: `/web/order-info/query-order-status/${orderNo}`,
    method: 'get'
  });
};

/**
 * 获取示例音频接口
 */
export const getDemoAudioApi = () => {
  return request<DemoAudioResponse>({
    url: '/web/vod/upload-demo',
    method: 'get'
  });
};
