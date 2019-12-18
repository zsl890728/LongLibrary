package com.slzhang.sqllitepal;

import android.app.Application;

import org.litepal.LitePal;

/**
 * application基类，
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
