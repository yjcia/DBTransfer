package com.yanjun.dbtransfer.main;

import com.yanjun.dbtransfer.service.TransferDataByTable;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Created by YanJun on 2016/5/9.
 */
public class Start {
    public static void main(String args[]){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TransferDataByTable transferDataByTable = (TransferDataByTable)context.getBean("transferDataByTable");
        Map<String, Object> returnMap = transferDataByTable.doSearchFromSourceTable("t_user");
        transferDataByTable.doInsertToTargetTable(returnMap, "t_user");
    }
}
