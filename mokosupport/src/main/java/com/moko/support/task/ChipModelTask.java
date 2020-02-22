package com.moko.support.task;


import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.ChipModelTask
 */
public class ChipModelTask extends OrderTask {

    public byte[] data;

    public ChipModelTask(MokoOrderTaskCallback callback, int sendDataType) {
        super(OrderType.writeAndNotify, callback, sendDataType);
        setData();
    }

    public void setData() {
        data = new byte[4];
        data[0] = (byte) 0xEA;
        data[1] = (byte) 0x5B;
        data[2] = 0;
        data[3] = 0;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
