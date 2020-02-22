package com.moko.support.task;


import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

/**
 * @Date 2020/1/14
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.task.SetWakeupIntervalTask
 */
public class SetWakeupIntervalTask extends OrderTask {

    public byte[] data;

    public SetWakeupIntervalTask(MokoOrderTaskCallback callback, int sendDataType) {
        super(OrderType.writeAndNotify, callback, sendDataType);
    }

    public void setData(int duration) {
        byte[] durationBytes = MokoUtils.toByteArray(duration, 3);
        data = new byte[7];
        data[0] = (byte) 0xEA;
        data[1] = (byte) 0x79;
        data[2] = 0;
        data[3] = 3;
        data[4] = durationBytes[0];
        data[5] = durationBytes[1];
        data[6] = durationBytes[2];
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
