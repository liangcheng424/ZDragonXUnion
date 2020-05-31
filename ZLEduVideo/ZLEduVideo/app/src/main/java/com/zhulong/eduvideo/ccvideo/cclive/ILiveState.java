package com.zhulong.eduvideo.ccvideo.cclive;

public interface ILiveState {

    /**
     * 是否正在直播
     *      通过接口的startTime和endTime进行判断
     * @return  0 未开始
     *          1 直播中
     *          -1 已结束
     */
    int isLiving();
}
