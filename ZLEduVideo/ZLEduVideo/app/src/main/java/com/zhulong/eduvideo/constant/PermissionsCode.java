package com.zhulong.eduvideo.constant;

/**
 * Created by Administrator on 2016/10/12.
 */

public class PermissionsCode {
    //权限
    public static final int RC_SETTINGS_SCREEN = 120;
    public static final int RC_READ_PHONE_STATE_PERM = 121;
    public static final int RC_WRITE_EXTERNAL_STORAGE_PERM = 122;
    public static final int RC_ACCESS_FINE_LOCATION_PERM = 123;
    public static final int RC_CAMERA_PERM = 124;
    public static final int RC_READ_EXTERNAL_STORAGE_PERM = 125;
    public static final int RC_READ_CONTACTS_PERM = 126;
    public static final int REQUEST_PERMISSION_STORAGE = 127;

    /**
     * 单个视频下载
     */
    public final static int MY_PERMISSIONS_DOWN_ONE = 0x0001;

    /**
     *  多个视频下载
     */
    public final static int MY_PERMISSIONS_DOWN_ALL = 0x0002;

    /**
     * 改变头像
     */
    public final static int MY_PERMISSIONS_CHANGER_AVER = 0x0002;

    /**
     * MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
     * 权限请求，请求手机的基础信息码
     */
    public final static int MY_PERMISSIONS_REQUEST = 1000;

    /**
     * 发帖回帖时请求权限
     */
    public final static int MY_PERMISSIONS_POST_RELPAY=0x0002;

    /**
     * 发帖回帖点击图片
     */
    public final static int MY_PERMISSIONS_POST_PIC=0x0003;

    /**
     * 发帖回帖点击语音
     */
    public final static int MY_PERMISSIONS_POST_VIDEO=0x0003;


    /**
     * 读取文件
     */
    public final static int MY_PERMISSIONS_READ_STORE=0x0004;
}
