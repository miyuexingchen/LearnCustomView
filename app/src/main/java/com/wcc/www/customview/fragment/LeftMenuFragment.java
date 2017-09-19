package com.wcc.www.customview.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wcc.www.customview.R;

public class LeftMenuFragment extends Fragment {

    private ListView mMenus;
    private String[] mMenuItemStr = { "Bear", "Bird", "Cat", "Tigers", "Panda" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_left_menu, container,
                false);
        mMenus = (ListView) view.findViewById(R.id.id_left_menu_lv);
        mMenus.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mMenuItemStr));
        return view;
    }

}
