package com.walker.dd.service;

import com.walker.common.util.Bean;
import com.walker.common.util.HttpUtil;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.Watch;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.core.OkHttpUtil;
import com.walker.mode.PushBindModel;
import com.walker.mode.PushModel;
import com.walker.mode.PushType;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebService extends Model {
    /**
     * 私有构造器
     */
    private WebService() {
    }

    /**
     * 私有静态内部类
     */
    private static class SingletonFactory {
        private static WebService instance;

        static {
            System.out.println("静态内部类初始化" + SingletonFactory.class);
            instance = new WebService();
        }
    }

    /**
     * 内部类模式 可靠
     */
    public static WebService getInstance() {
        return SingletonFactory.instance;
    }

    public OkHttpClient getClient() {
        OkHttpClient res = OkHttpUtil.getInstance().getClient();
        return res;
    }

    public Request.Builder getRequestBuilderWithToken() {
        return new Request.Builder()
                .header("User-Agent", "com.walker.dd")
                .header("TOKEN", NowUser.getToken())

                ;
    }

    public String get(String url, Bean data) throws Exception {
        String str = "";
        Watch watch = new Watch("get");
        try {
    //        String url = NetModel.getServerWebUrl() + "/push/push.do";
            url = NetModel.getServerWebUrl() + url;
            url = HttpUtil.makeUrl(url, data, "utf-8");
            watch.put("url", url);
            Request request = getRequestBuilderWithToken()
                    .url(url)
                    .get()
                    .build();
            watch.putln("header:" + String.valueOf(request.headers().toMultimap()));

            OkHttpClient okHttpClient = getClient();
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 404) {
                AndroidTools.log("post error 404 retry login", url, data, String.valueOf(response.body()));
                AndroidTools.toast("404 retry login");
                NowUser.setToken("");
                throw new RuntimeException("404");
            }
            str = String.valueOf(response.body().string());
            watch.res(str);
        }catch (Exception e){
            watch.exceptionWithThrow(e);
            throw new RuntimeException(e);
        }finally {
            AndroidTools.log(watch);

        }
        return str;
    }

    public String post(String url, Bean data) throws Exception {
//        String url = NetModel.getServerWebUrl() + "/push/push.do";

        String str = "";
        Watch watch = new Watch("post url:" + url + " data:" + data );
        try {
            url = NetModel.getServerWebUrl() + url;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (Object key : data.keySet()) {
                builder.addFormDataPart(String.valueOf(key), String.valueOf(data.get(key)));
            }
            RequestBody requestBody = builder.build();
            Request request = getRequestBuilderWithToken()
                    .url(url)
                    .post(requestBody)
                    .build();
            watch.putln("header:" + String.valueOf(request.headers().toMultimap()));

            OkHttpClient okHttpClient = getClient();
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 404) {
                AndroidTools.log("post error 404 retry login", url, data, String.valueOf(response.body()));
                AndroidTools.toast("404 retry login");
                NowUser.setToken("");
                throw new RuntimeException("404");
            }
            str = String.valueOf(response.body().string());
            watch.res(str);
        }catch (Exception e){
            watch.exceptionWithThrow(e);
            throw new RuntimeException(e);
        }finally {
            AndroidTools.log(watch);

        }
        return str;
    }


    public String getToken(String id, String pwd) {
        String res = "";
        Watch watch = new Watch("get");
        try {
            String url = NetModel.getServerWebUrl() + "/shiro/login.do";
            Bean data = new Bean()
                    .put("username", id)
                    .put("password", pwd);
            String str = OkHttpUtil.post(url, data);
//        http://127.0.0.1:8090/shiro/login.do?username=guest&password=a
//        {
//            "costTime": 222,
//                "data": {
//            "USER": {
//                "iD": "guest",
//                        "nAME": "guest",
//                        "pWD": "a",
//                        "sIGN": "sign"
//            },
//            "TOKEN": "T:guest:20200102162557"
//           },
//            "flag": true,
//                "info": "登录成功"
//        }
            Bean bean = JsonUtil.get(str);
            res = bean.get("data", new Bean()).get("TOKEN", "");
            if (res.length() == 0) {
                AndroidTools.toast("login web error ");
                throw new RuntimeException("login web error");
            }
            watch.res(res);
        }catch (Exception e){
            watch.exceptionWithThrow(e);
            throw new RuntimeException(e);
        }finally {
            AndroidTools.log(watch);
        }

        return res;
    }


    /**
     * 推送给目标用户
     */
    public Integer push(PushModel pushModel) {

        return -1;
    }

    /**
     * 绑定用户id和推送id和推送类别
     */
    public Boolean bind(String userId, String deviceId, String pushId, String type) {
        boolean res = false;

        try {
//http://127.0.0.1:8090/push/bind.do?USER_ID=001&DEVICE_ID=deviceId001&PUSH_ID=deviceId&TYPE=jpush
            String str = get("/push/bind.do", new Bean()
                    .put("USER_ID", userId)
                    .put("DEVICE_ID", deviceId)
                    .put("PUSH_ID", pushId)
                    .put("TYPE", type)
            );
            Bean bean = JsonUtil.get(str);
            res = bean.get("flag", false);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidTools.toast("bind push error ", userId, deviceId, pushId, type, e.getMessage());
        }
//{
//    "costTime": 675,
//        "data": [
//            {
//                "dEVICE_ID": "deviceId001",
//                    "iD": "",
//                    "pUSH_ID": "deviceId",
//                    "s_ATIME": "",
//                    "s_FLAG": "",
//                    "s_MTIME": "",
//                    "tYPE": "jpush",
//                    "uSER_ID": "001"
//            }
//        ],
//        "flag": true,
//        "info": "[001, deviceId001, PushBindModel{ID='null', S_MTIME='2020-01-02 17:48:45', S_ATIME='null', S_FLAG='1', USER_ID='001', PUSH_ID='deviceId', DEVICE_ID='deviceId001', TYPE='jpush'}, jpush]"
//}
        return res;

    }

    /**
     * 绑定用户id和推送id和推送类别
     *
     * @param userId
     * @return
     */
    public List<PushBindModel> findBind(String userId) {
        return null;
    }

    /**
     * 取消绑定用户id和推送id和推送类别
     * 0则取消所有
     *
     * @param pushBindModels
     * @return
     */
    public List<PushBindModel> unbind(List<PushBindModel> pushBindModels) {
        return null;
    }

    /**
     * 取消绑定用户id和推送id和推送类别
     *
     * @param userId
     * @return
     */
    public boolean unbind(String userId) {
        boolean res = false;
        try {
            //http://127.0.0.1:8090/push/bind.do?USER_ID=001&DEVICE_ID=deviceId001&PUSH_ID=deviceId&TYPE=jpush
            String str = get("/push/unbind.do", new Bean()
                    .put("USER_ID", userId));
            Bean bean = JsonUtil.get(str);
//        {
//            "costTime": 67,
//                "data": [
//            {
//                "dEVICE_ID": "deviceId001",
//                    "iD": "",
//                    "pUSH_ID": "deviceId",
//                    "s_ATIME": "",
//                    "s_FLAG": "",
//                    "s_MTIME": "",
//                    "tYPE": "jpush",
//                    "uSER_ID": "001"
//            }
//  ],
//            "flag": true,
//                "info": "[001]"
//        }
            AndroidTools.out(str);
            res = bean.get("flag", false);

        } catch (Exception e) {
            e.printStackTrace();
            AndroidTools.toast("unbind error", userId, e.getMessage());

        }
        return res;

    }


}
