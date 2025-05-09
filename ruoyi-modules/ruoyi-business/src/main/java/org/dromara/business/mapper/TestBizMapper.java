package org.dromara.business.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.business.domain.TestBiz;
import org.dromara.business.domain.vo.TestBizVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * @author lee
 * @description
 */
public interface TestBizMapper extends BaseMapperPlus<TestBiz, TestBizVo> {

//    @DataPermission({
//        @DataColumn(key = "deptName", value = "dept_id"),
//        @DataColumn(key = "userName", value = "user_id")
//    })
    Page<TestBizVo> customPageList(@Param("page") Page<TestBiz> page, @Param("ew") Wrapper<TestBiz> wrapper);

    @Override
//    @DataPermission({
//        @DataColumn(key = "deptName", value = "dept_id"),
//        @DataColumn(key = "userName", value = "user_id")
//    })
    default <P extends IPage<TestBizVo>> P selectVoPage(IPage<TestBiz> page, Wrapper<TestBiz> wrapper) {
        return selectVoPage(page, wrapper, this.currentVoClass());
    }

}
