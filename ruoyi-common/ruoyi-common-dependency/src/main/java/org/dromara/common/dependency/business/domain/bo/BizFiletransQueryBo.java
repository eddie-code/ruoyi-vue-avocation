package org.dromara.common.dependency.business.domain.bo;

import lombok.Data;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * @author lee
 * @description
 */
@Data
public class BizFiletransQueryBo extends BaseEntity {

    private Long memberId;

    private String lang;

    private String status;

    private String name;

}
