package org.dromara.business.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.TestBiz;
import org.dromara.business.domain.bo.TestBizBo;
import org.dromara.business.domain.vo.TestBizVo;
import org.dromara.business.mapper.TestBizMapper;
import org.dromara.business.service.ITestBizService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class ITestBizBizServiceImpl implements ITestBizService {

    @Resource
    private TestBizMapper baseMapper;

    @Override
    public int count() {
        return Math.toIntExact(baseMapper.selectCount(new LambdaQueryWrapper<TestBiz>()));
    }

    @Override
    public List<TestBizVo> queryList(TestBizBo bo) {
        return baseMapper.selectVoList(buildQueryWrapper(bo));
    }

    @Override
    public TestBizVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<TestBizVo> queryPageList(TestBizBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TestBiz> lqw = buildQueryWrapper(bo);
        Page<TestBizVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 自定义分页查询
     */
    @Override
    public TableDataInfo<TestBizVo> customPageList(TestBizBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TestBiz> lqw = buildQueryWrapper(bo);
        Page<TestBizVo> result = baseMapper.customPageList(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public Boolean insertByBo(TestBizBo bo) {
        TestBiz add = MapstructUtils.convert(bo, TestBiz.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            assert add != null;
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(TestBizBo bo) {
        TestBiz update = MapstructUtils.convert(bo, TestBiz.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
            List<TestBiz> list = baseMapper.selectByIds(ids);
            if (list.size() != ids.size()) {
                throw new ServiceException("您没有删除权限!");
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(TestBiz entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 构建查询包装器
     * 根据传入的业务对象（TestBo）构建一个LambdaQueryWrapper对象，用于后续的查询操作
     * 此方法主要目的是根据业务对象的不同属性值，动态生成SQL查询条件
     *
     * @param bo 业务对象（TestBo），包含查询所需的各种条件字段
     * @return LambdaQueryWrapper<Test> 查询条件包装器，用于执行后续的数据库查询操作
     */
    private LambdaQueryWrapper<TestBiz> buildQueryWrapper(TestBizBo bo) {
        // 创建一个LambdaQueryWrapper对象，用于封装查询条件
        LambdaQueryWrapper<TestBiz> lqw = Wrappers.lambdaQuery();

        // 根据业务对象的ID属性值，生成查询条件
        // 如果ID不为空，则添加等于条件到查询器中
        lqw.eq(ObjectUtil.isNotNull(bo.getId()), TestBiz::getId, bo.getId());

        // 根据业务对象的手机号属性值，生成查询条件
        // 如果手机号不为空且不只包含空格，则添加等于条件到查询器中
        lqw.eq(StringUtils.isNotBlank(bo.getMobile()), TestBiz::getMobile, bo.getMobile());

        // 根据业务对象的密码属性值，生成查询条件
        // 如果密码不为空且不只包含空格，则添加等于条件到查询器中
        lqw.eq(StringUtils.isNotBlank(bo.getPassword()), TestBiz::getPassword, bo.getPassword());

        // 添加排序条件，根据ID字段升序排序查询结果
        lqw.orderByAsc(TestBiz::getId);

        // 返回构建好的查询条件包装器
        return lqw;
    }
}
