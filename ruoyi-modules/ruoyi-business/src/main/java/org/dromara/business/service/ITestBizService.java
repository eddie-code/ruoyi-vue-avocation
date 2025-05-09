package org.dromara.business.service;

import org.dromara.business.domain.bo.TestBizBo;
import org.dromara.business.domain.vo.TestBizVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author lee
 * @description
 */
public interface ITestBizService {

    int count();

    /**
     * 查询列表
     */
    List<TestBizVo> queryList(TestBizBo bo);

    /**
     * 根据ID查询实体信息
     *
     * @param id 实体的唯一标识符
     * @return 返回查询到的实体对象，如果没有找到，则返回null
     */
    TestBizVo queryById(Long id);

    /**
     * 查询分页列表信息
     *
     * @param bo 查询条件对象，用于指定查询条件
     * @param pageQuery 分页查询对象，用于指定分页参数如页码和页面大小
     * @return 返回一个包含分页数据的TableDataInfo对象，包括数据列表和分页信息
     */
    TableDataInfo<TestBizVo> queryPageList(TestBizBo bo, PageQuery pageQuery);

    /**
     * 自定义查询分页列表信息
     *
     * @param bo 查询条件对象，用于指定查询条件
     * @param pageQuery 分页查询对象，用于指定分页参数如页码和页面大小
     * @return 返回一个包含分页数据的TableDataInfo对象，包括数据列表和分页信息
     * 此方法可能包含一些自定义的查询逻辑或数据处理，具体实现细节请参考方法体
     */
    TableDataInfo<TestBizVo> customPageList(TestBizBo bo, PageQuery pageQuery);

    /**
     * 根据新增业务对象插入测试单表
     *
     * @param bo 测试单表新增业务对象
     * @return
     */
    Boolean insertByBo(TestBizBo bo);

    /**
     * 根据编辑业务对象修改测试单表
     *
     * @param bo 测试单表编辑业务对象
     * @return
     */
    Boolean updateByBo(TestBizBo bo);

    /**
     * 校验并删除数据
     *
     * @param ids     主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     * @return
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
