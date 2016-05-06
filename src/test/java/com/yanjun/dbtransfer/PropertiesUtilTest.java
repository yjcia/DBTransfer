package com.yanjun.dbtransfer;

import com.yanjun.dbtransfer.util.PropertiesUtil;
import org.junit.Test;

/**
 * Created by YanJun on 2016/5/6.
 */
public class PropertiesUtilTest {

    @Test
    public void testProp(){
        System.out.println(PropertiesUtil.getValue("transfer.mode"));
    }
}
