package com.zhulong.eduvideo.ccvideo.ccdownload;

import android.text.TextUtils;

import com.bokecc.sdk.mobile.download.Downloader;
import com.zhulong.eduvideo.db.DownloadDBManager;
import com.zhulong.eduvideo.entity.DownloadInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 下载列表核心控制类
 */
public class DownloadController {

    private static final int DOWNLOADING_MAX = 3;
    private static boolean isInitialized ;

    //下载中列表
    static final ArrayList<DownloaderWrapper> downloadingList = new ArrayList<>();

    //下载完成列表
    static final ArrayList<DownloaderWrapper> downloadedList = new ArrayList<>();

    //初始化，需要在程序入口执行
    public static void init() {

        if (isInitialized){
            return;
        }

        if (isBackDownload) {
            return;
        }

        List<DownloadInfo> list = DownloadDBManager.getAllDownloadInfo();

        // 清空数据
        downloadingList.clear();
        downloadedList.clear();
        observers.clear();

        if (list == null) return;
        for (DownloadInfo info : list) {
            DownloaderWrapper wrapper = new DownloaderWrapper(info);

            if (info.getStatus() == Downloader.FINISH) {
                downloadedList.add(wrapper);
            } else {
                downloadingList.add(wrapper);
            }
        }

        isInitialized = true;
    }

    public static ArrayList<DownloaderWrapper> getAllDownloadList() {
        ArrayList<DownloaderWrapper> list = new ArrayList<>();
        list.addAll(downloadingList);
        list.addAll(downloadedList);
        Collections.sort(list, new Comparator<DownloaderWrapper>() {//时间顺序排序
            @Override
            public int compare(DownloaderWrapper o1, DownloaderWrapper o2) {
                long time1 = o1.getDownloadInfo().getCreateTime().getTime();
                long time2 = o2.getDownloadInfo().getCreateTime().getTime();
                return time1 - time2 > 0 ? 1 : -1;
            }
        });
        return list;
    }

    //新增下载信息
    public static void insertDownloadInfo(DownloadInfo info) {
        synchronized (downloadingList) {
            DownloaderWrapper wrapper = new DownloaderWrapper(info);
            downloadingList.add(wrapper);
            DownloadDBManager.addDownloadInfo(info);
        }
    }

    //暂停所有下载任务
    public static void pauseAllDownloader() {
        for (DownloaderWrapper wrapper : downloadingList) {
            if (wrapper.getStatus() == Downloader.DOWNLOAD
                    || wrapper.getStatus() == Downloader.WAIT) {
                wrapper.pause();
                DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
            }
        }
    }

    //暂停某课程的所有下载
    public static void pauseAllDownloader(String lessonId) {
        for (DownloaderWrapper wrapper : downloadingList) {
            if (TextUtils.equals(wrapper.getDownloadInfo().getString1(),lessonId)){
                if (wrapper.getStatus() == Downloader.DOWNLOAD
                        || wrapper.getStatus() == Downloader.WAIT) {
                    wrapper.pause();
                    DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
                }
            }
        }
    }

    //重新开始某课程的所有下载
    public static void resumeAllDownloader(String lessonId) {
        synchronized (downloadingList) {
            //获取正在下载数
            int downloadCount = getDownloadingCount();

            for (DownloaderWrapper wrapper : downloadingList) {
                if (TextUtils.equals(wrapper.getDownloadInfo().getString1(),lessonId)) {
                    if (downloadCount < DOWNLOADING_MAX) {  //小于最大下载数，变为下载状态
                        if (wrapper.getStatus() == Downloader.PAUSE) {
                            wrapper.resume();
                            downloadCount++;
                            DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
                        } else if (wrapper.getStatus() == Downloader.WAIT) {
                            wrapper.start();
                            downloadCount++;
                            DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
                        }
                    } else {    //大于等于最大下载数，变为等待状态
                        if (wrapper.getStatus() == Downloader.PAUSE) {
                            wrapper.setToWait();
                            DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
                        }
                    }
                }
            }
            notifyUpdate();
        }
    }

    //取消下载
    public static void cancelDownloadInfo(DownloaderWrapper wrapper) {
        synchronized (downloadingList) {
            if (wrapper.getStatus() == Downloader.FINISH) {
                downloadedList.remove(wrapper);
            } else {
                downloadingList.remove(wrapper);
            }
            wrapper.cancel();
            //删除对应数据和文件
            DownloadDBManager.removeDownloadInfo(wrapper.getDownloadInfo());
        }
    }

    //某课程中是否有下载任务
    public static boolean isDownloading(String lessonId) {
        int downloadCount = 0;

        for (DownloaderWrapper wrapper : downloadingList) {
            if (TextUtils.equals(wrapper.getDownloadInfo().getString1(), lessonId)
                    && (wrapper.getStatus() == Downloader.DOWNLOAD
                    || wrapper.getStatus() == Downloader.WAIT ) ) {
                downloadCount++;
            }
        }

        return downloadCount > 0;
    }

    //更新下载状态信息
    public static void update() {
        synchronized (downloadingList) {
            Iterator<DownloaderWrapper> iterator = downloadingList.iterator();
            int downloadCount = 0;

            //列表里有下载完成的，则需要更新列表
            while (iterator.hasNext()) {
                DownloaderWrapper wrapper = iterator.next();
                if (wrapper.getStatus() == Downloader.FINISH) {
                    iterator.remove();
                    downloadedList.add(wrapper);
                } else if (wrapper.getStatus() == Downloader.DOWNLOAD) {
                    DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());    //更新正在下载的信息
                    downloadCount++;
                }
            }

            //开启新的下载
            if (downloadCount < DOWNLOADING_MAX) {
                for (DownloaderWrapper wrapper : downloadingList) {
                    if (wrapper.getStatus() == Downloader.WAIT) {
                        wrapper.start();
                        DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
                        break;
                    }
                }
            }

            notifyUpdate();
        }
    }

    //处理暂停和开始下载
    public static void parseItemClick(DownloaderWrapper wrapper) {
        synchronized (downloadingList) {
            if (wrapper.getStatus() == Downloader.DOWNLOAD || wrapper.getStatus() == Downloader.WAIT) {
                wrapper.pause();
            } else if (wrapper.getStatus() == Downloader.PAUSE) {
                int count = getDownloadingCount();
                if (count < DOWNLOADING_MAX) {
                    wrapper.resume();
                } else {
                    wrapper.setToWait();
                }
            }

            DownloadDBManager.updateDownloadInfo(wrapper.getDownloadInfo());
        }
    }

    //获取下载中的个数
    private static int getDownloadingCount() {
        int downloadCount = 0;

        for (DownloaderWrapper wrapper : downloadingList) {
            if (wrapper.getStatus() == Downloader.DOWNLOAD) {
                downloadCount++;
            }
        }

        return downloadCount;
    }

    private static boolean isBackDownload = false;

    //如果设置为true，那么说明是后台下载中，list就不能被初始化，否则会导致出现野的downloader，无法控制
    public static void setBackDownload(boolean isBack) {
        isBackDownload = isBack;
    }

    public static List<Observer> observers = new ArrayList<>();

    public static void attach(Observer o) {
        observers.add(o);
    }

    public static void detach(Observer o) {
        observers.remove(o);
    }

    public static void notifyUpdate() {
        if (observers.size() > 0) {
            for (Observer o : observers) {
                o.update();
            }
        }
    }

    //观察者
    public interface Observer {
        void update();
    }
}
