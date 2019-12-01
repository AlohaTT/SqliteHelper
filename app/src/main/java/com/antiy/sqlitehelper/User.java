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

    Integer id;

    Double number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "name  " + name + "password" + password;
    }
}
