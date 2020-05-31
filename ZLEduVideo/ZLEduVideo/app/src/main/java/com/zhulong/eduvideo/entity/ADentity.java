package com.zhulong.eduvideo.entity;

/**
 * Created by whb on 2016/10/28.
 * Email 18720982457@163.com
 */

public class ADentity {

    /**
     * end_time : 1480262400
     * info_url : http://static.zhulong.com/poster/201610/27/165755bvt8nz8ysdd9ifjy.png
     * jump_url : http://edu.zhulong.com/exam-yijian1?f=lqlbt
     * start_time : 1477497600
     */

    private String end_time;
    private String info_url;
    private String jump_url;
    private String start_time;

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getInfo_url() {
        return info_url;
    }

    public void setInfo_url(String info_url) {
        this.info_url = info_url;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    @Override
    public String toString() {
        return "ADentity{" +
                "end_time='" + end_time + '\'' +
                ", info_url='" + info_url + '\'' +
                ", jump_url='" + jump_url + '\'' +
                ", start_time='" + start_time + '\'' +
                '}';
    }
}
