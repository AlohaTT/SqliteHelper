package com.antiy.sqlitehelper;

import com.antiy.helper.annotation.DbField;
import com.antiy.helper.annotation.DbTable;

/**
 * @ClassName User
 * @Description TODO
 * @Author tony
 * @Date 2019-11-30 20:01
 * @Version 1.0
 */
@DbTable("tb_user")
public class User {

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
    }

    @DbField("name")
    public String name;

    @DbField("password")
    public String password;
}
