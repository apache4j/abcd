package com.cloud.baowang.common.redis.aspect;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Order(Integer.MAX_VALUE - 1)
@Aspect
@Component
public class DistributedLockAspect {

    @Resource
    private RedissonClient redisson;

    private static final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        if (StrUtil.isBlank(distributedLock.name()) && StrUtil.isBlank(distributedLock.unique())) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        // 解析锁的名称
        String lockUniqueId = resolveLockName(distributedLock.unique(), joinPoint);
        String prefix = distributedLock.name();
        String lockName = prefix + lockUniqueId;
        RLock lock = distributedLock.fair() ? redisson.getFairLock(lockName) : redisson.getLock(lockName);

        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();

        boolean isLocked;

        try {
            isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (isLocked) {
                return joinPoint.proceed();
            } else {
                log.error("获取锁失败,锁名称:{}", lockName);
                throw new BaowangDefaultException(ResultCode.ONE_KEY_RECYCLING);
            }
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("锁释放,锁名称:{}", lockName);
            }
        }
    }

    private String resolveLockName(String lockName, ProceedingJoinPoint joinPoint) {
        if (StrUtil.isNotBlank(lockName)) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setRootObject(joinPoint.getTarget());

            // 设置与方法参数一致的变量
            Object[] args = joinPoint.getArgs();
            String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }

            // 解析 SpEL 表达式
            return parser.parseExpression(lockName).getValue(context, String.class);
        }
        return Strings.EMPTY;
    }
}

