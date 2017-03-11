package guide.by.android.com.guide.adapter;

import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import guide.by.android.com.guide.MyApplication;
import guide.by.android.com.guide.R;
import guide.by.android.com.guide.model.LanguageModel;
import guide.by.android.com.guide.util.Utils;

/**
 * Created by by.huang on 2016/11/17.
 */

public class LanguageAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<LanguageModel> mDatas = null;
    private Context mContext;
    private int size = 0;
    private int selectPosition;

    public LanguageAdapter(Context context, List<LanguageModel> datas) {
        this.mDatas = datas;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        if (mDatas != null && mDatas.size() > 0) {
            size = mDatas.size();
        }
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

    public List<LanguageModel> getDatas() {
        return mDatas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_language, null);
            holder.languageTxt = (TextView) convertView.findViewById(R.id.txt_language);
            Utils.setTTF(holder.languageTxt,mContext,"HeeboLight.ttf");
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mDatas != null && mDatas.size() > 0) {
            LanguageModel model = mDatas.get(position);
            holder.languageTxt.setText(model.getLanguage());
            if (model.isSelect()) {
                startAnim(holder.languageTxt,1.0f,1.5f);
                holder.languageTxt.setTextColor(MyApplication.mApplication.getResources().getColor(R.color.bg_color));
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            } else {
                if(selectPosition == position)
                {
                    startAnim(holder.languageTxt,1.5f,1.0f);
                }
                holder.languageTxt.setTextColor(MyApplication.mApplication.getResources().getColor(R.color.white));
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));


            }
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView languageTxt;
    }
    private void startAnim(final View view,float start,float end)
    {
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
    }
}
