package guide.by.android.com.guide.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.adapter.WifiAdapter;
import guide.by.android.com.guide.model.WifiModel;
import guide.by.android.com.guide.util.Constant;
import guide.by.android.com.guide.util.KeyboardUtil;
import guide.by.android.com.guide.util.Utils;
import guide.by.android.com.guide.util.wifi.WifiUtils;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN;
import static android.view.KeyEvent.KEYCODE_NUMPAD_ENTER;
import static guide.by.android.com.guide.util.Constant.Key_Ok;


/**
 * Created by by.huang on 2016/11/29.
 */

public class WifiConnectActivity extends Activity implements AdapterView.OnItemClickListener,TextView.OnEditorActionListener{

    private View mWifiLayout;
    private WifiAdapter mWifiAdapter;
    private ListView mWifiListView;
    private WifiUtils mWifiUtils;
    private List<WifiModel> datas = new ArrayList<>();
    private ScanResult mSelectScanResult;

    private View mPswLayout;
    private EditText mPswEdit;
    private BaseAdapter selectAdapter;
    private int selectPosition;

    private final static int Statu_GetIpAddress = 0;
    private final static int Statu_WifiFail = 1;
    private final static int Statu_WifiSuccess = 2;
    private boolean isShowKeyboard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_wifi);
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
        initWifi();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                datas = generateDatas();
                mWifiAdapter.updateDatas(datas);
            }
        }
    };


    private void initWifi()
    {
        mWifiUtils = WifiUtils.getInstance();
        mWifiUtils.init();

        int state = mWifiUtils.checkState();
        String toastStr = "";
        switch (state) {
            case WIFI_STATE_DISABLING:
                toastStr = "WIFI网卡正在关闭";
                break;
            case WIFI_STATE_DISABLED:
                toastStr = "WIFI网卡不可用";
                break;
            case WIFI_STATE_ENABLING:
                toastStr = "WIFI网卡正在打开";
                break;
            case WIFI_STATE_ENABLED:
                toastStr = "WIFI网卡可用";
                break;
            case WIFI_STATE_UNKNOWN:
                toastStr = "WIFI网卡状态未知";
                break;
        }

        mWifiUtils.openWifi();
        mWifiUtils.startScan();
        Toast.makeText(WifiConnectActivity.this, toastStr, Toast.LENGTH_SHORT).show();
    }

    private void initView() {


        TextView titleTxt =(TextView)findViewById(R.id.txt_title);
        Utils.setTTF(titleTxt,this,"HeeboMedium.ttf");

        TextView subtitleTxt =(TextView)findViewById(R.id.txt_subtitle);
        Utils.setTTF(subtitleTxt,this,"HeeboLight.ttf");

        mWifiLayout = findViewById(R.id.layout_wifi);

        mPswLayout = findViewById(R.id.layout_psw);
        mPswEdit = (EditText) findViewById(R.id.edit_psw);
        mPswEdit.setOnEditorActionListener(this);
        mPswEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("by",s.toString());
            }
        });

        mWifiListView = (ListView) mWifiLayout.findViewById(R.id.listview);
        datas = generateDatas();
        mWifiAdapter = new WifiAdapter(this, datas, mWifiUtils);
        mWifiListView.setAdapter(mWifiAdapter);
        mWifiListView.setOnItemClickListener(this);

    }


    private List<WifiModel> generateDatas() {
        datas.clear();
        List<ScanResult> scanResults = mWifiUtils.getWifiList();
        if (scanResults != null && scanResults.size() > 0) {
            String connectSsid = mWifiUtils.getConnectWifiSsid();
            for (ScanResult scanResult : scanResults) {
                WifiModel model = WifiModel.buildModel(scanResult, false);
                Log.i("by", scanResult.SSID + " --- " + connectSsid);
                if (connectSsid.equalsIgnoreCase(scanResult.SSID) && mWifiUtils.isWifiConnect()) {
                    model.isConnect = true;
                }
                datas.add(model);
            }
        }
        return datas;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
        if (adapter == mWifiAdapter) {
            selectAdapter = adapter;
            selectPosition = position;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String pswStr = mPswEdit.getText().toString();
            if (TextUtils.isEmpty(pswStr)) {
                Toast.makeText(this, "请输入wifi密码", Toast.LENGTH_SHORT).show();
                return true;
            }

            Toast.makeText(this, "正在连接...", Toast.LENGTH_SHORT).show();
            new ConnectWifiThread().execute(mSelectScanResult.SSID, mWifiUtils.getWifiCipherType(mSelectScanResult), pswStr);
            Utils.hideInputMethod(this);
            mPswLayout.setVisibility(View.GONE);
        }
        return false;
    }



    class ConnectWifiThread extends AsyncTask<Object, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                handler.sendEmptyMessage(Statu_GetIpAddress);
            } else {
                handler.sendEmptyMessage(Statu_WifiFail);
            }
        }


        @Override
        protected Boolean doInBackground(Object... params) {
            String SSID = params[0].toString();
            WifiUtils.WifiCipherType type = (WifiUtils.WifiCipherType) params[1];
            String psw = params[2].toString();
            boolean result = mWifiUtils.connect(SSID, type, psw);
            return result;
        }
    }


    /**
     * 获取网络ip地址
     *
     * @author passing
     */
    class RefreshSsidThread extends Thread {
        @Override
        public void run() {
            boolean flag = true;
            int i = 0;
            while (flag) {
                try {
                    sleep(500);
                    i++;
                    if(i == 20)
                    {
                        handler.sendEmptyMessage(Statu_WifiFail);
                        return;
                    }
                    Log.i("by","检查wifi连接");
                    if (mWifiUtils.isWifiConnect()) {
                        flag = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(Statu_WifiSuccess);
            super.run();
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Statu_GetIpAddress:
                    Toast.makeText(WifiConnectActivity.this, "正在获取ip地址...", Toast.LENGTH_SHORT)
                            .show();
                    new RefreshSsidThread().start();
                    break;
                case Statu_WifiFail:
                    Toast.makeText(WifiConnectActivity.this, "连接失败！", Toast.LENGTH_SHORT)
                            .show();
//                    mWifiUtils.disconnectWifi();
                    break;
                case Statu_WifiSuccess:
                    Toast.makeText(WifiConnectActivity.this, "连接成功！", Toast.LENGTH_SHORT)
                            .show();
                    generateDatas();
                    break;
            }
            super.handleMessage(msg);
        }

    };


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(keyCode ==  Key_Ok ) {
            if(isShowKeyboard)
            {
                Log.i("by","----------------------------------->"+keyCode);
            }
            else
            {
                List<WifiModel> datas = mWifiAdapter.getDatas();
                if (datas != null && datas.size() > 0) {
                    mSelectScanResult = datas.get(selectPosition).scanResult;
                }
                if (mWifiUtils.getConnectWifiSsid().equalsIgnoreCase(mSelectScanResult.SSID) && mWifiUtils.isWifiConnect()) {
                    mWifiUtils.disconnectWifi();
                    datas.get(selectPosition).isConnect = false;
                    mWifiAdapter.updateDatas(datas);
                    mSelectScanResult = null;
                    Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
                    return true;
                }
                mPswLayout.setVisibility(View.VISIBLE);
                mPswEdit.setFocusable(true);
                mPswEdit.setFocusableInTouchMode(true);
                mPswEdit.requestFocus();
                Utils.showInputMethod(this);
                isShowKeyboard = true;
            }
            return true;

        }
        else if(keyCode == Constant.Key_Back)
        {
            Utils.hideInputMethod(this);
            mPswLayout.setVisibility(View.GONE);
            isShowKeyboard = false;
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
