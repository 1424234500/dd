package com.walker.dd.activity.chat;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.walker.common.util.Bean;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.adapter.AdapterGvImage;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.core.Constant;
import com.walker.dd.core.EmotionUtils;

import com.walker.mode.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
  
public class FragmentEmoji extends FragmentBase implements  OnItemClickListener {  
  
       
    
	//ID TEXT
	List<Bean> listEmoji;
	AdapterGvImage adapterGridEmoji;
	
	
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidTools.log("FragmentEmoji onCreate");
		listEmoji = new ArrayList<Bean>();
	   	 Bean map;
		 Map<String, Integer> emojiMap =  EmotionUtils.getEmojiMap();
		 Object[] keys = emojiMap.keySet().toArray();
		 for(int i = 0; i < keys.length; i++){
			 map = new Bean();
             map.put(Key.ID, "" + keys[i] );
             map.put(Key.TEXT, "" + keys[i] );
			 map.put(Key.PROFILE, "" + emojiMap.get(keys[i]));
			 listEmoji.add(map);  
		 }  
		
		
		
		
	}


	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
//    	   View view = inflater.inflate(R.layout.fragment_two, container, false);  
//           mBtn = (Button) view.findViewById(R.id.id_fragment_two_btn);  
		AndroidTools.log("FragmentEmoji onCreateView");

		 AdapterGvImage adapterGridEmoji = new AdapterGvImage(getContext(), listEmoji);
//		 Tools.tip("emoji. getContext=" + (getContext()==null?"null":"not null"));
//		 Tools.tip("emoji. getActivity=" + (getActivity()==null?"null":"not null"));
		 
		 GridView viewEmoji = new GridView( getContext() );
		 viewEmoji.setNumColumns(7);
	     viewEmoji.setAdapter(adapterGridEmoji);
	     viewEmoji.setOnItemClickListener(this);
	     
	     return viewEmoji;
    }  
    
    Call call;

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

    public interface Call {
    	public void onCall(SpannableString spannableString);
    }
    public void setCall(Call call){
    	this.call = call;
    }
    
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int i, long l) {
		if(call != null){
		    Bean bean = listEmoji.get(i);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bean.get(Key.PROFILE, 0) );
            bitmap = Bitmap.createScaledBitmap(bitmap, Constant.emojiWH, Constant.emojiWH, true);
            ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
            SpannableString spannableString = new SpannableString(bean.get(Key.TEXT, ""));
            spannableString.setSpan(imageSpan, 0, bean.get(Key.TEXT, "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
          
			call.onCall(spannableString );
		}
	}  
  
}  