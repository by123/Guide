package guide.by.android.com.guide.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import guide.by.android.com.guide.MyApplication;
import guide.by.android.com.guide.R;
import guide.by.android.com.guide.model.WifiModel;
import guide.by.android.com.guide.util.Utils;
import guide.by.android.com.guide.util.wifi.WifiUtils;

/**
 * Created by by.huang on 2016/11/17.
 */

public class WifiAdapter extends BaseAdapter {
    private List<WifiModel> mDatas = new ArrayList<>();
    private Context mContext;
    private int size = 0;
    private WifiUtils mWifiUtils;
    private int selectPosition;

    public WifiAdapter(Context context, List<WifiModel> datas, WifiUtils wifiUtils) {
        this.mContext = context;
        this.mDatas = datas;
        this.mWifiUtils = wifiUtils;
        if (mDatas != null && mDatas.size() > 0) {
            size = mDatas.size();
        }
    }

    public void updateDatas(List<WifiModel> datas) {
        this.mDatas = datas;
        if (mDatas != null && mDatas.size() > 0) {
            size = mDatas.size();
        }
        notifyDataSetChanged();
    }

    public List<WifiModel> getDatas() {
        return mDatas;
    }

    public void setSelectPosition(int position) {
        selectPosition = position;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new WifiAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, null);
            holder.nameTxt = (TextView) convertView.findViewById(R.id.txt_name);
            Utils.setTTF(holder.nameTxt, mContext, "HeeboLight.ttf");
            holder.showImg = (ImageView) convertView.findViewById(R.id.img_wifi);
            convertView.setTag(holder);
        } else {
            holder = (WifiAdapter.ViewHolder) convertView.getTag();
        }
        if (mDatas != null && mDatas.size() > 0) {
            WifiModel model = mDatas.get(position);
            ScanResult scanResult = model.scanResult;
            boolean isNeedPsw = mWifiUtils.checkIsCurrentWifiHasPassword(scanResult);
            holder.nameTxt.setText(scanResult.SSID);

            if (model.isSelect) {
                startAnim(holder.nameTxt, 1.0f, 1.5f);
                startAnim(holder.showImg,1.0f,1.5f);
                holder.nameTxt.setTextColor(MyApplication.mApplication.getResources().getColor(R.color.bg_color));
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                if (model.isConnect) {
                    holder.showImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.right_blue));
                } else {
                    int level = scanResult.level;
                    if (level <= 0 && level >= -50) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi4_select, R.drawable.ic_wifi_lock4_select, isNeedPsw);
                    } else if (level < -50 && level >= -70) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi3_select, R.drawable.ic_wifi_lock3_select, isNeedPsw);

                    } else if (level < -70 && level >= -80) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi2_select, R.drawable.ic_wifi_lock2_select, isNeedPsw);

                    } else if (level < -80 && level >= -100) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi1_select, R.drawable.ic_wifi_lock1_select, isNeedPsw);
                    } else {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi0_select, R.drawable.ic_wifi_lock0_select, isNeedPsw);
                    }
                }
            } else {
                holder.nameTxt.setTextColor(MyApplication.mApplication.getResources().getColor(R.color.white));
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                if(selectPosition == position)
                {
                    startAnim(holder.nameTxt,1.5f,1.0f);
                    startAnim(holder.showImg,1.5f,1.0f);
                }
                if (model.isConnect) {
                    holder.showImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.right));
                } else {
                    int level = scanResult.level;
                    if (level <= 0 && level >= -50) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi4, R.drawable.ic_wifi_lock4, isNeedPsw);
                    } else if (level < -50 && level >= -70) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi3, R.drawable.ic_wifi_lock3, isNeedPsw);

                    } else if (level < -70 && level >= -80) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi2, R.drawable.ic_wifi_lock2, isNeedPsw);

                    } else if (level < -80 && level >= -100) {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi1, R.drawable.ic_wifi_lock1, isNeedPsw);
                    } else {
                        setWifiImage(holder.showImg, R.drawable.ic_wifi0, R.drawable.ic_wifi_lock0, isNeedPsw);
                    }
                }
            }
        }
        return convertView;
    }


    private void setWifiImage(ImageView imageView, int ResId, int ResId2, boolean isNeedPsw) {
        if (isNeedPsw) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(ResId2));
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(ResId));
        }
    }

    private class ViewHolder {
        private TextView nameTxt;
        private ImageView showImg;
    }

    private void startAnim(final View view, float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(300);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
//        final ScaleAnimation animation = new ScaleAnimation(start, end, start, end,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setDuration(300);
//        animation.setFillAfter(false);
//        view.setAnimation(animation);
//        animation.startNow();
    }
}
