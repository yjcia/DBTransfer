package com.yanjun.dbtransfer.main;

import com.yanjun.dbtransfer.service.TransferDataByTable;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Created by YanJun on 2016/5/9.
 */
public class Start {
    public static void main(String args[]){
        String tableStrs = args[0];
        String[] tables = tableStrs.split(",");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TransferDataByTable transferDataByTable = (TransferDataByTable)context.getBean("transferDataByTable");
        for(String tableName:tables){
            Map<String, Object> returnMap = transferDataByTable.doSearchFromSourceTable(tableName);
            transferDataByTable.doInsertToTargetTable(returnMap, tableName);
        }
    }
}
