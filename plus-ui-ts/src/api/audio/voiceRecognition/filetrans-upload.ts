import request from '@/utils/request';
import { FileTransUploadForm } from '@/api/audio/voiceRecognition/types';

/**
 * 调用后端接口获取上传凭证
 * @param data
 */
export const getCredentials = (data: FileTransUploadForm) => {
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
export const calculateAmount = (videoId: string) => {
  return request({
    url: `/web/vod/cal-amount/${videoId}`,
    method: 'get'
  });
};
