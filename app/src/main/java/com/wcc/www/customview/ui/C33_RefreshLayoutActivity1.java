package com.wcc.www.customview.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C39_RefreshLayout1;

public class C33_RefreshLayoutActivity1 extends AppCompatActivity {

    private ImageView refresh;
    private C39_RefreshLayout1 rl;

    private int[] ids = {
            R.mipmap.a,
            R.mipmap.b,
            R.mipmap.c,
            R.mipmap.d,
            R.mipmap.e,
    };
    private int i = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    if(i > 3)
                        i = 0;
                    refresh.setBackgroundResource(ids[++ i]);
                    rl.refreshDone();
                    break;
                case 1:
                    if(i == 0)
                        i = 4;
                    refresh.setBackgroundResource(ids[-- i]);
                    rl.loadDone();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c33__refresh_layout1);

        refresh = (ImageView) findViewById(R.id.iv_refresh);
        rl = (C39_RefreshLayout1) findViewById(R.id.c39_rl);
        rl.setOnRefreshListener(new C39_RefreshLayout1.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }

            @Override
            public void onLoad() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1);
                    }
                }.start();
            }
        });
    }
}
