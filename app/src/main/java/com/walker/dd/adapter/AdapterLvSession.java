package com.walker.dd.adapter;

import java.util.List;


import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.service.NetModel;
import com.walker.dd.struct.Session;
import com.walker.dd.util.EmotionUtils;
import com.walker.dd.util.KeyUtil;
import com.walker.dd.util.picasso.NetImage;

import com.walker.mode.*;
import com.walker.socket.server_1.plugin.*;
/**
 * 会话列表
 *
 */
public   class AdapterLvSession extends BaseAdapter    {
	private Context context; // 运行上下文

	private List<Session>  listItems = null; // listview的数据集合
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
	    Session bean = listItems.get(position);
		viewHolder = null;  

		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
				viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_session, null);	// 获取list_item布局文件的视图
//            convertView = getla.inflate(R.layout.item_session, null);	// 获取list_item布局文件的视图
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

        /**
         .set(Key.ID, msg.getUserTo().equals(NowUser.getId())?from.getId() : msg.getUserTo()) //当前会话id 关联from to
         .set(Key.NAME, "name")  //会话名 关联from to
         .set(Key.TYPE, Key.TEXT)  //文本类型
         .set(Key.FROM, msg.getUserFrom()) //消息来自谁发的 User[id, name, pwd]
         .set(Key.TO, msg.getUserTo) //消息发送的目标 user id group id
         .set(Key.TEXT, data.get(Key.TEXT))    //文本内容
         .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))   //时间
         .set(Key.NUM, 1)  //红点数
         */

//                .set(Key.ID, toId)
//                .set(Key.NAME, toName)
//                .set(Key.TIME, time)
//                .set(Key.TYPE, type)
//                .set(Key.TEXT, text)
//                .set(Key.NUM, num)
//        User from = new User(bean.get(Key.FROM, new Bean()));

		viewHolder.tvusername.setText(bean.getName()) ;
		String type = bean.getType();
		if(type.equals(Key.VOICE)){
			viewHolder.tvmsg.setText("[语音]");
		}else if(type.equals(Key.FILE)){
			viewHolder.tvmsg.setText("[文件]");
		}else if(type.equals(Key.PHOTO)){
			viewHolder.tvmsg.setText("[图片]");
		}else {
            SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolder.tvmsg,bean.getText());
            viewHolder.tvmsg.setText(spannableString);
            viewHolder.tvmsg.setText(bean.getText());
        }
		
			
		viewHolder.tvtime.setText(bean.getTime());
		int t = bean.getNum();
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
        NetImage.loadProfile(context, bean.getId(), viewHolder.ivprofile);


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
		
	
	public AdapterLvSession(Context context, List<Session> listItems) {
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
