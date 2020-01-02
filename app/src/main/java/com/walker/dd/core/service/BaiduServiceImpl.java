package com.walker.dd.core.service;

import android.content.Context;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.core.OkHttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaiduServiceImpl {
    static String SP_KEY = "BAIDU_ACCESS_TOKEN";

    //    Content-Type	application/x-www-form-urlencoded
//    content	string	是	待审核文本，UTF-8，不可为空，不超过20000字节
//    access_token	通过API Key和Secret Key获取的access_token,
    private static Request getBaiduRequestTextScan(Context context, String token, String content)  {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("access_token", token);
        builder.add("content", content);
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(AppServiceImpl.getBaiduTextScan(context))
                .post(requestBody)
                .build();

        return request;
    }
    private static void getToken(Context context, OnAuth onAuth) throws IOException {
        String token = SharedPreferencesServiceImpl.getTime(context, SP_KEY, "");
        if(token.length() == 0) {
            getAuth(context, onAuth);
        }else{
            onAuth.onAuth(token);
        }
    }

    public static Bean parseResponse(Response response) throws IOException {
        String str = response.body().string();
        AndroidTools.log(str);

        Bean bean = JsonUtil.get(str);
        List<String> res = new ArrayList<>();
        if(bean.get("spam", "").length() > 0){
            List<Bean> reject = bean.get("reject", new ArrayList<Bean>());
            for(Bean item : reject){
                if(item.get("label", -1) == 5){
                    res.add(item.get("hit", item.toString()) );
                }
            }
            bean.put("res", res);
        }
        return bean;
    }


    public interface OnRes{
        void onException(Exception e);

        /**
         * 0    异常信息
         * 1	暴恐违禁	默认开启，高级设置可选择关闭
         * 2	文本色情	默认开启，高级设置可选择关闭
         * 3	政治敏感	默认开启，高级设置可选择关闭
         * 4	恶意推广	默认开启，高级设置可选择关闭
         * 5	低俗辱骂	默认开启，高级设置可选择关闭
         * 6	低质灌水	默认关闭，高级设置可选择开启
         */
        void onRes(int lable, String info, List<String> list);


    }
    static String[] infos = {"异常信息", "暴恐违禁", "文本色情", "政治敏感", "恶意推广", "低俗辱骂", "低质灌水"};
    public static String getInfo(int lable){
        lable = lable % infos.length;
        return infos[lable];
    }
    public static void doTextScan(Context context, String content, OnRes onRes)  {
        try {
            getToken(context, new OnAuth() {
                @Override
                public void onAuth(String token)  {
                    OkHttpUtil.getInstance().getClient().newCall(getBaiduRequestTextScan(context, token, content)).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            onRes.onException(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                                try {
                                    String str = response.body().string();
                                    AndroidTools.log(str);

                                    Bean all = JsonUtil.get(str);
                                    Bean bean = all.get("result", new Bean());
                                    List<List<String>> las = new ArrayList<>();
                                    for (int i = 0; i < 6; i++) {
                                        las.add(new ArrayList<String>());
                                    }
                                    if (bean.get("spam", "").length() > 0) {
                                        List<Bean> reject = bean.get("reject", new ArrayList<Bean>());
                                        for (Bean item : reject) {
                                            las.get(item.get("label", 0)).add(item.get("hit", item.toString()));
                                        }
                                        for (int i = 0; i < las.size(); i++) {
                                            List<String> item = las.get(i);
                                            if (item.size() > 0) {
                                                onRes.onRes(i, getInfo(i), item);
                                            }
                                        }
                                    } else {
                                        onRes.onException(new Exception(str));
                                    }
                                }catch (Exception e){
                                    onRes.onException(e);
                                }
                        }
                    });
                }
            });
        } catch (IOException e) {
            onRes.onException(e);
        }

    }


    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public interface OnAuth{
        public void onAuth(String token) throws IOException;
    }
    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static void getAuth(Context context, OnAuth onAuth) throws IOException {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + AppServiceImpl.getBaiduApiKey(context)
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + AppServiceImpl.getBaiduSecretKey(context)
                ;

        OkHttpUtil.get(getAccessTokenUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Bean bean = JsonUtil.get(str);
                String access_token = bean.get("access_token", "");
                SharedPreferencesServiceImpl.putTime(context, SP_KEY, access_token, 10 * 24 * 3600 * 1000);
                onAuth.onAuth(access_token);

            }
        });
    }

/*
    参数名称	类型	详细说明
    logid	uint64	正确调用生成的唯一标识码，用于问题定位
    result	object	包含审核结果详情
+spam	int	请求中是否包含违禁，0表示非违禁，1表示违禁，2表示建议人工复审
+reject	array	审核未通过的类别列表与详情
+review	array	待人工复审的类别列表与详情
+pass	array	审核通过的类别列表与详情
++label	int	请求中的违禁类型
++score	float	违禁检测分，范围0~1，数值从低到高代表风险程度的高低
++hit	array	违禁类型对应命中的违禁词集合，可能为空

            违禁labels类型说明
    取值	详细说明	默认状态
1	暴恐违禁	默认开启，高级设置可选择关闭
2	文本色情	默认开启，高级设置可选择关闭
3	政治敏感	默认开启，高级设置可选择关闭
4	恶意推广	默认开启，高级设置可选择关闭
5	低俗辱骂	默认开启，高级设置可选择关闭
6	低质灌水	默认关闭，高级设置可选择开启

            审核通过的返回示例

    {
        "result": {
        "spam": 0,
        "reject": [],
        "review": [],
        "pass": [
            {"label":1,"score":0.3,"hit":[]},
            {"label":2,"score":0.33,"hit":[]},
            {"label":3,"score":0.2,"hit":[]},
            {"label":4,"score":0.31,"hit":[]},
            {"label":5,"score":0.19,"hit":[]},
          ]
    },
        "log_id": 5284009342430354247
    }
    审核未通过的返回示例

    {
        "result": {
        "spam": 1,
        "reject": [
            {"label":1,"score":0.07,"hit":["双筒猎枪"]},
            {"label":5,"score":0.29,"hit":["傻X"]},
          ],
        "review": [{"label":4,"score":0.5,"hit":[]}],
        "pass": [
            {"label":2,"score":0.3,"hit":[]},
            {"label":3,"score":0.6,"hit":[]}
          ]
    },
        "log_id": 5284009342430354247
    }
    审核需复查的返回示例

    {
        "result": {
        "spam": 2,
                "reject": [],
        "review": [
            {"label":1,"score":0.6,"hit":[]},
            {"label":3,"score":0.4,"hit":["起爆装置"]},
            {"label":4,"score":0.5,"hit":[]}
            {"label":5,"score":0.6,"hit":[]}
           ],
        "pass": [
            {"label":2,"score":0.3,"hit":[]},
           ]
        },
        "log_id": 5284009342430354247
    }
    异常返回示例

    {
        "error_code": 282000,
            "error_msg": "internal error",
            "log_id": 5284009342430354247
    }
*/



}
