package org.dromara.common.vod.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FiletransLangEnum {

    LANG0("5jYCe7Ls9ziaMWNc", "普通话"),
    LANG1("U9qIhvyLaf5kN6oX", "英语"),
    LANG2("vIHpSICVL6C34sg1", "中英混合"),
    LANG3("AFwLebxf7f3ZTRDV", "日语"),
    LANG4("O4NiETbfRr4OOVzQ", "泰语"),
    LANG5("1k2bzW12JGl1jg0X", "韩语")
    ;

    @Getter
    private final String code;

    @Getter
    private final String desc;

}
