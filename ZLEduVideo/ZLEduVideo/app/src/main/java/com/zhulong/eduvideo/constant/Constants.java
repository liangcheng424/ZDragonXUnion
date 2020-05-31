package com.zhulong.eduvideo.constant;

import android.content.res.Resources;

import com.zhulong.eduvideo.ApplicationEx;
import com.zhulong.eduvideo.R;
import com.zhulong.eduvideo.entity.ImageItem;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Constants {

    static Resources resources;
    public static String RSA_PUBLIC;

    static {
        resources = ApplicationEx.getInstance().getResources();
        RSA_PUBLIC = resources.getString(R.string.rsa_public);
    }

    //线上
//    public static final String RSA_PUBLIC =
//            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrWVnSOu9m7O8X3taQGXzVlB9B"
//                    +"0Gw1Mvbc0MxKZOxT8SlQVB1Krpu3KfuoxgGE1TikX/JkYJf+D4tTqENqk5pnSZc7"
//                    +"84gWZPEs2O+uz5R/8Ay8qP06uHDzw1oGDrpo8wxWQ7Ae2IwE2gTDtpcyg8NUJp14"
//                    +"uYwsvA47iDpXHGmPxQIDAQAB";
    //线下
//	public static final String RSA_PUBLIC ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDz"
//    +"8JjgRF8rOxriq7g1H6l9jnl8WbyJeQe7iUoBsLkNLDvcGalLOIBS+54JyxBFgoryO/W4x+"
//    +"cw/wluUVsShtTIW09IkxvLqz3wmIJMUfBvvjHE3sf0EtFJabneqi4FCcA9xkCbMl60Kfnzv"
//    +"hsgSNjvUgHu5hlcQuYmVcBe+euJtQIDAQAB";
    // APP_ID
    public static final String QQ_APP_ID = "1105712307";
    public static final String WX_APP_ID = "wx4b6c402fee6860c8";
    public static final String WB_APP_ID = "918914443";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     */
    public static final String REDIRECT_URL = "http://edu.zhulong.com";



    public static final String appid = "301";//
    public static final String ZHULONG_PHONE = "4009008066"; //电话
    public static final String ZHULONG_UID = "873885"; //电话
    public static final String CLIENT_ID = "205"; //筑龙教育app表示，用于区分筑龙的每一个APP
    public static final String SUBJECT_ID = "subject_id";//专业id；
    public static final String SUBJECT_NAME = "subject_name";//专业id
    public static final String REPORTLOG = "ReportLog";//记录已经观看的课程
    public static final String F_ID = "fid";
    public static final String ZHULONG_SNS = "zhulong_sns2.2zhulong_edu2.6";

    public static final int LOAD_MORE = 1000;
    public static final int REFRESH = 1001;

    public static final String TYPE_NORMAL_LESSON = "1";
    public static final String TYPE_MICRO_LESSON = "5";
    public static final String TYPE_OTHER_LESSON = "2";

    public static final int PAY_VIP = 10;
    public static final int PAY_CLASS = 11;
    public static final int PAY_EVIP = 12;
    public static int payFrom = PAY_VIP;

    //验证登录    160 未绑定电话验证 159 已经绑定电话验证 161 邮箱验证 162 自动登录
    public static final String MOBILE_READY = "159";
    public static final String MOBILE_NOT_READY = "160";
    public static final String EMAIL_READY = "161";
    public static final String MOBILE_LOGIN_AUTO = "162";

    public static final int LOGIN_FOR_RESULT = 100;
    public static final int LOGIN_FOR_BIND = 102;

    /**
     * 推荐小组的request码
     */
    public static final int REG_GROUP = 101;

    public static int CAN_OWN = 0;

    //public static ArrayList<ImageItem> photos;
    //筑龙所有的文件根路径   下载路径已经不能修改
    private final static String BASEPATH="zhulongfiles";

    // 配置视频下载文件路径
    public final static String DOWNLOAD_DIR = "ZhulongVideos";
    //配置文件下载文件路径
    public final static String DOWNLOADFILE_DIR = BASEPATH+"/downloadfiles";
    //离线缓存文件保存路径
    public static final String OFFLINE_CACHE_DIR = BASEPATH + "/offlineCache";

    //是否是第一次进入
    public static final String FIRST_INTO = "first";

    //专业缓存
    public static final String SUBJECT_JSON = "SUBJECT_JSON";

    /**
     * 所有的图片，设置为静态，防止图片过多，传参数出错
     */
    public static ArrayList<ImageItem> photos;

    /**
     * UserAgent
     */
    public static String USER_AGENT_END = "fhdsjhfkjdshflkdhfjkldsaflhasjkldsdlfdsfghjk" +
            "fkjdskla;a;a;a;a;a;a;a;a;a;a;a;a;a;a;a;a;jhjhf" +
            "dkfjdskjfa;djfkd;ljlas;sldfkjdklsjafkldsajjhgjgh" +
            "fjkjdaslfjdksla;;;;;;;slfkjkdjsafkdjfkajsdfkljjhgj" +
            "fjkldsajffffffffffdfjkdjeowpfa;fdfkdslafjdksjaffjf" +
            "fkdsljaf;kjdskfnvfddjiaeurqprua[foiad[sif[dasfjfgjjhg" +
            "fdskjfkdsja;jeiurpoeq[erifkdajfklsdajfksdajfl;asffjf" +
            "fndjksafhkasdjfkjdsalkfjdsaklfjwieurq[ropiekqrjkdjfk" +
            "fklsdajfoidsafuewurporw[qriewq[fsldjkfdjsfkjsdfkajfjhg" +
            "fklsdajfoidsafuewurporw[qriewq[fsldjkfdjsfkjsdfkajfjhg" +
            "fklsdajfoidsafuewurporw[qriewq[fsldjkfdjsfkjsdfkajfjhg" +
            "fklsdajfoidsafuewurporw[qriewq[fsldjkfdjsfkjsdfkajfjhg" +
            "fklsdajfoidsafuewurporw[qriewq[fsldjkfdjsfkjsdfkajfjhg";

    public static final int SPECIAL_NORMAL = 0;   //论坛交流小组
    public static final int SPECIAL_DATA = 2;     //资料小组
    public static final int SPECIAL_TUKU = 3;     //图酷小组
}
