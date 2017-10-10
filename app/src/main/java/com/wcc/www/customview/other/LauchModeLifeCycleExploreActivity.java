package com.wcc.www.customview.other;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;

public class LauchModeLifeCycleExploreActivity extends AppCompatActivity {

    public static final String TAG = "LifeCycle Method ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch_mode_life_cycle_explore);
        System.out.println(TAG + "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println(TAG + "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(TAG + "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println(TAG + "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println(TAG + "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println(TAG + "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println(TAG + "onRestart");
    }
}
