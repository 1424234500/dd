package com.walker.dd.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.adapter.AdapterLvSession;
import com.walker.dd.util.AndroidTools;

import java.util.*;

public class FragmentChat extends FragmentBase implements  AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    SwipeRefreshLayout srl;

    ListView lv;
    //type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
    public static List<Bean> listItems;
    AdapterLvSession adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.main_fragment_chat,container,false);

        srl = v.findViewById(R.id.srl);
        lv =  v.findViewById(R.id.lv);
        adapter = new AdapterLvSession(getActivity(), listItems);
        lv.setAdapter( adapter);

        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

//        /设置刷新时动画的颜色，可以设置4个
        srl.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AndroidTools.toast(getContext(), "refresh");
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
                listItems.add(new Bean().set("MSG", "TEXT").set("NAME", "test" + i).set("TEXT", "text" + i)
                        .set("VOICE", "").set("FILE", "").set("PHOTO", "").set("NUM", 88).set("PROFILEPATH", ""));

                //NAME TEXT VOICE FILE PHOTO NUM PROFILEPATH
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
        AndroidTools.toast(getContext(), "chat onReceive " + msg);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bean bean = listItems.get(position);
        AndroidTools.toast(getActivity(), "click " + bean.toString());
        sendSocket("echo", bean);

    }

    /**
     * Callback method to be invoked when an item in this view has been
     * clicked and held.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent   The AbsListView where the click happened
     * @param view     The view within the AbsListView that was clicked
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Bean bean = listItems.get(position);
        AndroidTools.toast(getActivity(), "long click " + bean.toString());
        sendSocket("echo", bean);
        return false;
    }
}
