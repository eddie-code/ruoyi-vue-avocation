package org.dromara.business.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.business.domain.BizFiletrans;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 语音识别业务对象 biz_filetrans
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizFiletrans.class, reverseConvertGenerate = false)
public class BizFiletransBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long memberId;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 音频文件时长|秒
     */
    private Integer second;

    /**
     * 金额|元，second*单价
     */
    private BigDecimal amount;

    /**
     * 文件链接
     */
    private String audio;

    /**
     * 文件签名md5
     */
    private String fileSign;

    /**
     * 支付状态|枚举[FiletransPayStatusEnum];
     */
    private String payStatus;

    /**
     * 识别状态|枚举[FiletransStatusEnum];
     */
    private String status;

    /**
     * 音频语言|枚举[FiletransLangEnum]
     */
    @NotBlank(message = "音频语言|枚举[FiletransLangEnum]不能为空", groups = { AddGroup.class, EditGroup.class })
    private String lang;

    /**
     * VOD|videoId
     */
    private String vod;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 转换状态码
     */
    private Long transStatusCode;

    /**
     * 转换状态说明
     */
    private String transStatusText;

    /**
     * 转换时间|开始转换的时间
     */
    private Date transTime;

    /**
     * 完成时间|录音文件识别完成的时间
     */
    private Date solveTime;


}
