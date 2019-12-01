package com.antiy.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.antiy.helper.annotation.DbField;
import com.antiy.helper.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    public long insert(T entity) {
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
    public int update(T entity, T where) {
        int result = -1;
        Map<String, String> values = getValues(entity);
        Map<String, String> whereCause = getValues(where);
        Condition condition = new Condition(whereCause);
        ContentValues contentValues = getContentValues(values);
        result = mDatabase.update(tableName, contentValues, condition.getWhereCause(), condition.getWhereArgs());
        return result;
    }


    /**
     * 创建表
     *
     * @return
     */
    protected abstract String createTable();

    /**
     * 封装修改语句
     */
    class Condition {
        /**
         * 查询条件
         */
        private String whereCause;

        private String[] whereArgs;

        public Condition(Map<String, String> whereCause) {
            ArrayList<Object> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            sb.append(" 1=1 ");
            Set<String> keys = whereCause.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = whereCause.get(key);
                if (value != null) {
                    //拼接条件查询语句
                    sb.append(" and " + key + " =?");
                    list.add(value);
                }
                this.whereCause = sb.toString();
                this.whereArgs = list.toArray(new String[list.size()]);
            }
        }

        public String getWhereCause() {
            return whereCause;
        }

        public void setWhereCause(String whereCause) {
            this.whereCause = whereCause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }

        public void setWhereArgs(String[] whereArgs) {
            this.whereArgs = whereArgs;
        }
    }

    /**
     * 删
     *
     * @param where
     * @return
     */
    @Override
    public int delete(T where) {
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        int result = mDatabase.delete(tableName, condition.getWhereCause(), condition.getWhereArgs());
        return result;
    }

    /**
     * 查
     *
     * @param where
     */
    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map<String, String> map = getValues(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = mDatabase.query(tableName, null, condition.getWhereCause(),
                condition.getWhereArgs(), null, null, orderBy, limitString);
        List<T> result = getResult(cursor, where);
        cursor.close();
        return result;
    }

    /**
     * 获取查询结果
     *
     * @param cursor
     * @param where
     * @return
     */
    private List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList<>();
        Object item;
        while (cursor.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = mCacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    //得到列名
                    String columnName = entry.getKey();
                    //以列名拿到返回的index
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    Field field = entry.getValue();
                    Class type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            //反射方式赋值
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            field.set(item, cursor.getBlob(columnIndex));
                        } else {
                            /**
                             * 不支持的类型
                             */
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 预留多表查询等复杂查询的接口
     *
     * @param sql
     * @return
     */
    @Override
    public List<T> query(String sql) {
        return null;
    }
}
