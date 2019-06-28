package com.walker.dd.util;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.walker.common.util.Bean;
import com.walker.common.util.Tools;

public class AndroidTools {


    public static Intent putMapToIntent(Intent intent, Bean map) {
        if(intent != null) {
            Set<Object> keys = map.keySet();
            for (Object key : keys) {
                intent.putExtra(String.valueOf(key), String.valueOf(map.get(key)));
            }
            return intent;
        }else{
            AndroidTools.log("putMapToIntent intent is null");
            return intent;
        }
    }

    public static Bean getMapFromIntent(Intent intent) {
        Bean map = new Bean();
        Bundle b = intent.getExtras();
        Set<String> keys = b.keySet();
        for (String key : keys) {
            Object obj = b.get(key);
            if(obj != null)
                map.put(key, b.get(key));
            else{
                map.put(key, null);
            }
        }
        return map;
    }


    public static void dialogTip1(Context context, String tip, String ok, final Call<Integer> callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(tip);
        builder.setTitle("提示");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                callback.call(arg1);
                arg0.dismiss();
            }
        });
        builder.create().show();
    }


    /*
     * 剪切图片             AndroidTools.cutPhoto(this, uri,makePhoto, ACTIVITY_RESULT_CUT_WALL, 1000, 618);
     */
    public static void cutPhoto(Activity ac, Uri uri, String file, int code) {
        cutPhoto(ac, uri, file, code, 400, 400);
    }

    public static void cutPhoto(Activity ac, Uri uri, String file, int code, int w, int h) {
        if (uri == null) {
            return;
        }
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 100 * w / h);
        intent.putExtra("aspectY", 100);
        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", w);
//        intent.putExtra("outputY", h);

        //  intent.putExtra("outputFormat", "JPEG");// 图片格式
        //  intent.putExtra("outputFormat", "PNG");// 图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式， 无效？？
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);    //true返回bitmap,false 输出文件

        File temp = new File(file);
        Uri imageFileUri = Uri.fromFile(temp);//获取文件的Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);


        ac.startActivityForResult(intent, code);
    }







    public static Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;


    }


    public static boolean isOnClick(View view, float x, float y) {
        boolean res = false;
        int[] xy = new  int[2] ;
        view.getLocationInWindow(xy); //获取在当前窗口内的绝对坐标
//        view.getLocationOnScreen(xy);//获取在整个屏幕内的绝对坐标
        int xx = xy[0];
        int yy = xy[1];
        int w = view.getWidth();
        int h = view.getHeight();
        if (x < xx + w && x > xx && y < yy + h && y > yy) {
            res = true;
        }

        return res;
    }

    public static boolean isOnClick(float xx, float yy, float ww, float hh, float x, float y) {
        boolean res = false;

        if (x < xx + ww && x > xx && y < yy + hh && y > yy) {
            res = true;
        }

        return res;
    }


    public static void systemVoiceToast(Context context) {
        RingtoneManager.getRingtone(context.getApplicationContext(),
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
    }


    /**
     * 打开文件时调用
     * 高版本api 文件权限uri转换
     */
    public static void openFile(Activity ac, String filesPath) {
//        Uri uri = Uri.parse("file://" + filesPath);
        File file = new File(filesPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(ac, ac.getPackageName() + ".fileprovider", file);
            //【content://{$authority}/external/temp.apk】或【content://{$authority}/files/bqt/temp2.apk】
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//【file:///storage/emulated/0/temp.apk】
            uri = Uri.fromFile(file);
        }
        AndroidTools.log(ac, filesPath, uri);
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        String type = getMIMEType(filesPath);
        intent.setDataAndType(uri, type);
        if (!type.equals("*/*")) {
            try {
                ac.startActivity(intent);
            } catch (Exception e) {
                ac.startActivity(showOpenTypeDialog(filesPath));
            }
        } else {
            ac.startActivity(showOpenTypeDialog(filesPath));
        }

    }


    //显示打开方式
    public static void showOpenFile(Activity ac, String filesPath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        ac.startActivity(showOpenTypeDialog(filesPath));

    }

    public static Intent showOpenTypeDialog(String param) {
        AndroidTools.log("showOpenTypeDialog", param);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    /**
     * --获取文件类型 --
     */
    public static String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    /**
     * -- MIME 列表 --
     */
    public static final String[][] MIME_MapTable =
            {
                    // --{后缀名， MIME类型}   --
                    {".3gp", "video/3gpp"},
                    {".3gpp", "video/3gpp"},
                    {".aac", "audio/x-mpeg"},
                    {".amr", "audio/x-mpeg"},
                    {".apk", "application/vnd.android.package-archive"},
                    {".avi", "video/x-msvideo"},
                    {".aab", "application/x-authoware-bin"},
                    {".aam", "application/x-authoware-map"},
                    {".aas", "application/x-authoware-seg"},
                    {".ai", "application/postscript"},
                    {".aif", "audio/x-aiff"},
                    {".aifc", "audio/x-aiff"},
                    {".aiff", "audio/x-aiff"},
                    {".als", "audio/x-alpha5"},
                    {".amc", "application/x-mpeg"},
                    {".ani", "application/octet-stream"},
                    {".asc", "text/plain"},
                    {".asd", "application/astound"},
                    {".asf", "video/x-ms-asf"},
                    {".asn", "application/astound"},
                    {".asp", "application/x-asap"},
                    {".asx", " video/x-ms-asf"},
                    {".au", "audio/basic"},
                    {".avb", "application/octet-stream"},
                    {".awb", "audio/amr-wb"},
                    {".bcpio", "application/x-bcpio"},
                    {".bld", "application/bld"},
                    {".bld2", "application/bld2"},
                    {".bpk", "application/octet-stream"},
                    {".bz2", "application/x-bzip2"},
                    {".bin", "application/octet-stream"},
                    {".bmp", "image/bmp"},
                    {".c", "text/plain"},
                    {".class", "application/octet-stream"},
                    {".conf", "text/plain"},
                    {".cpp", "text/plain"},
                    {".cal", "image/x-cals"},
                    {".ccn", "application/x-cnc"},
                    {".cco", "application/x-cocoa"},
                    {".cdf", "application/x-netcdf"},
                    {".cgi", "magnus-internal/cgi"},
                    {".chat", "application/x-chat"},
                    {".clp", "application/x-msclip"},
                    {".cmx", "application/x-cmx"},
                    {".co", "application/x-cult3d-object"},
                    {".cod", "image/cis-cod"},
                    {".cpio", "application/x-cpio"},
                    {".cpt", "application/mac-compactpro"},
                    {".crd", "application/x-mscardfile"},
                    {".csh", "application/x-csh"},
                    {".csm", "chemical/x-csml"},
                    {".csml", "chemical/x-csml"},
                    {".css", "text/css"},
                    {".cur", "application/octet-stream"},
                    {".doc", "application/msword"},
                    {".dcm", "x-lml/x-evm"},
                    {".dcr", "application/x-director"},
                    {".dcx", "image/x-dcx"},
                    {".dhtml", "text/html"},
                    {".dir", "application/x-director"},
                    {".dll", "application/octet-stream"},
                    {".dmg", "application/octet-stream"},
                    {".dms", "application/octet-stream"},
                    {".dot", "application/x-dot"},
                    {".dvi", "application/x-dvi"},
                    {".dwf", "drawing/x-dwf"},
                    {".dwg", "application/x-autocad"},
                    {".dxf", "application/x-autocad"},
                    {".dxr", "application/x-director"},
                    {".ebk", "application/x-expandedbook"},
                    {".emb", "chemical/x-embl-dl-nucleotide"},
                    {".embl", "chemical/x-embl-dl-nucleotide"},
                    {".eps", "application/postscript"},
                    {".epub", "application/epub+zip"},
                    {".eri", "image/x-eri"},
                    {".es", "audio/echospeech"},
                    {".esl", "audio/echospeech"},
                    {".etc", "application/x-earthtime"},
                    {".etx", "text/x-setext"},
                    {".evm", "x-lml/x-evm"},
                    {".evy", "application/x-envoy"},
                    {".exe", "application/octet-stream"},
                    {".fh4", "image/x-freehand"},
                    {".fh5", "image/x-freehand"},
                    {".fhc", "image/x-freehand"},
                    {".fif", "image/fif"},
                    {".fm", "application/x-maker"},
                    {".fpx", "image/x-fpx"},
                    {".fvi", "video/isivideo"},
                    {".flv", "video/x-msvideo"},
                    {".gau", "chemical/x-gaussian-input"},
                    {".gca", "application/x-gca-compressed"},
                    {".gdb", "x-lml/x-gdb"},
                    {".gif", "image/gif"},
                    {".gps", "application/x-gps"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".gif", "image/gif"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".h", "text/plain"},
                    {".hdf", "application/x-hdf"},
                    {".hdm", "text/x-hdml"},
                    {".hdml", "text/x-hdml"},
                    {".htm", "text/html"},
                    {".html", "text/html"},
                    {".hlp", "application/winhlp"},
                    {".hqx", "application/mac-binhex40"},
                    {".hts", "text/html"},
                    {".ice", "x-conference/x-cooltalk"},
                    {".ico", "application/octet-stream"},
                    {".ief", "image/ief"},
                    {".ifm", "image/gif"},
                    {".ifs", "image/ifs"},
                    {".imy", "audio/melody"},
                    {".ins", "application/x-net-install"},
                    {".ips", "application/x-ipscript"},
                    {".ipx", "application/x-ipix"},
                    {".it", "audio/x-mod"},
                    {".itz", "audio/x-mod"},
                    {".ivr", "i-world/i-vrml"},
                    {".j2k", "image/j2k"},
                    {".jad", "text/vnd.sun.j2me.app-descriptor"},
                    {".jam", "application/x-jam"},
                    {".jnlp", "application/x-java-jnlp-file"},
                    {".jpe", "image/jpeg"},
                    {".jpz", "image/jpeg"},
                    {".jwc", "application/jwc"},
                    {".jar", "application/java-archive"},
                    {".java", "text/plain"},
                    {".jpeg", "image/jpeg"},
                    {".jpg", "image/jpeg"},
                    {".js", "application/x-javascript"},
                    {".kjx", "application/x-kjx"},
                    {".lak", "x-lml/x-lak"},
                    {".latex", "application/x-latex"},
                    {".lcc", "application/fastman"},
                    {".lcl", "application/x-digitalloca"},
                    {".lcr", "application/x-digitalloca"},
                    {".lgh", "application/lgh"},
                    {".lha", "application/octet-stream"},
                    {".lml", "x-lml/x-lml"},
                    {".lmlpack", "x-lml/x-lmlpack"},
                    {".log", "text/plain"},
                    {".lsf", "video/x-ms-asf"},
                    {".lsx", "video/x-ms-asf"},
                    {".lzh", "application/x-lzh "},
                    {".m13", "application/x-msmediaview"},
                    {".m14", "application/x-msmediaview"},
                    {".m15", "audio/x-mod"},
                    {".m3u", "audio/x-mpegurl"},
                    {".m3url", "audio/x-mpegurl"},
                    {".ma1", "audio/ma1"},
                    {".ma2", "audio/ma2"},
                    {".ma3", "audio/ma3"},
                    {".ma5", "audio/ma5"},
                    {".man", "application/x-troff-man"},
                    {".map", "magnus-internal/imagemap"},
                    {".mbd", "application/mbedlet"},
                    {".mct", "application/x-mascot"},
                    {".mdb", "application/x-msaccess"},
                    {".mdz", "audio/x-mod"},
                    {".me", "application/x-troff-me"},
                    {".mel", "text/x-vmel"},
                    {".mi", "application/x-mif"},
                    {".mid", "audio/midi"},
                    {".midi", "audio/midi"},
                    {".m4a", "audio/mp4a-latm"},
                    {".m4b", "audio/mp4a-latm"},
                    {".m4p", "audio/mp4a-latm"},
                    {".m4u", "video/vnd.mpegurl"},
                    {".m4v", "video/x-m4v"},
                    {".mov", "video/quicktime"},
                    {".mp2", "audio/x-mpeg"},
                    {".mp3", "audio/x-mpeg"},
                    {".mp4", "video/mp4"},
                    {".mpc", "application/vnd.mpohun.certificate"},
                    {".mpe", "video/mpeg"},
                    {".mpeg", "video/mpeg"},
                    {".mpg", "video/mpeg"},
                    {".mpg4", "video/mp4"},
                    {".mpga", "audio/mpeg"},
                    {".msg", "application/vnd.ms-outlook"},
                    {".mif", "application/x-mif"},
                    {".mil", "image/x-cals"},
                    {".mio", "audio/x-mio"},
                    {".mmf", "application/x-skt-lbs"},
                    {".mng", "video/x-mng"},
                    {".mny", "application/x-msmoney"},
                    {".moc", "application/x-mocha"},
                    {".mocha", "application/x-mocha"},
                    {".mod", "audio/x-mod"},
                    {".mof", "application/x-yumekara"},
                    {".mol", "chemical/x-mdl-molfile"},
                    {".mop", "chemical/x-mopac-input"},
                    {".movie", "video/x-sgi-movie"},
                    {".mpn", "application/vnd.mophun.application"},
                    {".mpp", "application/vnd.ms-project"},
                    {".mps", "application/x-mapserver"},
                    {".mrl", "text/x-mrml"},
                    {".mrm", "application/x-mrm"},
                    {".ms", "application/x-troff-ms"},
                    {".mts", "application/metastream"},
                    {".mtx", "application/metastream"},
                    {".mtz", "application/metastream"},
                    {".mzv", "application/metastream"},
                    {".nar", "application/zip"},
                    {".nbmp", "image/nbmp"},
                    {".nc", "application/x-netcdf"},
                    {".ndb", "x-lml/x-ndb"},
                    {".ndwn", "application/ndwn"},
                    {".nif", "application/x-nif"},
                    {".nmz", "application/x-scream"},
                    {".nokia-op-logo", "image/vnd.nok-oplogo-color"},
                    {".npx", "application/x-netfpx"},
                    {".nsnd", "audio/nsnd"},
                    {".nva", "application/x-neva1"},
                    {".oda", "application/oda"},
                    {".oom", "application/x-atlasMate-plugin"},
                    {".ogg", "audio/ogg"},
                    {".pac", "audio/x-pac"},
                    {".pae", "audio/x-epac"},
                    {".pan", "application/x-pan"},
                    {".pbm", "image/x-portable-bitmap"},
                    {".pcx", "image/x-pcx"},
                    {".pda", "image/x-pda"},
                    {".pdb", "chemical/x-pdb"},
                    {".pdf", "application/pdf"},
                    {".pfr", "application/font-tdpfr"},
                    {".pgm", "image/x-portable-graymap"},
                    {".pict", "image/x-pict"},
                    {".pm", "application/x-perl"},
                    {".pmd", "application/x-pmd"},
                    {".png", "image/png"},
                    {".pnm", "image/x-portable-anymap"},
                    {".pnz", "image/png"},
                    {".pot", "application/vnd.ms-powerpoint"},
                    {".ppm", "image/x-portable-pixmap"},
                    {".pps", "application/vnd.ms-powerpoint"},
                    {".ppt", "application/vnd.ms-powerpoint"},
                    {".pqf", "application/x-cprplayer"},
                    {".pqi", "application/cprplayer"},
                    {".prc", "application/x-prc"},
                    {".proxy", "application/x-ns-proxy-autoconfig"},
                    {".prop", "text/plain"},
                    {".ps", "application/postscript"},
                    {".ptlk", "application/listenup"},
                    {".pub", "application/x-mspublisher"},
                    {".pvx", "video/x-pv-pvx"},
                    {".qcp", "audio/vnd.qcelp"},
                    {".qt", "video/quicktime"},
                    {".qti", "image/x-quicktime"},
                    {".qtif", "image/x-quicktime"},
                    {".r3t", "text/vnd.rn-realtext3d"},
                    {".ra", "audio/x-pn-realaudio"},
                    {".ram", "audio/x-pn-realaudio"},
                    {".ras", "image/x-cmu-raster"},
                    {".rdf", "application/rdf+xml"},
                    {".rf", "image/vnd.rn-realflash"},
                    {".rgb", "image/x-rgb"},
                    {".rlf", "application/x-richlink"},
                    {".rm", "audio/x-pn-realaudio"},
                    {".rmf", "audio/x-rmf"},
                    {".rmm", "audio/x-pn-realaudio"},
                    {".rnx", "application/vnd.rn-realplayer"},
                    {".roff", "application/x-troff"},
                    {".rp", "image/vnd.rn-realpix"},
                    {".rpm", "audio/x-pn-realaudio-plugin"},
                    {".rt", "text/vnd.rn-realtext"},
                    {".rte", "x-lml/x-gps"},
                    {".rtf", "application/rtf"},
                    {".rtg", "application/metastream"},
                    {".rtx", "text/richtext"},
                    {".rv", "video/vnd.rn-realvideo"},
                    {".rwc", "application/x-rogerwilco"},
                    {".rar", "application/x-rar-compressed"},
                    {".rc", "text/plain"},
                    {".rmvb", "audio/x-pn-realaudio"},
                    {".s3m", "audio/x-mod"},
                    {".s3z", "audio/x-mod"},
                    {".sca", "application/x-supercard"},
                    {".scd", "application/x-msschedule"},
                    {".sdf", "application/e-score"},
                    {".sea", "application/x-stuffit"},
                    {".sgm", "text/x-sgml"},
                    {".sgml", "text/x-sgml"},
                    {".shar", "application/x-shar"},
                    {".shtml", "magnus-internal/parsed-html"},
                    {".shw", "application/presentations"},
                    {".si6", "image/si6"},
                    {".si7", "image/vnd.stiwap.sis"},
                    {".si9", "image/vnd.lgtwap.sis"},
                    {".sis", "application/vnd.symbian.install"},
                    {".sit", "application/x-stuffit"},
                    {".skd", "application/x-koan"},
                    {".skm", "application/x-koan"},
                    {".skp", "application/x-koan"},
                    {".skt", "application/x-koan"},
                    {".slc", "application/x-salsa"},
                    {".smd", "audio/x-smd"},
                    {".smi", "application/smil"},
                    {".smil", "application/smil"},
                    {".smp", "application/studiom"},
                    {".smz", "audio/x-smd"},
                    {".sh", "application/x-sh"},
                    {".snd", "audio/basic"},
                    {".spc", "text/x-speech"},
                    {".spl", "application/futuresplash"},
                    {".spr", "application/x-sprite"},
                    {".sprite", "application/x-sprite"},
                    {".sdp", "application/sdp"},
                    {".spt", "application/x-spt"},
                    {".src", "application/x-wais-source"},
                    {".stk", "application/hyperstudio"},
                    {".stm", "audio/x-mod"},
                    {".sv4cpio", "application/x-sv4cpio"},
                    {".sv4crc", "application/x-sv4crc"},
                    {".svf", "image/vnd"},
                    {".svg", "image/svg-xml"},
                    {".svh", "image/svh"},
                    {".svr", "x-world/x-svr"},
                    {".swf", "application/x-shockwave-flash"},
                    {".swfl", "application/x-shockwave-flash"},
                    {".t", "application/x-troff"},
                    {".tad", "application/octet-stream"},
                    {".talk", "text/x-speech"},
                    {".tar", "application/x-tar"},
                    {".taz", "application/x-tar"},
                    {".tbp", "application/x-timbuktu"},
                    {".tbt", "application/x-timbuktu"},
                    {".tcl", "application/x-tcl"},
                    {".tex", "application/x-tex"},
                    {".texi", "application/x-texinfo"},
                    {".texinfo", "application/x-texinfo"},
                    {".tgz", "application/x-tar"},
                    {".thm", "application/vnd.eri.thm"},
                    {".tif", "image/tiff"},
                    {".tiff", "image/tiff"},
                    {".tki", "application/x-tkined"},
                    {".tkined", "application/x-tkined"},
                    {".toc", "application/toc"},
                    {".toy", "image/toy"},
                    {".tr", "application/x-troff"},
                    {".trk", "x-lml/x-gps"},
                    {".trm", "application/x-msterminal"},
                    {".tsi", "audio/tsplayer"},
                    {".tsp", "application/dsptype"},
                    {".tsv", "text/tab-separated-values"},
                    {".ttf", "application/octet-stream"},
                    {".ttz", "application/t-time"},
                    {".txt", "text/plain"},
                    {".ult", "audio/x-mod"},
                    {".ustar", "application/x-ustar"},
                    {".uu", "application/x-uuencode"},
                    {".uue", "application/x-uuencode"},
                    {".vcd", "application/x-cdlink"},
                    {".vcf", "text/x-vcard"},
                    {".vdo", "video/vdo"},
                    {".vib", "audio/vib"},
                    {".viv", "video/vivo"},
                    {".vivo", "video/vivo"},
                    {".vmd", "application/vocaltec-media-desc"},
                    {".vmf", "application/vocaltec-media-file"},
                    {".vmi", "application/x-dreamcast-vms-info"},
                    {".vms", "application/x-dreamcast-vms"},
                    {".vox", "audio/voxware"},
                    {".vqe", "audio/x-twinvq-plugin"},
                    {".vqf", "audio/x-twinvq"},
                    {".vql", "audio/x-twinvq"},
                    {".vre", "x-world/x-vream"},
                    {".vrml", "x-world/x-vrml"},
                    {".vrt", "x-world/x-vrt"},
                    {".vrw", "x-world/x-vream"},
                    {".vts", "workbook/formulaone"},
                    {".wax", "audio/x-ms-wax"},
                    {".wbmp", "image/vnd.wap.wbmp"},
                    {".web", "application/vnd.xara"},
                    {".wav", "audio/x-wav"},
                    {".wma", "audio/x-ms-wma"},
                    {".wmv", "audio/x-ms-wmv"},
                    {".wi", "image/wavelet"},
                    {".wis", "application/x-InstallShield"},
                    {".wm", "video/x-ms-wm"},
                    {".wmd", "application/x-ms-wmd"},
                    {".wmf", "application/x-msmetafile"},
                    {".wml", "text/vnd.wap.wml"},
                    {".wmlc", "application/vnd.wap.wmlc"},
                    {".wmls", "text/vnd.wap.wmlscript"},
                    {".wmlsc", "application/vnd.wap.wmlscriptc"},
                    {".wmlscript", "text/vnd.wap.wmlscript"},
                    {".wmv", "video/x-ms-wmv"},
                    {".wmx", "video/x-ms-wmx"},
                    {".wmz", "application/x-ms-wmz"},
                    {".wpng", "image/x-up-wpng"},
                    {".wps", "application/vnd.ms-works"},
                    {".wpt", "x-lml/x-gps"},
                    {".wri", "application/x-mswrite"},
                    {".wrl", "x-world/x-vrml"},
                    {".wrz", "x-world/x-vrml"},
                    {".ws", "text/vnd.wap.wmlscript"},
                    {".wsc", "application/vnd.wap.wmlscriptc"},
                    {".wv", "video/wavelet"},
                    {".wvx", "video/x-ms-wvx"},
                    {".wxl", "application/x-wxl"},
                    {".x-gzip", "application/x-gzip"},
                    {".xar", "application/vnd.xara"},
                    {".xbm", "image/x-xbitmap"},
                    {".xdm", "application/x-xdma"},
                    {".xdma", "application/x-xdma"},
                    {".xdw", "application/vnd.fujixerox.docuworks"},
                    {".xht", "application/xhtml+xml"},
                    {".xhtm", "application/xhtml+xml"},
                    {".xhtml", "application/xhtml+xml"},
                    {".xla", "application/vnd.ms-excel"},
                    {".xlc", "application/vnd.ms-excel"},
                    {".xll", "application/x-excel"},
                    {".xlm", "application/vnd.ms-excel"},
                    {".xls", "application/vnd.ms-excel"},
                    {".xlt", "application/vnd.ms-excel"},
                    {".xlw", "application/vnd.ms-excel"},
                    {".xm", "audio/x-mod"},
                    {".xml", "text/xml"},
                    {".xmz", "audio/x-mod"},
                    {".xpi", "application/x-xpinstall"},
                    {".xpm", "image/x-xpixmap"},
                    {".xsit", "text/xml"},
                    {".xsl", "text/xml"},
                    {".xul", "text/xul"},
                    {".xwd", "image/x-xwindowdump"},
                    {".xyz", "chemical/x-pdb"},
                    {".yz1", "application/x-yz1"},
                    {".z", "application/x-compress"},
                    {".zac", "application/x-zaurus-zac"},
                    {".zip", "application/zip"},
                    {"", "*/*"}
            };



    public static void post(View view, Runnable run) {
        view.postDelayed(run, 100);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static boolean fileExist(String str) {
        File f = new File(str);
        return f.exists() && f.isFile() && f.length() > 10;
    }


    public static String getLocalIp(Context c) {
        WifiManager wifiManager = (WifiManager) c
                .getSystemService(c.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();
        // 返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }
    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);

    }
    public static void toast(Context c, Object...objects) {
        String str = Tools.objects2string(objects);
        toast(c, str);
    }
    public static void toast(Context c, String str){
        Toast.makeText(c, str, Toast.LENGTH_SHORT).show();
        log("toast." + str);
    }
    public static void out(Object...objects) {
        log(objects);
    }

    public static void log(Object...objects){
        String str = Tools.objects2string(objects);
        log(str);
    }
    public static void log(String str) {
        Log.e("tools.", str);
    }

    //指定为数序列生成器
    public static String hexs = "0123456789ABCDEF"; //16
    public static String rgb = "AA0099";    //6 * 16

    public static double deta = hexs.length() / 16;   //AA
    /**
     * 获取随机颜色 FF FF FF AA
     *
     button1.setBackgroundColor(0xFFFF00FF);
     button1.setBackgroundColor(Color.parseColor("#FFFCCC"));
     */
    public static int getRandomColor() {
        String str = "#";
        for(int i = 0; i < rgb.length(); i++){
            str += hexs.charAt((int) (Math.random() * hexs.length()));
        }
        return Color.parseColor(str);
    }



    public static int screenH = 0;
    public static int screenW = 0;

    public static void init(Activity activity){
        //初始化屏幕尺寸
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenH = dm.heightPixels;
        screenW = dm.widthPixels;

    }

    public static int getScreenWidth(){
        return screenW;
    }
    public static int getScreenHeight(){
        return screenH;
    }



    public static <T> int listIndex(List<T> list, T item, Comparator<T> compre){
        int res = -1;
        for(int i = list.size() - 1; i >= 0; i--){
            if(compre.compare(list.get(i), item) == 0){
                res = i;
            }
        }
        return res;
    }
    public static <T> int listIndexRemoveAll(List<T> list, T item, Comparator<T> compre){
        List<T> cp = new ArrayList<>();
        int res = -1;
        for(int i = list.size() - 1; i >= 0; i--){
            if(compre.compare(list.get(i), item) == 0){
                res++;
            }else{
                cp.add(list.get(i));
            }
        }
        list.clear();
        cp.addAll(cp);
        return res;
    }

    public static <T> int listReplaceIndex(int index, List<T> list, T item, Comparator<T> compre){
        for(int i = list.size() - 1; i >= 0; i--){
            if(compre.compare(list.get(i), item) == 0){
                list.remove(i);
            }
        }
        list.add(index, item);
        return list.size();
    }

    /**
     * 移除新集合中存在的 并把新集合中的置顶添加
     * @param list
     * @param items
     * @param compre
     * @param <T>
     * @return
     */
    public static <T> int listReplaceIndexAndAdd(int index, List<T> list, List<T> items, Comparator<T> compre){
        try {
            List<T> on = new ArrayList<>();
            for (int i = list.size() - 1; i >= 0; i--) {
                for (int j = items.size() - 1; j >= 0; j--) {
                    if (compre.compare(list.get(i), items.get(j)) == 0) {
//                        list.remove(i);
                        continue;
                    }else{
                        on.add(list.get(i) );
                    }
                }
            }
            list.clear();
            list.addAll(on );
            list.addAll(index, items);
        }catch (Exception e){
            e.printStackTrace();
            log("listReplaceIndexAndAdd", e.toString(), list, items, index);
        }
        return list.size();
    }





    /**
     * 拍照 ACTIVITY_RESULT_TAKEPHOTO 存入path
     * @param ac
     * @param path
     */
    public static  void takePhoto(Activity ac, String path ){
        AndroidTools.log("takePhoto", ac, path);
        File file = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = ac.getPackageName() + ".fileprovider"; //【清单文件中provider的authorities属性的值】
            uri = FileProvider.getUriForFile(ac, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        ac.startActivityForResult(intent, Constant.ACTIVITY_RESULT_TAKEPHOTO);

    }
    /**
     * Date 2017-5-7 下午6:15:52
     * Desc: 选择图片调用
     *
     * @param ac
     * @param code
     */
    public static void chosePhoto(Activity ac, int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ac.startActivityForResult(Intent.createChooser(intent, "选择图片"), code);
    }

    /**
     * 查询最近10张照片路径
     * @param ac
     * @return
     */
    public static List<Bean> searchPhoto(Context ac, int count){
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor mCursor = ac.getContentResolver().query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "  + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
        if(mCursor == null){  return new ArrayList<>();   }
        int i = 0;
        List<Bean> list = new ArrayList<>();
        while(mCursor.moveToNext() && i++ < count){//只显示最多30张图片
            Bean map = new Bean();
            map.put("chose", "false");
            map.put("path", mCursor.getString(mCursor .getColumnIndex(MediaStore.Images.Media.DATA)));
            list.add(map );
        }
        return list;
    }




    /**
     * 选择文件 ACTIVITY_RESULT_PATH
     *     //intent.setType(“image/*”);//选择图片
     *     //intent.setType(“audio/*”); //选择音频
     *     //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
     *     //intent.setType(“video/*;image/*”);//同时选择视频和图片
     * @param ac
     * @param type 类型
     */
    public static void choseFile(Activity ac, String type){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type.length() > 0 ? type : "*/*");
        ac.startActivityForResult(Intent.createChooser(intent, "选择文件"), Constant.ACTIVITY_RESULT_PATH );
    }



}
