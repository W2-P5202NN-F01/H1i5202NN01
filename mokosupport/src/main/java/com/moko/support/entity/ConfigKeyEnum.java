package com.moko.support.entity;


import java.io.Serializable;

public enum ConfigKeyEnum implements Serializable {
    GET_DEVICE_MAC(0x20),
    SET_DEVICE_MAC(0x30),
    GET_RUNNING_TIME(0x59),
    SET_CLOSE(0x26),
    ;

    private int configKey;

    ConfigKeyEnum(int configKey) {
        this.configKey = configKey;
    }


    public int getConfigKey() {
        return configKey;
    }

    public static ConfigKeyEnum fromConfigKey(int configKey) {
        for (ConfigKeyEnum configKeyEnum : ConfigKeyEnum.values()) {
            if (configKeyEnum.getConfigKey() == configKey) {
                return configKeyEnum;
            }
        }
        return null;
    }
}
