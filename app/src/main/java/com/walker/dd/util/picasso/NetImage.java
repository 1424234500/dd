package com.walker.dd.util.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.walker.dd.R;
import com.walker.dd.activity.Application;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.KeyUtil;
import com.walker.dd.util.MyImage;
import com.walker.dd.util.picasso.transform.PicassoRoundTransform;


public class NetImage {

    public static void init(Application app) {
//        String imageCacheDir = app.getExternalCacheDir().getPath()+"/image/";
        String imageCacheDir = KeyUtil.getFileLocal("");

        Picasso picasso = new Picasso
                .Builder(app)
                .downloader(new OkHttpDownloader(new File(imageCacheDir)))
                .build();
        /**  * 左上角会显示个三角形，不同的颜色代表加载的来源* 红色：代表从网络下载的图片* 黄色：代表从磁盘缓存加载的图片* 绿色：代表从内存中加载的图片*/
        picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }



	public static void pause(Context context){
        AndroidTools.out("listview 滚动，暂停加载图片");
        Picasso.with(context).pauseTag(context);
	}
	public static void resume(Context context){
        AndroidTools.out("listview 空闲，开始加载图片");
        Picasso.with(context).resumeTag(context);
	}

    /**
     *
     * @param context
     * @param id
     * @param imageView
     */
    public static void loadProfile(Context context, String id, ImageView imageView) {
        loadRound(context, KeyUtil.getProfile(id), imageView);
    }
    /**
     * 加载图片 圆的
     * @param context
     * @param key    若key对应的本地文件存在则按本地 否则按http网络加载
     * @param imageView
     */
    public static void loadRound(Context context, String key, ImageView imageView) {
        String path = KeyUtil.getFileLocal(key);
        if(new File(path).exists()){
            AndroidTools.out("picasso local " + path);
            Picasso .with(context) .load(new File(path)).placeholder(R.drawable.loading)
                    .error(R.drawable.loaderror)
                    .transform( new PicassoRoundTransform()  )
                    .into(imageView);
        }else{
            String url = KeyUtil.getFileHttp(key);
            AndroidTools.out("picasso http " + url);
            Picasso .with(context) .load(url).placeholder(R.drawable.loading)
                    .error(R.drawable.loaderror)
                    .transform( new PicassoRoundTransform()  )
                    .into(imageView);
        }
    }
    public static void loadRound(Context context, int resId, ImageView imageView) {
        Picasso .with(context) .load(resId).placeholder(R.drawable.loading)
                .error(R.drawable.loaderror)
                .transform( new PicassoRoundTransform()  )
                .into(imageView);
    }
        /**
         * 加载图片 方的
         * @param context
         * @param key    若key对应的本地文件存在则按本地 否则按http网络加载
         * @param imageView
         */
    public static void load(Context context, String key, ImageView imageView) {
        String path = KeyUtil.getFileLocal(key);
        if(new File(path).exists()){
            AndroidTools.out("picasso local " + path);
            Picasso .with(context) .load(new File(path)).placeholder(R.drawable.loading)
                    .error(R.drawable.loaderror)
                    .into(imageView);
        }else{
            String url = KeyUtil.getFileHttp(key);
            AndroidTools.out("picasso http " + url);
            Picasso .with(context) .load(url).placeholder(R.drawable.loading)
                    .error(R.drawable.loaderror)
                    .into(imageView);
        }
    }
    public static void load(Context context, int resId, ImageView imageView) {
        Picasso .with(context) .load(resId).placeholder(R.drawable.loading)
                .error(R.drawable.loaderror)
                .into(imageView);
    }
	//加载本地图片并放缩
	public static void loadLocalImgResize(Context context, String url, ImageView imageView){
		loadLocalImgResize(context, url, Constant.ablumMaxH, imageView);
	}
	public static void loadLocalImgResize(Context context, String url, final int maxHeight, ImageView imageView){
	
//		//高速加载文件oom？
//		 BitmapFactory.Options opt = new BitmapFactory.Options();    
//    	 opt.inPreferredConfig = Bitmap.Config.RGB_565;     
//    	 opt.inPurgeable = true;    
//    	 opt.inInputShareable = true; 
//    	 opt.inJustDecodeBounds = true;  //设为true时，构造出的bitmap=null，单opt会被赋值长宽等配置信息，但比较快，设为false时，才有图片  
//
//         BitmapFactory.decodeFile(url, opt);
//         int w = opt.outWidth;
//         int h = opt.outHeight;
//        float ra = MyImage.calculateInSampleSizeFloat( opt, maxHeight, maxHeight);
//		w *= ra;
//		h *= ra;
//		
//		loadLocalImgResize(context, url, w, h, imageView);
		
 //		loadLocalImgResize(context, url,maxHeight, maxHeight, imageView);
		 
		Picasso.with(context) 
		.load(new File(url))
		.placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror)
	    .transform(new Transformation() {
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
				return "key";
			}
		})
	    .into(imageView);
	}
	public static void loadLocalImgResize(Context context, String url, int width, int height, ImageView imageView){
		Picasso.with(context) 
		.load(new File(url))
		.placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror)
	    .resize(width, height)
	    .centerCrop().into(imageView);
	}
	
	public static void loadImage(Context context,int resourceId, ImageView imageView){
		Picasso  .with(context)  .load(resourceId) .placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror) .into(imageView);
	}
	public static void loadImage(Context context,int resourceId, int width, int height, ImageView imageView){
		Picasso  .with(context)  .load(resourceId) .placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror) 
		  .resize(width, height)
		  .centerCrop().into(imageView);
	}
	
	 
	
	public static Bitmap getNetImage(Context context, String url)  {
		try {
			return Picasso .with(context) .load(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static void clear(Context context, String url) {
		Picasso.with(context).invalidate(url);
	}
	 
	 
	 
}