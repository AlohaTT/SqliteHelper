package com.antiy.sqlitehelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.antiy.helper.BaseDao;
import com.antiy.helper.BaseDaoFactory;

import java.util.List;

/**
 * @author tony
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BaseDao baseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},22);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23);
        }
        baseDao = BaseDaoFactory.getInstance()
                .getDbHelper(UserDao.class, User.class);
    }

    public void save(View view) {
        User user = new User("teacher", "123456");
        baseDao.insert(user);
    }

    public void update(View view) {
        User where = new User();
        where.setName("teacher");
        User user = new User("David", "123456789");
        baseDao.update(user, where);
    }

    public void delete(View view) {
        User user = new User();
        user.setName("David");
        baseDao.delete(user);
    }

    public void queryList(View view) {
        User where = new User();
        where.setName("teacher");
        List<User> list = baseDao.query(where);
        Log.d(TAG, "queryList: "+list.size());
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "queryList: " + list.get(i).toString() + i);
        }
    }
}
