package org.dromara.business.controller.web;


import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.common.core.validate.QueryGroup;
import org.dromara.common.dependency.business.api.IBizFiletransSubtitleService;
import org.dromara.common.dependency.business.domain.bo.BizFiletransSubtitleBo;
import org.dromara.common.dependency.business.domain.bo.GenSubtitleBo;
import org.dromara.common.dependency.business.domain.bo.GenTextBo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransSubtitleVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
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
     *
     * @param bo 文件字幕业务对象，包含查询条件，需经过QueryGroup分组验证
     * @param pageQuery 分页查询参数对象
     * @return TableDataInfo<BizFiletransSubtitleVo> 分页查询结果，包含分页信息和数据列表
     */
    @SaCheckPermission("web:filetransSubtitle:list")
    @GetMapping("/list")
    public TableDataInfo<BizFiletransSubtitleVo> list(@Validated(QueryGroup.class) BizFiletransSubtitleBo bo, PageQuery pageQuery) {
        return bizFiletransSubtitleService.queryPageList(bo, pageQuery);
    }

    /**
     * 生成字幕文件
     *
     * @param bo 生成字幕业务对象，需经过QueryGroup分组验证
     * @return R<String> 包含操作结果和生成的字幕文件URL
     *         成功时返回操作成功消息和URL，失败时返回错误信息
     */
    @SaCheckPermission("web:filetransSubtitle:genSubtitle")
    @GetMapping("/genSubtitle")
    public R<String> genSubtitle(@Validated(QueryGroup.class) GenSubtitleBo bo) {
        String url = bizFiletransSubtitleService.genSubtitle(bo);
        // 如果是实体返回值, 不是String类型, 就返回data, 不是msg
        return R.ok("操作成功", url);
    }

    /**
     * 生成文本文件
     *
     * @param bo 生成文本业务对象，需经过QueryGroup分组验证
     * @return R<String> 包含操作结果和生成的文本文件URL
     *         成功时返回操作成功消息和URL，失败时返回错误信息
     */
    @SaCheckPermission("web:filetransSubtitle:genText")
    @GetMapping("/genText")
    public R<String> genText(@Validated(QueryGroup.class) GenTextBo bo) {
        String url = bizFiletransSubtitleService.genText(bo);
        // 如果是实体返回值, 不是String类型, 就返回data, 不是msg
        return R.ok("操作成功", url);
    }


}
