package com.walker.dd.adapter;


import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.service.NetModel;
import com.walker.dd.util.Constant;
import com.walker.dd.util.KeyUtil;
import com.walker.dd.util.picasso.NetImage;
import com.walker.socket.server_1.Key;

/**
 * 登陆 输入框 的下拉lv适配器,暴露接口 回调函数
 */
public class AdapterLvIds extends BaseAdapter {
	private Context context; // 运行上下文
	List<Bean>  listItems = null; // listview的数据集合
	private LayoutInflater listContainer; // 视图容器

	// private boolean[] hasChecked; //记录商品选中状态

	public final class ListItemView { // 自定义控件集合
		public ImageView ivprofile; 
		public ImageView ivdel;
		public TextView tvid; 
		// public CheckBox check;
		// public Button detail;
	}

	public AdapterLvIds(Context context, List<Bean> liststr) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = liststr;
	 
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return listItems.size();
	}

	@Override
	public Object getItem(int i) {
		// TODO 自动生成的方法存根
		return listItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		// TODO 自动生成的方法存根
		return 0;
	}
	ListItemView listItemView ;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int posi = position;
		Bean bean = listItems.get(position);

		// 自定义视图
		listItemView = null; //根据item_layout构建的一条数据
		if (convertView == null) {
			listItemView = new ListItemView();
			
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listview_popup_item, null);
			// 获取控件对象
			listItemView.ivdel = (ImageView) convertView .findViewById(R.id.ivdel);
			listItemView.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
			listItemView.tvid = (TextView) convertView .findViewById(R.id.tvid);

		// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
	 
		
		// 设置文字和图片
	 	listItemView.tvid.setText(bean.get(Key.ID, "")) ;
	 	NetImage.loadProfile(context, bean.get(Key.ID, "default"), listItemView.ivprofile);

        listItemView.ivdel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                if(onClick != null)
                    onClick.onDel(  listItems.get( posi ));			}
		});
		listItemView.ivprofile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                if(onClick != null)
                    onClick.onOk(  listItems.get( posi ));			}
		});
		listItemView.tvid.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    if(onClick != null)
				onClick.onOk(  listItems.get( posi ));
			}
		});

		 
		return convertView;
	}
	OnClick onClick;
	public void setOnClick(OnClick onClick){
	    this.onClick = onClick;
    }
	public interface OnClick{
	    public void onOk(Bean item);
	    public void onDel(Bean item);
    }


}
