package org.dromara.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 语音识别对象 biz_filetrans
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_filetrans")
public class BizFiletrans extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 音频文件时长|秒
     */
    private Long second;

    /**
     * 金额|元，second*单价
     */
    private Long amount;

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

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;


}
