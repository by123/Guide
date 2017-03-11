package guide.by.android.com.guide.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.adapter.LanguageAdapter;
import guide.by.android.com.guide.util.Constant;
import guide.by.android.com.guide.util.Utils;

/**
 * Created by by.huang on 2016/11/29.
 */

public class BluetoothSuccessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_bluetooth_success);


        TextView searchTxt = (TextView) findViewById(R.id.txt_search);
        Utils.setTTF(searchTxt, this, "HeeboLight.ttf");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.Key_Ok:
                finish();
                startActivity(new Intent(BluetoothSuccessActivity.this, LanguageActivity.class));
                break;
            case Constant.Key_Back:
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}


