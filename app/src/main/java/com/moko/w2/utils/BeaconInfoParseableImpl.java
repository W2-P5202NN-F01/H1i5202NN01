package com.moko.w2.utils;

import android.os.ParcelUuid;
import android.text.TextUtils;

import com.moko.support.entity.DeviceInfo;
import com.moko.support.service.DeviceInfoParseable;
import com.moko.w2.entity.BeaconInfo;

import java.util.Map;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * @Date 2020/2/22
 * @Author wenzheng.liu
 * @Description 通用解析工具类
 * @ClassPath com.moko.w2.utils.BeaconInfoParseableImpl
 */
public class BeaconInfoParseableImpl implements DeviceInfoParseable<BeaconInfo> {

    @Override
    public BeaconInfo parseDeviceInfo(DeviceInfo deviceInfo) {
        ScanResult result = deviceInfo.scanResult;
        byte[] scanRecordBytes = result.getScanRecord().getBytes();
        if (0x11 != (scanRecordBytes[3] & 0xFF) || 0x07 != (scanRecordBytes[4] & 0xFF))
            return null;
        Map<ParcelUuid, byte[]> map = result.getScanRecord().getServiceData();
        if (map == null || map.isEmpty()) {
            return null;
        }
        String serviceDataUuid = null;
        for (ParcelUuid uuid : map.keySet()) {
            serviceDataUuid = uuid.getUuid().toString();
            break;
        }
        if (TextUtils.isEmpty(serviceDataUuid))
            return null;
        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.pid = deviceInfo.name;
        beaconInfo.rssi = deviceInfo.rssi;
        beaconInfo.uuid = serviceDataUuid;
        beaconInfo.mac = deviceInfo.mac;
        return beaconInfo;
    }
}
