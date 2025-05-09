package org.dromara.business.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.business.domain.bo.TestBizBo;
import org.dromara.business.domain.vo.TestBizVo;
import org.dromara.business.service.ITestBizService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.core.validate.QueryGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lee
 * @description
 */
@SaIgnore // 跳过鉴权
@RestController
public class TestBizController extends BaseController {

    @Resource
    private ITestBizService service;

    @GetMapping("/hello")
    public R<String> hello() {
        return R.ok("Hello World!");
    }

    @GetMapping("/count")
    public R<Integer> count() {
        return R.ok(service.count());
    }

    @GetMapping("/query")
    public R<List<TestBizVo>> query(@RequestBody TestBizBo bo) {
        List<TestBizVo> list = service.queryList(bo);
        return R.ok(list);
    }

    /**
     * 查询测试单表列表
     */
//    @SaCheckPermission("demo:demo:list")
    @GetMapping("/list")
    public TableDataInfo<TestBizVo> list(@Validated(QueryGroup.class) TestBizBo bo, PageQuery pageQuery) {
        return service.queryPageList(bo, pageQuery);
    }

    /**
     * 自定义分页查询
     */
//    @SaCheckPermission("demo:demo:list")
    @GetMapping("/page")
    public TableDataInfo<TestBizVo> page(@Validated(QueryGroup.class) TestBizBo bo, PageQuery pageQuery) {
        return service.customPageList(bo, pageQuery);
    }

    //    @SaCheckPermission("demo:demo:query")
    @GetMapping("/{id}")
    public R<TestBizVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(service.queryById(id));
    }

    //    @SaCheckPermission("demo:demo:add")
//    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit(interval = 2, timeUnit = TimeUnit.SECONDS, message = "{repeat.submit.message}")
    @PostMapping()
    public R<Void> add(@RequestBody TestBizBo bo) {
        // 使用校验工具对标 @Validated(AddGroup.class) 注解
        // 用于在非 Controller 的地方校验对象
        ValidatorUtils.validate(bo, AddGroup.class);
        return toAjax(service.insertByBo(bo));
    }

    //    @SaCheckPermission("demo:demo:edit")
//    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TestBizBo bo) {
        return toAjax(service.updateByBo(bo));
    }

    //    @SaCheckPermission("demo:demo:remove")
//    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(service.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
