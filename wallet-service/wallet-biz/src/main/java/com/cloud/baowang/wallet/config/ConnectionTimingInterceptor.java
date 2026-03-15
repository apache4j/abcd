package com.cloud.baowang.wallet.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;


@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, org.apache.ibatis.session.RowBounds.class, org.apache.ibatis.session.ResultHandler.class})
})
public class ConnectionTimingInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取 MappedStatement 和传入的参数
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        // 获取 SQL 语句
        String sqlCommandType = mappedStatement.getSqlCommandType().name();
        String sql = mappedStatement.getBoundSql(parameter).getSql();

        // 打印 SQL 语句和操作类型
        log.info("Executing SQL: {}" ,sql);
        log.info("SQL Command Type: {}", sqlCommandType);  // 增、删、改、查

        // 执行实际的 SQL 操作
        Object result = invocation.proceed();

        // 记录执行时间
        long endTime = System.currentTimeMillis();
        log.info("SQL Execution Time: {}ms" , (endTime - startTime));
        return result;
    }

    @Override
    public Object plugin(Object target) {
        // 使用Plugin.wrap包装目标对象，拦截目标对象的方法
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
