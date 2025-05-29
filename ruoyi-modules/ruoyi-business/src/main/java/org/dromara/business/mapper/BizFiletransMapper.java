package org.dromara.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.common.dependency.business.domain.BizFiletrans;
import org.dromara.common.dependency.business.domain.vo.BizFiletransVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 语音识别Mapper接口
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@Mapper
public interface BizFiletransMapper extends BaseMapperPlus<BizFiletrans, BizFiletransVo> {

}
