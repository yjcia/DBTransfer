package com.yanjun.dbtransfer;

import com.yanjun.dbtransfer.service.TransferDataByTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

/**
 * Created by YanJun on 2016/5/6.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
public class DBtransferTest {

    @Autowired
    private TransferDataByTable transferDataByTable;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDBTransfer(){

        Map<String,Object> returnMap = transferDataByTable.doSearchFromSourceTable("t_user");
        transferDataByTable.doInsertToTargetTable(returnMap,"t_user");

    }

    @Test
    public void testGetColumnName(){
        String sql = "select * from t_user";
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for(int i=1;i<=columnCount;i++){
                String columnName = metaData.getColumnName(i);
                String columnType = metaData.getColumnTypeName(i);
                int columnTypeSize = metaData.getColumnDisplaySize(i);
                int precision = metaData.getPrecision(i);
                //System.out.println(columnName + "--" + columnType + "--" + columnTypeSize + "--" + precision);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
