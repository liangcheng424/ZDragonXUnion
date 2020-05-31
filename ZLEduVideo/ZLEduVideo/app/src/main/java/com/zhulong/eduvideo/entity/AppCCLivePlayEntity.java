package com.zhulong.eduvideo.entity;

/**
 * Created by xxl on 2017/7/13.
 * CC 直播实体
 */


public class AppCCLivePlayEntity {

    /**
     * cc_key : YS5rAn7Od//1XIV9WH2+kz3OxFf01FLttqAQYOxmmVF80NN+E6U/NfU++iBK9pmh
     * cc_uid : pTWwd4psGiXovasIUInRSeVSfhQem+lP0Iw/aOCqrjQ=
     * live_platform : 2
     * roomid : 50F314E3F8BA57BB9C33DC5901307461
     */

    private String cc_key;
    private String cc_uid;
    private String live_platform;
    private String roomid;
    private String publisherpass;//讲师推流密码

    public String getPublisherpass() {
        return publisherpass;
    }
    public void setPublisherpass (String  publisherpass) {
        this.publisherpass = publisherpass;
    }

    public String getCc_key() {
        return cc_key;
    }

    public void setCc_key(String cc_key) {
        this.cc_key = cc_key;
    }

    public String getCc_uid() {
        return cc_uid;
    }

    public void setCc_uid(String cc_uid) {
        this.cc_uid = cc_uid;
    }

    public String getLive_platform() {
        return live_platform;
    }

    public void setLive_platform(String live_platform) {
        this.live_platform = live_platform;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }
}
