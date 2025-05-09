package org.dromara.business.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.business.domain.TestBiz;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lee
 * @description
 */
@Data
@AutoMapper(target = TestBiz.class)
public class TestBizVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String mobile;

    private String password;

}
