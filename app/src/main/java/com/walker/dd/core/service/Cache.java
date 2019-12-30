package com.walker.dd.core.service;

import com.walker.common.util.Bean;
import com.walker.dd.core.AndroidTools;

public class Cache extends Bean {

    /**
     * 私有构造器
     */
    private Cache(){}
    /**
     * 私有静态内部类
     */
    private static class SingletonFactory{
        private static Cache instance;
        static {
            AndroidTools.out("静态内部类初始化" + SingletonFactory.class);
            instance = new Cache();
        }
    }
    /**
     * 内部类模式 可靠
     */
    public static Cache getInstance(){
        return SingletonFactory.instance;
    }

}
