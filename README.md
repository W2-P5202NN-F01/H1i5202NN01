#MK20200102YK1B Android SDK Instruction DOC（English）


## 1. Import project

**1.1 Import "Module mokosupport" to root directory**

**1.2 Edit "settings.gradle" file**

```
include ':app', ':mokosupport'
```

**1.3 Edit "build.gradle" file under the APP project**


	dependencies {
		...
		implementation project(path: ':mokosupport')
	}

## 2. How to use

**Initialize sdk at project initialization**

```
MokoSupport.getInstance().init(getApplicationContext());
```

**SDK provides three main functions:**

* Scan the device;
* Connect to the device;
* Send and receive data.

### 2.1 Scan the device

 **Start scanning**

```
MokoSupport.getInstance().startScanDevice(callback);
```

 **End scanning**

```
MokoSupport.getInstance().stopScanDevice();
```
 **Implement the scanning callback interface**

```java
/**
 * @ClassPath com.moko.support.callback.MokoScanDeviceCallback
 */
public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
```
* **Analysis `DeviceInfo` ; inferred `BeaconInfo`**

```
BeaconInfo beaconInfo = new BeaconInfoParseableImpl().parseDeviceInfo(device);
```

Device types can be distinguished by `parseDeviceInfo(DeviceInfo deviceInfo)`.Refer `deviceInfo.scanResult.getScanRecord().getServiceUuids()` we can get parcelUuid,etc.

```
if (0x11 != (scanRecordBytes[3] & 0xFF) || 0x07 != (scanRecordBytes[4] & 0xFF))
            return null;
        List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
        ...

```

### 2.2 Connect to the device


```
MokoSupport.getInstance().connDevice(context, address, mokoConnStateCallback);
```

When connecting to the device, context, MAC address and callback interface of connection status (`MokoConnStateCallback`) should be transferred in.


```java
public interface MokoConnStateCallback {

    /**
     * @Description  Connecting succeeds
     */
    void onConnectSuccess();

    /**
     * @Description  Disconnect
     */
    void onDisConnected();
}
```

"Demo Project" implements callback interface in Service. It uses `EventBus` to notify activity after receiving the status, and send and receive data after connecting to the device whit `broadcast`

### 2.3 Send and receive data.

All the request data is encapsulated into **TASK**, and sent to the device in a **QUEUE** way.
SDK gets task status from task callback (`MokoOrderTaskCallback`) after sending tasks successfully.

* **Task**

At present, all the tasks sent from the SDK can be divided into 4 types:

> 1.  READ：Readable
> 2.  WRITE：Writable
> 3.  NOTIFY：Can be listened( Need to enable the notification property of the relevant characteristic values)
> 4.  WRITE_NO_RESPONSE：After enabling the notification property, send data to the device and listen to the data returned by device.

Encapsulated tasks are as follows:

|Task Class|Task Type|Function
|----|----|----
|`NotifyConfigTask`|NOTIFY|Enable notification property


Custom device information
--

|Task Class|Task Type|Function
|----|----|----
|`LockStateTask`|READ|Get Lock State; **0x00** stands for LOCKED and needs to be unlocked; **0x01** stands for UNLOCKED; **0x02** stands for Uulocked and automatic relock disabled.
|`LockStateTask`|WRITE|Set new password; AES encryption of 16 byte new password with 16 byte old password ( To prevent the new password from being broadcast in the clear, the client shall AES-128-ECB encrypt the new password with the existing password. The Beacon shall perform the decryption with its existing password and set that value as the new password. ).
|`UnLockTask`|READ|Get a 128-bit challenge token. This token is for one-time use and cannot be replayed.To securely unlock the Beacon, the host must write a one-time use unlock_token into the characteristic. To create the unlock_token, it first reads the randomly generated 16-byte challenge and generates it using AES-128-ECB.encrypt (key=password[16], text=challenge[16]).
|`UnLockTask`|WRITE|Unlock，If the result of this calculation matches the unlock_token written to the characteristic, the beacon is unlocked. Sets the LOCK STATE to 0x01 on success.
|`ManufacturerTask`|READ|Get manufacturer.
|`ProductModelTask`|READ|Get product model.
|`ManufactureDateTask`|READ|Get manufacturer date.
|`HardwareVersionTask`|READ|Get hardware version.
|`FirmwareVersionTask`|READ|Get firmware version.
|`SoftwareVersionTask`|READ|Get software version.
|`BatteryTask`|READ|Get battery capacity.
|`RadioTxPowerTask`|READ|Get current SLOT Tx Power.
|`RadioTxPowerTask`|WRITE|Set current SLOT Tx Power(1bytes). Please take `TxPowerEnum` as reference
|`AdvIntervalTask`|READ|Get current SLOT broadcasting Interval.
|`AdvIntervalTask`|WRITE|Set current SLOT broadcasting Interval(2bytes). Range：100ms- 5000ms. Example：0x03E8=1000 (Unit:ms).
|`ResetDeviceTask`|WRITE|Reset
|`WriteConfigTask`|WRITE_NO_RESPONSE|Write `ConfigKeyEnum.GET_DEVICE_MAC`，get MAC address.
|`WriteConfigTask`|WRITE_NO_RESPONSE|Write`ConfigKeyEnum.SET_CLOSE`，close the device.
|`WriteConfigTask`|WRITE_NO_RESPONSE|Write `ConfigKeyEnum.GET_RUNNING_TIME`，get running time params.

PID information
--

|Task Class|Task Type|Function
|----|----|----
|`AdvSlotDataTask`|READ|get the current SLOT data
|`AdvSlotDataTask`|WRITE|set the current SLOT data

	PID data composition：SLOT type(0x90) + UUID(16bytes) + PID(7bytes)

* **Create tasks**

The task callback (`MokoOrderTaskCallback`) and task type need to be passed when creating a task. Some tasks also need corresponding parameters to be passed.

Examples of creating tasks are as follows:

```
    /**
     * @Description   get LOCK STATE
     */
    public OrderTask getLockState() {
        LockStateTask lockStateTask = new LockStateTask(this, OrderTask.RESPONSE_TYPE_READ);
        return lockStateTask;
    }
	...
	/**
     * @Description set slot data
     */
    public OrderTask setSlotData(byte[] data) {
        AdvSlotDataTask advSlotDataTask = new AdvSlotDataTask(this, OrderTask.RESPONSE_TYPE_WRITE);
        advSlotDataTask.setData(data);
        return advSlotDataTask;
    }
	...
    /**
     * @Description   get device mac
     */
    public OrderTask getDeviceMac() {
        WriteConfigTask writeConfigTask = new WriteConfigTask(this);
        writeConfigTask.setData(ConfigKeyEnum.GET_DEVICE_MAC);
        return writeConfigTask;
    }
```

* **Send tasks**

```
MokoSupport.getInstance().sendOrder(OrderTask... orderTasks);
```

The task can be one or more.

* **Task callback**

```java
/**
 * @ClassPath com.moko.support.callback.OrderCallback
 */
public interface MokoOrderTaskCallback {

    void onOrderResult(OrderTaskResponse response);

    void onOrderTimeout(OrderTaskResponse response);

    void onOrderFinish();
}
```
`void onOrderResult(OrderTaskResponse response);`

	After the task is sent to the device, the data returned by the device can be obtained by using the `onOrderResult` function, and you can determine witch class the task is according to the `response.orderType` function. The `response.responseValue` is the returned data.

`void onOrderTimeout(OrderTaskResponse response);`

	Every task has a default timeout of 3 seconds to prevent the device from failing to return data due to a fault and the fail will cause other tasks in the queue can not execute normally. After the timeout, the `onOrderTimeout` will be called back. You can determine witch class the task is according to the `response.orderType` function and then the next task continues.

`void onOrderFinish();`

	When the task in the queue is empty, `onOrderFinish` will be called back.

Get `OrderTaskResponse` from the **intent** of `onReceive`, and the corresponding **key** value is `MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK`.

## 3. Special instructions

> 1. AndroidManifest.xml of SDK has declared to access SD card and get Bluetooth permissions.
> 2. The SDK comes with logging, and if you want to view the log in the SD card, please to use "LogModule". The log path is : root directory of SD card/w2Log/H1i5202NN01. It only records the log of the day and the day before.
> 3. Just connecting to the device successfully, it needs to delay 1 second before sending data, otherwise the device can not return data normally.
> 4. We suggest that sending and receiving data should be executed in the "Service". There will be a certain delay when the device returns data, and you can broadcast data to the "Activity" after receiving in the "Service". Please refer to the "Demo Project".















