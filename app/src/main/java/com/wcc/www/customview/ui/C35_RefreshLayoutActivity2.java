package com.wcc.www.customview.ui;

import android.content.Context;
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
import com.wcc.www.customview.custom.C41_RefreshLayout2;

import java.util.ArrayList;
import java.util.List;

public class C35_RefreshLayoutActivity2 extends AppCompatActivity {

    private ListView lv;
    private int[] ids = {
        R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e,
    };
    private List<Integer> idls;

    private Adapter adapter;
    private C41_RefreshLayout2 rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c35__refresh_layout2);
        lv = (ListView) findViewById(R.id.c41_lv);
        idls = new ArrayList<>();
        head = 0;
        tail = 4;
        idls.add(R.mipmap.b);

        adapter = new Adapter(this, idls);
        lv.setAdapter(adapter);
        rl = (C41_RefreshLayout2) lv.getParent();
        rl.setOnRefreshListener(new C41_RefreshLayout2.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(500);
                        }catch(Exception e)
                        {}
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }

            @Override
            public void onLoad() {
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(500);
                        }catch (Exception e)
                        {}
                        handler.sendEmptyMessage(1);
                    }
                }.start();
            }
        });
    }

    private int head, tail;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    adapter.append(0, ids[head ++]);
                    if(head == 5)
                        head = 0;
                    rl.refreshDone();
                    break;

                case 1:
                    adapter.append(1, ids[tail --]);
                    if(tail < 0)
                        tail = 4;
                    rl.loadDone();
                    break;
            }
        }
    };

    private class Adapter extends BaseAdapter{

        private Context ctx;
        private List<Integer> ids;
        public Adapter(Context ctx, List<Integer> ids)
        {
            this.ctx = ctx;
            this.ids = ids;
        }

        public void append(int index, int id)
        {
            if(index == 0)
                ids.add(index, id);
            else
                ids.add(id);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return ids == null ? 0 : ids.size();
        }

        @Override
        public Object getItem(int position) {
            return ids.get(position);
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
                convertView = LayoutInflater.from(ctx).inflate(R.layout.layout_imageview, null);
                holder.imageView = (ImageView) convertView;
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                convertView.setTag(holder);
            }else
                holder = (ViewHolder) convertView.getTag();

            holder.imageView.setImageResource(ids.get(position));
            return convertView;
        }

        private class ViewHolder{
            ImageView imageView;
        }
    }
}
