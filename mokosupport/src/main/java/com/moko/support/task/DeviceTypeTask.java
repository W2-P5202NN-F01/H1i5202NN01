package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderEnum;
import com.moko.support.entity.OrderType;

/**
 * @Date 2018/1/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.BatteryTask
 */
public class DeviceTypeTask extends OrderTask {

    public byte[] data;

    public DeviceTypeTask(MokoOrderTaskCallback callback) {
        super(OrderType.productModel, OrderEnum.DEVICE_TYPE, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
