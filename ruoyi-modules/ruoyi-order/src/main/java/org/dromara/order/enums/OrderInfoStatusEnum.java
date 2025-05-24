package org.dromara.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderInfoStatusEnum {

    I("I", "未支付"),
    P("P", "处理中"),
    S("S", "支付成功"),
    F("F", "支付失败");

    @Getter
    private String code;

    @Getter
    private String desc;

}
