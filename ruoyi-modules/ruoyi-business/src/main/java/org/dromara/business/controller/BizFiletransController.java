package org.dromara.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.business.domain.bo.BizFiletransBo;
import org.dromara.business.domain.vo.BizFiletransVo;
import org.dromara.business.service.IBizFiletransService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 语音识别
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/audio/filetrans")
public class BizFiletransController extends BaseController {

    private final IBizFiletransService bizFiletransService;

    /**
     * 查询语音识别列表
     */
    @SaCheckPermission("audio:filetrans:list")
    @GetMapping("/list")
    public TableDataInfo<BizFiletransVo> list(BizFiletransBo bo, PageQuery pageQuery) {
        return bizFiletransService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出语音识别列表
     */
    @SaCheckPermission("audio:filetrans:export")
    @Log(title = "语音识别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BizFiletransBo bo, HttpServletResponse response) {
        List<BizFiletransVo> list = bizFiletransService.queryList(bo);
        ExcelUtil.exportExcel(list, "语音识别", BizFiletransVo.class, response);
    }

    /**
     * 获取语音识别详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("audio:filetrans:query")
    @GetMapping("/{id}")
    public R<BizFiletransVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(bizFiletransService.queryById(id));
    }

    /**
     * 新增语音识别
     */
    @SaCheckPermission("audio:filetrans:add")
    @Log(title = "语音识别", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BizFiletransBo bo) {
        return toAjax(bizFiletransService.insertByBo(bo));
    }

    /**
     * 修改语音识别
     */
    @SaCheckPermission("audio:filetrans:edit")
    @Log(title = "语音识别", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BizFiletransBo bo) {
        return toAjax(bizFiletransService.updateByBo(bo));
    }

    /**
     * 删除语音识别
     *
     * @param ids 主键串
     */
    @SaCheckPermission("audio:filetrans:remove")
    @Log(title = "语音识别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(bizFiletransService.deleteWithValidByIds(List.of(ids), true));
    }
}
