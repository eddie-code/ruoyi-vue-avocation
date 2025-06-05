package org.dromara.common.dependency.business.domain.bo;

import org.dromara.common.dependency.business.domain.BizFiletransSubtitle;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 语音识别字幕业务对象 biz_filetrans_subtitle
 *
 * @author Eddie Lee
 * @date 2025-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizFiletransSubtitle.class, reverseConvertGenerate = false)
public class BizFiletransSubtitleBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 录音转换ID
     */
    @NotNull(message = "录音转换ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long filetransId;

    /**
     * 索引号
     */
    @NotNull(message = "索引号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long index;

    /**
     * 开始时间，毫秒
     */
    @NotNull(message = "开始时间，毫秒不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long begin;

    /**
     * 结束时间，毫秒
     */
    @NotNull(message = "结束时间，毫秒不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long end;

    /**
     * 字幕
     */
    private String text;


}
