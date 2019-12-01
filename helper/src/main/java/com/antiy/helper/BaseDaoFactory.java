package com.antiy.helper;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * @ClassName BaseDaoFactory
 * @Description TODO
 * @Author tony
 * @Date 2019-11-30 20:05
 * @Version 1.0
 */
public class BaseDaoFactory {
    private static String mPath;
    private SQLiteDatabase mDatabase;
    private volatile static BaseDaoFactory instance;

    public static BaseDaoFactory getInstance() {
        if (instance == null) {
            synchronized (BaseDaoFactory.class) {
                if (instance == null) {
                    instance = new BaseDaoFactory();
                }
            }
        }
        return instance;
    }

    private BaseDaoFactory() {
        openDatabase();
    }

    /**
     * 配置数据库存储路径
     *
     * @param path
     */
    public static void init(@NonNull String path) {
        mPath = path;
    }

    public synchronized <T extends BaseDao<M>, M> T getDbHelper(Class<T> clazz, Class<M> entityClazz) {
        BaseDao baseDao = null;
        try {
            baseDao = clazz.newInstance();
            baseDao.init(entityClazz, mDatabase);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

    private void openDatabase() {
        if (mPath == null) {
            throw new NullPointerException("You need to init database path first!");
        }
        this.mDatabase = SQLiteDatabase.openOrCreateDatabase(mPath, null);
    }
}
