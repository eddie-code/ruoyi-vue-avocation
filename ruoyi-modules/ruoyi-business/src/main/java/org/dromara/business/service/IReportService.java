package org.dromara.business.service;

import org.dromara.common.dependency.business.domain.vo.StatisticVo;

/**
 * @author lee
 * @description
 */
public interface IReportService {

    /**
     * 首页数字统计
     *
     * @return
     */
    StatisticVo queryStatistic();

}
