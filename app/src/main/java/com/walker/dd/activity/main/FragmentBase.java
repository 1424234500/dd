package com.walker.dd.activity.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.adapter.AdapterLvSession;
import com.walker.dd.util.AndroidTools;

import java.util.ArrayList;
import java.util.List;

public abstract class FragmentBase extends Fragment {

    /**
     * 传递引用对象数据初始化
     */
    public abstract  void setData(Object data);
    /**
     * 更新数据 后 通知更新页面
     */
    public abstract void notifyDataSetChanged();


    /**
     * 数据广播传递 activity通过baseAc收到广播后派发给当前fragment
     */
    public abstract void onReceive(String msg);

    /**
     * 发送广播 获取当前ActivityBase
     */
    public void sendSocket(String plugin, Bean data){
        ((AcBase)getActivity()).sendSocket(plugin, data);
    }
}
