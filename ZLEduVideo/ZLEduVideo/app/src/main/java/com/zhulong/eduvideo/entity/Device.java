package com.zhulong.eduvideo.entity;


public class Device {
	private String deviceName;
	private String version;
	private String system;
	private int screenWidth;
	private int screenHeight;
	private String deviceId;
	private String user_agent;// agent
	private String localIp;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}



	public String getVersion() {
		return version;
	}

	public String getSystem() {
		return system;
	}

	

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	
}
