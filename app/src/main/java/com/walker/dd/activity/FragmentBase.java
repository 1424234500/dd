package com.walker.dd.activity;

import android.support.v4.app.Fragment;

import com.walker.common.util.Bean;

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
