package com.yanjun.dbtransfer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by YanJun on 2016/5/6.
 */
public class PropertiesUtil {
    private static Properties prop = new Properties();
    static{
        try {
            prop.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("transfer.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String propKey){
        return String.valueOf(prop.get(propKey));
    }
}
