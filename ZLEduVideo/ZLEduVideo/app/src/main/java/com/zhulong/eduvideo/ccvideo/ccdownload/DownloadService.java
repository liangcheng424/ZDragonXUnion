package com.zhulong.eduvideo.ccvideo.ccdownload;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zhulong.eduvideo.net.NetworkUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * DownloadService，用于支持后台下载
 *
 * @author CC视频
 */
public class DownloadService extends Service {

    Timer timer = new Timer();

    private boolean isNetworkAvailable;
    private BroadcastReceiver netReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        isNetworkAvailable = NetworkUtils.isNetworkAvailable(this);
        registerNetReceiver();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isNetworkAvailable) {
                    DownloadController.update();
                } else {    //无网时，不进行和网络有关的状态更新，如下载进度变更等
                    DownloadController.notifyUpdate();
                }
            }
        }, 1000, 1000);
    }

    private void registerNetReceiver() {
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isNetworkAvailable = NetworkUtils.isNetworkAvailable(context);
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netReceiver, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        DownloadController.pauseAllDownloader();
        unregisterReceiver(netReceiver);
        super.onDestroy();
    }

}
