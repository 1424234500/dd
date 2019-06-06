package com.walker.dd.activity.other;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.walker.dd.R;
import com.walker.dd.activity.AcBase;

public class ActivityCompose extends AcBase {

    TextView tv;
    /**
     * 生命周期
     *
     * @param savedInstanceState
     */
    @Override
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_compose);


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


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
