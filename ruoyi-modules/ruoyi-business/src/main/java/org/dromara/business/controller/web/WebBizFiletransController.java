package org.dromara.business.controller.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.validate.QueryGroup;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.dromara.common.core.domain.R;
import org.dromara.common.dependency.business.domain.bo.BizFiletransBo;
import org.dromara.common.dependency.business.domain.bo.BizFiletransQueryBo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransVo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 语音识别
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
//@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/filetrans")
public class WebBizFiletransController extends BaseController {

    private final IBizFiletransService bizFiletransService;

    /**
     * 语音识别支付接口
     *
     * @param req 文件传输业务对象，包含支付所需参数
     * @return 统一响应结果，包含订单支付信息
     * @throws Exception 支付过程中可能抛出的异常
     */
    @SaCheckPermission("web:filetrans:pay")
    @PostMapping("/pay")
    public R<OrderInfoPayVo> pay(@Valid @RequestBody BizFiletransBo req) throws Exception {
        log.info("语音识别支付开始");
        OrderInfoPayVo orderInfoPayVo =  bizFiletransService.pay(req);
        log.info("语音识别支付结束");
        return R.ok(orderInfoPayVo);
    }

    /**
     * 分页查询文件传输记录
     *
     * @param bo 查询条件对象，需符合QueryGroup验证分组规则
     * @param pageQuery 分页查询参数
     * @return 分页数据结果，包含文件传输记录列表
     */
    @SaCheckPermission("web:filetrans:list")
    @GetMapping("/list")
    public TableDataInfo<BizFiletransVo> list(@Validated(QueryGroup.class) BizFiletransQueryBo bo, PageQuery pageQuery) {
        // 设置当前用户ID作为查询条件（会员端只能查询自己的记录）
        bo.setMemberId(LoginHelper.getUserId());
        return bizFiletransService.queryPageList(bo, pageQuery);
    }


}
