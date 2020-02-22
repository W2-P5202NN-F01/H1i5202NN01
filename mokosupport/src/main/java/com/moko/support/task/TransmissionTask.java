package com.moko.support.task;


import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.TransmissionTask
 */
public class TransmissionTask extends OrderTask {

    public byte[] data;

    public TransmissionTask(MokoOrderTaskCallback callback, int sendDataType) {
        super(OrderType.transmission, callback, sendDataType);
    }

    public void setData(int transmission) {
        data = MokoUtils.hex2bytes(Integer.toHexString(transmission));
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
