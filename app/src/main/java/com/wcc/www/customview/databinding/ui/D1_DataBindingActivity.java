package com.wcc.www.customview.databinding.ui;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wcc.www.customview.BR;
import com.wcc.www.customview.R;
import com.wcc.www.customview.databinding.ActivityD1DataBindingBinding;
import com.wcc.www.customview.databinding.entity.Person;

public class D1_DataBindingActivity extends AppCompatActivity {

    private Person person = new Person("Juhyun", "Seo");
    private ActivityD1DataBindingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_d1__data_binding);
        binding.setPerson(person);
        binding.setVariable(BR.person, person);
        binding.setPresenter(new Presenter());
        binding.viewstub.getViewStub().inflate();
        person.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (i == BR.firstname)
                    System.out.println("firstname changed " + person.firstname);
                if (i == BR.isFired)
                    System.out.println("isFired changed " + person.isFired.get());
            }
        });

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding) {
                ViewGroup root = (ViewGroup) binding.getRoot();
                TransitionManager.beginDelayedTransition(root);
                return true;
            }
        });
    }

    public class Presenter {

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            person.setFirstname(s.toString());
            person.setFired(!person.isFired.get());
//            person.firstname = s.toString();
//            binding.setPerson(person);
        }

        public void onClick(View v) {
            Toast.makeText(D1_DataBindingActivity.this, "clicked", Toast.LENGTH_SHORT).show();
        }

        public void onClickBinding(Person person) {
            Toast.makeText(D1_DataBindingActivity.this, person.lastname, Toast.LENGTH_SHORT).show();
        }
    }
}
