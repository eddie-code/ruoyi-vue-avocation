package org.dromara.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.common.dependency.business.domain.vo.StatisticDateVo;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 查询最近30天的注册用户数量统计
     *
     * 该函数用于获取系统最近30天内每天的注册用户数量统计信息，返回的数据通常用于展示用户增长趋势图表或报表
     *
     * @return List<StatisticDateVo> 包含30天注册统计数据的列表，每个StatisticDateVo对象包含日期和对应日期的注册用户数
     *         返回的列表按日期升序排列，最早日期在前，最近日期在后
     *         如果某天没有注册用户，对应的统计对象中注册数应为0
     */
    List<StatisticDateVo> query30RegisterCount();

    /**
     * 查询近30天的文件传输数量统计
     *
     * @return List<StatisticDateVo> 包含日期和对应文件传输数量的统计结果列表
     */
    List<StatisticDateVo> query30FiletransCount();

    /**
     * 查询近30天的文件传输耗时统计（单位：秒）
     *
     * @return List<StatisticDateVo> 包含日期和对应文件传输耗时的统计结果列表
     */
    List<StatisticDateVo> query30FiletransSecond();

    /**
     * 查询近30天的订单数量统计
     *
     * @return List<StatisticDateVo> 包含日期和对应订单数量的统计结果列表
     */
    List<StatisticDateVo> query30OrderCount();

    /**
     * 查询近30天的订单金额统计
     *
     * @return List<StatisticDateVo> 包含日期和对应订单金额的统计结果列表
     */
    List<StatisticDateVo> query30OrderAmount();


}
