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
import com.wcc.www.customview.custom.C62_XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class C43_XListViewActivity extends AppCompatActivity {

    private C62_XListView xlv;

    private int[] ids = {
            R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e,
    };
    private List<Integer> idss;
    private BaseAdapter adapter;

    private int index = 1;
    private Handler handler = new Handler(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    if(index > 4)
                        index = 0;
                    idss.add(0, ids[index ++]);
                    adapter.notifyDataSetChanged();
                    xlv.setRefreshTime(format.format(System.currentTimeMillis()));
                    xlv.stopRefresh();
                    break;
                case 1:
                    if(index == 0)
                        index = 4;
                    idss.add(ids[-- index]);
                    adapter.notifyDataSetChanged();
                    xlv.stopLoadMore();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c43__xlist_view);
        idss = new ArrayList<>();
        idss.add(ids[0]);
        xlv = (C62_XListView) findViewById(R.id.xlv_c62);
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
                    convertView = LayoutInflater.from(C43_XListViewActivity.this).inflate(R.layout.layout_imageview, null);
                    holder = new ViewHolder();
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
        xlv.setAdapter(adapter);
        xlv.setPullLoadEnable(true);
        xlv.setPullRefreshEnable(true);
        xlv.setXListViewListener(new C62_XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0, 500);
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(1, 500);
            }
        });
    }
}
