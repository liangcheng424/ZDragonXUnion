package com.zhulong.eduvideo.entity;

public class ConcernAboutmeEntity {

    /**
     * gid : 695
     * logo : https://avatar.zhulong.com/group/000/00/06/95_logo_middle.jpg
     * group_name : 室内施工
     * member_num : 168834
     * introduce : 多么美丽的装修都是建立在施工上的，室内装饰装修施工的超多经验与讨论，还不快来！这里是优秀室内设计师免费分享室内施工工艺、解决措施、技术总结、详细图解的家园
     * attention : 1
     */

    private String gid;
    private String logo;
    private String group_name;
    private String member_num;
    private String introduce;
    private int attention;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getMember_num() {
        return member_num;
    }

    public void setMember_num(String member_num) {
        this.member_num = member_num;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }
}
