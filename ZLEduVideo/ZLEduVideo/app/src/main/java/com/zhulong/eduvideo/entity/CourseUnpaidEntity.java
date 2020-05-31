package com.zhulong.eduvideo.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/31.
 */

public class CourseUnpaidEntity implements Serializable {
    private static final long serialVersionUID = 1L; // 定义序列化ID
    /**
     * lesson_id : 3134
     * lesson_name : 供配电设计基础知识讲解应用
     * thumb : http://attach.zhulong.com/edu//201610/20/8/191808tw1ym96zyciguzty_1_0_150_0.jpg
     * money : 878
     * type : 1
     * order_sn : d2016103116617
     * order_id : 179888
     */

    private String id;
    private String lesson_id;
    private String lesson_name;
    private String thumb;
    private String money;
    private String type;
    private String order_sn;
    private String order_id;
    private int is_live;
    private int live_status;
    private String live_id;
    private String real_money;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getLesson_name() {
        return lesson_name;
    }

    public void setLesson_name(String lesson_name) {
        this.lesson_name = lesson_name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getIs_live() {
        return is_live;
    }

    public void setIs_live(int is_live) {
        this.is_live = is_live;
    }

    public int getLive_status() {
        return live_status;
    }

    public void setLive_status(int live_status) {
        this.live_status = live_status;
    }

    public String getLive_id() {
        return live_id;
    }

    public void setLive_id(String live_id) {
        this.live_id = live_id;
    }

    public String getReal_money() {
        return real_money;
    }

    public void setReal_money(String real_money) {
        this.real_money = real_money;
    }
}
