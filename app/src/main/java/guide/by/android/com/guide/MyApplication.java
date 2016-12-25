package guide.by.android.com.guide;

import android.app.Application;

/**
 * Created by by.huang on 2016/11/17.
 */

public class MyApplication extends Application{

    public static MyApplication mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
