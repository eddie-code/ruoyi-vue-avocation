package org.dromara.common.dependency.order.domain.vo;

import lombok.Data;

/**
 * @author lee
 * @description 提供，前端返回值
 */
@Data
public class OrderInfoPayVo {

    private String orderNo;

    /**
     * 调用支付渠道的返回值
     */
    private String channelResult;
}
