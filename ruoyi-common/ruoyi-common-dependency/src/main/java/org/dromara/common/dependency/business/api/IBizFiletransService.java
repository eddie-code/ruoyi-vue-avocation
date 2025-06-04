package org.dromara.common.dependency.business.api;

import jakarta.validation.Valid;
import com.alibaba.fastjson.JSONObject;
import org.dromara.common.dependency.business.domain.bo.BizFiletransBo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;

/**
 * 文件传输服务接口
 * 提供与文件传输相关的业务操作，如支付、支付成功后处理、传输结果处理等
 */
public interface IBizFiletransService {

    /**
     * 处理文件传输的支付请求
     *
     * @param req 经过验证的文件传输业务对象，包含支付所需的信息
     * @return 返回支付信息对象，包含支付结果和相关数据
     * @throws Exception 如果支付处理过程中发生错误，则抛出异常
     */
    OrderInfoPayVo pay(@Valid BizFiletransBo req) throws Exception;

    /**
     * 支付成功后的处理
     *
     * @param id 支付记录的标识符，用于更新支付状态和进行后续操作
     */
    void afterPaySuccess(Long id);

    /**
     * 文件传输完成后的处理
     *
     * @param jsonResult 包含文件传输结果的JSON对象，用于更新传输状态和结果
     */
    void afterTrans(JSONObject jsonResult);
}
