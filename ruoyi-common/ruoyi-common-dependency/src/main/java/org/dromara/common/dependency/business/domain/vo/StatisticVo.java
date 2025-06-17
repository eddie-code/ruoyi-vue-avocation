package org.dromara.common.dependency.business.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StatisticVo {

    /**
     * 实时在线
     */
    private Integer onlineCount;

    /**
     * 注册人数
     */
    private Integer registerCount;

    /**
     * 语音识别数
     */
    private Integer filetransCount;

    /**
     * 语音识别时长，秒
     */
    private Integer filetransSecond;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 近30天注册人数
     */
    private List<StatisticDateVo> registerCountList;

    /**
     * 近30天语音识别数
     */
    private List<StatisticDateVo> filetransCountList;

    /**
     * 近30天语音识别时长，秒
     */
    private List<StatisticDateVo> filetransSecondList;

    /**
     * 近30天订单数
     */
    private List<StatisticDateVo> orderCountList;

    /**
     * 近30天订单金额
     */
    private List<StatisticDateVo> orderAmountList;

}
