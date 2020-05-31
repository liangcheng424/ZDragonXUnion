package com.zhulong.eduvideo.entity;

/**
 * Created by Administrator on 2016/11/1.
 */

public class ClassCollectBean {

    /**
     * lesson_id : 2996
     * lesson_name : BIM建筑（Revit Arc）+结构（Revit Str） 操作详解及实战应用
     * pic : http://attach.zhulong.com/edu/201407/31/182820xkzpzucozjdgwwfw_0_1_360_270.jpg
     * price : 149
     * type : 1
     */

    private String lesson_id;
    private String lesson_name;
    private String pic;
    private String price;
    private String type;
    private String studentnum;
    private String comment_level;

    private String rate;//好评率

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getStudentnum() {
        return studentnum;
    }

    public void setStudentnum(String studentnum) {
        this.studentnum = studentnum;
    }

    public String getComment_level() {
        return comment_level;
    }

    public void setComment_level(String comment_level) {
        this.comment_level = comment_level;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
