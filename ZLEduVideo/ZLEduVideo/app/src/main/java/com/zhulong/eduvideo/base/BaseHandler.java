package com.zhulong.eduvideo.base;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 弱引用封装Handler类
 *
 * 防止内存泄漏:
 * 1.使用时采用静态内部类继承此类
 * 2.onDestroy()中调用Handler的removeCallbacksAndMessages(null)方法
 * @param <T> 宿主Activity/Fragment，实现BaseHandlerCallBack接口
 */
public class BaseHandler<T extends BaseHandler.BaseHandlerCallBack> extends Handler {

    private final WeakReference<T> mReference;

    public BaseHandler(T t) {
        this.mReference = new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t = mReference.get();
        if (t != null) {
            t.handMessageCallBack(msg);
        }
    }

    public interface BaseHandlerCallBack {

        /**
         * handMessage回调方法，处理消息
         * @param msg
         */
        void handMessageCallBack(Message msg);
    }

}
