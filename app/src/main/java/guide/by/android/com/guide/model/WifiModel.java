package guide.by.android.com.guide.model;

import android.media.midi.MidiOutputPort;
import android.net.wifi.ScanResult;

/**
 * Created by by.huang on 2016/11/26.
 */

public class WifiModel {
    public ScanResult scanResult;
    public boolean isConnect;

    public static WifiModel buildModel(ScanResult scanResult, boolean isConnect) {
        WifiModel model = new WifiModel();
        model.scanResult = scanResult;
        model.isConnect = isConnect;
        return model;
    }
}
