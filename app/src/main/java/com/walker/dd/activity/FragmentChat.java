package com.walker.dd.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.adapter.AdapterLvSession;
import com.walker.dd.util.AndroidTools;

import java.util.*;

public class FragmentChat extends Fragment {

    SwipeRefreshLayout srl;

    ListView lv;
    //type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
    List<Bean> listItems = new ArrayList<>();
    AdapterLvSession adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.main_fragment_chat,container,false);

        srl = v.findViewById(R.id.srl);
        lv =  v.findViewById(R.id.lv);
        adapter = new AdapterLvSession(getActivity(), listItems);
        lv.setAdapter( adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                AndroidTools.toast(getActivity(), "click " + listItems.get(arg2).toString());
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  int arg2, long arg3) {
                AndroidTools.toast(getActivity(), "remove " + listItems.get(arg2).toString());
                return true;
            }
        });

//        /设置刷新时动画的颜色，可以设置4个
        srl.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AndroidTools.toast(getContext(), "refresh");
            }
        });


        init();
        notifyDataSetChanged();

        return v;
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }
    public void init(){
        for(int i = 0; i < 10; i++) {
            listItems.add(new Bean().set("MSG", "TEXT").set("NAME", "test" + i).set("TEXT", "text" + i)
                    .set("VOICE", "").set("FILE", "").set("PHOTO", "").set("NUM", 88).set("PROFILEPATH", ""));

            //NAME TEXT VOICE FILE PHOTO NUM PROFILEPATH
        }

    }


}
