package com.zhulong.eduvideo.auth;

import java.io.Serializable;

/**
 * Created by xxl on 2017/10/17.
 * 授权给三方的信息
 */

public class AuthorInfo implements Serializable {

    /**
     * errNo : 0
     * result : {"avatar":"http://avatar.zhulong.com/avatar/011/09/43/43_avatar_big.jpg","uid":"11094343"}
     */
    private String msg;
    private int errNo;
    private ResultBean result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * avatar : http://avatar.zhulong.com/avatar/011/09/43/43_avatar_big.jpg
         * uid : 11094343
         */

        private String avatar;
        private String uid;
        private String username;
        private String is_vip;

        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
