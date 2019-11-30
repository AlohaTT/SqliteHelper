package com.antiy.helper;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * @ClassName BaseDaoFactory
 * @Description TODO
 * @Author tony
 * @Date 2019-11-30 20:05
 * @Version 1.0
 */
public class BaseDaoFactory {
    private String mPath;
    private SQLiteDatabase mDatabase;
    private static BaseDaoFactory instance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    private BaseDaoFactory() {
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/teacher.db";
        openDatabase();
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
        this.mDatabase = SQLiteDatabase.openOrCreateDatabase(mPath, null);
    }
}
