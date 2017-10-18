package com.wcc.www.customview.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C50_PullableListView;
import com.wcc.www.customview.customstore.C50_Strengthen2RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class C41_Strengthen2RefreshLayoutActivity extends AppCompatActivity {

    private C50_PullableListView lv;
    private C50_Strengthen2RefreshLayout rl;

    private int[] ids = {
            R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e,
    };
    private int index = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    if(index > 4)
                        index = 0;
                    idss.add(0, ids[index ++]);
                    adapter.notifyDataSetChanged();
                    rl.refreshFinish(C50_Strengthen2RefreshLayout.SUCCEED);
                    break;

                case 1:
                    if(index == 0)
                        index = 4;
                    idss.add(ids[-- index]);
                    adapter.notifyDataSetChanged();
                    rl.loadmoreFinish(C50_Strengthen2RefreshLayout.SUCCEED);
                    break;
            }
        }
    };
    private BaseAdapter adapter;

    private List<Integer> idss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c41__strengthen2_refresh_layout);

        idss = new ArrayList<>();
        idss.add(ids[0]);
        lv = (C50_PullableListView) findViewById(R.id.plv_c50);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return idss.size();
            }

            @Override
            public Object getItem(int position) {
                return idss.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if(convertView == null)
                {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(C41_Strengthen2RefreshLayoutActivity.this).inflate(R.layout.layout_imageview, null);
                    holder.iv = (ImageView) convertView;
                    convertView.setTag(holder);
                }else
                    holder = (ViewHolder) convertView.getTag();

                holder.iv.setImageResource((Integer) getItem(position));
                return convertView;
            }

            class ViewHolder{
                ImageView iv;
            }
        };
        lv.setAdapter(adapter);
        rl = (C50_Strengthen2RefreshLayout) lv.getParent();
        rl.setOnRefreshListener(new C50_Strengthen2RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(C50_Strengthen2RefreshLayout C50_Strengthen2RefreshLayout) {
                handler.sendEmptyMessageDelayed(0, 500);
            }

            @Override
            public void onLoadMore(C50_Strengthen2RefreshLayout C50_Strengthen2RefreshLayout) {
                handler.sendEmptyMessageDelayed(1, 500);
            }
        });
    }
}
