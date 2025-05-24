package org.dromara.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderInfoChannelEnum {

    ALIPAY("A", "支付宝"),
    WXPAY("W", "微信");

    @Getter
    private String code;

    @Getter
    private String desc;

}
