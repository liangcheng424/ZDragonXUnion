package com.zhulong.eduvideo.entity;

import java.util.List;

/**
 * @author wangzhen
 * @date 2019/12/02
 */
public class ClassScoreEntity {

    /**
     * id : 5457
     * class_id : 3
     * username : zl320
     * task_score : 0
     * live_score : 0
     * sum_score : 0
     * total : 2
     * rank : 1
     * live_log : [{"live_name":"测试CC视频直播","sort":"1","score":0,"sort_name":"第1次"},{"live_name":"测试乐视直播是否被CC直播影响","sort":"2","score":0,"sort_name":"第2次"},{"live_name":"测试","sort":"3","score":0,"sort_name":"第3次"},{"live_name":"cc技术专用直播测试","sort":"4","score":0,"sort_name":"第4次"}]
     * task_log : [{"task_name":"[讨论]【已完成】是否需要课程下架邮件通知功能","sort":"1","task_url":"http://bbs.zhulong.com/9040_group_100532/detail31047869","score":0,"sort_name":"第1次作业"},{"task_name":"测试上传","sort":"2","task_url":"www.baidu.com","score":0,"sort_name":"第2次作业"}]
     */

    private String id;
    private String class_id;
    private String username;
    private String task_score;
    private String live_score;
    private String sum_score;
    private String total;
    private int rank;
    private List<LiveLog> live_log;
    private List<TaskLog> task_log;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTask_score() {
        return task_score;
    }

    public void setTask_score(String task_score) {
        this.task_score = task_score;
    }

    public String getLive_score() {
        return live_score;
    }

    public void setLive_score(String live_score) {
        this.live_score = live_score;
    }

    public String getSum_score() {
        return sum_score;
    }

    public void setSum_score(String sum_score) {
        this.sum_score = sum_score;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<LiveLog> getLive_log() {
        return live_log;
    }

    public void setLive_log(List<LiveLog> live_log) {
        this.live_log = live_log;
    }

    public List<TaskLog> getTask_log() {
        return task_log;
    }

    public void setTask_log(List<TaskLog> task_log) {
        this.task_log = task_log;
    }

    public static class LiveLog {
        /**
         * live_name : 测试CC视频直播
         * sort : 1
         * score : 0
         * sort_name : 第1次
         */

        private String live_name;
        private String sort;
        private int score;
        private String sort_name;

        public String getLive_name() {
            return live_name;
        }

        public void setLive_name(String live_name) {
            this.live_name = live_name;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getSort_name() {
            return sort_name;
        }

        public void setSort_name(String sort_name) {
            this.sort_name = sort_name;
        }
    }

    public static class TaskLog {
        /**
         * task_name : [讨论]【已完成】是否需要课程下架邮件通知功能
         * sort : 1
         * task_url : http://bbs.zhulong.com/9040_group_100532/detail31047869
         * score : 0
         * sort_name : 第1次作业
         */

        private String task_name;
        private String sort;
        private String task_url;
        private int score;
        private String sort_name;

        public String getTask_name() {
            return task_name;
        }

        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getTask_url() {
            return task_url;
        }

        public void setTask_url(String task_url) {
            this.task_url = task_url;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getSort_name() {
            return sort_name;
        }

        public void setSort_name(String sort_name) {
            this.sort_name = sort_name;
        }
    }
}
