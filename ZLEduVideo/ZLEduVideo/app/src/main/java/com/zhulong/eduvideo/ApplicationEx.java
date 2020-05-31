package com.zhulong.eduvideo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bokecc.sdk.mobile.drm.DRMServer;
import com.bokecc.sdk.mobile.push.DWPushEngine;
import com.facebook.stetho.Stetho;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.mob.MobSDK;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.smtt.sdk.QbSdk;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhulong.eduvideo.ccvideo.ccdownload.DownloadController;
import com.zhulong.eduvideo.ccvideo.ccdownload.DownloadService;
import com.zhulong.eduvideo.entity.UserInfo;
import com.zhulong.eduvideo.manager.ApplicationConfig;
import com.zhulong.eduvideo.manager.UserLocalManager;
import com.zhulong.eduvideo.mvpbase.ActivityLifeCycleHandler;
import com.zhulong.eduvideo.net.HttpUrlUtil;
import com.zhulong.eduvideo.receiver.NetReceiver;
import com.zhulong.eduvideo.utils.UncaughtException;
import com.zhulong.eduvideo.utils.http.ZLImageLoader;

import org.xutils.x;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.magicwindow.MWConfiguration;
import cn.magicwindow.MagicWindowSDK;


public class ApplicationEx extends MultiDexApplication {
    private static ApplicationEx applicationEx;
    private ApplicationConfig mApplicationConfig;
    private List<Activity> activityList;
    private DRMServer drmServer;
    private int drmServerPort;
    private NetReceiver mNetReceiver;


    public static ApplicationEx getInstance() {
        return applicationEx;
    }
     @Override
    public void onCreate() {
         // 多进程导致多次初始化Application,这里只初始化App主进程的Application
         String curProcessName = getCurProcessName(this);
         if (!curProcessName.equals(getPackageName())) {
             return;
         }
        super.onCreate();
        initTypeface();
        applicationEx = this;
        mApplicationConfig = new ApplicationConfig();
        activityList = new LinkedList<>();

        if (BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this);
        }
        //内存检测初始化
//        setupLeakCanary();

        //开启线上线下选择模式
        HttpUrlUtil.getInstance().openSelectServer(true);

         //初始化本地用户管理类
         UserLocalManager.init(this);

        //xUtils
        x.Ext.init(this);
//        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        x.Ext.setDebug(false);

        //异常崩溃处理-在Bugly之前注册
        UncaughtException.register(this);

        //子线程初始化第三方库
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //Bugly
                Bugly.init(getApplicationContext(),"516cd87785", false);
                // 开发设备
                Bugly.setIsDevelopmentDevice(getApplicationContext(),false);

                //Logger
                Logger.init();
                Logger.init("ZHULONG_LOG");

                //极光
                JPushInterface.init(getApplicationContext());
                JPushInterface.setDebugMode(BuildConfig.DEBUG);

                //初始化x5
                iniX5Webview();

                //image loader
                configImageLoader();

            }
        }).start();

        //延迟初始化第三方库
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //初始化CC视频
                startDRMServer();

                //初始化CC直播
//              DWPushEngine.init(this, true, true);
                DWPushEngine.init(getApplicationContext(),  true,true);

                //初始化讯飞语音
                SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=58db56cd");

                //初始化Zxing二维码扫描库
                ZXingLibrary.initDisplayOpinion(getApplicationContext());

                //初始化分享
                MobSDK.init(getApplicationContext());
            }
        },3000);

        //注册全局网络监听
        //在清单文件中注册无法收到数据状态改变监听
        registerNetworkReceiver();

         //魔窗
         initMW();
//         /**
//          * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
//          * 第一个参数：应用程序上下文
//          * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
//          */
//         //初始化全局右滑关闭页面
//         List<Class<? extends View>> classList = new ArrayList<>();
//          classList.add(BeatsImageView.class);
//         BGASwipeBackHelper.init(this,classList);

         //监听应用前后台
         registerActivityLifecycleCallbacks(new ActivityLifeCycleHandler());

    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess:activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    private void initMW() {
        MWConfiguration config = new MWConfiguration(this);
        config.setLogEnable(true);//打开魔窗Log信息
        MagicWindowSDK.initSDK(config);
    }

    private void registerNetworkReceiver() {
        mNetReceiver = new NetReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetReceiver, filter);
    }

    protected RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }


    private void configImageLoader() {
        ZLImageLoader.LoaderConfig config = new ZLImageLoader.LoaderConfig();
        config.errorResId = R.drawable.ic_pic_load;
        config.defaultResId = R.drawable.ic_pic_load;
        config.imageStyle = new ZLImageLoader.ImageStyle[]{ZLImageLoader.ImageStyle.centerCrop};
        ZLImageLoader.getInstance().setConfig(config);
    }
    // 启动DRMServer

    public void startDRMServer() {
        if (drmServer == null) {
            drmServer = new DRMServer();
            drmServer.setRequestRetryCount(10);
        }

        try {
            drmServer.start();
            setDrmServerPort(drmServer.getPort());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "启动解密服务失败，请检查网络限制情况", Toast.LENGTH_LONG).show();
        }
    }

    public ApplicationConfig getAppConfig() {
        return mApplicationConfig;
    }

    public int getDrmServerPort() {
        return drmServerPort;
    }

    public void setDrmServerPort(int drmServerPort) {
        this.drmServerPort = drmServerPort;
    }

    public DRMServer getDRMServer() {
        return drmServer;
    }

    @Override
    public void onTerminate() {
        if (drmServer != null) {
            drmServer.stop();
        }
        if (mNetReceiver != null)
            unregisterReceiver(mNetReceiver);
//        //退出应用时本地用户退出登录
//        UserLocalManager.logout();
        super.onTerminate();
        Beta.unInit();
    }

    /**
     * 获取当前进程名字
     */
    public static String getProcessName(Context cxt, int pid) {

        ActivityManager am = (ActivityManager)
                cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * 添加Activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 在全局注销Activity，防止内存泄漏。
     *
     * @param activity 注销Activity
     */
    public void destroyActivity(Activity activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    /**
     * 开始下载服务
     */
    public void startDownloadService(){
        //下载视频初始化
        DownloadController.init();
        startService(new Intent(this, DownloadService.class));
    }

    /**
     * 退出APP，注销所有Activity
     */
    public void exitApp() {
//        stopService(new Intent(this, DownloadService.class));
        DownloadController.pauseAllDownloader();
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    public void iniX5Webview() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("APPAplication", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public void restartApp(Class<?> cls) {
        for (Activity activity : activityList) {
            activity.finish();
        }
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public List<Activity> getActivities() {
        return activityList;
    }

    /**
     * 判断是否已经登陆
     */
    public boolean isLogin() {
        return UserLocalManager.isLogin();
    }

    public void logout() {
        UserLocalManager.logout();
    }

    public UserInfo getUserInfo() {
        return UserLocalManager.getUser();
    }

    public void setUserInfo(UserInfo userInfo) {
        UserLocalManager.setUser(userInfo);
    }

    public String getUid() {
        if (UserLocalManager.getUser() == null)
            return "";

        return UserLocalManager.getUser().getUid();
    }

    public String getUserName() {
        if (UserLocalManager.getUser() == null)
            return "";
        return UserLocalManager.getUser().getUsername();
    }

    public String getWeekLearnTime() {
        //周学习时长
        if (UserLocalManager.getUser() == null) {
            return "";
        }
        return UserLocalManager.getUser().getPersonHeader().getLessonTime();
    }

    public String getPostNum() {//发帖数
        if (UserLocalManager.getUser() == null) {
            return "";
        }
        return UserLocalManager.getUser().getPersonHeader().getThreadnum();
    }

    public boolean isVip() {
        boolean isVip = false;
        if (!isLogin()) {
            return false;
        }
        UserInfo user = UserLocalManager.getUser();
        if (user != null && user.getPersonHeader() != null) {
            isVip = TextUtils.equals("1", user.getPersonHeader().getIs_vip());
        }
        return isVip;
    }

    /**
     * 腾讯bugly增加uid标识
     *
     * @param uid
     */
    public void setCashUid(String uid) {
        Bugly.setUserId(this,uid);
    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 安装tinker
        Beta.installTinker();
    }
    private void initTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        try {
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set("null",typeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
