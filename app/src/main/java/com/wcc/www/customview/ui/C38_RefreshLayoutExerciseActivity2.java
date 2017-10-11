package com.wcc.www.customview.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C44_RefreshLayoutExercise2;

import java.util.ArrayList;
import java.util.List;

public class C38_RefreshLayoutExerciseActivity2 extends AppCompatActivity {

    private ListView lv;
    private C44_RefreshLayoutExercise2 rl;
    private int ids[] = {
            R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e,
    };

    private List<Integer> idss;
    private int index = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    if(index > 4)
                        index = 0;
                    if(index < 0)
                        index = 0;
                    idss.add(0, ids[index ++]);
                    break;

                case 1:
                    if(index > 4)
                        index = 4;
                    if(index < 0)
                        index = 4;
                    idss.add(ids[index --]);
                    break;
            }
            adapter.notifyDataSetChanged();
            rl.stopPullBehavior();
        }
    };
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c38__refresh_layout_exercise2);

        idss = new ArrayList<>();
        idss.add(ids[0]);
        lv = (ListView) findViewById(R.id.lv_c44);
        rl = (C44_RefreshLayoutExercise2) lv.getParent();
        rl.setOnPullListener(new C44_RefreshLayoutExercise2.OnPullListener() {
            @Override
            public void pullDown() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }

            @Override
            public void pullUp() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1);
                    }
                }.start();
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
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(C38_RefreshLayoutExerciseActivity2.this).inflate(R.layout.layout_imageview, null);
                    holder.iv = (ImageView) convertView;
                    convertView.setTag(holder);
                } else
                    holder = (ViewHolder) convertView.getTag();

                holder.iv.setImageResource(idss.get(position));
                return convertView;
            }

            class ViewHolder {
                ImageView iv;
            }
        };
        lv.setAdapter(adapter);
    }
}
