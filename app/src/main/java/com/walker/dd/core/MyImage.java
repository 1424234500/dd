package com.walker.dd.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

	/**
	 * 处理图片的工具类.
	 */
public class MyImage {

	    public static final int LEFT = 0;
	    public static final int RIGHT = 1;
	    public static final int TOP = 3;
	    public static final int BOTTOM = 4;

	    /** */
	    /**
	     * 图片去色,返回灰度图片
	     * 
	     * @param bmpOriginal
	     *            传入的图片
	     * @return 去色后的图片
	     */
	    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
	        int width, height;
	        height = bmpOriginal.getHeight();
	        width = bmpOriginal.getWidth();
	        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
	                Config.RGB_565);
	        Canvas c = new Canvas(bmpGrayscale);
	        Paint paint = new Paint();
	        ColorMatrix cm = new ColorMatrix();
	        cm.setSaturation(0);
	        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	        paint.setColorFilter(f);
	        c.drawBitmap(bmpOriginal, 0, 0, paint);
	        return bmpGrayscale;
	    }

	    /** */
	    /**
	     * 去色同时加圆角
	     * 
	     * @param bmpOriginal
	     *            原图
	     * @param pixels
	     *            圆角弧度
	     * @return 修改后的图片
	     */
	    public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
	        return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	    }

	    /** */
	    /**
	     * 把图片变成圆角
	     * 

	     *            需要修改的图片
	     * @param pixels
	     *            圆角的弧度
	     * @return 圆角图片
	     */
	    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	                bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);
	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);
	        final float roundPx = pixels;
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	        return output;
	    }

	    /** */
	    /**
	     * 使圆角功能支持BitampDrawable
	     * 
Drawable
	     * @param pixels
	     * @return
	     */
	    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
	            int pixels) {
	        Bitmap bitmap = bitmapDrawable.getBitmap();
	        bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
	        return bitmapDrawable;
	    }

	    /**
	     * 读取路径中的图片，然后将其转化为缩放后的bitmap
	     * 
	     * @param path
	     */
	    public static void saveBefore(String path) {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        // 获取这个图片的宽和高
	        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
	        options.inJustDecodeBounds = false;
	        // 计算缩放比
	        int be = (int) (options.outHeight / (float) 200);
	        if (be <= 0)
	            be = 1;
	        options.inSampleSize = 2; // 图片长宽各缩小二分之一
	        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
	        bitmap = BitmapFactory.decodeFile(path, options);
	        int w = bitmap.getWidth();
	        int h = bitmap.getHeight();
	        System.out.println(w + " " + h);
	        // savePNG_After(bitmap,path);
	        saveJPGE_After(bitmap, path);
	    }

	    /**
	     * 保存图片为PNG
	     * 

	     * @param name
	     */
	    public static void savePNG(String name) {
	    	savePNG_After(MyImage.getBitmapByDecodeFile(name), name);
	    }
	    public static void savePNG_After(Bitmap bitmap, String name) {
	        File file = new File(name);
	        try {
	            FileOutputStream out = new FileOutputStream(file);
	            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
	                out.flush();
	                out.close();
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * 保存图片为JPEG
	     * 

	     * @param path
	     */
	    public static void saveJPGE_After(Bitmap bitmap, String path) {
	        File file = new File(path);
	        try {
	            FileOutputStream out = new FileOutputStream(file);
	            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
	                out.flush();
	                out.close();
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * 水印
	     * 

	     * @return
	     */
	    public static Bitmap createBitmapForWatermark(Bitmap src, Bitmap watermark) {
	        if (src == null) {
	            return null;
	        }
	        int w = src.getWidth();
	        int h = src.getHeight();
	        int ww = watermark.getWidth();
	        int wh = watermark.getHeight();
	        // create the new blank bitmap
	        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
	        Canvas cv = new Canvas(newb);
	        // draw src into
	        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
	        // draw watermark into
	        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
	        // save all clip
	        cv.save();// 保存
	        // store
	        cv.restore();// 存储
	        return newb;
	    }

	    /**
	     * 图片合成
	     * 
	     * @return
	     */
	    public static Bitmap potoMix(int direction, Bitmap... bitmaps) {
	        if (bitmaps.length <= 0) {
	            return null;
	        }
	        if (bitmaps.length == 1) {
	            return bitmaps[0];
	        }
	        Bitmap newBitmap = bitmaps[0];
	        // newBitmap = createBitmapForFotoMix(bitmaps[0],bitmaps[1],direction);
	        for (int i = 1; i < bitmaps.length; i++) {
	            newBitmap = createBitmapForFotoMix(newBitmap, bitmaps[i], direction);
	        }
	        return newBitmap;
	    }

	    private static Bitmap createBitmapForFotoMix(Bitmap first, Bitmap second,
	            int direction) {
	        if (first == null) {
	            return null;
	        }
	        if (second == null) {
	            return first;
	        }
	        int fw = first.getWidth();
	        int fh = first.getHeight();
	        int sw = second.getWidth();
	        int sh = second.getHeight();
	        Bitmap newBitmap = null;
	        if (direction == LEFT) {
	            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
	                    Config.ARGB_8888);
	            Canvas canvas = new Canvas(newBitmap);
	            canvas.drawBitmap(first, sw, 0, null);
	            canvas.drawBitmap(second, 0, 0, null);
	        } else if (direction == RIGHT) {
	            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
	                    Config.ARGB_8888);
	            Canvas canvas = new Canvas(newBitmap);
	            canvas.drawBitmap(first, 0, 0, null);
	            canvas.drawBitmap(second, fw, 0, null);
	        } else if (direction == TOP) {
	            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh,
	                    Config.ARGB_8888);
	            Canvas canvas = new Canvas(newBitmap);
	            canvas.drawBitmap(first, 0, sh, null);
	            canvas.drawBitmap(second, 0, 0, null);
	        } else if (direction == BOTTOM) {
	            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh,
	                    Config.ARGB_8888);
	            Canvas canvas = new Canvas(newBitmap);
	            canvas.drawBitmap(first, 0, 0, null);
	            canvas.drawBitmap(second, 0, fh, null);
	        }
	        return newBitmap;
	    }

	    /**
	     * 将Bitmap转换成指定大小
	     * 

	     * @param width
	     * @param height
	     * @return
	     */
	    public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
	        return Bitmap.createScaledBitmap(bitmap, width, height, true);
	    }

	    /**
	     * Drawable 转 Bitmap
	     * 
	     * @param drawable
	     * @return
	     */
	    public static Bitmap drawableToBitmapByBD(Drawable drawable) {
	        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
	        return bitmapDrawable.getBitmap();
	    }

	    /**
	     * Bitmap 转 Drawable
	     * 

	     * @return
	     */
	    public static Drawable bitmapToDrawableByBD(Bitmap bitmap) {
	        Drawable drawable = new BitmapDrawable(bitmap);
	        return drawable;
	    }

	    /**
	     * 把一个文件转化为字节
	     * 
	     * @param file
	     * @return byte[]
	     * @throws Exception
	     */
	    public static byte[] getByte(File file) throws Exception {
	        byte[] bytes = null;
	        if (file != null) {
	            InputStream is = new FileInputStream(file);
	            int length = (int) file.length();
	            if (length > Integer.MAX_VALUE) // 当文件的长度超过了int的最大值
	            {
	                System.out.println("this file is max ");
	                return null;
	            }
	            bytes = new byte[length];
	            int offset = 0;
	            int numRead = 0;
	            while (offset < bytes.length
	                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
	                offset += numRead;
	            }
	            // 如果得到的字节长度和file实际的长度不一致就可能出错了
	            if (offset < bytes.length) {
	                System.out.println("file length is error");
	                return null;
	            }
	            is.close();
	        }
	        return bytes;
	    }

	    /**
	     * byte[] 转 bitmap
	     * 
	     * @param b
	     * @return
	     */
	    public static Bitmap bytesToBimap(byte[] b) {
	        if (b.length != 0) {
	            return BitmapFactory.decodeByteArray(b, 0, b.length);
	        } else {
	            return null;
	        }
	    }

	    /**
	     * bitmap 转 byte[]
	     * 
	     * @param bm
	     * @return
	     */
	    public static byte[] bitmapToBytes(Bitmap bm) {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
	        return baos.toByteArray();
	    }



		public static Bitmap toBitmap(String string){
			//将字符串转换成Bitmap类型
			Bitmap bitmap=null;
			try {
				byte[]bitmapArray;
				bitmapArray= Base64.decode(string, Base64.DEFAULT);
				bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}
	
	
	
	public static Bitmap getBitmapByDecodeFile(String path){
		return getBitmapByDecodeFile(path,Constant.photoSend );
	}
	   /**  
	 * 通过DecodeFile解析SD卡中的图片   , oom问题，！

	 * @return Bitmap  
	 */  
	public static Bitmap getBitmapByDecodeFile(String path, int maxHeight ){  
	//	Tools.out("获取图片:" + path);
	    Bitmap newBit = null;  
	     try {  
	    	 BitmapFactory.Options opt = new BitmapFactory.Options();    
	    	 opt.inPreferredConfig = Config.RGB_565;
	    	 opt.inPurgeable = true;    
	    	 opt.inInputShareable = true; 
	    	 opt.inJustDecodeBounds = true;  //设为true时，构造出的bitmap=null，单opt会被赋值长宽等配置信息，但比较快，设为false时，才有图片  
	    
//	    	 FileInputStream fis = new FileInputStream(new File(path));  
//	           BufferedInputStream bis = new BufferedInputStream(fis);  
//	          BitmapFactory.decodeStream(bis, null, opt);

	           BitmapFactory.decodeFile(path, opt);
	          
	           // 调用上面定义的方法计算inSampleSize值  
	           opt.inSampleSize = calculateInSampleSize(opt, maxHeight, maxHeight);  
	           // 使用获取到的inSampleSize值再次解析图片  
	           opt.inJustDecodeBounds = false;  
	           
	           newBit = BitmapFactory.decodeFile(path, opt);
	           
//	           bis.close();  
//	           fis.close();  
	        }  catch (Exception e) {  
	           e.printStackTrace();  
	        }  
	     return newBit;  
	}  
	public static int calculateInSampleSize(BitmapFactory.Options options,  int reqWidth, int reqHeight) {  
	    // 源图片的高度和宽度  
	    final int height = options.outHeight;  
	    final int width = options.outWidth;  
	    int inSampleSize = 1;  
	    if (height > reqHeight || width > reqWidth) {  
	        // 计算出实际宽高和目标宽高的比率  
	        final int heightRatio = Math.round((float) height / (float) reqHeight);  
	        final int widthRatio = Math.round((float) width / (float) reqWidth);  
	        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
	        // 一定都会大于等于目标的宽和高。  
	        inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;  
	    }  
	    return inSampleSize;  
	}  
	public static float calculateInSampleSizeFloat(int w, int h,  int reqWidth, int reqHeight) {  
	    // 源图片的高度和宽度  
	    final int height = h;  
	    final int width = w;  
	    float inSampleSize = 1;  
	    if (height > reqHeight || width > reqWidth) {  
	        // 计算出实际宽高和目标宽高的比率  
	        final float heightRatio = (float) height / (float) reqHeight ;  
	        final float widthRatio =  (float) width / (float) reqWidth ;  
	        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
	        // 一定都会大于等于目标的宽和高。  
	        inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;  
	    }  
	    return inSampleSize;  
	} 
	   /**  
		 * 通过inputstream解析SD卡中的图片   , oom问题，！
	
		 * @return Bitmap  
		 */  
		public static Bitmap getBitmapByInputStream(InputStream is ){  
		    Bitmap newBit = null;  
		     try {  
		    	 BitmapFactory.Options opt = new BitmapFactory.Options();    
		    	 opt.inPreferredConfig = Config.RGB_565;
		    	 opt.inPurgeable = true;    
		    	 opt.inInputShareable = true; 
		    	 //为了节约内存
		    	// inSampleSize  这个成员变量 根据图片实际的宽高和我们期望的宽高来计算得到这个值
		    	 opt.inDither=false;    /*不进行图片抖动处理*/
		    	 opt.inPreferredConfig=null;  /*设置让解码器以最佳方式解码*/
		    	 // options.inJustDecodeBounds  设为true时，构造出的bitmap没有图片，只有一些长宽等配置信息，但比较快，设为false时，才有图片  
		           Bitmap bitmap= BitmapFactory.decodeStream(is, null, opt); 
		           int w = bitmap.getWidth();
		           int h = bitmap.getHeight();
		           w = w*600/h;
		           newBit  = Bitmap.createScaledBitmap(bitmap, w, 600, false);  
		        }  catch (Exception e) {  
		           e.printStackTrace();  
		        }  
		     return newBit;  
		}  
		
}