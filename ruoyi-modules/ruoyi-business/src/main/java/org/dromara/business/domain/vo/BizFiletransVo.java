package org.dromara.business.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.business.domain.BizFiletrans;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 语音识别视图对象 biz_filetrans
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizFiletrans.class)
public class BizFiletransVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 会员ID
     */
    @ExcelProperty(value = "会员ID")
    private Long memberId;

    /**
     * 文件名称
     */
    @ExcelProperty(value = "文件名称")
    private String name;

    /**
     * 音频文件时长|秒
     */
    @ExcelProperty(value = "音频文件时长|秒")
    private Integer second;

    /**
     * 金额|元，second*单价
     */
    @ExcelProperty(value = "金额|元，second*单价")
    private BigDecimal amount;

    /**
     * 文件链接
     */
    @ExcelProperty(value = "文件链接")
    private String audio;

    /**
     * 文件签名md5
     */
    @ExcelProperty(value = "文件签名md5")
    private String fileSign;

    /**
     * 支付状态|枚举[FiletransPayStatusEnum];
     */
    @ExcelProperty(value = "支付状态|枚举[FiletransPayStatusEnum];")
    private String payStatus;

    /**
     * 识别状态|枚举[FiletransStatusEnum];
     */
    @ExcelProperty(value = "识别状态|枚举[FiletransStatusEnum];")
    private String status;

    /**
     * 音频语言|枚举[FiletransLangEnum]
     */
    @ExcelProperty(value = "音频语言|枚举[FiletransLangEnum]")
    private String lang;

    /**
     * VOD|videoId
     */
    @ExcelProperty(value = "VOD|videoId")
    private String vod;

    /**
     * 任务ID
     */
    @ExcelProperty(value = "任务ID")
    private String taskId;

    /**
     * 转换状态码
     */
    @ExcelProperty(value = "转换状态码")
    private Long transStatusCode;

    /**
     * 转换状态说明
     */
    @ExcelProperty(value = "转换状态说明")
    private String transStatusText;

    /**
     * 转换时间|开始转换的时间
     */
    @ExcelProperty(value = "转换时间|开始转换的时间")
    private Date transTime;

    /**
     * 完成时间|录音文件识别完成的时间
     */
    @ExcelProperty(value = "完成时间|录音文件识别完成的时间")
    private Date solveTime;


}
