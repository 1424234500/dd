package com.walker.dd.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;


/**
 * @author Walker
 * @date 2017-3-1 上午10:26:27
 * Description: 组装图片文字
 */
public class ImageText extends LinearLayout  {
	private Context context = null;
	public ImageView iv = null;
	public TextView tv = null;
	
	
	private  static int DEFAULT_IMAGE_WIDTH = 90;
	private  static int DEFAULT_IMAGE_HEIGHT = 90;
	int imgs[] = new int[2];
	int colors[] = new int[2];
	String text = "tip";
	
	 

	public ImageText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.item_image_text, this, true);
		iv = (ImageView)parentView.findViewById(R.id.iv);
		tv = (TextView)parentView.findViewById(R.id.tv);
	
	}
	//设置图片
	public void setImages(int img1, int img2){
	    img2 = img2 == 0 ? img1 : img2;
		imgs[0] = img1;
		imgs[1] = img2;
		if(iv != null){
			iv.setImageResource(img1);
            iv.setBackgroundColor(Color.argb(0, 0, 0, 0));
			setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
		}
	}
	//设置文本
	public void setText(String str, int color1, int color2 ){
	    color2 = color2 == 0 ? color1 : color2;
		colors[0] = color1;
		colors[1] = color2;
		text = str;
		tv.setText(str);
		if(color1 != 0) {
            tv.setTextColor(color1);
        }
		if(imgs[0] == 0){
            iv.setBackgroundColor(AndroidTools.getRandomColor());
        }
	}
	
	public  void setImageSize(int w, int h){
		this.DEFAULT_IMAGE_HEIGHT = h;
		this.DEFAULT_IMAGE_WIDTH = w;
		if(iv != null){
			ViewGroup.LayoutParams params = iv.getLayoutParams();
			params.width = w;
			params.height = h;
			iv.setLayoutParams(params);
		}
	}
	  
	public void setChecked(boolean flag){
		int i = flag?1:0;
		
		iv.setImageResource(imgs[i]);
		setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
//		iv.setShowPoint(false);
		tv.setTextColor( colors[i]);
		tv.setText(text);

	}

	public String getText() {
		return this.text;
	}

	public void setShowPoint(boolean flag){
//		iv.setShowPoint(flag);
	}


}
