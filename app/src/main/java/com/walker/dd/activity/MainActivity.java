package com.walker.dd.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;

public class MainActivity extends AcBase  {

    TextView mTextMessage;

    Fragment fragmentChat;
    Fragment fragmentList;
    Fragment fragmentOther;
    android.support.v4.app.FragmentManager fragmentManager;
    Fragment fragmentNow;




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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //初始化系统
        AndroidTools.init(this);


        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        fragmentChat = new FragmentChat();
        fragmentList = new FragmentList();
        fragmentOther = new FragmentOther();

//        fragmentManager = getFragmentManager();
        fragmentManager = getSupportFragmentManager();
        turnToFragment(fragmentChat);



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

    }

    //        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentChat).commit();
    public void turnToFragment(Fragment fragment){
        if(fragment == fragmentNow) return;

        FragmentTransaction t = fragmentManager.beginTransaction();
        if(1==1 || fragmentNow == null){
            t.replace(R.id.main_fragment, fragment);
        }else{
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
