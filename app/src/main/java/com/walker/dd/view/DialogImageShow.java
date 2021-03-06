package com.walker.dd.view;



import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.walker.dd.R;
import com.walker.dd.core.AndroidTools;

public class DialogImageShow extends Dialog {  
	ImageViewDrable isv;
	
    public DialogImageShow(Context context) {  
        super(context, R.style.BlackBackDialog);
		isv = new ImageViewDrable(context);
        setContentView(isv );  
        android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = AndroidTools.getScreenWidth(); //宽度设置为屏幕的0.5
        p.height = AndroidTools.getScreenHeight(); //宽度设置为屏幕的0.5

        this.getWindow().setAttributes(p); //设置生效
    }
    public DialogImageShow(Context context, String path) {  
    	this(context);
    	
    	isv.setBitmap(BitmapFactory.decodeFile(path));
    }
    public DialogImageShow(Context context, Bitmap bitmap) {
        this(context);

        isv.setBitmap(bitmap);
    }
      
} 