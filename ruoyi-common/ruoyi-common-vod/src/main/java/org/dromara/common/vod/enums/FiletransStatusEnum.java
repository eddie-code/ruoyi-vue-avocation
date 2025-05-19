package org.dromara.common.vod.enums;

public enum FiletransStatusEnum {
    INIT("I", "初始"),
    SUBTITLE_INIT("SI", "待生成字幕"),
    SUBTITLE_PENDING("SP", "生成字幕中"),
    SUBTITLE_SUCCESS("SS", "生成字幕成功"),
    SUBTITLE_FAILURE("SF", "生成字幕失败"),
    DELETED("D", "失效");

    private String code;

    private String desc;

    FiletransStatusEnum(String code, String desc) {
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

    public static FiletransStatusEnum getByCode(String code){
        for(FiletransStatusEnum e: FiletransStatusEnum.values()){
            if(code.equals(e.getCode())){
                return e;
            }
        }
        return  null;
    }
}
