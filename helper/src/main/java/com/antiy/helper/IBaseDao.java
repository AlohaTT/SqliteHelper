package com.antiy.helper;

import java.util.List;

/**
 * @ClassName IBaseDao
 * @Description TODO
 * @Author tony
 * @Date 2019-11-30 19:17
 * @Version 1.0
 */
public interface IBaseDao<T> {
    /**
     * 插入数据
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * 改
     * @param entity
     * @param where
     * @return
     */
    int update(T entity, T where);

    /**
     * 删
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 查
     */
    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    /**
     * 预留多表查询等复杂查询的接口
     * @param sql
     * @return
     */
    List<T> query(String sql);
}
