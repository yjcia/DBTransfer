package com.yanjun.dbtransfer;

import com.yanjun.dbtransfer.common.DBTransferAttribute;
import com.yanjun.dbtransfer.datasource.DBContextHolder;
import com.yanjun.dbtransfer.util.DBUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}
