package com.zhulong.eduvideo.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/4.
 */

public class CouponBean implements Serializable{

    /**
     [id] => 26
     [class_id] => 64//优惠券所属种类，coupon_class表里的主键id
     [coupon_sn] =>
     [range] => 3//应用范围
     [terminal] => 2,3,4,5
     [usemoney] => 500//满500元可用此优惠劵
     [money] => 80.00//优惠金额
     [uid] => 7170569
     [status] =>0//0未使用，大于0已使用，小于0已过期
     [ispay] => 0//该优惠券涉及到的订单是否已真正支付，1是已支付
     [start_time] => 1457366400//有效期开始时间
     [end_time] => 1497583027//有效期结束时间
     [create_time] => 1457366400//创建时间
     [use_time] => 1463654851//使用时间
     [pay_time] => 0//支付时间（只有有支付时间的才是真正使用的）
     [pay_money] => 0.00
     [lesson_id] => 3693//限制使用的课程id
     [coupon_code] =>zl_5816e34d3aabc //优惠劵编码，此码别人可用长度为16位数字字母字符串
     [type] => 1//类型1固定金额，2随机金额3.折扣金额
     [discount] => 0//优惠券类型为3时的折扣比例.
     [price] => 0//直播价格
     */

    private int range;
    private String status;
    private String use_time;
    private String usemoney;
    private String coupon_code;
    private String pay_time;
    private String type;
    private String discount;
    private String id;
    private String pay_money;
    private String class_id;
    private String terminal;
    private String coupon_sn;
    private String end_time;
    private String create_time;
    private String money;
    private String start_time;
    private String ispay;
    private String lesson_id;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUse_time() {
        return use_time;
    }

    public void setUse_time(String use_time) {
        this.use_time = use_time;
    }

    public String getUsemoney() {
        return usemoney;
    }

    public void setUsemoney(String usemoney) {
        this.usemoney = usemoney;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPay_money() {
        return pay_money;
    }

    public void setPay_money(String pay_money) {
        this.pay_money = pay_money;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getCoupon_sn() {
        return coupon_sn;
    }

    public void setCoupon_sn(String coupon_sn) {
        this.coupon_sn = coupon_sn;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getIspay() {
        return ispay;
    }

    public void setIspay(String ispay) {
        this.ispay = ispay;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }
}
