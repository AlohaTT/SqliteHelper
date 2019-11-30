package com.antiy.sqlitehelper;

import com.antiy.helper.BaseDao;

/**
 * @ClassName UserDao
 * @Description TODO
 * @Author tony
 * @Date 2019-11-30 20:21
 * @Version 1.0
 */
public class UserDao extends BaseDao {
    /**
     * 创建表
     *
     * @return
     */
    @Override
    protected String createTable() {
        return "create table if not exists tb_user(name varchar(20), password varchar(10))";
    }
}
