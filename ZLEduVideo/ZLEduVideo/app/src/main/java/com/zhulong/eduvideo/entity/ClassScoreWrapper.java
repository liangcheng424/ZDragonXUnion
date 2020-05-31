package com.zhulong.eduvideo.entity;

/**
 * @author wangzhen
 * @date 2019/11/29
 */
public class ClassScoreWrapper {

    private String left;
    private String right;
    private int textSize = 12;
    private boolean isLast; //是否是最后一行

    public ClassScoreWrapper(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public ClassScoreWrapper(String left, String right, int textSize) {
        this.left = left;
        this.right = right;
        this.textSize = textSize;
    }

    public ClassScoreWrapper(String left, String right, int textSize, boolean isLast) {
        this.left = left;
        this.right = right;
        this.textSize = textSize;
        this.isLast = isLast;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
