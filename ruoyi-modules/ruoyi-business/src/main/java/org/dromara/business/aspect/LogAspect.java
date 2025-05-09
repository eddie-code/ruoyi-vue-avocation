package org.dromara.business.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lee
 * @description AOP打印请求参数和返回结果
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 定义一个切点
     */
    @Pointcut("execution(public * org.dromara..*Controller.*(..))")
    public void pointcut() {
        //...
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
//        log.info("=== 前置通知 ===");
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("=== 环绕通知开始 ===");
        long startTime = System.currentTimeMillis();
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = proceedingJoinPoint.getSignature();
        String name = signature.getName();

        // 打印请求信息
        log.info("请求地址: {} {}", request.getRequestURL().toString(), request.getMethod());
        log.info("类名方法: {}.{}", signature.getDeclaringTypeName(), name);
        log.info("远程地址: {}", request.getRemoteAddr());

        // 打印请求参数
        Object[] args = proceedingJoinPoint.getArgs();
        // LOG.info("请求参数: {}", JSONObject.toJSONString(args));

        // 排除特殊类型的参数，如文件类型
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest
                || args[i] instanceof ServletResponse
                || args[i] instanceof MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }
        // 排除字段，敏感字段或太长的字段不显示：身份证、手机号、邮箱、密码等
        String[] excludeProperties = {"cvv2", "idCard"};
        // 创建PropertyPreFilters对象，用于过滤属性
        PropertyPreFilters filters = new PropertyPreFilters();
        // 添加一个自定义的属性预过滤器，用于指定需要排除的属性
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        // 设置需要排除的属性，这些属性不会被日志记录
        excludefilter.addExcludes(excludeProperties);
        // 使用过滤器排除指定属性后，记录请求参数的日志
        log.info("请求参数: {}", JSONObject.toJSONString(arguments, excludefilter));
        // 调用当前连接点的方法，并获取结果
        Object result = proceedingJoinPoint.proceed();
        // 排除字段，敏感字段或太长的字段不显示：身份证、手机号、邮箱、密码等
        log.info("返回结果: {}", JSONObject.toJSONString(result, excludefilter));
        log.info("------------- 环绕通知结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
        return result;
    }

    @After("pointcut()")
    public void doAfter(JoinPoint joinPoint) {
//        log.info("=== 后置通知 ===");
    }

}
