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
import android.widget.ListView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C77_2RefreshLayoutExercise10;

import java.util.ArrayList;
import java.util.List;

public class C40_2RefreshLayoutActivity2 extends AppCompatActivity {

    private BaseAdapter adapter;
    private int index = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(index > 4)
                index = 0;
            idss.add(0, ids[index ++]);
            adapter.notifyDataSetChanged();
            rl.refreshFinish();
        }
    };

    private int[] ids = {
            R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e,
    };
    private List<Integer> idss;
    private C77_2RefreshLayoutExercise10 rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c40_2_refresh_layout2);

        idss = new ArrayList<>();
        idss.add(ids[0]);
        ListView lv = (ListView) findViewById(R.id.lv_c49);
        rl = (C77_2RefreshLayoutExercise10) lv.getParent();
        rl.setOnRefreshListener(new C77_2RefreshLayoutExercise10.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0, 500);
            }
        });
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
                    convertView = LayoutInflater.from(C40_2RefreshLayoutActivity2.this).inflate(R.layout.layout_imageview, null);
                    holder = new ViewHolder();
                    holder.iv = (ImageView) convertView;
                    convertView.setTag(holder);
                }else
                    holder = (ViewHolder) convertView.getTag();

                holder.iv.setImageResource((int) getItem(position));
                return convertView;
            }

            class ViewHolder{
                ImageView iv;
            }
        };
        lv.setAdapter(adapter);
    }
}
