package com.ruoyi.framework.aspectj;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.ruoyi.common.annotation.RequestTiming;
import com.ruoyi.common.utils.StringUtils;

/**
 * 基于 {@link RequestTiming} 的方法耗时日志
 *
 * @author daming
 */
@Aspect
@Component
public class RequestTimingAspect
{
    private static final Logger timingLog = LoggerFactory.getLogger("com.dm.quiz.timing.Request");

    @Around("@annotation(requestTiming)")
    public Object around(ProceedingJoinPoint point, RequestTiming requestTiming) throws Throwable
    {
        long start = System.currentTimeMillis();
        try
        {
            return point.proceed();
        }
        finally
        {
            long cost = System.currentTimeMillis() - start;
            String signature = point.getSignature().toShortString();
            String tag = StringUtils.isNotEmpty(requestTiming.value()) ? requestTiming.value() : signature;
            String uri = resolveRequestUri();
            long slow = requestTiming.slowMs();
            String msg = "[接口耗时] {}ms | {} | URI={} | {}";
            if (slow > 0L && cost > slow)
            {
                timingLog.warn(msg, cost, tag, uri, signature);
            }
            else
            {
                timingLog.info(msg, cost, tag, uri, signature);
            }
        }
    }

    private static String resolveRequestUri()
    {
        try
        {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null)
            {
                return "-";
            }
            HttpServletRequest request = attrs.getRequest();
            if (request == null)
            {
                return "-";
            }
            return StringUtils.substring(request.getRequestURI(), 0, 255);
        }
        catch (Exception e)
        {
            return "-";
        }
    }
}
