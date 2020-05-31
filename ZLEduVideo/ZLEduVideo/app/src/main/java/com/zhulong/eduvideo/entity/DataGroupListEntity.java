package com.zhulong.eduvideo.entity;

/**
 * @author wangzhen
 * @date 2019/10/12
 */
public class DataGroupListEntity {


    /**
     * gid : 200101
     * group_name : 居住建筑
     * introduce : 本小组汇集住宅建筑及别墅建筑的方案文本、方案图及施工图。建筑师朋友们可以自由分享与下载独栋别墅、高层住宅、多层住宅、高层住宅楼、住宅区规划、塔式住宅、板式住宅、公寓及宿舍楼、底商住宅、联排别墅、双拼别墅、新农村自建房等的方案文本、方案图及施工图，我分享我快乐！
     * thread_num : 8652
     * avatar : https://avatar.zhulong.com/group/000/20/01/01_logo_middle.jpg
     * is_ftop : 0
     */

    private String gid;
    private String group_name;
    private String introduce;
    private String member_num;
    private String avatar;
    private int is_ftop;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getMember_num() {
        return member_num;
    }

    public void setMember_num(String member_num) {
        this.member_num = member_num;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIs_ftop() {
        return is_ftop;
    }

    public void setIs_ftop(int is_ftop) {
        this.is_ftop = is_ftop;
    }
}
