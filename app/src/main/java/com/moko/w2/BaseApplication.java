package com.moko.w2;

import android.app.Application;
import android.content.Intent;

import com.moko.w2.service.MokoService;
import com.moko.support.MokoSupport;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.w2.BaseApplication
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MokoSupport.getInstance().init(getApplicationContext());
        // 启动蓝牙服务
        startService(new Intent(this, MokoService.class));
    }
}
