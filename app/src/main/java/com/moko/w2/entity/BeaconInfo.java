package com.moko.w2.entity;

import java.io.Serializable;

/**
 * @Date 2020/2/22
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.w2.entity.BeaconInfo
 */
public class BeaconInfo implements Serializable{
    public String pid;
    public int rssi;
    public String mac;
    public String uuid;


    @Override
    public String toString() {
        return "BeaconInfo{" +
                "pid='" + pid + '\'' +
                ", rssi=" + rssi +
                ", mac='" + mac + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
