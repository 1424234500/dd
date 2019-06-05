package com.walker.dd.adapter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.util.EmotionUtils;

/**
 * 会话列表
 *
 */
public   class AdapterLvSession extends BaseAdapter    {
	private Context context; // 运行上下文
	private List<Bean>  listItems = null; // listview的数据集合
	private LayoutInflater layoutInflater; // 视图容器
	//控件集合实例
	private ViewHolder viewHolder ;
	//布局类型
    //自定义控件集合  布局类型
	public final class ViewHolder {
		public ImageView ivprofile; 
		public TextView tvusername; 
		public TextView tvmsg;
		public TextView tvtime;
		public TextView tvnum;
		
	}
	 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolder = null;  

		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
				viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.listview_adapter_session_item, null);	// 获取list_item布局文件的视图
//            convertView = getla.inflate(R.layout.listview_adapter_session_item, null);	// 获取list_item布局文件的视图
				viewHolder.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolder.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolder.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolder.tvmsg = (TextView) convertView .findViewById(R.id.tvmsg);
				viewHolder.tvnum = (TextView) convertView .findViewById(R.id.tvnum);
				
				convertView.setTag(viewHolder);// 设置控件集到convertView
		} else {//若有可复用布局
				viewHolder = (ViewHolder) convertView.getTag();
		}
		// 设置文字和图片和监听
		//	viewHolder.tvusername.setText("aaaaaaaaaaaaaaaaaa") ;
        //NAME TEXT VOICE FILE PHOTO NUM PROFILEPATH

		viewHolder.tvusername.setText(listItems.get(position).get("NAME", "")) ;
		//viewHolder.tvmsg.setText(listItems.get(position, "MSG")) ;
		if(listItems.get(position).get("MSG", "").equals("TEXT")){
			SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolder.tvmsg,listItems.get(position).get("MSG", ""));
			viewHolder.tvmsg.setText(spannableString);
		}else if(listItems.get(position).get("MSG", "").equals("VOICE")){
			viewHolder.tvmsg.setText("[语音]");
		}else if(listItems.get(position).get("MSG", "").equals("FILE")){
			viewHolder.tvmsg.setText("[文件]");
		}else if(listItems.get(position).get("MSG", "").equals("PHOTO")){
			viewHolder.tvmsg.setText("[图片]");
		}else {
			SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolder.tvmsg,listItems.get(position).get("MSG", ""));
			viewHolder.tvmsg.setText(spannableString);
		}
		
			
		viewHolder.tvtime.setText(listItems.get(position).get("TIME", ""));
		int t = listItems.get(position).get("NUM", 0);
		if(t <= 0){
			viewHolder.tvnum.setText( "") ;
			viewHolder.tvnum.setVisibility(View.INVISIBLE);
		}else if(t > 99){
			viewHolder.tvnum.setText( "99+") ;
			viewHolder.tvnum.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvnum.setText( ""+t) ;
			viewHolder.tvnum.setVisibility(View.VISIBLE);
		}
//	 	NetImage.loadProfile(context, listItems.get(position, "PROFILEPATH"), viewHolder.ivprofile);
		 
		return convertView; 
	}
	
	//必须实现，通知adapter有几种布局类型
		@Override
		public int getViewTypeCount() {
			return 1;
		}
		//必须实现，让adapter可控布局类型
		@Override
		public int getItemViewType(int position) {
			 return 1;
		}
		
	
	public AdapterLvSession(Context context, List<Bean> listItems) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.listItems = listItems;
	 
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int i) {
		return listItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

 

	 
	

}
