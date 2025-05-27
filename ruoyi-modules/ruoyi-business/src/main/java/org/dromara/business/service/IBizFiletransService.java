package org.dromara.business.service;

import jakarta.validation.Valid;
import org.dromara.business.domain.bo.BizFiletransBo;
import org.dromara.order.domain.vo.OrderInfoPayVo;

/**
 * 语音识别Service接口
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
public interface IBizFiletransService {

    OrderInfoPayVo pay(@Valid BizFiletransBo req) throws Exception;

}
