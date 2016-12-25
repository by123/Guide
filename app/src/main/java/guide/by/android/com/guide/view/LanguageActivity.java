package guide.by.android.com.guide.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import guide.by.android.com.guide.R;
import guide.by.android.com.guide.adapter.LanguageAdapter;
import guide.by.android.com.guide.model.LanguageModel;
import guide.by.android.com.guide.util.Constant;
import guide.by.android.com.guide.util.Utils;

import static guide.by.android.com.guide.util.Constant.languages;

/**
 * Created by by.huang on 2016/11/29.
 */

public class LanguageActivity extends Activity implements AdapterView.OnItemClickListener {

    private View mLanguageLayout;
    private LanguageAdapter mLanguageAdapter;
    private ListView mLanguageListView;
    private TextView mLanguageTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_language);
        initView();
    }

    private void initView() {

        mLanguageTxt = (TextView)findViewById(R.id.txt_language);
        Utils.setTTF(mLanguageTxt,this,"HeeboMedium.ttf");

        mLanguageLayout = findViewById(R.id.layout_language);
        mLanguageListView = (ListView) mLanguageLayout.findViewById(R.id.listview);
        List<LanguageModel> datas = new ArrayList<>();
        for (String language : languages) {
            datas.add(LanguageModel.getModel(language));
        }
        mLanguageAdapter = new LanguageAdapter(this, datas);
        mLanguageListView.setAdapter(mLanguageAdapter);
        mLanguageListView.setOnItemClickListener(this);
        mLanguageListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<LanguageModel> datas = mLanguageAdapter.getDatas();
                if (datas != null && datas.size() > 0) {
                    for (LanguageModel data : datas) {
                        data.setSelect(false);
                    }
                    datas.get(position).setSelect(true);
                    mLanguageAdapter.notifyDataSetChanged();
                }

                switch (position)
                {
                    case 0:
                        mLanguageTxt.setText("Language");
                        break;
                    case 1:
                        mLanguageTxt.setText("语言");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<LanguageModel> datas = mLanguageAdapter.getDatas();
        if (datas != null && datas.size() > 0) {
            for (LanguageModel data : datas) {
                data.setSelect(false);
            }
            datas.get(position).setSelect(true);
            mLanguageAdapter.notifyDataSetChanged();
        }
        finish();
        startActivity(new Intent(LanguageActivity.this,WifiConnectActivity.class));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==  Constant.Key_Ok) {
            finish();
            startActivity(new Intent(LanguageActivity.this,WifiConnectActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }
}
