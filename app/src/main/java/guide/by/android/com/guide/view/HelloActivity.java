package guide.by.android.com.guide.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.util.Utils;

/**
 * Created by by.huang on 2016/11/29.
 */

public class HelloActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_hello);

        TextView text = (TextView) findViewById(R.id.txt_hello);
        Utils.setTTF(text,this,"HeeboMedium.ttf");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(HelloActivity.this,BluetoothConnectActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
