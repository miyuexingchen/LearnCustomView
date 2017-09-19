package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C28_HorizontalProgressBarWithNumber;

public class C23_HorizontalProgressBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c23__horizontal_progress_bar);

        C28_HorizontalProgressBarWithNumber pb = (C28_HorizontalProgressBarWithNumber) findViewById(R.id.c28);
        pb.setMax(100);
        pb.postDelayed(new AutoSetProgress(pb, 1), 50);
    }

    private class AutoSetProgress implements Runnable{
        private C28_HorizontalProgressBarWithNumber pb;
        private int progress;

        public AutoSetProgress(C28_HorizontalProgressBarWithNumber pb, int progress) {
            this.pb = pb;
            this.progress = progress;
        }

        @Override
        public void run() {
            pb.setProgress(progress);
            if(progress < pb.getMax()) {
                progress += Math.random() * 2 + 1;
            }else
            {
                progress = 100;
            }
            pb.postDelayed(this, 50);
        }
    }
}
