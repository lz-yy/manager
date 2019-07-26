package com.voucher.manage2.aop.interceptor;

import com.voucher.manage2.dto.SysUserDTO;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.redis.JedisUtil0;
import com.voucher.manage2.service.UserService;
import com.voucher.manage2.utils.CommonUtils;
import com.voucher.manage2.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author lz
 * @description
 * @date 2019/6/11
 */
@Slf4j
@Aspect
@Component
public class LoginInterceptor {
    @Autowired
    private UserService userService;

    //验证controller
    @Pointcut("execution(public * com.voucher.manage2.controller..*.*(..))")
    public void controllerPointcut() {
        //|| execution(public * com.voucher.manage.controller..*.*(..))
    }

    // 登录登出功能不需要验证
    @Pointcut("execution(public * com.voucher.manage2.controller.UserController.login(..))||execution(public * com.voucher.manage2.controller.UserController.logout(..))")
    public void rootPointcut() {
    }

    // 公司注册,获取权限功能不需要验证
    @Pointcut("execution(public * com.voucher.manage2.controller.CompanyController.saveCompany(..))")
    public void registerPointcut() {
    }

    @Pointcut("controllerPointcut()&&(!rootPointcut())&&(!registerPointcut())")
    public void projectPointcut() {
    }

    @Around("projectPointcut()")
    public Object sessionTimeOutAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Object result;
        SysUserDTO userDTO;
        Class<?> controller = pjp.getTarget().getClass();
        Method proxyMethod = ((MethodSignature) pjp.getSignature()).getMethod();
        //log.debug("----------------执行方法-----------------");
        //log.debug("类名：" + controller.getSimpleName() + " 方法名：" + proxyMethod.getName());
        //类上面controller的值 获取不到@Controller
        //String corName = controller.getAnnotation(Controller.class).value();

        //真实的方法上才有注解
        //Method realMethod = pjp.getTarget().getClass().getDeclaredMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        //String name = realMethod.getAnnotation(RequestMapping.class).name();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String url = request.getServletPath();
        String tokenId;
        Object[] obj = pjp.getArgs();
        tokenId = request.getHeader("X-Token");
        //String requestURI = request.getRequestURI();
        try {
            if (ObjectUtils.isNotEmpty(tokenId)) {
                // 查看redis是否还有token1
                // 获取 user
                userDTO = JedisUtil0.getObject(tokenId);
                if (userDTO == null) {
                    //log.warn("拦截会话超时请求,tokenId:{}, URL:{}", tokenId, url);
                    throw BaseException.getDefault("登录已过期,重新登录!");
                }
                CommonUtils.setUser(userDTO);
                //T 判断是否有权限访问接口
                if (userService.hasPermission(userDTO, url)) {
                    result = pjp.proceed(obj);
                } else {
                    //log.warn("拦截没有权限的用户:{}访问URL:{}", userDTO, url);
                    throw BaseException.getDefault("没有权限");
                }

            } else {
                //log.warn("非法访问已被拦截URL:{}", url);
                throw BaseException.getDefault("非法访问");
            }
        } catch (UndeclaredThrowableException e) {
            throw BaseException.getDefault(e);
        }
        Long lastFreshTime = userDTO.getLastFreshTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastFreshTime > 3600000L) {
            //距离上次刷新token超过60min(1000*60*60) 则刷新token时间
            userDTO.setLastFreshTime(currentTimeMillis);
            JedisUtil0.setUserDTO(tokenId, userDTO);
        }

        return result;
    }
}
