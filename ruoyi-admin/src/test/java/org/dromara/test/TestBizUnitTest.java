package org.dromara.test;

import org.dromara.business.domain.TestBiz;
import org.dromara.business.domain.bo.TestBizBo;
import org.dromara.business.domain.vo.TestBizVo;
import org.dromara.common.core.utils.MapstructUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lee
 * @description
 */
@SpringBootTest
@DisplayName("")
public class TestBizUnitTest {

//    @Autowired
//    private Converter converter;

    @DisplayName("测试 @AutoMapper 注解")
    @Test
    public void test() {
        TestBiz testBiz = new TestBiz();
        testBiz.setId(10);
        testBiz.setMobile("13700137000");
        testBiz.setPassword("123456");

        TestBizVo testBizVo = MapstructUtils.convert(testBiz, TestBizVo.class);
        System.out.println(testBizVo);
        assert testBiz.getId() == testBizVo.getId();
        assert testBiz.getMobile().equals(testBizVo.getMobile());
        assert testBiz.getPassword().equals(testBizVo.getPassword());

        System.out.println("========================================================");

        TestBiz newTestBiz = MapstructUtils.convert(testBizVo, TestBiz.class);
        System.out.println(newTestBiz);
        assert testBiz.getId() == newTestBiz.getId();
        assert testBiz.getMobile().equals(newTestBiz.getMobile());
        assert testBiz.getPassword().equals(newTestBiz.getPassword());

        System.out.println("========================================================");

        // TestBizBo.class里面的注解 @AutoMapper(target = TestBiz.class, reverseConvertGenerate = false)
        // 说明 没有生成从 TestBiz 到 TestBizBo 的映射转换器，导致调用 MapstructUtils.convert(testBiz, TestBizBo.class) 时报错
        // 当 reverseConvertGenerate = false, TestBiz → TestBizBo（反向）是不能转换的
        TestBizBo testBizBo = MapstructUtils.convert(testBiz, TestBizBo.class);
        System.out.println(testBizBo);
        assert testBiz.getId() == testBizBo.getId();
        assert testBiz.getMobile().equals(testBizBo.getMobile());
        assert testBiz.getPassword().equals(testBizBo.getPassword());

        TestBiz newTestBizBo = MapstructUtils.convert(testBizBo, TestBiz.class);
        System.out.println(newTestBizBo);
        assert testBiz.getId() == newTestBizBo.getId();
        assert testBiz.getMobile().equals(newTestBizBo.getMobile());
        assert testBiz.getPassword().equals(newTestBizBo.getPassword());

        System.out.println("========================================================");

        TestBizBo testBizBoOld = new TestBizBo();
        testBizBoOld.setId(10);
        testBizBoOld.setMobile("13700137000");
        testBizBoOld.setPassword("123456");
        TestBiz newTestBiz2 = MapstructUtils.convert(testBizBoOld, TestBiz.class);
        System.out.println(newTestBiz2);
        assert testBiz.getId() == newTestBiz2.getId();
        assert testBiz.getMobile().equals(newTestBiz2.getMobile());
        assert testBiz.getPassword().equals(newTestBiz2.getPassword());

    }

}
