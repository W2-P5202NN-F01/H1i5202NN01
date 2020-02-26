package com.moko.w2.entity;

import android.text.TextUtils;

public class ValidParams {
    public String uuidAndPid;
    public String txPower;
    public String advInterval;
    public String battery;
    public String mac;
    public String manufactureDate;
    public String productModel;
    public String softwareVersion;
    public String firmwareVersion;
    public String hardwareVersion;
    public String manufacture;
    public String runningTime;

    public void reset() {
        uuidAndPid = "";
        txPower = "";
        advInterval = "";
        battery = "";
        mac = "";
        manufactureDate = "";
        productModel = "";
        softwareVersion = "";
        firmwareVersion = "";
        hardwareVersion = "";
        manufacture = "";
        runningTime = "";
    }

    public boolean isEmpty() {
        if (TextUtils.isEmpty(uuidAndPid)
                || TextUtils.isEmpty(txPower)
                || TextUtils.isEmpty(advInterval)
                || TextUtils.isEmpty(battery)
                || TextUtils.isEmpty(mac)
                || TextUtils.isEmpty(manufactureDate)
                || TextUtils.isEmpty(productModel)
                || TextUtils.isEmpty(softwareVersion)
                || TextUtils.isEmpty(firmwareVersion)
                || TextUtils.isEmpty(hardwareVersion)
                || TextUtils.isEmpty(manufacture)
                || TextUtils.isEmpty(runningTime)) {
            return true;
        }
        return false;
    }
}
