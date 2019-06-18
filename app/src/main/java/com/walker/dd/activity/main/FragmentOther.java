package com.walker.dd.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.activity.other.ActivityCompose;
import com.walker.dd.activity.other.ActivityTestEcho;
import com.walker.dd.activity.other.ActivityTestSocket;
import com.walker.dd.adapter.AdapterGvImageText;
import com.walker.dd.util.AndroidTools;
import com.walker.socket.server_1.Key;

import java.util.ArrayList;
import java.util.List;

public class FragmentOther extends FragmentBase {


    GridView gv;
    List<Bean> listItems = new ArrayList<>();
    // * IMAGE, TEXT
    AdapterGvImageText adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidTools.log("FragmentMore onCreateView");

        View v=inflater.inflate(R.layout.main_fragment_other,container,false);

        adapter = new AdapterGvImageText(getActivity(), listItems);

        gv = (GridView)v.findViewById(R.id.gv);
        gv.setNumColumns(6);
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


        return v;
    }

    /**
     * 传递引用对象数据初始化
     *
     * @param data
     */
    @Override
    public void setData(Object data) {

        this.listItems = (List<Bean>) data;
        if(this.listItems.size() <= 0){
            listItems.add(new Bean().set(Key.PROFILE, "").set(Key.TEXT, "socket"));
            listItems.add(new Bean().set(Key.PROFILE, "").set(Key.TEXT, "compose"));
            listItems.add(new Bean().set(Key.PROFILE, "").set(Key.TEXT, "autochat"));
            for(int i = 0; i < 10; i++) {
                listItems.add(new Bean().set(Key.PROFILE, "").set(Key.TEXT, "text" + i));
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 更新数据
     */
    @Override
    public void notifyDataSetChanged() {
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }


    /**
     * 数据广播传递 activity通过baseAc收到广播后派发给当前fragment
     *
     * @param msg
     */
    @Override
    public void onReceive(String msg) {

    }


    public void onClick(Bean bean){
        String text = bean.get(Key.TEXT, "");
        switch (text){
            case "socket":
                startActivity(new Intent(getActivity(), ActivityTestSocket.class));
                break;
            case "compose":
                startActivity(new Intent(getActivity(), ActivityCompose.class));
                break;
            case "autochat":
                startActivity(new Intent(getActivity(), ActivityTestEcho.class));

            default:
                sendSocket("echo", bean);
                    break;
        }


    }



}
