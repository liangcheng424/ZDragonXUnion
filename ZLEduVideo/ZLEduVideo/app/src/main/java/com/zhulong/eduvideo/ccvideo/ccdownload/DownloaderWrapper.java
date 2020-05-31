package com.zhulong.eduvideo.ccvideo.ccdownload;

import com.bokecc.sdk.mobile.download.DownloadListener;
import com.bokecc.sdk.mobile.download.Downloader;
import com.bokecc.sdk.mobile.exception.DreamwinException;
import com.zhulong.eduvideo.db.DownloadDBManager;
import com.zhulong.eduvideo.entity.DownloadInfo;
import com.zhulong.eduvideo.utils.MediaUtil;
import com.zhulong.eduvideo.utils.ParamsUtil;

import java.io.File;

/**
 * 下载downloader包装类
 */

public class DownloaderWrapper {

    private Downloader downloader;
    private DownloadInfo downloadInfo;
    private int progress;
    private String progressText = "";

    private boolean isDownloadError;

    DownloaderWrapper(final DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;

        File file ;
        if (downloadInfo.getPath() == null || !new File(downloadInfo.getPath()).exists()) {
            file = MediaUtil.createFile(downloadInfo.getVideoId(), MediaUtil.PCM_FILE_SUFFIX);
            this.downloadInfo.setPath(file.getPath());
        } else {
            file = new File(downloadInfo.getPath());
        }


        downloader = new Downloader(file, downloadInfo.getVideoId(), downloadInfo.getCc_uid(),
                downloadInfo.getCc_key(),"");
 
        downloader.setDownloadDefinition(downloadInfo.getDefinition());
        downloader.setReconnectLimit(3);    //设置重连次数
        downloader.setHttps(false);
        downloader.setDownloadListener(new DownloadListener() {
            @Override
            public void handleProcess(long start, long end, String videoId) {
                downloadInfo.setFileLength(end);
                progress = (int) ((double) start / end * 100);
                if (progress <= 100) {
                    progressText = ParamsUtil.byteToM(start).
                            concat(" M / ").
                            concat(ParamsUtil.byteToM(end).
                                    concat(" M"));
                }
                downloadInfo.setProgress(progress);
                downloadInfo.setProgressText(progressText);
                if (downloadInfo.getStatus() == Downloader.FINISH) {
                    DownloadDBManager.updateDownloadInfo(downloadInfo);

                    if (start != end) {//处理特殊CC异常，没有下载完成状态却变成了Finish
                        DownloadDBManager.removeDownloadInfo(downloadInfo);
                    }
                }
            }

            @Override
            public void handleException(DreamwinException exception, int status) {
                //下载出错时变更为等待状态
                downloadInfo.setStatus(Downloader.WAIT);
                isDownloadError = true;
                downloadInfo.setException(exception);
            }

            @Override
            public void handleStatus(String videoId, int status) {
                if (status == downloadInfo.getStatus())
                	return;
                if (isDownloadError) {  //下载出错时变更为等待状态
                    downloadInfo.setStatus(Downloader.WAIT);
                    isDownloadError = false;
                } else {
                    downloadInfo.setStatus(status);
                }
                DownloadDBManager.updateDownloadInfo(downloadInfo);
            }

            @Override
            public void handleCancel(String videoId) {}
        });

        if (downloadInfo.getStatus() == Downloader.DOWNLOAD) {
            downloader.start();
        }
    }

    public DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public int getStatus() {
        return downloadInfo.getStatus();
    }

    /**
     * 此方法仅在Downloader处于WAIT状态下可用
     */
    public void start() {
    	downloadInfo.setStatus(Downloader.DOWNLOAD);
        downloader.start();
    }

    /**
     * 此方法仅在Downloader处于PAUSE状态可用
     */
    public void resume() {
    	downloadInfo.setStatus(Downloader.DOWNLOAD);
        downloader.resume();
    }

    /**
     * 终止downloader状态为wait状态
     */
    public void setToWait() {
    	downloadInfo.setStatus(Downloader.WAIT);
        downloader.setToWaitStatus();
    }

    public void pause() {
    	downloadInfo.setStatus(Downloader.PAUSE);
        downloader.pause();
    }

    public void cancel() {
    	downloadInfo.setStatus(Downloader.PAUSE);
        downloader.cancel();
    }

    @Override
    public String toString() {
        return "DownloaderWrapper{" +
                "downloader=" + downloader +
                ", downloadInfo=" + downloadInfo +
                '}';
    }
}
