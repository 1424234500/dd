package com.walker.dd.core.push;

import android.content.Context;

public interface PushService {

    void bind(Context context) throws Exception;
    void rebind(Context context) throws Exception;
    void unbind(Context context) throws Exception;


}
