package com.walker.dd.util;

import android.os.Environment;

import com.walker.common.util.FileUtil;
import com.walker.dd.R;
import com.walker.dd.service.NetModel;

public class Constant {
    public static final String BROAD_URL = "broad_url";
    public static final String BROAD_KEY = "msg";
    public static final int[] SRLColors = {android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_green_light};
    public static final int NUM = 15;   //分页参数


    //本地文件存储路径
// /storage/emulated/0/mycc/record/100-1493005573881.amr 
    public static final String root = Environment.getExternalStorageDirectory() + "/aadd/";
//    public static final String root = Environment.getDataDirectory() + "/mydd/";
	public static final String dirVoice = root + "record/";
	public static final String dirPhoto =  root + "photo/";  
	public static final String dirFile =  root + "file/";  
	public static final String dirCamera = root +  "camera/";  
	public static final String dirProfile = root +  "profile/";  
	public static final String dirProfileWall = root +  "profilewall/";



	public static final String split = "OTOTO";

	protected static final int maxChatNum = 32;//最多聊天界面记录




    public static String TAKEPHOTO = "";  //拍照临时文件路径

    /**
     * 头像选择,剪切,文件选择
     */
    public static final int ACTIVITY_RESULT_PROFILEWALL = 0;
    public static final int ACTIVITY_RESULT_PROFILE = 1;
    public static final int ACTIVITY_RESULT_PROFILEWALL_CUT = 2;
    public static final int ACTIVITY_RESULT_PROFILE_CUT = 3;
    public static final int ACTIVITY_RESULT_PATH = 4;		//文件路径 图片/文件选取
    public static final int ACTIVITY_RESULT_FILE = 5;		//文件选取
    public static final int ACTIVITY_RESULT_TAKEPHOTO = 6;	//拍照

	
	//聊天图片加载最大高度
	public static int photoMaxH = 800;
	//发送图片压缩高度
	public static int photoSend = 800;
	//相册加载最大高度
	public static int ablumMaxH = 600;
	//手机信息
	public static int screenH = 1980;
	public static int screenW = 1080;
	
	//emoji表情大小
	public static int emojiWH = 80;


    /**
     * 通过文件后缀或者文件名获取文件图标
     */
	public static int getFileImageByType(String type) {
		type = type.toLowerCase();
		type = FileUtil.getFileType(type);
		int id = R.drawable.icon_filetype_unkonwn;
		if(type.equals("apk")){
			id = R.drawable.icon_filetype_apk;
		}else if(type.equals("doc") || type.equals("docx") ){
			id = R.drawable.icon_filetype_doc ;
		} else if(type.equals("dir")){
			id = R.drawable.icon_filetype_dir ;
		} else if(type.equals("html") || type.equals("htm") || type.equals("jsp") || type.equals("asp")){
			id = R.drawable.icon_filetype_html ;
		} else if(type.equals("png") || type.equals("jpg") || type.equals("jpeg") || type.equals("gif") || type.equals("bmp")){
			id = R.drawable.icon_filetype_img ;
		} else if(type.equals("ipa")){
			id = R.drawable.icon_filetype_ipa ;
		} else if(type.equals("mp3") || type.equals("ape")){
			id = R.drawable.icon_filetype_music ;
		} else if(type.equals("pdf")){
			id = R.drawable.icon_filetype_pdf;
		} else if(type.equals("ppt") || type.equals("pptx")){
			id = R.drawable.icon_filetype_ppt;
		} else if(type.equals("bt")){
			id = R.drawable.icon_filetype_torrent;
		} else if(type.equals("txt")){
			id = R.drawable.icon_filetype_txt;
		} else if(type.equals("vcf")){
			id = R.drawable.icon_filetype_vcf;
		}  else if(type.equals("mp4") || type.equals("mkv") || type.equals("rmvb") || type.equals("avi")){
			id = R.drawable.icon_filetype_vedio;
		}  else if(type.equals("vsd")){
			id = R.drawable.icon_filetype_vsd;
		}  else if(type.equals("xls") || type.equals("xlsx")){
			id = R.drawable.icon_filetype_xls;
		}  else if(type.equals("zip")){
			id = R.drawable.icon_filetype_zip;
		}

		return id ;
	}
	
	
}
