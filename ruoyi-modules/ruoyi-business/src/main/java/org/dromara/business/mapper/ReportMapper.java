package org.dromara.business.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface ReportMapper {

    /**
     * 查询注册用户数量
     *
     * @return 注册用户总数，返回值为Integer类型
     */
    Integer queryRegisterCount();

    /**
     * 查询文件传输次数
     *
     * @return 文件传输总次数，返回值为Integer类型
     */
    Integer queryFiletransCount();

    /**
     * 查询文件传输总耗时（秒）
     *
     * @return 文件传输消耗的总秒数，返回值为Integer类型
     */
    Integer queryFiletransSecond();

    /**
     * 查询订单数量
     *
     * @return 订单总数，返回值为Integer类型
     */
    Integer queryOrderCount();

    /**
     * 查询今日成功订单总金额
     *
     * @return
     */
    BigDecimal queryOrderAmount();


}
