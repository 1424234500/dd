package com.walker.dd.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    Fragment fragmentChat;
    Fragment fragmentList;
    Fragment fragmentOther;

    FragmentManager fragmentManager;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentChat).commit();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentList).commit();

                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentOther).commit();
//                    startActivity(new Intent(MainActivity.this, ActivityTestSocket.class));

                    return true;
            }


            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        fragmentChat = new FragmentChat();
        fragmentList = new FragmentList();
        fragmentOther = new FragmentOther();

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentChat).commit();


//        //获取到FragmentManager，在V4包中通过getSupportFragmentManager，
//        //在系统中原生的Fragment是通过getFragmentManager获得的。
//        FragmentManager FM = getFragmentManager();
//        //2.开启一个事务，通过调用beginTransaction方法开启。
//        FragmentTransaction MfragmentTransaction =FM.beginTransaction();
//        //把自己创建好的fragment创建一个对象
//        Fragment01  f1 = new Fragment01();
//        //向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
//        MfragmentTransaction.add(R.id.fragment_buju,f1);
//        //提交事务，调用commit方法提交。
//        MfragmentTransaction.commit();


//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(nowFragment)).commit();
//        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(nowFragment)).commit();


    }


    public void onClick(View v){
        switch (v.getId()){
            case R.id.rlsearch:
                AndroidTools.toast(this, "rlsearch");
            break;



        }

    }

}
