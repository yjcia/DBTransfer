package com.yanjun.dbtransfer.service;

import com.yanjun.dbtransfer.annotation.DataSource;
import com.yanjun.dbtransfer.common.DBTransferAttribute;
import com.yanjun.dbtransfer.datasource.DBContextHolder;
import com.yanjun.dbtransfer.util.DBUtil;
import com.yanjun.dbtransfer.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YanJun on 2016/5/5.
 */

@Service
public class TransferDataByTableImpl implements TransferDataByTable {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Map<String, Object> doSearchFromSourceTable(String fromTableName) {
        Map<String, Object> returnMap = DBUtil.genObjDataFromTable(fromTableName, jdbcTemplate);
        return returnMap;
    }


    public void doInsertToTargetTable(Map<String, Object> dataMap, String toTableName) {
        try {
            if (PropertiesUtil.getValue(DBTransferAttribute.TRANSFER_MODE).
                    equals(DBTransferAttribute.TRANSFER_MODE_FULL)) {
                doDeleteAllFromTargetTable(toTableName);
                String[] columnNameArr = (String[]) dataMap.get(DBTransferAttribute.COLUMN_NAME);
                String insertColumnStr = DBUtil.getInsertColumnStr(columnNameArr);
                int[] insertColumnTypeArr = (int[]) dataMap.get(DBTransferAttribute.COLUMN_TYPE);
                Object[] insertValues;
                System.out.println(insertColumnStr);
                List<Object> dataList = (List) dataMap.get(DBTransferAttribute.DATA_LIST);
                for (Object cglibBeanObj : dataList) {
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    Method[] cglibMethods = cglibBeanObj.getClass().getDeclaredMethods();
                    for (Method method : cglibMethods) {
                        if (method.getName().startsWith("get")) {
                            Object methodReturnValue = method.invoke(cglibBeanObj, null);
                            System.out.println(method.getName() + "-->" + methodReturnValue);
                            paramMap.put(DBUtil.getInsertColumnByMethod(method.getName()),methodReturnValue);
                        }
                    }
                    insertValues = DBUtil.getSortedInsertDataValue(paramMap,columnNameArr);
                    String insertSql = "insert into " + toTableName + " ("
                            + insertColumnStr + " ) values ( " + DBUtil.getSqlParamSymbol(
                            Integer.parseInt(dataMap.get(DBTransferAttribute.COLUMN_COUNT).toString())) + " ) ";
                    jdbcTemplate.update(insertSql, insertValues, insertColumnTypeArr);
                }

            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public void doDeleteAllFromTargetTable(String tableName) {
        String deleteSql = "delete from " + tableName;
        jdbcTemplate.update(deleteSql);
    }


}
