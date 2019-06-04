package com.walker.dd.activity;

import android.app.Fragment;
import android.os.Bundle;
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

    SwipeRefreshLayout srlsession;

    ListView lvSession;
    //type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
    AdapterLvSession adapterLvSession;

    List<Bean> listSessions = new ArrayList<>();


    ListView lvsession;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.main_fragment_chat,container,false);

        srlsession = v.findViewById(R.id.srlsession);
        lvsession =  v.findViewById(R.id.lvsession);
        adapterLvSession = new AdapterLvSession(getContext(), listSessions);
        lvSession.setAdapter( adapterLvSession);
        lvSession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                AndroidTools.toast(getActivity(), "click " + listSessions.get(arg2).toString());
            }
        });
        lvSession.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  int arg2, long arg3) {
                AndroidTools.toast(getActivity(), "remove " + listSessions.get(arg2).toString());
                return true;
            }
        });

//        /设置刷新时动画的颜色，可以设置4个
        srlsession.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        srlsession.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        adapterLvSession.notifyDataSetChanged();
    }
    public void init(){
        for(int i = 0; i < 10; i++) {
            listSessions.add(new Bean().set("MSG", "TEXT").set("NAME", "test" + i).set("TEXT", "text" + i)
                    .set("VOICE", "").set("FILE", "").set("PHOTO", "").set("NUM", 88).set("PROFILEPATH", ""));

            //NAME TEXT VOICE FILE PHOTO NUM PROFILEPATH
        }

    }


}
