package com.walker.dd.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.walker.dd.R;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    FragmentChat fragmentChat;
    FragmentList fragmentList;
    FragmentOther fragmentOther;

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


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        giftFragment = new Fragment1();
        hatFragment = new Fragment2();
        messageFragment = new Fragment3();
        centerFragment = new Fragment4();

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, giftFragment).commit();


//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(nowFragment)).commit();
//        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(nowFragment)).commit();

    }

}
