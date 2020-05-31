package com.zhulong.eduvideo.entity;

import com.bokecc.sdk.mobile.download.Downloader;
import com.bokecc.sdk.mobile.exception.DreamwinException;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name = "CCDownloadInfo")
public class DownloadInfo {
    @Column(name = "primarykey", isId = true)
    private String primarykey;
    @Column(name = "id")
    private int id;
    @Column(name = "videoId")
    private String videoId;
    @Column(name = "title")
    private String title;
    @Column(name = "progress")
    private int progress;
    @Column(name = "progressText")
    private String progressText;
    @Column(name = "status")
    private int status;
    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "definition")
    private int definition;
    @Column(name = "cc_uid")
    private String cc_uid;
    @Column(name = "cc_key")
    private String cc_key;
    @Column(name = "path")
    private String path;
    private Long fileLength;

    //预留字段
    @Column(name = "String1")
    private String String1;//课程id
    @Column(name = "String2")
    private String String2;//章节ID   //作为区别不同视频的唯一标识
    @Column(name = "String3")
    private String String3;//缩略图
    @Column(name = "String4")
    private String String4;//用户uid

    @Column(name = "String5")
    private String String5;
    @Column(name = "String6")
    private String String6;
    @Column(name = "String7")
    private String String7;
    @Column(name = "String8")
    private String String8;

    private DreamwinException exception;  //下载错误信息

    public DownloadInfo() {
    }

    public DownloadInfo(String videoId, String title, int progress, String progressText, int status, Date createTime) {
        this.videoId = videoId;
        this.title = title;
        this.progress = progress;
        this.progressText = progressText;
        this.status = status;
        this.createTime = createTime;
        this.definition = -1;
    }

    public DownloadInfo(String videoId, String title, int progress, String progressText, int status, Date createTime, int definition) {
        this(videoId, title, progress, progressText, status, createTime);
        this.definition = definition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        if (videoId != null)
            return videoId.trim();
        return null;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getDefinition() {
        return definition;
    }

    public void setDefinition(int definition) {
        this.definition = definition;
    }

    public String getCc_uid() {
        return cc_uid;
    }

    public void setCc_uid(String cc_uid) {
        this.cc_uid = cc_uid;
    }

    public String getCc_key() {
        return cc_key;
    }

    public void setCc_key(String cc_key) {
        this.cc_key = cc_key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getString1() {
        return String1;
    }

    public void setString1(String string1) {
        String1 = string1;
    }

    public String getString2() {
        return String2;
    }

    public void setString2(String string2) {
        String2 = string2;
    }

    public String getString3() {
        return String3;
    }

    public void setString3(String string3) {
        String3 = string3;
    }

    public String getString4() {
        return String4;
    }

    public void setString4(String string4) {
        String4 = string4;
    }

    public Long getFileLength() {
        try {//由于数据库没有储存fileLength，只能够逆向转
            if (progressText == null) return fileLength;
            if (fileLength == null) {
                double mLength;
                if (progressText.contains("/")) {
                    mLength = Double.parseDouble(progressText.split("/")[1].replace("M", "").replace(" ", ""));
                } else {
                    mLength = Double.parseDouble(progressText.replace("M", "").replace(" ", ""));
                }
                fileLength = (long) (mLength * 1024 * 1024);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileLength;

    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public String getPrimarykey() {
        return primarykey;
    }

    public void setPrimarykey(String primarykey) {
        this.primarykey = primarykey;
    }

    public String getString5() {
        return String5;
    }

    public void setString5(String string5) {
        String5 = string5;
    }

    public String getString8() {
        return String8;
    }

    public void setString8(String string8) {
        String8 = string8;
    }

    public String getString7() {
        return String7;
    }

    public void setString7(String string7) {
        String7 = string7;
    }

    public String getString6() {
        return String6;
    }

    public void setString6(String string6) {
        String6 = string6;
    }

    public String getProgressText() {
        if (progressText == null) {
            return "0M / 0M";
        } else if (status == Downloader.FINISH) {
            return progressText.split("/")[0];
        }
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public String getStatusInfo() {
        String statusInfo = "";
        switch (status) {
            case Downloader.WAIT:
                statusInfo = "等待中";
                break;
            case Downloader.DOWNLOAD:
                statusInfo = "下载中";
                break;
            case Downloader.PAUSE:
                statusInfo = "暂停中";
                break;
            case Downloader.FINISH:
                statusInfo = "已下载";
                break;
            default:
                statusInfo = "下载失败";
                break;
        }

        return statusInfo;
    }

    public DreamwinException getException() {
        return exception;
    }

    public void setException(DreamwinException exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "primarykey='" + primarykey + '\'' +
                ", id=" + id +
                ", videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", progress=" + progress +
                ", progressText='" + progressText + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", definition=" + definition +
                ", cc_uid='" + cc_uid + '\'' +
                ", cc_key='" + cc_key + '\'' +
                ", path='" + path + '\'' +
                ", fileLength=" + fileLength +
                ", String1='" + String1 + '\'' +
                ", String2='" + String2 + '\'' +
                ", String3='" + String3 + '\'' +
                ", String4='" + String4 + '\'' +
                ", String5='" + String5 + '\'' +
                ", String6='" + String6 + '\'' +
                ", String7='" + String7 + '\'' +
                ", String8='" + String8 + '\'' +
                '}';
    }
}
