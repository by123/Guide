package guide.by.android.com.guide.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static guide.by.android.com.guide.util.Constant.Action_NetWork_Success;

/**
 * Created by by.huang on 2016/12/30.
 */

public class NetWorkChangeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if(activeInfo!=null && activeInfo.isAvailable() && activeInfo.isConnected())
        {
            Intent netIntent = new Intent(Action_NetWork_Success);
            context.sendBroadcast(netIntent);
        }
    }

}
