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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.adapter.WifiAdapter;
import guide.by.android.com.guide.model.WifiModel;
import guide.by.android.com.guide.util.Constant;
import guide.by.android.com.guide.util.Utils;
import guide.by.android.com.guide.util.wifi.WifiUtils;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static guide.by.android.com.guide.util.Constant.Action_NetWork_Success;
import static guide.by.android.com.guide.util.Constant.Key_Ok;
import static guide.by.android.com.guide.util.wifi.WifiUtils.WifiCipherType.WIFICIPHER_NOPASS;


/**
 * Created by by.huang on 2016/11/29.
 */

public class WifiConnectActivity extends Activity implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    private View mWifiLayout;
    private WifiAdapter mWifiAdapter;
    private ListView mWifiListView;
    private WifiUtils mWifiUtils;
    private List<WifiModel> datas = new ArrayList<>();
    private ScanResult mSelectScanResult;

    private View mPswLayout;
    private EditText mPswEdit;
    private int clickPosition;

    private final static int Statu_GetIpAddress = 0;
    private final static int Statu_WifiFail = 1;
    private final static int Statu_WifiSuccess = 2;
    private boolean isShowKeyboard = false;
    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_wifi);
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final IntentFilter filter2 = new IntentFilter(Action_NetWork_Success);
        registerReceiver(mReceiver, filter);
        registerReceiver(mReceiver, filter2);

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
//                datas = generateDatas();
//                mWifiAdapter.updateDatas(datas);
            } else if (action.equalsIgnoreCase(Action_NetWork_Success)) {
                finish();
                startActivity(new Intent(WifiConnectActivity.this, WifiConnectSuccessActivity.class));
            }
        }
    };


    private void initWifi() {
        mWifiUtils = WifiUtils.getInstance();
        mWifiUtils.init();

        int state = mWifiUtils.checkState();
//        String toastStr = "";
//        switch (state) {
//            case WIFI_STATE_DISABLING:
//                toastStr = "WIFI网卡正在关闭";
//                break;
//            case WIFI_STATE_DISABLED:
//                toastStr = "WIFI网卡不可用";
//                break;
//            case WIFI_STATE_ENABLING:
//                toastStr = "WIFI网卡正在打开";
//                break;
//            case WIFI_STATE_ENABLED:
//                toastStr = "WIFI网卡可用";
//                break;
//            case WIFI_STATE_UNKNOWN:
//                toastStr = "WIFI网卡状态未知";
//                break;
//        }

        mWifiUtils.openWifi();
        mWifiUtils.startScan();
//        Toast.makeText(WifiConnectActivity.this, toastStr, Toast.LENGTH_SHORT).show();
    }

    private void initView() {


        TextView titleTxt = (TextView) findViewById(R.id.txt_title);
        Utils.setTTF(titleTxt, this, "HeeboMedium.ttf");

        TextView subtitleTxt = (TextView) findViewById(R.id.txt_subtitle);
        Utils.setTTF(subtitleTxt, this, "HeeboLight.ttf");

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

            }
        });

        Utils.hideInputMethod(this);
        mWifiListView = (ListView) mWifiLayout.findViewById(R.id.listview);
        datas = generateDatas();
        mWifiAdapter = new WifiAdapter(this, datas, mWifiUtils);
        mWifiListView.setAdapter(mWifiAdapter);
        mWifiListView.setOnItemClickListener(this);
        mWifiListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                clickPosition = position;
                List<WifiModel> datas = mWifiAdapter.getDatas();
                if (datas != null && datas.size() > 0) {
                    for (int i = 0; i < datas.size(); i++) {
                        WifiModel data = datas.get(i);
                        if (data.isSelect) {
                            data.setSelect(false);
                            mWifiAdapter.setSelectPosition(i);
                        }
                    }
                    datas.get(position).setSelect(true);
                    mWifiAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private List<WifiModel> generateDatas() {

        datas.clear();
        List<ScanResult> scanResults = mWifiUtils.getWifiList();
        if (scanResults != null && scanResults.size() > 0) {
            String connectSsid = mWifiUtils.getConnectWifiSsid();
            for (ScanResult scanResult : scanResults) {
                WifiModel model = WifiModel.buildModel(scanResult, false, false);
                if (connectSsid.equalsIgnoreCase(scanResult.SSID) && mWifiUtils.isWifiConnect()) {
                        model.isConnect = true;
                }
                boolean isNeedPsw = mWifiUtils.checkIsCurrentWifiHasPassword(scanResult);
                model.isNeedPsw = isNeedPsw;
                datas.add(model);
            }
        }
        return getResult(datas);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
        if (adapter == mWifiAdapter) {
            clickPosition = position;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String pswStr = mPswEdit.getText().toString();
            if (TextUtils.isEmpty(pswStr)) {
                Toast.makeText(this, "Enter the password", Toast.LENGTH_SHORT).show();
                return true;
            }

            Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
            new ConnectWifiThread().execute(mSelectScanResult.SSID, mWifiUtils.getWifiCipherType(mSelectScanResult), pswStr);
            Utils.hideInputMethod(this);
            mPswLayout.setVisibility(View.GONE);
            mPswEdit.setText("");
            isShowKeyboard = false;
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
                    if (i == 20) {
                        handler.sendEmptyMessage(Statu_WifiFail);
                        return;
                    }
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
                    Toast.makeText(WifiConnectActivity.this, "searching IP address...", Toast.LENGTH_SHORT)
                            .show();
                    new RefreshSsidThread().start();
                    break;
                case Statu_WifiFail:
                    Toast.makeText(WifiConnectActivity.this, "connection failed.", Toast.LENGTH_SHORT)
                            .show();
//                    mWifiUtils.disconnectWifi();
                    break;
                case Statu_WifiSuccess:
                    Toast.makeText(WifiConnectActivity.this, "connection successful.", Toast.LENGTH_SHORT)
                            .show();
//                    generateDatas();
                    startActivity(new Intent(WifiConnectActivity.this, WifiConnectSuccessActivity.class));
                    break;
            }
            super.handleMessage(msg);
        }

    };


    private void OnClick(final int keyCode) {
        try {
            String keyCommand = "input keyevent " + keyCode;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(keyCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == Key_Ok) {
            if (isShowKeyboard) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    OnClick(KEYCODE_DPAD_CENTER);
                }
            } else {
                if (!isFirst) {
                    isFirst = true;
                    return super.dispatchKeyEvent(event);
                }
                List<WifiModel> datas = mWifiAdapter.getDatas();
                if (datas != null && datas.size() > 0) {
                    mSelectScanResult = datas.get(clickPosition).scanResult;
                }
                if (mWifiUtils.getConnectWifiSsid().equalsIgnoreCase(mSelectScanResult.SSID) && mWifiUtils.isWifiConnect()) {
                    mWifiUtils.disconnectWifi();
                    datas.get(clickPosition).isConnect = false;
                    mWifiAdapter.updateDatas(datas);
                    mSelectScanResult = null;
                    Toast.makeText(this, "disconected.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (datas.get(clickPosition).isNeedPsw) {
                    mPswLayout.setVisibility(View.VISIBLE);
                    mPswEdit.setFocusable(true);
                    mPswEdit.setFocusableInTouchMode(true);
                    mPswEdit.requestFocus();
                    Utils.showInputMethod(this);
                    isShowKeyboard = true;
                } else {
                    new ConnectWifiThread().execute(mSelectScanResult.SSID, WIFICIPHER_NOPASS, "");
                    isShowKeyboard = false;
                }

            }
            return true;

        } else if (keyCode == Constant.Key_Back) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (!isShowKeyboard) {
                    finish();
                    return true;
                }
                Utils.hideInputMethod(this);
                mPswLayout.setVisibility(View.GONE);
                isShowKeyboard = false;
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private List<WifiModel> getResult (List<WifiModel> datas)
    {
        for (int i = 0; i < datas.size() - 1; i++)
        {
            for (int j = i + 1; j < datas.size(); j++)
            {
                if (datas.get(i).scanResult.SSID.equalsIgnoreCase(datas.get(j).scanResult.SSID))
                {
                    datas.remove(j);
                    j--;
                }
            }
        }
        return datas;
    }
}
