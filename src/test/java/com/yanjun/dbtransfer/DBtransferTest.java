package com.yanjun.dbtransfer;

import com.yanjun.dbtransfer.service.TransferDataByTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by YanJun on 2016/5/6.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
public class DBtransferTest {

    @Autowired
    private TransferDataByTable transferDataByTable;

    @Test
    public void testDBTransfer(){

        Map<String,Object> returnMap = transferDataByTable.doSearchFromSourceTable("t_user");
        transferDataByTable.doInsertToTargetTable(returnMap,"t_user");

    }

    public void testGetColumnName(){

    }
}
