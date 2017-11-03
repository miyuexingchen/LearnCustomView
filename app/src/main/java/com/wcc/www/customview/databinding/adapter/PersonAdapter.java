package com.wcc.www.customview.databinding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.wcc.www.customview.BR;
import com.wcc.www.customview.R;
import com.wcc.www.customview.databinding.ItemPersonBinding;
import com.wcc.www.customview.databinding.ItemPersonOffBinding;
import com.wcc.www.customview.databinding.entity.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 王晨晨 on 2017/11/1.
 */

public class PersonAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private static final int ITEM_VIEW_TYPE_ON = 1;
    private static final int ITEM_VIEW_TYPE_OFF = 2;

    private LayoutInflater mLayoutInflater;
    public interface OnItemClickListener{
        void onItemClick(Person person);
    }
    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }
    private List<Person> personList;

    public PersonAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        personList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        Person person = personList.get(position);
        if(person.isFired.get())
            return ITEM_VIEW_TYPE_OFF;
        else
            return ITEM_VIEW_TYPE_ON;
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if(viewType == ITEM_VIEW_TYPE_ON)
            binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_person, parent, false);
        else
            binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_person_off, parent, false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.item, personList.get(position));
        holder.getBinding().executePendingBindings();
        holder.getBinding().setVariable(BR.presenter, new Presenter(position));
    }

    public class Presenter{
        int position;
        public Presenter(int position) {
            this.position = position;
        }
        public void onClick(View v)
        {
            if(mListener != null)
                mListener.onItemClick(personList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void addAll(List<Person> add)
    {
        personList.addAll(add);
    }

    Random random = new Random(System.currentTimeMillis());
    public void add(Person person)
    {
        int position = random.nextInt(personList.size() + 1);
        personList.add(position, person);
        notifyItemInserted(position);
    }

    public void remove()
    {
        if(personList.size() == 0) return;
        int position = random.nextInt(personList.size());
        personList.remove(position);
        notifyItemRemoved(position);

    }
}
