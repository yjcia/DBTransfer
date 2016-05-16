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
                String[] columnNameArr = (String[]) dataMap.get(DBTransferAttribute.COLUMN_NAME);
                int[] insertColumnTypeArr = (int[]) dataMap.get(DBTransferAttribute.COLUMN_TYPE);
                if(!DBUtil.getTableName(jdbcTemplate,toTableName)
                        && Boolean.parseBoolean(PropertiesUtil.getValue(DBTransferAttribute.NEED_CREATE_TABLE))){
                    doCreateTargetTable(toTableName);
                }
                doDeleteAllFromTargetTable(toTableName);
                String insertColumnStr = DBUtil.getInsertColumnStr(columnNameArr);
                Object[] insertValues;
                //System.out.println(insertColumnStr);
                List<Object> dataList = (List) dataMap.get(DBTransferAttribute.DATA_LIST);
                for (Object cglibBeanObj : dataList) {
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    Method[] cglibMethods = cglibBeanObj.getClass().getDeclaredMethods();
                    for (Method method : cglibMethods) {
                        if (method.getName().startsWith("get")) {
                            Object methodReturnValue = method.invoke(cglibBeanObj, null);
                            paramMap.put(DBUtil.getInsertColumnByMethod(method.getName()),methodReturnValue);
                        }
                    }
                    insertValues = DBUtil.getSortedInsertDataValue(paramMap,columnNameArr);
                    String insertSql = "insert into " + toTableName + " ("
                            + insertColumnStr + " ) values ( " + DBUtil.getSqlParamSymbol(
                            Integer.parseInt(dataMap.get(DBTransferAttribute.COLUMN_COUNT).toString())) + " ) ";
                    if(jdbcTemplate.getMaxRows() > 0){
                        doDeleteAllFromTargetTable(toTableName);
                    }
                    jdbcTemplate.update(insertSql, insertValues, insertColumnTypeArr);
                }

            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCreateTargetTable(String tableName) {
        String createTableSqlStr = DBUtil.genCreateTable(jdbcTemplate,tableName);
        DBContextHolder.clearDBType();
        DBContextHolder.setDBType(DBTransferAttribute.DATA_SOURCE_ORACLE);
        jdbcTemplate.update(createTableSqlStr);
    }


    public void doDeleteAllFromTargetTable(String tableName) {
        String deleteSql = "delete from " + tableName ;
        jdbcTemplate.execute(deleteSql);
    }

}
