package com.wcc.www.customview.databinding.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.databinding.entity.Person;

public class D3_ExpressionActivity extends AppCompatActivity {

    private com.wcc.www.customview.databinding.ActivityD3ExpressionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_d3__expression);
        Person person = new Person("hyun", "seo");
        person.setAvatar("http://n1image.hjfile.cn/mh/2016/11/01/9cfff64fc7c9483d60a8cf01c79869de.jpg");
        binding.setPerson(person);
    }


}
