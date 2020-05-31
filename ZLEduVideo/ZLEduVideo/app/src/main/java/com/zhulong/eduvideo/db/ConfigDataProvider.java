package com.zhulong.eduvideo.db;

import android.content.Context;

import com.zhulong.eduvideo.ApplicationEx;


public class ConfigDataProvider extends ConfigDataBase{

    public final static String  MONITORING_UUID = "uuid";
    public final static String  MONITORING_UUID_PWD = "uuid_pwd";

    public final static String  GLASSES_ADDRESS = "address";

    public ConfigDataProvider(Context context) {
        super(context);
    }

    public static ConfigDataProvider getInstance() {
        return ConfigDataProviderHolder.INSTANCE;
    }

    private static class ConfigDataProviderHolder {
        private static final ConfigDataProvider INSTANCE = new ConfigDataProvider(ApplicationEx.getInstance());
    }

    public void addUuid(String uuid){
        addStringData(MONITORING_UUID,uuid);
    }

    public String getUuid(){
        return  getStringData(MONITORING_UUID);
    }

    public void addUuidPwd(String pwd){
        addStringData(MONITORING_UUID_PWD,pwd);
    }

    public void addGlassesAddress(String mac){
        addStringData(GLASSES_ADDRESS,mac);
    }

    public String getGlassesAddress(){
        return  getStringData(GLASSES_ADDRESS);
    }


    public String getUuidPwd(){
        return getStringData(MONITORING_UUID_PWD);
    }

    public void clearData(){
        addUuid("");
        addUuidPwd("");
    }


}
