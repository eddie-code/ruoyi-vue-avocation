package org.dromara.common.dependency.business.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 语音识别字幕对象 biz_filetrans_subtitle
 *
 * @author Eddie Lee
 * @date 2025-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_filetrans_subtitle")
public class BizFiletransSubtitle extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 录音转换ID
     */
    private Long filetransId;

    /**
     * 索引号
     */
    @TableField("`index`")  // 使用反引号转义MySQL关键字
    private Integer index;

    /**
     * 开始时间，毫秒
     */
    private Integer begin;

    /**
     * 结束时间，毫秒
     */
    private Integer end;

    /**
     * 字幕
     */
    private String text;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;


}
