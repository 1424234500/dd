package com.walker.dd.activity.chat;
 
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walker.dd.R;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.view.VoiceView;

public class FragmentVoice extends FragmentBase {  
	VoiceView vl;
	
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidTools.log("FragmentVoice onCreate");
		
	}
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
    	View view = inflater.inflate(R.layout.layout_voice, container, false);  
		AndroidTools.log("FragmentVoice onCreateView");

    	vl = new VoiceView(getContext(), view);
    	view.setOnTouchListener(vl);
    	vl.setOnVoice(onVoice);
    	vl.setFileBegin(begin);
    	
	    return view;
    }  
    VoiceView.OnVoice onVoice;
    public void setCall(VoiceView.OnVoice onVoice){
    	this.onVoice = onVoice;
    	AndroidTools.log("FragmentVoice setCall");

    }
    //设置存储文件名前缀
    public void setFileBegin(String toid) {
		begin = toid;
	} 
    String begin = "";

    /**
     * 传递引用对象数据初始化
     *
     * @param data
     */
    @Override
    public void setData(Object data) {

    }

    /**
     * 更新数据 后 通知更新页面
     */
    @Override
    public void notifyDataSetChanged() {

    }

    /**
     * 数据广播传递 activity通过baseAc收到广播后派发给当前fragment
     *
     * @param msg
     */
    @Override
    public void onReceive(String msg) {

    }
}