package com.zhulong.eduvideo.entity;

import java.io.Serializable;

public class Classify implements Serializable {

	private static final long serialVersionUID = 6917786918425022640L;

	private String classid;
	private String classname;
	private String uid;
	private String dateline;
	private String count;

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

}
