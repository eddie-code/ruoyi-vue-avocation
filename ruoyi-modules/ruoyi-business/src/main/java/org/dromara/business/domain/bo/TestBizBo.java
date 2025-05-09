package org.dromara.business.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.business.domain.TestBiz;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lee
 * @description
 */
@Data
@AutoMapper(target = TestBiz.class, reverseConvertGenerate = false)
public class TestBizBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Integer id;

    @NotBlank(message = "mobile不能为空", groups = {AddGroup.class, EditGroup.class})
    private String mobile;

    @NotBlank(message = "password不能为空", groups = {AddGroup.class, EditGroup.class})
    private String password;

}
