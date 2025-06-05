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

    @PostMapping("/pay")
    public R<OrderInfoPayVo> pay(@Valid @RequestBody BizFiletransBo req) throws Exception {
        log.info("语音识别支付开始");
        OrderInfoPayVo orderInfoPayVo =  bizFiletransService.pay(req);
        log.info("语音识别支付结束");
        return R.ok(orderInfoPayVo);
    }

    /**
     * 自定义分页查询
     */
    @SaCheckPermission("web:filetrans:list")
    @GetMapping("/list")
    public TableDataInfo<BizFiletransVo> list(@Validated(QueryGroup.class) BizFiletransQueryBo bo, PageQuery pageQuery) {
        bo.setMemberId(LoginHelper.getUserId()); // 会员端, 只查询自己的
        return bizFiletransService.queryPageList(bo, pageQuery);
    }

}
