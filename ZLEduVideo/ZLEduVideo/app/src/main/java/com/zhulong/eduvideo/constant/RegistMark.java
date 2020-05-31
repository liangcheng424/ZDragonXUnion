package com.zhulong.eduvideo.constant;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class RegistMark {
    public static final String REGIST_ENTITRY = "regist";
    public static int login;
    public static boolean is_recommend = false;
    public static List<Activity> mActivities;

    public static List<Activity> getInstance() {
        if (mActivities == null) {
            mActivities = new ArrayList<>();
        }
        return mActivities;
    }
}
