import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { FiletransVO, FiletransForm, FiletransQuery } from '@/api/audio/filetrans/types';

/**
 * 查询语音识别列表
 * @param query
 * @returns {*}
 */

export const listFiletrans = (query?: FiletransQuery): AxiosPromise<FiletransVO[]> => {
  return request({
    url: '/web/filetrans/list',
    method: 'get',
    params: query
  });
};

// /**
//  * 查询语音识别详细
//  * @param id
//  */
// export const getFiletrans = (id: string | number): AxiosPromise<FiletransVO> => {
//   return request({
//     url: '/audio/filetrans/' + id,
//     method: 'get'
//   });
// };
//
// /**
//  * 新增语音识别
//  * @param data
//  */
// export const addFiletrans = (data: FiletransForm) => {
//   return request({
//     url: '/audio/filetrans',
//     method: 'post',
//     data: data
//   });
// };
//
// /**
//  * 修改语音识别
//  * @param data
//  */
// export const updateFiletrans = (data: FiletransForm) => {
//   return request({
//     url: '/audio/filetrans',
//     method: 'put',
//     data: data
//   });
// };
//
// /**
//  * 删除语音识别
//  * @param id
//  */
// export const delFiletrans = (id: string | number | Array<string | number>) => {
//   return request({
//     url: '/audio/filetrans/' + id,
//     method: 'delete'
//   });
// };
