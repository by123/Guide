package guide.by.android.com.guide.util.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashSet;
import java.util.List;

import guide.by.android.com.guide.MyApplication;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by by.huang on 2016/11/17.
 */

public class WifiUtils {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiManager.WifiLock mWifiLock;

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    private static WifiUtils mInstance;

    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";

    public static WifiUtils getInstance() {
        if (mInstance == null) {
            synchronized (WifiUtils.class) {
                if (mInstance == null) {
                    mInstance = new WifiUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     */
    public void init() {
        mWifiManager = (WifiManager) MyApplication.mApplication.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        takeWifiLock();
    }


    /**
     * 打开wifi
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查wifi状态
     *
     * @return
     */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 获取wifi加密类型
     *
     * @param scanResult
     * @return
     */
    public WifiCipherType getWifiCipherType(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
            return WifiCipherType.WIFICIPHER_WPA;

        } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
            return WifiCipherType.WIFICIPHER_WEP;
        } else {
            return WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    /**
     * 锁定wifi，防止休眠时断开
     */
    public void takeWifiLock() {
        if (mWifiLock == null) {
            mWifiLock = mWifiManager.createWifiLock("Guide");
            mWifiLock.setReferenceCounted(false);
        }
        mWifiLock.acquire();
    }

    /**
     * 解锁wifi
     */
    private void releaseWifiLock() {
        if (mWifiLock != null) {
            mWifiLock.release();
            mWifiLock = null;
        }
    }


    /**
     * 扫描wifi
     */
    public void startScan() {
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    /**
     * 获得配置好的网络
     *
     * @return
     */
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }


    /**
     * 指定配置好的网络进行连接
     *
     * @param index
     */
    public void connectConfiguration(int index) {
        if (index > mWifiConfiguration.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    /**
     * 得到网络列表
     *
     * @return
     */
    public List<ScanResult> getWifiList() {
        mWifiList = mWifiManager.getScanResults();
        HashSet<ScanResult> hs = new HashSet<ScanResult>(mWifiList);
        mWifiList.clear();
        for (ScanResult scanResult : hs) {
            mWifiList.add(scanResult);
        }
        return mWifiList;
    }

    /**
     * 查看扫描结果
     *
     * @return
     */
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    /**
     * 获得SSID
     *
     * @return
     */
    public String getConnectWifiSsid() {
        return (mWifiInfo == null) ? null : mWifiInfo.getSSID().replace("\"","");
    }

    /**
     * 得到MAC地址
     *
     * @return
     */
    public String getMacAddress() {
        return (mWifiInfo == null) ? null : mWifiInfo.getMacAddress();
    }

    /**
     * 得到接入点的BSSID
     *
     * @return
     */
    public String getBSSID() {
        return (mWifiInfo == null) ? null : mWifiInfo.getBSSID();
    }

    /**
     * 得到IP地址
     *
     * @return
     */
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 得到连接的ID
     *
     * @return
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到WifiInfo的所有信息包
     *
     * @return
     */
    public String getWifiInfo() {
        return (mWifiInfo == null) ? null : mWifiInfo.toString();
    }

    /**
     * 添加一个网络并连接
     *
     * @param wcg
     */
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }

    /**
     * 断开指定ID的网络
     */
    public void disconnectWifi() {
        int netId = getNetworkId();
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
        releaseWifiLock();
    }


    /**
     * 判断wifi是否需要密码
     *
     * @param scanResult
     * @return
     */
    public boolean checkIsCurrentWifiHasPassword(ScanResult scanResult) {
        if (scanResult.capabilities != null) {
            String capabilities = scanResult.capabilities.trim();
            if (capabilities != null && (capabilities.equals(WIFI_AUTH_OPEN) || capabilities.equals(WIFI_AUTH_ROAM))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 连接wifi
     *
     * @param SSID
     * @param type
     * @param psw
     * @return
     */
    public boolean connect(String SSID, WifiCipherType type, String psw) {
//        helsinki
        WifiConfiguration wifiConfig = this
                .CreateWifiInfo(SSID, psw, type);
        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean bRet = mWifiManager.enableNetwork(netID, true);
        if (bRet) {
            mWifiManager.saveConfiguration();
        }
        return bRet;
    }

    /**
     * 连接wifi配置
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    private WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.allowedAuthAlgorithms.clear();
        wc.allowedGroupCiphers.clear();
        wc.allowedKeyManagement.clear();
        wc.allowedPairwiseCiphers.clear();
        wc.allowedProtocols.clear();
        wc.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            wc.wepKeys[0] = "";
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wc.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            wc.wepKeys[0] = "\"" + Password + "\"";
            wc.hiddenSSID = true;
            wc.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wc.wepTxKeyIndex = 0;
            System.out.println(wc);
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            wc.preSharedKey = "\"" + Password + "\"";
            wc.hiddenSSID = true;
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // for WPA
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // for WPA2
        } else {
            return null;
        }
        return wc;
    }


    /**
     * wifi是否连接成功
     *
     * @return
     */
    public boolean isWifiConnect() {
        boolean result = false;
//        String ssid = mWifiInfo.getSSID();
//        if (SSID.equalsIgnoreCase(ssid)) {
            ConnectivityManager connManager = (ConnectivityManager) MyApplication.mApplication.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            result = mWifi.isConnected();
            Log.i("by", "" + result);
//        }
        return result;
    }


}