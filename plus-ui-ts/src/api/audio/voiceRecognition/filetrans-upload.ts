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
