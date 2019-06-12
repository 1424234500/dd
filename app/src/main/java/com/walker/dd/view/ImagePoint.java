package com.walker.dd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.walker.dd.R;


/**
 * @author Walker
 * @date 2017-3-1 下午1:34:40
 * Description: 组装图片和小红点
 */
public class ImagePoint extends RelativeLayout  {
	Context context;
	ImageView ivPoint;
	ImageView ivImage;
	
	public ImagePoint(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.item_image_point, this, true);
		ivPoint = (ImageView)parentView.findViewById(R.id.image_point_point);
		ivImage = (ImageView)parentView.findViewById(R.id.image_point_image);
		
		setShowPoint(false);
	}
	public void setImageResource(int id){
		ivImage.setImageResource(id);
	}
	public void setPoint(int id){
		ivPoint.setImageResource(id);
	}
	
	
	public void setShowPoint(boolean flag){
		if(flag){
			ivPoint.setVisibility(VISIBLE);
			ivPoint.setAlpha(255);
		}else{
			ivPoint.setVisibility(INVISIBLE);
			ivPoint.setAlpha(0);

		}
	}
		
	 
	
	
	

}
