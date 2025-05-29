package org.dromara.common.dependency.business.api;

import jakarta.validation.Valid;
import org.dromara.common.dependency.business.domain.bo.BizFiletransBo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;

/**
 * 语音识别Service接口
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
public interface IBizFiletransService {

    OrderInfoPayVo pay(@Valid BizFiletransBo req) throws Exception;

    void afterPaySuccess(Long id);
}
