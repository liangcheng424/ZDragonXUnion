package com.zhulong.eduvideo.db;

import android.database.sqlite.SQLiteException;

import com.orhanobut.logger.Logger;
import com.zhulong.eduvideo.entity.DownloadInfo;
import com.zhulong.eduvideo.entity.DownloadInfoPackageNewEntity;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by whb on 2017/2/15.
 * Email 18720982457@163.com
 * CC视频下载信息数据库
 */

public class DownloadDBManager {


    public static DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("downloadlession.db")
            .setDbVersion(1)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    try {
                        db.getDatabase().enableWriteAheadLogging();
                    } catch (SQLiteException e) {
                        Logger.e("数据库异常");
                        e.printStackTrace();
                    }
                }
            })
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                }
            });

    /*------------------------------------------视频下载信息---------------------------------------*/

    public static List<DownloadInfo> getAllDownloadInfo() {
        try {
            return x.getDb(DownloadDBManager.daoConfig).selector(DownloadInfo.class).findAll();
        } catch (DbException e) {
            Logger.e("数据库异常");
            e.printStackTrace();
        }
        return null;
    }

    public static List<DownloadInfo> getDownloadInfos(String lessonId,String uid) {
        try {
            return x.getDb(DownloadDBManager.daoConfig).selector(DownloadInfo.class)
                    .where("String1","=",lessonId)
                    .and("String4","=",uid).findAll();
        } catch (DbException e) {
            Logger.e("数据库异常");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存下载信息
     * @param downloadInfo
     */
    public static void addDownloadInfo(DownloadInfo downloadInfo){
        try {
            x.getDb(DownloadDBManager.daoConfig).saveOrUpdate(downloadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void updateDownloadInfo(DownloadInfo downloadInfo){
        try {
            x.getDb(DownloadDBManager.daoConfig).update(downloadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void removeDownloadInfo(DownloadInfo downloadInfo){
        try {
            //删除文件
            File file = new File(downloadInfo.getPath());
            if (file.exists()){
                file.delete();
            }
            x.getDb(DownloadDBManager.daoConfig).delete(downloadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /*------------------------------------------视频章节信息---------------------------------------*/

    /**
     * 保存下载课程的章信息
     * @param entity
     */
    public static void saveLessonDownloadPackageInfo(DownloadInfoPackageNewEntity entity){
        try {
            x.getDb(DownloadDBManager.daoConfig).saveOrUpdate(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除下载课程的章信息
     * @param entity
     */
    public static void deleteLessonDownloadPackageInfo(DownloadInfoPackageNewEntity entity){
        try {
            x.getDb(DownloadDBManager.daoConfig).delete(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除下载课程的信息
     * @param lessonId 课程id
     * @param uid 用户id
     */
    public static void deleteLessonDownloadPackageInfo(String uid,String lessonId){
        try {
            //删除课程信息
            x.getDb(DownloadDBManager.daoConfig).delete(DownloadInfoPackageNewEntity.class,
                    WhereBuilder.b("lesson_id","=",lessonId));
            //删除视频文件
            final List<DownloadInfo> downloadInfoList = x.getDb(DownloadDBManager.daoConfig).selector(DownloadInfo.class)
                    .where("String4", "=",  uid)
                    .and("String1", "=", lessonId)
                    .findAll();
            for (DownloadInfo downloadInfo : downloadInfoList) {
                File file = new File(downloadInfo.getPath());
                if (file.exists()){
                    file.delete();
                }
            }
            //删除章节信息
            x.getDb(DownloadDBManager.daoConfig).delete(DownloadInfo.class,
                    WhereBuilder.b("String1","=",lessonId));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新下载课程的过期时间
     * @param lessonId 课程ID
     * @param expiration_time 新的过期时间
     */
    public static void updateLessonDownloadPackageExpirationTime(String uid,String lessonId,String expiration_time){
        try {
            x.getDb(DownloadDBManager.daoConfig).update(DownloadInfoPackageNewEntity.class,
                    WhereBuilder.b("uid", "=",  uid)
                            .and("lesson_id","=",lessonId),
                    new KeyValue("expiration_time",expiration_time));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得用户下载的所有课程章信息
     * @param uid
     * @return
     */
    public static List<DownloadInfoPackageNewEntity> getAllLessonDownloadPackages(String uid){
        try {
            return x.getDb(DownloadDBManager.daoConfig).selector(DownloadInfoPackageNewEntity.class)
                    .where("uid", "=",  uid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
