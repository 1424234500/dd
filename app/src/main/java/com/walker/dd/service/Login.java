package com.walker.dd.service;

import com.walker.common.util.Bean;
import com.walker.dd.activity.AcBase;
import com.walker.dd.activity.main.ActivityRegiste;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.plugin.Plugin;

public class Login {

    public static void login(AcBase socket, String id, String pwd, String name){
        socket.sendSocket(Plugin.KEY_LOGIN, new Bean().put(Key.ID, id).put(Key.PWD, pwd).put(Key.NAME, name));
    }


    public static void registe(ActivityRegiste activityRegiste, String username, String email, String sex, String pwd) {


    }
}
