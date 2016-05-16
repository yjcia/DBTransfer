package com.yanjun.dbtransfer.util;

import com.yanjun.dbtransfer.common.DBTransferAttribute;
import com.yanjun.dbtransfer.datasource.DBContextHolder;
import com.yanjun.dbtransfer.model.CglibBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;

import java.sql.*;
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
            return "decimal";
        } else if (sqlType == Types.NUMERIC) {
            return "numeric";
        } else if (sqlType == Types.VARCHAR) {
            return "varchar";
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

    public static String genCreateTable(JdbcTemplate jdbcTemplate, String tableName) {
        DBContextHolder.clearDBType();
        DBContextHolder.setDBType(DBTransferAttribute.DATA_SOURCE_MYSQL);
        StringBuilder createTableSqlStr = new StringBuilder();
        String sql = "select * from " + tableName;
        String sqlStr = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            rs = conn.createStatement().executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            createTableSqlStr.append("create table " + tableName + " ( ");
            for(int i=1;i<=columnCount;i++){
                String columnName = metaData.getColumnName(i);
                String columnTypeName = metaData.getColumnTypeName(i);
                int columnTypeSize = metaData.getColumnDisplaySize(i);
                int precision = metaData.getPrecision(i);
                if(columnTypeName.equals("DECIMAL")){
                    createTableSqlStr.append("\""+columnName + "\" " +
                            columnTypeName + "(" + columnTypeSize +","+ (columnTypeSize-precision) +"),");
                }else if(columnTypeName.equals("INT")){
                    createTableSqlStr.append("\""+columnName + "\" " + columnTypeName + ",");
                }else{
                    createTableSqlStr.append("\""+columnName + "\" " + columnTypeName + "(" + columnTypeSize +"),");
                }
            }
            sqlStr = createTableSqlStr.substring(0,createTableSqlStr.lastIndexOf(","));
            sqlStr += ")";
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return sqlStr;
    }

    public static boolean getTableName(JdbcTemplate jdbcTemplate,String tableName) throws Exception {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        ResultSet tabs = null;
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String[] types = { "TABLE" };
            tabs = dbMetaData.getTables(null, null, tableName.toUpperCase(), types);
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
