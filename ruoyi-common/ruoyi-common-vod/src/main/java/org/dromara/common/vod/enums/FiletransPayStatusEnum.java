package org.dromara.common.vod.enums;

public enum FiletransPayStatusEnum {
    I("I", "未支付"),
    P("P", "处理中"),
    S("S", "支付成功"),
    F("F", "支付失败");

    private String code;

    private String desc;

    FiletransPayStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
