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
import com.wcc.www.customview.custom.C42_RefreshLayout3;

import java.util.ArrayList;
import java.util.List;

public class C36_RefreshLayoutActivity3 extends AppCompatActivity {

    private ListView lv;
    private C42_RefreshLayout3 rl;
    private int index = 1;
    private int[] ids = {
            R.mipmap.a,R.mipmap.b,R.mipmap.c,R.mipmap.d,R.mipmap.e,
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(index > 4)
                index = 0;
            if(msg.what == 0)
                idList.add(0, ids[index ++]);
            else
                idList.add(ids[index ++]);
            adapter.notifyDataSetChanged();
            rl.stopPullBehavior();
        }
    };
    private Adapter adapter;
    private List<Integer> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c36__refresh_layout3);

        lv = (ListView) findViewById(R.id.lv_c42);
        idList = new ArrayList<>();
        adapter = new Adapter(this, idList);
        idList.add(ids[0]);
        lv.setAdapter(adapter);
        rl = (C42_RefreshLayout3) lv.getParent();
        rl.setOnPullListener(new C42_RefreshLayout3.OnPullListener() {
            @Override
            public void onPullDown() {
                dodo(0);
            }

            @Override
            public void onPullUp() {
                dodo(1);
            }
        });
    }

    public void dodo(final int what)
    {
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(what);
            }
        }.start();
    }

    private class Adapter extends BaseAdapter{

        private Context ctx;
        private List<Integer> ids;

        public Adapter(Context context, List<Integer> idList){
            ctx = context;
            ids = idList;
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
                convertView = LayoutInflater.from(ctx).inflate(R.layout.layout_imageview1, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                convertView.setTag(holder);
            }else
                holder = (ViewHolder) convertView.getTag();

            holder.iv.setImageResource(ids.get(position));
            return convertView;
        }

        private class ViewHolder{
            ImageView iv;
        }
    }
}
