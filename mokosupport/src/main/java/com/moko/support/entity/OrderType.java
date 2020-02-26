package com.moko.support.entity;

import java.io.Serializable;

/**
 * @Date 2020/2/22
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.entity.OrderType
 */
public enum OrderType implements Serializable {
    manufacturer("manufacturer", "00002a29-0000-1000-8000-00805f9b34fb"),
    productModel("productModel", "00002a24-0000-1000-8000-00805f9b34fb"),
    manufactureDate("manufactureDate", "00002a25-0000-1000-8000-00805f9b34fb"),
    hardwareVersion("hardwareVersion", "00002a27-0000-1000-8000-00805f9b34fb"),
    firmwareVersion("firmwareVersion", "00002a26-0000-1000-8000-00805f9b34fb"),
    softwareVersion("softwareVersion", "00002a28-0000-1000-8000-00805f9b34fb"),

    battery("battery", "e62a0006-1362-4f28-9327-f5b74e970801"),

    notifyConfig("notifyConfig", "e62a0003-1362-4f28-9327-f5b74e970801"),
    writeConfig("writeConfig", "e62a0002-1362-4f28-9327-f5b74e970801"),

    advInterval("advInterval", "a3c87503-8ed3-4bdf-8a39-a01bebede295"),
    radioTxPower("radioTxPower", "a3c87504-8ed3-4bdf-8a39-a01bebede295"),

    lockState("lockState", "a3c87506-8ed3-4bdf-8a39-a01bebede295"),
    unLock("unLock", "a3c87507-8ed3-4bdf-8a39-a01bebede295"),

    advSlotData("advSlotData", "a3c8750a-8ed3-4bdf-8a39-a01bebede295"),
    resetDevice("resetDevice", "a3c8750b-8ed3-4bdf-8a39-a01bebede295");


    private String uuid;
    private String name;

    OrderType(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
