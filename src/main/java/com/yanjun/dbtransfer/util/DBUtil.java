package com.yanjun.dbtransfer.util;

import com.yanjun.dbtransfer.common.DBTransferAttribute;
import com.yanjun.dbtransfer.model.CglibBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YanJun on 2016/5/5.
 */
public class DBUtil {
    public static Map<String,Object> genObjDataFromTable(String tableName, JdbcTemplate jdbcTemplate) {
        List<Object> resultList = null;
        Map<String,Object> returnMap = new HashMap<String,Object>();
        try {
            String sql = "select * from " + tableName;
            RowCountCallbackHandler handler = new RowCountCallbackHandler();
            jdbcTemplate.query(sql, handler);
            String[] columnName = handler.getColumnNames();
            int[] columnType = handler.getColumnTypes();
            HashMap<String, Object> propertyMap = new HashMap<String, Object>();
            for (int i = 0; i < columnName.length; i++) {
                propertyMap.put(columnName[i], Class.forName(sqlType2JavaType(columnType[i])));
            }

            CglibBean cglibBean = new CglibBean(propertyMap);
            Object targetObj = cglibBean.getObject();
            Class clazz = targetObj.getClass();
            resultList = jdbcTemplate.query(sql,new BeanPropertyRowMapper(clazz));
            returnMap.put(DBTransferAttribute.DATA_LIST,resultList);
            returnMap.put(DBTransferAttribute.COLUMN_COUNT,columnName.length);
            returnMap.put(DBTransferAttribute.CGLIB_BEAN_NAME,tableName);
            returnMap.put(DBTransferAttribute.COLUMN_NAME,columnName);
            returnMap.put(DBTransferAttribute.COLUMN_TYPE,columnType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return returnMap;
    }


    public static String sqlType2JavaType(int sqlType) {
        if (sqlType == Types.BIT) {
            return "java.lang.Boolean";
        } else if (sqlType == Types.TINYINT) {
            return "java.lang.Byte";
        } else if (sqlType == Types.SMALLINT) {
            return "java.lang.Short";
        } else if (sqlType == Types.INTEGER) {
            return "java.lang.Integer";
        } else if (sqlType == Types.BIGINT) {
            return "java.lang.Long";
        } else if (sqlType == Types.FLOAT) {
            return "java.lang.Float";
        } else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
            return "java.lang.Double";
        } else if (sqlType == Types.VARCHAR || sqlType == Types.CHAR
                || sqlType == Types.NVARCHAR || sqlType == Types.NCHAR) {
            return "java.lang.String";
        } else if (sqlType == Types.TIMESTAMP) {
            return "java.sql.TimeStamp";

        }
        return null;
    }

    public static String sqlType2SqlTypeStr(int sqlType) {
        if (sqlType == Types.BIT) {
            return "bit";
        } else if (sqlType == Types.TINYINT) {
            return "tinyint";
        } else if (sqlType == Types.SMALLINT) {
            return "smallint";
        } else if (sqlType == Types.INTEGER) {
            return "int";
        } else if (sqlType == Types.BIGINT) {
            return "bigint";
        } else if (sqlType == Types.FLOAT) {
            return "float";
        } else if (sqlType == Types.DECIMAL) {
            return "decimal(10,4)";
        } else if (sqlType == Types.NUMERIC) {
            return "numeric";
        } else if (sqlType == Types.VARCHAR) {
            return "varchar(255)";
        } else if (sqlType == Types.CHAR) {
            return "char";
        } else if (sqlType == Types.NVARCHAR) {
            return "nvarchar";
        } else if (sqlType == Types.NCHAR) {
            return "nchar";
        } else if (sqlType == Types.TIMESTAMP) {
            return "timestamp";

        }
        return null;
    }

    public static String getSqlParamSymbol(int columnCount){
        StringBuilder symbol = new StringBuilder();
        for(int i =0;i<columnCount;i++){
            symbol.append(" ?, ");
        }
        return symbol.substring(0,symbol.length()-2);
    }

    public static String getInsertColumnStr(String[] columnNames){
        StringBuilder columnNameStr = new StringBuilder();
        for(String columnName:columnNames){
            columnNameStr.append("\""+columnName+"\",");
        }
        return columnNameStr.substring(0,columnNameStr.length()-1);
    }

    public static String getInsertColumnByMethod(String getMethodName){
        return getMethodName.substring(3).toLowerCase();
    }

    public static Object[] getSortedInsertDataValue(Map<String, Object> paramMap,String[] columnNames){
        Object[] dataObjArr = new Object[columnNames.length];
        for(int i=0;i<columnNames.length;i++){
            dataObjArr[i] = paramMap.get(columnNames[i]);
        }

        return dataObjArr;
    }

    public static String genCreateTable(String[] columnNameArr,
                                               int[] insertColumnTypeArr, String tableName) {
        StringBuilder createTableSqlStr = new StringBuilder();
        createTableSqlStr.append("create table " + tableName + " ( ");
        for(int i=0;i<insertColumnTypeArr.length;i++){
            String sqlTypeStr = sqlType2SqlTypeStr(insertColumnTypeArr[i]);
            createTableSqlStr.append("\""+columnNameArr[i]).append("\" ").append(sqlTypeStr).append(",");
        }
        String sqlStr = createTableSqlStr.substring(0,createTableSqlStr.lastIndexOf(","));
        sqlStr += ")";

        return sqlStr;
    }

    public static boolean getTableName(JdbcTemplate jdbcTemplate,String tableName) throws Exception {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        ResultSet tabs = null;
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String[] types = { "TABLE" };
            tabs = dbMetaData.getTables(null, null, tableName, types);
            if (tabs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            tabs.close();
            conn.close();
        }
        return false;
    }
}
