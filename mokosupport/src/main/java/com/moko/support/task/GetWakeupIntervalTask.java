package com.moko.support.task;


import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

/**
 * @Date 2020/1/14
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.task.GetWakeupIntervalTask
 */
public class GetWakeupIntervalTask extends OrderTask {

    public byte[] data;

    public GetWakeupIntervalTask(MokoOrderTaskCallback callback, int sendDataType) {
        super(OrderType.writeAndNotify, callback, sendDataType);
        data = new byte[4];
        data[0] = (byte) 0xEA;
        data[1] = (byte) 0x78;
        data[2] = 0;
        data[3] = 0;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
