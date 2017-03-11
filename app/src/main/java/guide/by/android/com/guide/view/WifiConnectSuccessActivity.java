package guide.by.android.com.guide.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.util.Constant;

/**
 * Created by by.huang on 2016/12/27.
 */

public class WifiConnectSuccessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_wifi_success);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.Key_Ok:
                finish();
                //走之后的流程
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

}
