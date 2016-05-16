package com.yanjun.dbtransfer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by YanJun on 2016/5/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
public class DBConnectionTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Test
    public void testConnection(){
//        DBContextHolder.setDBType(DBTransferAttribute.DATA_SOURCE_MYSQL);
//        DBUtil.genObjFromTable("t_user",jdbcTemplate);
    }

    @Test
    public void testInsert(){
        Connection conn=null;
        String url="jdbc:mysql://localhost:3306/java_study";
        String user="root";
        String password="root";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,password);
            Statement stat = conn.createStatement();
            stat.execute("select * from t_user where name = '张三' --update t_user set name = 'yanjun'");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
