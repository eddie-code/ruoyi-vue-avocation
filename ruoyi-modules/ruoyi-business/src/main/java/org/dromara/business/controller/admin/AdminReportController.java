package org.dromara.business.controller.admin;

import lombok.RequiredArgsConstructor;
import org.dromara.business.service.IReportService;
import org.dromara.common.core.domain.R;
import org.dromara.common.dependency.business.domain.vo.StatisticVo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lee
 * @description
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/report")
public class AdminReportController extends BaseController {

    private final IReportService reportService;

    /**
     * 查询统计信息
     *
     * @return 包含统计信息的响应对象，其中data为StatisticVo类型，包含具体的统计信息
     */
    @GetMapping("/queryStatistic")
    public R<StatisticVo> queryStatistic() {
        // 调用reportService查询统计信息并封装返回结果
        StatisticVo statisticVo = reportService.queryStatistic();
        return R.ok(statisticVo);
    }


}
