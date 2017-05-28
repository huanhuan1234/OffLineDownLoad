package hhh.bawei.com.offlinedownload;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Huangminghuan on 2017/5/28.
 */

public class IApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
    }
}
