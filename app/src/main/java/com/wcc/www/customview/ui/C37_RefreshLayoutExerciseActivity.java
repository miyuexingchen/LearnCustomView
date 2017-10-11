package com.wcc.www.customview.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C43_RefreshLayoutExercise1;
import com.wcc.www.customview.custom.C44_RefreshLayoutExercise2;

import java.util.ArrayList;
import java.util.List;

public class C37_RefreshLayoutExerciseActivity extends AppCompatActivity {

    private C44_RefreshLayoutExercise2 rl;

    private int[] ids = {
            R.mipmap.a,
            R.mipmap.b,
            R.mipmap.c,
            R.mipmap.d,
            R.mipmap.e,
    };
    private RecyclerView rv;
    private List<Integer> idss;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c37__refresh_layout_exercise);

        idss = new ArrayList<>();
        idss.add(ids[0]);
        rv = (RecyclerView) findViewById(R.id.rv_c37);
        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(C37_RefreshLayoutExerciseActivity.this)
                        .inflate(R.layout.layout_imageview, parent, false);
                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Holder h = (Holder) holder;
                h.iv.setImageResource(idss.get(position));
            }

            @Override
            public int getItemCount() {
                return idss.size();
            }

            class Holder extends RecyclerView.ViewHolder{

                ImageView iv;
                public Holder(View itemView) {
                    super(itemView);
                    iv = (ImageView) itemView;
                }
            }
        };
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rl = (C44_RefreshLayoutExercise2) rv.getParent();
        rl.setOnPullListener(new C44_RefreshLayoutExercise2.OnPullListener() {
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
        });
    }

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
                    if(index < 0)
                        index = 4;
                    if(index > 4)
                        index = 4;
                    idss.add(ids[index --]);
                    break;
            }
            adapter.notifyDataSetChanged();
            rl.stopPullBehavior();
        }
    };
}
