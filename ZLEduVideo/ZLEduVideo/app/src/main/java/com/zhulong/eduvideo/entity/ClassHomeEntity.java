package com.zhulong.eduvideo.entity;

import java.util.List;

/**
 * 直播讨论实体类
 *
 * Author:Jason
 * <p>
 * Date:2016/11/10
 */
public class ClassHomeEntity {


    /**
     * errNo : 0
     * result : {"open_time":"1502424399","is_open":1,"sum_score":"0","student_id":"5457","class_id":"3","class_name":"测试班级一","learn_part":4,"all_part":355,"learn_rate":11,"live_list":[],"teach_list":[{"avatar":"https://avatar.zhulong.com/avatar/007/17/05/69_avatar_small.jpg","name":"张增凯","title":"班主任"},{"avatar":"https://avatar.zhulong.com/avatar/007/17/05/69_avatar_small.jpg","name":"小葵花","title":"服务员"}]}
     * exeTime : 0
     */

    private int errNo;
    private ResultBean result;
    private int exeTime;

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

    public int getExeTime() {
        return exeTime;
    }

    public void setExeTime(int exeTime) {
        this.exeTime = exeTime;
    }

    public static class ResultBean {
        /**
         * open_time : 1502424399
         * is_open : 1
         * sum_score : 0
         * student_id : 5457
         * class_id : 3
         * class_name : 测试班级一
         * learn_part : 4
         * all_part : 355
         * learn_rate : 11
         * live_list : []
         * teach_list : [{"avatar":"https://avatar.zhulong.com/avatar/007/17/05/69_avatar_small.jpg","name":"张增凯","title":"班主任"},{"avatar":"https://avatar.zhulong.com/avatar/007/17/05/69_avatar_small.jpg","name":"小葵花","title":"服务员"}]
         */

        private String open_time;
        private int is_open;
        private String sum_score;
        private String student_id;
        private String class_id;
        private String class_name;
        private int learn_part;
        private int all_part;
        private int learn_rate;
        private List<?> live_list;
        private List<TeachListBean> teach_list;

        public String getOpen_time() {
            return open_time;
        }

        public void setOpen_time(String open_time) {
            this.open_time = open_time;
        }

        public int getIs_open() {
            return is_open;
        }

        public void setIs_open(int is_open) {
            this.is_open = is_open;
        }

        public String getSum_score() {
            return sum_score;
        }

        public void setSum_score(String sum_score) {
            this.sum_score = sum_score;
        }

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public String getClass_id() {
            return class_id;
        }

        public void setClass_id(String class_id) {
            this.class_id = class_id;
        }

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }

        public int getLearn_part() {
            return learn_part;
        }

        public void setLearn_part(int learn_part) {
            this.learn_part = learn_part;
        }

        public int getAll_part() {
            return all_part;
        }

        public void setAll_part(int all_part) {
            this.all_part = all_part;
        }

        public int getLearn_rate() {
            return learn_rate;
        }

        public void setLearn_rate(int learn_rate) {
            this.learn_rate = learn_rate;
        }

        public List<?> getLive_list() {
            return live_list;
        }

        public void setLive_list(List<?> live_list) {
            this.live_list = live_list;
        }

        public List<TeachListBean> getTeach_list() {
            return teach_list;
        }

        public void setTeach_list(List<TeachListBean> teach_list) {
            this.teach_list = teach_list;
        }

        public static class TeachListBean {
            /**
             * avatar : https://avatar.zhulong.com/avatar/007/17/05/69_avatar_small.jpg
             * name : 张增凯
             * title : 班主任
             */

            private String avatar;
            private String name;
            private String title;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
