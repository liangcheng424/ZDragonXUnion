package com.zhulong.eduvideo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 建筑圈微博图片信息
 */
public class BuildCirclePic implements Parcelable {
    /**
     * 原图
     */
    private String original;
    /**
     * 放大图
     */
    private String bmiddle;
    /**
     * 缩略图
     */
    private String thumbnail;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getBmiddle() {
        return bmiddle;
    }

    public void setBmiddle(String bmiddle) {
        this.bmiddle = bmiddle;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.original);
        dest.writeString(this.bmiddle);
        dest.writeString(this.thumbnail);
    }

    public BuildCirclePic() {
    }

    protected BuildCirclePic(Parcel in) {
        this.original = in.readString();
        this.bmiddle = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Creator<BuildCirclePic> CREATOR = new Creator<BuildCirclePic>() {
        @Override
        public BuildCirclePic createFromParcel(Parcel source) {
            return new BuildCirclePic(source);
        }

        @Override
        public BuildCirclePic[] newArray(int size) {
            return new BuildCirclePic[size];
        }
    };

    @Override
    public String toString() {
        return "BuildCirclePic{" +
                "original='" + original + '\'' +
                ", bmiddle='" + bmiddle + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
