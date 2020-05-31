package com.zhulong.eduvideo.entity;

/**
 * Created by Administrator on 2016/11/1.
 */

public class CourseBoughtEntity {

    /**
     * order_sn : yl2016102870606
     * lesson_id : 3341
     * lesson_name : 平法钢筋识图算量基础教程及常见问题解析
     * type : 1
     * end : 1509182106
     * pic : http://attach.zhulong.com/edu/201606/03/33/161533jk3xoillorr2hjdq_0_1_360_270.jpg
     * length : 126
     * learn : 0
     * create_time : 1477645930
     * package : [{"order_sn":"yl2016102870606","lesson_id":"3097","lesson_name":"平法钢筋识图算量基础教程","type":"1","end":"1509182108","pic":"http://attach.zhulong.com/edu/201508/14/25/095225citmrjayryd4zyfw_0_1_360_270.jpg","length":"101","learn":"21","create_time":"1477645930"},{"order_sn":"yl2016102870606","lesson_id":"3017","lesson_name":"平法钢筋计算常见问题解析","type":"1","end":"1509182108","pic":"http://attach.zhulong.com/edu/201409/25/29/0924299ank6l5kzxihnvwv_0_1_360_270.jpg","length":"25","learn":"2","create_time":"1477645930"}]
     */

    private String order_sn;
    private String lesson_id;
    private String lesson_name;
    private String type;
    private String date_str;
    private String pic;
    private String length;
    private String learn;
    private String create_time;
    private String is_live;
    private int live_status;
    private String live_id;
    private String groupbuy_id;
    private String course_type;
    private String gid;

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate_str() {
        return date_str;
    }

    public void setDate_str(String date_str) {
        this.date_str = date_str;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLearn() {
        return learn;
    }

    public void setLearn(String learn) {
        this.learn = learn;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_live() {
        return is_live;
    }

    public void setIs_live(String is_live) {
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

    public String getGroupbuy_id() {
        return groupbuy_id;
    }

    public void setGroupbuy_id(String groupbuy_id) {
        this.groupbuy_id = groupbuy_id;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}
