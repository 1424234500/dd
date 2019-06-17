package com.walker.dd.service;

public class Model {
    public static String nvl(String sour, Object def){
        return sour == null || sour.length() == 0 ? String.valueOf(def) : sour;
    }


}
