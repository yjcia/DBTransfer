package com.yanjun.dbtransfer.aop;

import com.yanjun.dbtransfer.annotation.DataSource;
import com.yanjun.dbtransfer.common.DBTransferAttribute;
import com.yanjun.dbtransfer.datasource.DBContextHolder;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import java.lang.reflect.Method;


public class DataSourceAspect implements MethodBeforeAdvice,AfterReturningAdvice
{  

    public void afterReturning(Object returnValue, Method method,
            Object[] args, Object target) throws Throwable {
        DBContextHolder.clearDBType();
    }
  

    public void before(Method method, Object[] args, Object target)  
            throws Throwable {
        if (method.isAnnotationPresent(DataSource.class))
        {  
            DataSource dataSource = method.getAnnotation(DataSource.class);
            DBContextHolder.setDBType(dataSource.type());
        }
          
    }
}