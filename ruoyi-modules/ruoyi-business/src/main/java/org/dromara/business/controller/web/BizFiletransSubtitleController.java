package org.dromara.business.controller.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.common.core.validate.QueryGroup;
import org.dromara.common.dependency.business.api.IBizFiletransSubtitleService;
import org.dromara.common.dependency.business.domain.bo.BizFiletransQueryBo;
import org.dromara.common.dependency.business.domain.bo.BizFiletransSubtitleBo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransSubtitleVo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransVo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.mybatis.core.page.TableDataInfo;
/**
 * 语音识别字幕
 *
 * @author Eddie Lee
 * @date 2025-06-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/filetransSubtitle")
public class BizFiletransSubtitleController extends BaseController {

    private final IBizFiletransSubtitleService bizFiletransSubtitleService;

    /**
     * 自定义分页查询
     */
    @SaCheckPermission("web:filetransSubtitle:list")
    @GetMapping("/list")
    public TableDataInfo<BizFiletransSubtitleVo> list(@Validated(QueryGroup.class) BizFiletransSubtitleBo bo, PageQuery pageQuery) {
        return bizFiletransSubtitleService.queryPageList(bo, pageQuery);
    }

}
