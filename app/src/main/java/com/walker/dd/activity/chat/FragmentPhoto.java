package com.walker.dd.activity.chat;
 
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.adapter.AdapterAblum;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.view.HorizontalListView;

public class FragmentPhoto extends FragmentBase {  
  
	List<Bean> listPhoto = new ArrayList<>();
	AdapterAblum adapterAblum;
	TextView tvok;
	TextView tvablum;
	View viewPhoto;
	
 
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidTools.log("FragmentPhoto onCreate");
 
	}


	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		AndroidTools.log("FragmentPhoto onCreateView");

    	viewPhoto = inflater.inflate(R.layout.layout_photo, null);
	   	     tvablum = ((TextView)viewPhoto.findViewById(R.id.tvablum));
	   	   tvok = ((TextView)viewPhoto.findViewById(R.id.tvok));
	   	   tvok.setOnClickListener(new OnClickListener() {
		   @Override
			public void onClick(View arg0) {
			   List<String> files = new ArrayList<String>();
				for(int i = 0; i < listPhoto.size(); i++){
					//chose,path
					if(listPhoto.get(i).get("chose").equals("true")){
						files.add(listPhoto.get(i).get("path", ""));
						listPhoto.get(i).put("chose", "false");
					}
				}
//				Object[] obj = listPhoto.toArray();
//				listPhoto.clear();//????????????
 				adapterAblum.notifyDataSetChanged();
				tvok.setClickable(false);
				tvok.setBackgroundResource( R.drawable.selector_button_login_disable);
			 
				 if(call != null){
					 call.onCall(files);
				 }
//				 for(int i = 0; i < obj.length; i++){
//					 listPhoto.add((Map<String, String>) obj[i]);
//				 }
				 
			}
   	   });
   	   
   	   HorizontalListView lvablum = ((HorizontalListView)viewPhoto.findViewById(R.id.lvablum));
   	   tvablum.setOnClickListener(new OnClickListener() {
   		@Override
   		public void onClick(View arg0) {
//   			chosePhoto();
   		}
   	   });
   	   adapterAblum = new AdapterAblum(getContext(), listPhoto);
   	   lvablum.setAdapter(adapterAblum);
   	   adapterAblum.setOnChose(new AdapterAblum.OnChose(){
    	 public void onChose(int position){
    		   if(! tvok.isClickable()){
    			   tvok.setClickable(true);
    			   tvok.setBackgroundResource( R.drawable.selector_button_login);
    		   }
    	 }  
		@Override
		public void onInChose(int position) {
			for(int i = 0; i < listPhoto.size(); i++){
				//chose,path
				if(listPhoto.get(i).get("chose").equals("true")){
					return;
				}
			}
			tvok.setClickable(false);
			tvok.setBackgroundResource( R.drawable.selector_button_login_disable);
		  }
    	});
   	   
   	   openChosePhoto();
   	   
	   return viewPhoto;
    }  
    
    
 	public static String TAKEPHOTO = "";

	public void resultChosePath(String path) {
		List<String> list = new ArrayList<String>();
		list.add(path);
		if(call != null){
			call.onCall(list);
		}
	}

	public void resultTakePhoto() {
		List<String> list = new ArrayList<String>();
		list.add(this.TAKEPHOTO);
		if(call != null){
			call.onCall(list);
		}
	}

	public void openChosePhoto(){
		//重构选择数据
		listPhoto.clear();
		//tvSend.setClickable(true);
		//tvSend.setBackgroundResource( R.drawable.selector_button_login);
		tvok.setClickable(false);
		tvok.setBackgroundResource(R.drawable.selector_button_login_disable);
	   
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	    Cursor mCursor = getContext().getContentResolver().query(mImageUri, null,
	                  MediaStore.Images.Media.MIME_TYPE + "=? or "  + MediaStore.Images.Media.MIME_TYPE + "=?",
	                  new String[] { "image/jpeg", "image/png" }, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
	    if(mCursor == null){  return;   }
	    int i = 0;
	    while(mCursor.moveToNext() && i++ < 10){//只显示最多30张图片
	    	   Bean map = new Bean();
	    	   map.put("chose", "false");
	    	   map.put("path", mCursor.getString(mCursor .getColumnIndex(MediaStore.Images.Media.DATA)));
	    	   listPhoto.add(map);
	    }
	   // Tools.log(Tools.list2strings(listPhoto));
	    adapterAblum.notifyDataSetChanged();
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
    	public void onCall(List<String> files);
    }
    public void setCall(Call call){
    	this.call = call;
    }
    
     
  
}  