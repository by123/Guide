package guide.by.android.com.guide.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.util.Constant;
import guide.by.android.com.guide.util.Utils;
import guide.by.android.com.guide.util.bluetooth.BluetoothManager;

import static guide.by.android.com.guide.R.id.txt_hold;
import static guide.by.android.com.guide.util.Constant.DeviceName;
import static guide.by.android.com.guide.util.bluetooth.BluetoothManager.getInputDeviceHiddenConstant;

/**
 * Created by by.huang on 2016/12/12.
 */

public class BluetoothConnectActivity extends Activity {

    private final static long SCAN_PERIOD = 10000;
    private BluetoothAdapter mBluetoothAdapter;
    private BlueBroadcastReceiver mBlueBroadcastReceiver;
    private BluetoothManager mBluetoothManager;
    private BluetoothDevice mCurrentDevice;
    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_bluetooth_connect);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mBlueBroadcastReceiver);
    }

    public void registerReceiver() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        localIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        localIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        localIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        localIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        localIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        localIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        this.registerReceiver(mBlueBroadcastReceiver, localIntentFilter);
    }

    private void initView() {
        this.mBluetoothManager = BluetoothManager.getInstance();

        TextView holdTxt = (TextView) findViewById(R.id.txt_hold);
        Utils.setTTF(holdTxt, this, "HeeboLight.ttf");

        TextView connectTxt = (TextView) findViewById(R.id.txt_connect);
        Utils.setTTF(connectTxt, this, "HeeboLight.ttf");

        TextView searchTxt = (TextView) findViewById(R.id.txt_search);
        Utils.setTTF(searchTxt, this, "HeeboLight.ttf");

        initBluetooth();
    }

    private Handler mHandler = new Handler();

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if (mCurrentDevice == null) {
                        Toast.makeText(BluetoothConnectActivity.this, "Can not find remote,  trying again..", Toast.LENGTH_LONG).show();
                        scanLeDevice(true);
                    } else {
                        initPair();
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device == null)
                        return;
                    if (device.getName() == null)
                        return;
                    Log.i("by", "检测到设备->" + device.getName());
                    if (device.getName().equals(DeviceName)) {
                        mCurrentDevice = device;
                    }
                }
            });
        }
    };

    private void initBluetooth() {
        //初始化广播接收
        mBlueBroadcastReceiver = new BlueBroadcastReceiver();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT);
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "unable to use bluetooth", Toast.LENGTH_LONG).show();
            //不支持蓝牙
            return;
        }
        //如果没有打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        //4.0以上才支持HID模式
        if (Build.VERSION.SDK_INT >= 14) {
            BluetoothManager.getInstance().getHidConncetList(new BluetoothManager.GetHidConncetListListener() {
                @Override
                public void getSuccess(ArrayList<BluetoothDevice> list) {

                    boolean isConnect = false;
                    for (BluetoothDevice bluetoothDevice : list) {

                        if (bluetoothDevice.getName().equals(Constant.DeviceName)) {
                            isConnect = true;
                            break;
                        }
                    }
                    if (isConnect) {
                        Toast.makeText(BluetoothConnectActivity.this, "Remote paired", Toast.LENGTH_LONG).show();
                        goNext();
                    } else {
                        scanLeDevice(true);
                    }
                }
            });
        } else {
            Toast.makeText(this, "unable to use bluetooth", Toast.LENGTH_LONG).show();
        }
    }

    //配对
    private void initPair() {
        if (mCurrentDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            Toast.makeText(BluetoothConnectActivity.this, "pairing...", Toast.LENGTH_LONG).show();
            mBluetoothManager.connect(mCurrentDevice, listener);

        } else {
            Toast.makeText(BluetoothConnectActivity.this, "found remote, start pairing", Toast.LENGTH_LONG).show();
            mBluetoothManager.pair(mCurrentDevice);
        }
    }

    //接收蓝牙通知广播
    private class BlueBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                switch (mCurrentDevice.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
//                        Toast.makeText(BluetoothConnectActivity.this, "正在配对", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
//                        Toast.makeText(BluetoothConnectActivity.this, "完成配对,开始连接", Toast.LENGTH_LONG).show();
                        mBluetoothManager.connect(mCurrentDevice, listener);
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Toast.makeText(BluetoothConnectActivity.this, "pairing failed", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == getInputDeviceHiddenConstant()) {
                    if (mCurrentDevice != null) {
                        Method method = proxy.getClass().getMethod("connect",
                                new Class[]{BluetoothDevice.class});
                        method.invoke(proxy, mCurrentDevice);
                        goNext();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {

        }
    };

    private void goNext() {
        finish();
        startActivity(new Intent(BluetoothConnectActivity.this, BluetoothSuccessActivity.class));
    }

}
