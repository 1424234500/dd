package com.walker.dd.core.picasso.transform;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;
import com.walker.dd.core.MyImage;


//图片处理
	public class PicassoResizeTransform implements Transformation {
	    int maxHeight = 200;
	    public PicassoResizeTransform(int maxWidthOrHeight){
	        this.maxHeight = maxWidthOrHeight;
        }
    @Override
    public Bitmap transform(Bitmap source) {
        int h = source.getHeight();
        int w = source.getWidth();
        float ra = MyImage.calculateInSampleSizeFloat(w, h, maxHeight, maxHeight);
        w /= ra;
        h /= ra;

        Bitmap result = Bitmap.createScaledBitmap(source, w, h, false);
        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle();
        }
        return result;
    }
    @Override
    public String key() {
        return "resize";
    }

	  
	}  