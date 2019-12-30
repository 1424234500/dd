package com.walker.dd.view;


import com.walker.dd.R;
import com.walker.dd.core.AndroidTools;

import android.app.Dialog;
import android.content.Context;

public class DialogBeats extends Dialog {  
 
	
    public DialogBeats(Context context) {  
        super(context, R.style.NoTitleDialog);
        setContentView(R.layout.dialog_beats);
//        setContentView(new android.widget.ProgressBar(context));
        
        android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = AndroidTools.getScreenWidth(); //宽度设置为屏幕的0.5
        this.getWindow().setAttributes(p); //设置生效
        
    }

	 
} 