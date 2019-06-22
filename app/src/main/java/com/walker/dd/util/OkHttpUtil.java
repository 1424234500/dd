package com.walker.dd.util;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.dd.service.MsgModel;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.plugin.Plugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {

    public static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.SECONDS)
                .writeTimeout(7, TimeUnit.SECONDS)
                .build();
    }

    public static void demo(){
        String url = "";
        String file = "";
        String name = "";
        String path = "";

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", name, fileBody)
                .addFormDataPart("path", path)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = OkHttpUtil.getClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {


            }
        });

    }
}
