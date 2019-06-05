package com.walker.dd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.adapter.AdapterGvOther;
import com.walker.dd.adapter.AdapterLvSession;
import com.walker.dd.util.AndroidTools;

import java.util.ArrayList;
import java.util.List;

public class FragmentOther extends Fragment {


    GridView gv;
    List<Bean> listItems = new ArrayList<>();
    // * IMAGE, TEXT
    AdapterGvOther adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidTools.toast(getContext(), "FragmentOther onCreateView");

        View v=inflater.inflate(R.layout.main_fragment_other,container,false);

        adapter = new AdapterGvOther(getContext(), listItems);

        gv = v.findViewById(R.id.gv);
        gv.setNumColumns(4);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                AndroidTools.toast(getActivity(), "click " + listItems.get(arg2).toString());
                onClick(listItems.get(arg2));
            }
        });
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  int arg2, long arg3) {
                AndroidTools.toast(getActivity(), "remove " + listItems.get(arg2).toString());
                return true;
            }
        });

        init();
        notifyDataSetChanged();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }
    public void init(){
        listItems.add(new Bean().set("IMAGE", "").set("TEXT", "socket"));
        listItems.add(new Bean().set("IMAGE", "").set("TEXT", "compose"));

        for(int i = 0; i < 10; i++) {
            listItems.add(new Bean().set("IMAGE", "").set("TEXT", "text" + i));

            //NAME TEXT VOICE FILE PHOTO NUM PROFILEPATH
        }
    }


    public void onClick(Bean bean){
        String text = bean.get("TEXT", "");
        switch (text){
            case "socket":
                startActivity(new Intent(getActivity(), ActivityTestSocket.class));
                break;
            case "compose":
                startActivity(new Intent(getActivity(), ActivityTestSocket.class));
                break;
        }


    }



}
