
package com.ya.yair.domain;

public class DeviceDomain {

    private String mac;// 设备mac地址

    private String state;// 设备的状态
    
    private String poweron;//是否开关机
    
    private String air;//室内环境质量
    
    private String bm;//设备的别名

    public String getPoweron() {
        return poweron;
    }

    public void setPoweron(String poweron) {
        this.poweron = poweron;
    }

    public String getAir() {
        return air;
    }

    public void setAir(String air) {
        this.air = air;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }
    
}
