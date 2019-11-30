package com.antiy.helper;

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
    Long insert(T entity);

    /**
     * 改
     * @param entity
     * @param where
     * @return
     */
    Long update(T entity, T where);

}
