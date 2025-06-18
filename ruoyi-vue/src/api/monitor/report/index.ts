import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import {StatisticVO } from './types';

// 查询统计数据接口
export function queryStatistic(): AxiosPromise<StatisticVO> {
  return request({
    url: '/admin/report/queryStatistic', // 使用相对路径，由代理转发
    method: 'get'
  });
}
