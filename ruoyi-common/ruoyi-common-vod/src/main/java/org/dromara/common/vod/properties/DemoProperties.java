package org.dromara.common.vod.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 演示属性配置类
 *
 * <p>通过@ConfigurationProperties绑定配置文件中的"demo"前缀属性，
 * 用于集中管理演示模块的相关配置参数</p>
 *
 * @author lee
 */
@Data
@Component
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {

    /**
     * 演示名称
     * <p>对应需要上传的文件名</p>
     */
    private String name;

    /**
     * 音频资源标识
     * <p>可配置音频文件路径或资源ID</p>
     * <p>对应配置项：demo.audio</p>
     */
    private String audio;

    /**
     * 安全密钥
     * <p>正常来说是前端生成，但是示例，就后端自己随便写一个，免得做交互</p>
     */
    private String key;

    /**
     * 金额数值
     * <p>使用BigDecimal保证精确计算</p>
     * <p>对应配置项：demo.amount</p>
     */
    private BigDecimal amount;

    /**
     * 语言代码
     * <p>FiletransLangEnum枚举值</p>
     */
    private String lang;

    /**
     * 视频资源ID
     * <p>阿里云VOD视频点播的视频ID</p>
     * <p>对应配置项：demo.vid</p>
     */
    private String vid;

    /**
     * 获取音频时长
     */
    private String duration;
}

