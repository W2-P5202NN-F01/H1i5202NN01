package com.moko.support.entity;


import java.io.Serializable;

public enum AdvIntervalEnum implements Serializable {
    ADVINTERVAL_20(20),
    ADVINTERVAL_50(50),
    ADVINTERVAL_100(100),
    ADVINTERVAL_200(200),
    ADVINTERVAL_250(250),
    ADVINTERVAL_350(350),
    ADVINTERVAL_500(500),
    ADVINTERVAL_1000(1000),;

    private int advInterval;

    AdvIntervalEnum(int advInterval) {
        this.advInterval = advInterval;
    }

    public static AdvIntervalEnum fromOrdinal(int ordinal) {
        for (AdvIntervalEnum advIntervalEnum : AdvIntervalEnum.values()) {
            if (advIntervalEnum.ordinal() == ordinal) {
                return advIntervalEnum;
            }
        }
        return null;
    }
    public static AdvIntervalEnum fromAdvInterval(int advInterval) {
        for (AdvIntervalEnum advIntervalEnum : AdvIntervalEnum.values()) {
            if (advIntervalEnum.getAdvInterval() == advInterval) {
                return advIntervalEnum;
            }
        }
        return null;
    }

    public int getAdvInterval() {
        return advInterval;
    }
}
