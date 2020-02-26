package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

/**
 * @Date 2018/1/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.WriteConfigTask
 */
public class WriteConfigTask extends OrderTask {
    public byte[] data;

    public WriteConfigTask(MokoOrderTaskCallback callback) {
        super(OrderType.writeConfig, OrderEnum.WRITE_CONFIG, callback, OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ConfigKeyEnum key) {
        switch (key) {
            case GET_DEVICE_MAC:
            case SET_CLOSE:
                createGetConfigData(key.getConfigKey());
                break;
        }
    }

    private void createGetConfigData(int configKey) {
        data = new byte[]{(byte) 0xEA, (byte) configKey, (byte) 0x00, (byte) 0x00};
    }
}
