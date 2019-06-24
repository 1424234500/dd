package com.walker.dd.util;

import com.walker.common.util.Bean;
import com.walker.common.util.FileUtil;
import com.walker.dd.service.NetModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class OkHttpUtil {

    public static void get(String url, Callback callback){
        AndroidTools.log("get", url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        OkHttpClient okHttpClient = OkHttpUtil.getClient();
        okHttpClient.newCall(request).enqueue(callback);
    }
    public static void post(String url, Bean data, Callback callback){
        AndroidTools.log("get", url);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for(Object key : data.keySet()){
            builder.addFormDataPart(String.valueOf(key), String.valueOf(data.get(key)));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = OkHttpUtil.getClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    static OkHttpClient client = null;
    public static OkHttpClient getClient() {
        if(client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(4, TimeUnit.SECONDS)
                    .writeTimeout(7, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }



    public static void upload(String path, Callback callback){
        upload(NetModel.httpUpload(), path, "", callback);
    }
    public static void upload(String path, String key, Callback callback){
        upload(NetModel.httpUpload(), path, key, callback);
    }

    public static void upload(String url, String path, String key, Callback callback){
        File file = new File(path);
        String name = FileUtil.getFileName(path);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", name, fileBody)
                .addFormDataPart("path", path)
                .addFormDataPart("key", key)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = OkHttpUtil.getClient();
        AndroidTools.log("upload", url, path, requestBody, request, okHttpClient);
        okHttpClient.newCall(request).enqueue(callback);
    }



    public interface OnHttp {
        void onOk(Call call, Response response);
        void onLoading(int progress);
        void onError(Throwable e);
    }

    /**
     * @param url 下载连接
     * @param savePath 储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public static void download(final String url, final String savePath, final OnHttp listener) {
        AndroidTools.log("download", url, savePath);
        Request request = new Request.Builder()
                .url(url)
                .build();

        final ProgressListener progressListener = new ProgressListener() {
            boolean firstUpdate = true;

            @Override public void update(long bytesRead, long contentLength, boolean done) {
                AndroidTools.out(bytesRead, contentLength, done);
                if (done) {
                    AndroidTools.log("download", "complete", url, savePath);
                } else {
                    if (firstUpdate) {
                        firstUpdate = false;
                    }

                    if (contentLength != -1) {
                        long prog = (100 * bytesRead) / contentLength;
                        listener.onLoading((int)prog);
                    }
                }
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                })
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                AndroidTools.log("download", "onFailure", url, savePath, e.toString());
                listener.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                AndroidTools.log("download", "onResponse", url, savePath, response);
                AndroidTools.log(response.headers());

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                    }
                    fos.flush();
                    // 下载完成
                    listener.onOk(call, response);
                } catch (Exception e) {
                    listener.onError(e);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override public long contentLength() {
            return responseBody.contentLength();
        }

        @Override public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

}
