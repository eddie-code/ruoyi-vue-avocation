package org.dromara.common.dependency.business.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.dependency.business.domain.BizFiletransSubtitle;

import java.io.Serial;
import java.io.Serializable;



/**
 * 语音识别字幕视图对象 biz_filetrans_subtitle
 *
 * @author Eddie Lee
 * @date 2025-06-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizFiletransSubtitle.class)
public class BizFiletransSubtitleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 录音转换ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ExcelProperty(value = "录音转换ID")
    private Long filetransId;

    /**
     * 索引号
     */
    @ExcelProperty(value = "索引号")
    private Integer index;

    /**
     * 开始时间，毫秒
     */
    @ExcelProperty(value = "开始时间，毫秒")
    private Integer begin;

    /**
     * 结束时间，毫秒
     */
    @ExcelProperty(value = "结束时间，毫秒")
    private Integer end;

    /**
     * 字幕
     */
    @ExcelProperty(value = "字幕")
    private String text;


}
