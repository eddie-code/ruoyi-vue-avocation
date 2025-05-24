package org.dromara.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderInfoOrderTypeEnum {

    FILETRANS_PAY("1", "语音识别单次付费"),
    ;

    @Getter
    private String code;

    @Getter
    private String desc;

}
