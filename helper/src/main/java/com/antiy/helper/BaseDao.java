package com.antiy.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.antiy.helper.annotation.DbField;
import com.antiy.helper.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName BaseDao
 * @Description TODO
 * @Author tony
 * @Date 2019-11-30 19:54
 * @Version 1.0
 */
public abstract class BaseDao<T> implements IBaseDao<T> {
    /**
     * 持有数据库操作类的引用
     */
    private SQLiteDatabase mDatabase;
    /**
     * 保证实例化一次
     */
    private boolean isInit = false;
    /**
     * 持有操作数据库表所对应的java类型
     */
    private Class<T> entityClass;

    /**
     * 维护表名和成员变量名的映射关系
     * key 表名
     * value
     */
    private HashMap<String, Field> mCacheMap;

    private String tableName;

    /**
     * 实例化
     *
     * @param entity
     * @param sqLiteDatabase
     * @return
     */
    protected boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            entityClass = entity;
            this.mDatabase = sqLiteDatabase;
            DbTable dbTable = entity.getAnnotation(DbTable.class);
            if (dbTable == null) {
                //默认名
                tableName = entity.getClass().getSimpleName();
            } else {
                this.tableName = dbTable.value();
            }
            if (!mDatabase.isOpen()) {
                return false;
            }
            if (!TextUtils.isEmpty(createTable())) {
                mDatabase.execSQL(createTable());
            }

            initCacheMap();
            isInit = true;
        }
        return isInit;
    }

    /**
     * 维护映射关系
     */
    private void initCacheMap() {
        mCacheMap = new HashMap<>();
        String sql = "select * from " + this.tableName + " limit 1 , 0";
        Cursor cursor;
        cursor = mDatabase.rawQuery(sql, null);
        try {
            //表的列名数组
            String[] columnNames = cursor.getColumnNames();
            //拿到field数组
            Field[] columnFields = entityClass.getFields();
            for (Field field : columnFields) {
                field.setAccessible(true);
            }
            for (String columnName : columnNames) {
                Field columnField = null;
                for (Field field : columnFields) {
                    String fieldName;
                    DbField dbField = field.getAnnotation(DbField.class);
                    if (dbField != null) {
                        fieldName = dbField.value();
                    } else {
                        fieldName = field.getName();
                    }
                    //如果表的列名等于了成员变量的注解名字
                    if (columnName.equals(fieldName)) {
                        columnField = field;
                        break;
                    }
                }
                //找到了对应的关系
                if (columnField != null) {
                    mCacheMap.put(columnName, columnField);
                }
            }
        } finally {
            cursor.close();
        }

    }

    /**
     * 创建表
     *
     * @return
     */
    protected abstract String createTable();



    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String, String> result = new HashMap<>();
        Iterator<Field> iterator = mCacheMap.values().iterator();
        while (iterator.hasNext()) {
            Field columnToField = iterator.next();
            String cacheKey = null;
            String cacheValue = null;
            DbField dbField = columnToField.getAnnotation(DbField.class);
            if (dbField != null) {
                cacheKey = dbField.value();
            } else {
                cacheKey = columnToField.getName();
            }
            try {
                if (columnToField.get(entity) == null) {
                    continue;
                }
                cacheValue = columnToField.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);
        }
        return result;
    }

    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    @Override
    public Long insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        return mDatabase.insert(tableName, null, values);
    }

    /**
     * 改
     *
     * @param entity
     * @param where
     * @return
     */
    @Override
    public Long update(T entity, T where) {

        return null;
    }
}
