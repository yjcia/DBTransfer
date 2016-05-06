package com.yanjun.dbtransfer.service;

import com.yanjun.dbtransfer.annotation.DataSource;

import java.util.Map;

/**
 * Created by YanJun on 2016/5/5.
 */


public interface TransferDataByTable {

    @DataSource(type = "mysql")
    Map<String, Object> doSearchFromSourceTable(String fromTableName);

    @DataSource(type = "oracle")
    void doInsertToTargetTable(Map<String, Object> dataMap, String toTableName);

    @DataSource(type = "oracle")
    void doDeleteAllFromTargetTable(String tableName);


}
