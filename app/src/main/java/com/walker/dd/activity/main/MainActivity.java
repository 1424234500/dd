package com.walker.dd.activity.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.TimeUtil;
import com.walker.core.encode.Pinyin;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.MySP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AcBase {

    TextView mTextMessage;

    //所有fragment的数据都用static共用数据
    FragmentBase fragmentChat;
    /**
     * NAME TEXT TIME NUM
     */
    List<Bean> listItemChat = new ArrayList<>();
    Comparator<Bean> sessionCompare = new Comparator<Bean>() {
        @Override
        public int compare(Bean o1, Bean o2) {
            return o1.get("NAME", "").compareTo(o2.get("NAME", ""));
        }
    };
    FragmentBase fragmentList;

    FragmentBase fragmentOther;
    List<Bean> listItemOther = new ArrayList<>();
    android.support.v4.app.FragmentManager fragmentManager;
    FragmentBase fragmentNow;




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    turnToFragment(fragmentChat);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    turnToFragment(fragmentList);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    turnToFragment(fragmentOther);
                    return true;
            }


            return false;
        }
    };

    @Override
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        //初始化系统
        AndroidTools.init(this);


        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        fragmentChat = new FragmentSession();
        fragmentChat.setData(listItemChat);
        fragmentList = new FragmentContact();
        fragmentOther = new FragmentOther();
        fragmentOther.setData(listItemOther);

//        fragmentManager = getFragmentManager();
        fragmentManager = getSupportFragmentManager();  //v4

        turnToFragment(fragmentChat);

        sendSocket("session", new Bean());
        String user = MySP.get(getContext(), "user", Pinyin.getChinese());
        String pwd = MySP.get(getContext(), "pwd", Math.random() * 10 + "");
        if(user.length() > 0){
            sendSocket("login", new Bean().put("user", user).put("pwd", pwd));
        }else{
            //todo:登录弹窗
        }

    }

    /**
     * 回退键处理，返回false则执行finish，否则不处理
     */
    @Override
    public boolean OnBackPressed() {
        return false;
    }

    /**
     * 收到广播处理
     *
     * @param msg
     */
    @Override
    public void OnReceive(String msg) {
        if(fragmentNow != null){
            Bean bean = JsonUtil.get(msg);
            String plugin = bean.get("type", "message");

            if(plugin.equals("message")){
// {"time_client":1560235377468,"time_do":1560235377498,"
// data":{"type":"txt","body":"看看"},
// "sfrom":"223.104.210.192:27959","wait_size":0,
// "from":"洋","to":"洋",
// "time_reveive":1560235377469,"type":"message",
// "sto":"223.104.210.192:27959"}
                Bean data = bean.get("data", new Bean());

                Bean item = new Bean()
                        .set("MSG", "TEXT")
                        .set("NAME", bean.get("from", "name?"))
                        .set("TEXT", data.get("body", "text?"))
                        .set("TIME", TimeUtil.format(bean.get("time_do", 0L), "yyyy-MM-dd HH:mm:ss"))
                        .set("NUM", 1)
                        .set("PROFILEPATH", "");
                int i = AndroidTools.listIndex(listItemChat, item, sessionCompare);
                if(i >= 0){
                    item.set("NUM", listItemChat.get(i).get("NUM", 0) + 1);
                    listItemChat.remove(i);
                }
                listItemChat.add(0, item);

                fragmentChat.notifyDataSetChanged();
            } else if(plugin.equals("session")){
//{"time_client":1560238312727,"time_do":1560238312778,
// "data":[
// {"TIME":"2019-06-11 14:49:59","ID":"蓼","KEY":"223.104.210.192:27961"},
// {"TIME":"2019-06-11 15:31:18","ID":"岘","KEY":"223.104.210.192:29886"}],
// "sfrom":"223.104.210.192:29886","wait_size":0,
// "from":"岘","to":"","time_reveive":1560238312728,"type":"session","sto":"223.104.210.192:29886"}
                List<Bean> list = bean.get("data", new ArrayList<Bean>());
                List<Bean> newList = new ArrayList<>();
                for(Bean data : list) {
                    Bean item = new Bean()
                            .set("MSG", "TEXT")
                            .set("NAME", data.get("ID", "name?"))
                            .set("TEXT", data.get("KEY", "text?"))
                            .set("TIME", data.get("TIME", "time?"))
                            .set("NUM", 1)
                            .set("PROFILEPATH", "");
                    newList.add(item);
                }
                AndroidTools.listReplaceIndexAndAdd(0, listItemChat, newList, sessionCompare);
//            fragmentNow.onReceive(msg);
                fragmentChat.notifyDataSetChanged();
            }

        }
    }

    //fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentChat).commit();
    public void turnToFragment(FragmentBase fragment){
        if(fragment == fragmentNow) return;

        FragmentTransaction t = fragmentManager.beginTransaction();
        if(fragmentNow == null){
            t.replace(R.id.main_fragment, fragment);
        }else{
            if(!fragment.isAdded()){
                t.add(R.id.main_fragment, fragment);
            }
            t.hide(fragmentNow).show(fragment);
        }
        fragmentNow = fragment;
        t.commit();

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rlsearch:
                AndroidTools.toast(this, "rlsearch");
                break;

        }

    }
}
