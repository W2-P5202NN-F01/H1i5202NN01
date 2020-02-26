package com.moko.w2.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoConnStateCallback;
import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.handler.BaseMessageHandler;
import com.moko.support.log.LogModule;
import com.moko.support.task.AdvIntervalTask;
import com.moko.support.task.AdvSlotDataTask;
import com.moko.support.task.BatteryTask;
import com.moko.support.task.FirmwareVersionTask;
import com.moko.support.task.HardwareVersionTask;
import com.moko.support.task.LockStateTask;
import com.moko.support.task.ManufactureDateTask;
import com.moko.support.task.ManufacturerTask;
import com.moko.support.task.NotifyConfigTask;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.task.ProductModelTask;
import com.moko.support.task.RadioTxPowerTask;
import com.moko.support.task.ResetDeviceTask;
import com.moko.support.task.SoftwareVersionTask;
import com.moko.support.task.UnLockTask;
import com.moko.support.task.WriteConfigTask;
import com.moko.support.utils.MokoUtils;
import com.moko.w2.utils.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.w2.service.MokoService
 */
public class MokoService extends Service implements MokoConnStateCallback, MokoOrderTaskCallback {


    @Override
    public void onConnectSuccess() {
        ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
        connectStatusEvent.setAction(MokoConstants.ACTION_DISCOVER_SUCCESS);
        EventBus.getDefault().post(connectStatusEvent);
    }

    @Override
    public void onDisConnected() {
        ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
        connectStatusEvent.setAction(MokoConstants.ACTION_CONN_STATUS_DISCONNECTED);
        EventBus.getDefault().post(connectStatusEvent);
    }

    @Override
    public void onOrderResult(OrderTaskResponse response) {
        Intent intent = new Intent(new Intent(MokoConstants.ACTION_ORDER_RESULT));
        intent.putExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK, response);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    public void onOrderTimeout(OrderTaskResponse response) {
        Intent intent = new Intent(new Intent(MokoConstants.ACTION_ORDER_TIMEOUT));
        intent.putExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK, response);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    public void onOrderFinish() {
        sendOrderedBroadcast(new Intent(MokoConstants.ACTION_ORDER_FINISH), null);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            LogModule.i("启动后台服务");
        }
        mHandler = new ServiceHandler(this);

    }

    public void connectBluetoothDevice(String address) {
        MokoSupport.getInstance().connDevice(this, address, this);
    }

    /**
     * @Date 2017/5/23
     * @Author wenzheng.liu
     * @Description 断开手环
     */
    public void disConnectBle() {
        MokoSupport.getInstance().disConnectBle();
    }


    @Override
    public void onDestroy() {
        LogModule.i("关闭后台服务");
        MokoSupport.getInstance().disConnectBle();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MokoService getService() {
            return MokoService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onLowMemory() {
        LogModule.v("内存吃紧，销毁MokoService...onLowMemory");
        disConnectBle();
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogModule.v("解绑MokoService...onUnbind");
        return super.onUnbind(intent);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 处理应答
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @Description 获取通道数据
     */
    public OrderTask getSlotData() {
        AdvSlotDataTask advSlotDataTask = new AdvSlotDataTask(this, OrderTask.RESPONSE_TYPE_READ);
        return advSlotDataTask;
    }

    /**
     * @Description 设置通道信息
     */
    public OrderTask setSlotData(byte[] data) {
        AdvSlotDataTask advSlotDataTask = new AdvSlotDataTask(this, OrderTask.RESPONSE_TYPE_WRITE);
        advSlotDataTask.setData(data);
        return advSlotDataTask;
    }

    /**
     * @Description 获取信号强度
     */
    public OrderTask getRadioTxPower() {
        RadioTxPowerTask radioTxPowerTask = new RadioTxPowerTask(this, OrderTask.RESPONSE_TYPE_READ);
        return radioTxPowerTask;
    }

    /**
     * @Description 设置信号强度
     */
    public OrderTask setRadioTxPower(byte[] data) {
        RadioTxPowerTask radioTxPowerTask = new RadioTxPowerTask(this, OrderTask.RESPONSE_TYPE_WRITE);
        radioTxPowerTask.setData(data);
        return radioTxPowerTask;
    }

    /**
     * @Description 获取广播间隔
     */
    public OrderTask getAdvInterval() {
        AdvIntervalTask advIntervalTask = new AdvIntervalTask(this, OrderTask.RESPONSE_TYPE_READ);
        return advIntervalTask;
    }

    /**
     * @Description 设置广播间隔
     */
    public OrderTask setAdvInterval(byte[] data) {
        AdvIntervalTask advIntervalTask = new AdvIntervalTask(this, OrderTask.RESPONSE_TYPE_WRITE);
        advIntervalTask.setData(data);
        return advIntervalTask;
    }

    public OrderTask getBattery() {
        BatteryTask batteryTask = new BatteryTask(this);
        return batteryTask;
    }

    public OrderTask getDeviceMac() {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setData(ConfigKeyEnum.GET_DEVICE_MAC);
        return writeConfigTask;
    }

    public OrderTask getProductModel() {
        ProductModelTask deviceModelTask = new ProductModelTask(this);
        return deviceModelTask;
    }

    public OrderTask getSoftwareVersion() {
        SoftwareVersionTask softwareVersionTask = new SoftwareVersionTask(this);
        return softwareVersionTask;
    }

    public OrderTask getFirmwareVersion() {
        FirmwareVersionTask firmwareVersionTask = new FirmwareVersionTask(this);
        return firmwareVersionTask;
    }

    public OrderTask getHardwareVersion() {
        HardwareVersionTask hardwareVersionTask = new HardwareVersionTask(this);
        return hardwareVersionTask;
    }

    public OrderTask getManufacturerDate() {
        ManufactureDateTask manufactureDateTask = new ManufactureDateTask(this);
        return manufactureDateTask;
    }

    public OrderTask getManufacturer() {
        ManufacturerTask manufacturerTask = new ManufacturerTask(this);
        return manufacturerTask;
    }

    /**
     * @Description 获取设备锁状态get lock state
     */
    public OrderTask getLockState() {
        LockStateTask lockStateTask = new LockStateTask(this, OrderTask.RESPONSE_TYPE_READ);
        return lockStateTask;
    }

    public OrderTask resetDevice() {
        ResetDeviceTask resetDeviceTask = new ResetDeviceTask(this);
        return resetDeviceTask;
    }

    /**
     * @Description 设置设备锁方式
     */
    public OrderTask setLockStateDirected(boolean isDirected) {
        LockStateTask lockStateTask = new LockStateTask(this, OrderTask.RESPONSE_TYPE_WRITE);
        lockStateTask.setData(isDirected ? new byte[]{0x02} : new byte[]{0x01});
        return lockStateTask;
    }

    /**
     * @Description 设置设备锁状态set lock state
     */
    public OrderTask setLockState(String newPassword) {
        if (passwordBytes != null) {
            LogModule.i("旧密码：" + MokoUtils.bytesToHexString(passwordBytes));
            byte[] bt1 = newPassword.getBytes();
            byte[] newPasswordBytes = new byte[16];
            for (int i = 0; i < newPasswordBytes.length; i++) {
                if (i < bt1.length) {
                    newPasswordBytes[i] = bt1[i];
                } else {
                    newPasswordBytes[i] = (byte) 0xff;
                }
            }
            LogModule.i("新密码：" + MokoUtils.bytesToHexString(newPasswordBytes));
            // 用旧密码加密新密码
            byte[] newPasswordEncryptBytes = Utils.encrypt(newPasswordBytes, passwordBytes);
            if (newPasswordEncryptBytes != null) {
                LockStateTask lockStateTask = new LockStateTask(this, OrderTask.RESPONSE_TYPE_WRITE);
                byte[] unLockBytes = new byte[newPasswordEncryptBytes.length + 1];
                unLockBytes[0] = 0;
                System.arraycopy(newPasswordEncryptBytes, 0, unLockBytes, 1, newPasswordEncryptBytes.length);
                lockStateTask.setData(unLockBytes);
                return lockStateTask;
            }
        }
        return null;
    }

    /**
     * @Description 获取解锁加密内容get unlock
     */
    public OrderTask getUnLock() {
        UnLockTask unLockTask = new UnLockTask(this, OrderTask.RESPONSE_TYPE_READ);
        return unLockTask;
    }

    private byte[] passwordBytes;

    /**
     * @Description 解锁set unlock
     */
    public OrderTask setUnLock(String password, byte[] value) {
        byte[] bt1 = password.getBytes();
        passwordBytes = new byte[16];
        for (int i = 0; i < passwordBytes.length; i++) {
            if (i < bt1.length) {
                passwordBytes[i] = bt1[i];
            } else {
                passwordBytes[i] = (byte) 0xff;
            }
        }
        LogModule.i("密码：" + MokoUtils.bytesToHexString(passwordBytes));
        byte[] unLockBytes = Utils.encrypt(value, passwordBytes);
        if (unLockBytes != null) {
            UnLockTask unLockTask = new UnLockTask(this, OrderTask.RESPONSE_TYPE_WRITE);
            unLockTask.setData(unLockBytes);
            return unLockTask;
        }
        return null;
    }

    /**
     * @Description 打开配置通知set config notify
     */
    public OrderTask setConfigNotify() {
        NotifyConfigTask notifyConfigTask = new NotifyConfigTask(this, OrderTask.RESPONSE_TYPE_NOTIFY);
        return notifyConfigTask;
    }

    public OrderTask closeDevice() {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setData(ConfigKeyEnum.SET_CLOSE);
        return writeConfigTask;
    }

    public ServiceHandler mHandler;

    public class ServiceHandler extends BaseMessageHandler<MokoService> {

        public ServiceHandler(MokoService service) {
            super(service);
        }

        @Override
        protected void handleMessage(MokoService service, Message msg) {
        }
    }
}
