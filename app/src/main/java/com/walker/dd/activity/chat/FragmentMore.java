package com.walker.dd.activity.chat;

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

public class FragmentMore extends FragmentBase {


    GridView gv;
    List<Bean> listItems = new ArrayList<>();
    // * ID, TEXT
    AdapterGvImageText adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidTools.log("FragmentMore onCreateView");

        View v=inflater.inflate(R.layout.main_fragment_other,container,false);

        listItems = new ArrayList<>();
        listItems.add(new Bean().set(Key.ID, R.drawable.icon_filetype_dir).set(Key.TEXT, "文件"));
        listItems.add(new Bean().set(Key.ID, R.drawable.icon_filetype_doc).set(Key.TEXT, "文档"));
        listItems.add(new Bean().set(Key.ID, R.drawable.icon_filetype_music).set(Key.TEXT, "音乐"));


        adapter = new AdapterGvImageText(getActivity(), listItems);

        gv = (GridView)v.findViewById(R.id.gv);
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
            for(int i = 0; i < 10; i++) {
                listItems.add(new Bean().set(Key.PROFILE, AndroidTools.getRandomColor()).set(Key.TEXT, "text" + i));
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
            case "文件":
                AndroidTools.choseFile(getActivity(), "");
                break;
            case "文档":
                break;
            case "音乐":

            default:
                sendSocket("echo", bean);
                break;
        }


    }



}
