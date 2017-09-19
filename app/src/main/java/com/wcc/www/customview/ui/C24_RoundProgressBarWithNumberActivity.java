package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C28_HorizontalProgressBarWithNumber;
import com.wcc.www.customview.custom.C29_RoundProgressBarWithNumberWithNumber;

public class C24_RoundProgressBarWithNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c24__round_progress_bar_with_number);

        C29_RoundProgressBarWithNumberWithNumber c29 = (C29_RoundProgressBarWithNumberWithNumber) findViewById(R.id.c29);
        c29.setMax(100);
        c29.postDelayed(new AutoSetProgress(c29, 1), 50);
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
