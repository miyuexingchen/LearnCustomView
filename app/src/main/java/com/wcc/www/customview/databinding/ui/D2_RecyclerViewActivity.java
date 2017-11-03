package com.wcc.www.customview.databinding.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.wcc.www.customview.R;
import com.wcc.www.customview.databinding.adapter.PersonAdapter;
import com.wcc.www.customview.databinding.entity.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class D2_RecyclerViewActivity extends AppCompatActivity {

    com.wcc.www.customview.databinding.ActivityD2RecyclerViewBinding binding;
    PersonAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_d2__recycler_view);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PersonAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PersonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Person person) {
                Toast.makeText(D2_RecyclerViewActivity.this, person.firstname, Toast.LENGTH_SHORT).show();
            }
        });
        List<Person> list = new ArrayList<>();
        list.add(new Person("hyun", "seo", false));
        list.add(new Person("hyun", "seo2", false));
        list.add(new Person("hyun", "seo3", true));
        list.add(new Person("hyun", "seo4", false));
        adapter.addAll(list);
        binding.setPresenter(new Presenter());
    }

    public class Presenter{
        public void add(View v)
        {
            Random random = new Random();
            int i = random.nextInt(3333);
            adapter.add(new Person("hyun", "seo"+i));
        }

        public void remove(View v)
        {
            adapter.remove();
        }
    }
}
