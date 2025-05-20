package org.dromara.common.vod.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对应阿里云-智能语音交互的项目，一个项目对应一个语言
 */
@AllArgsConstructor
public enum FiletransLangEnum {

    LANG0("yNlqb7o08TopM1WZ", "普通话"),
    LANG1("DJPxHbuDkSFzJSny", "粤语简"),
    LANG2("PWZFkB61ZFS2ed6I", "粤语繁"),
    LANG3("YNCu1rEWVIHpfCHU", "英语"),
    LANG4("6iYWEDPysn3oLRVK", "中英混合"),
    LANG5("KfhevtFDmYBxXyFm", "日语"),
    LANG6("jCPeAOGbVSG26N5e", "泰语"),
    LANG7("4XH2B97Dpo6kmvVB", "韩语")
    ;

    @Getter
    private final String code;

    @Getter
    private final String desc;

}
