package com.walker.dd.adapter;

import java.util.*;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;

/**
 * IMAGE, TEXT
 */
public class AdapterGvOther extends BaseAdapter{
    // 运行上下文
	private Context context;
    // 数据集引用
	private List<Bean>  listItems = null;
    // 视图容器
	private LayoutInflater layoutInflater;
	//布局类型
    //自定义控件集合  布局类型
	public final class ViewHolder {
		public ImageView iv;  
		public TextView tv;
	}
	 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.item_image_text, null);	// 获取list_item布局文件的视图
            viewHolder.iv = convertView .findViewById(R.id.iv);
            viewHolder.tv = convertView .findViewById(R.id.tv);

			convertView.setTag(viewHolder);// 设置控件集到convertView
		} else {//若有可复用布局
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 设置属性
//        viewHolder.iv.setImageResource(R.drawable.profile);

        viewHolder.iv.setBackgroundColor(AndroidTools.getRandomColor());
        viewHolder.tv.setText(listItems.get(position).get("TEXT", ""));

		
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
		
	
	public AdapterGvOther(Context context, List<Bean> listItems) {
		this.layoutInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.context = context;
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