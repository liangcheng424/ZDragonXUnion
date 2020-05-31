package com.zhulong.eduvideo.entity;

import java.util.List;

/**
 * Created by Lusion on 1/5 0005.
 */

public class AppLivePlayEntity {

    /**
     * liveNum : 1
     * lives : [{"machine":"1","pushUrl":"rtmp://w.gslb.lecloud.com/live/201701043000000c899?sign=81736668a3a38d74ee41d1305a46705c&tm=20170105151139","status":"0","streamId":"201701043000000c899"}]
     */

    private int liveNum;
    private List<LivesBean> lives;

    public int getLiveNum() {
        return liveNum;
    }

    public void setLiveNum(int liveNum) {
        this.liveNum = liveNum;
    }

    public List<LivesBean> getLives() {
        return lives;
    }

    public void setLives(List<LivesBean> lives) {
        this.lives = lives;
    }

    public static class LivesBean {
        /**
         * machine : 1
         * pushUrl : rtmp://w.gslb.lecloud.com/live/201701043000000c899?sign=81736668a3a38d74ee41d1305a46705c&tm=20170105151139
         * status : 0
         * streamId : 201701043000000c899
         */
        private String machine;
        private String pushUrl;
        private String status;
        private String streamId;

        public String getMachine() {
            return machine;
        }

        public void setMachine(String machine) {
            this.machine = machine;
        }

        public String getPushUrl() {
            return pushUrl;
        }

        public void setPushUrl(String pushUrl) {
            this.pushUrl = pushUrl;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStreamId() {
            return streamId;
        }

        public void setStreamId(String streamId) {
            this.streamId = streamId;
        }
    }
}
