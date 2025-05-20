package org.dromara.business.service;

import jakarta.validation.Valid;
import org.dromara.business.domain.bo.BizFiletransBo;

/**
 * 语音识别Service接口
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
public interface IBizFiletransService {

    Boolean pay(@Valid BizFiletransBo req) throws Exception;

}
