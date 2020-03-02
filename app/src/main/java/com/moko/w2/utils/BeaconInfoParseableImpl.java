package com.moko.w2.utils;

import android.os.ParcelUuid;
import android.text.TextUtils;

import com.moko.support.entity.DeviceInfo;
import com.moko.support.service.DeviceInfoParseable;
import com.moko.support.utils.MokoUtils;
import com.moko.w2.entity.BeaconInfo;

import java.util.Arrays;
import java.util.List;

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
        List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
        if (uuids == null || uuids.isEmpty()) {
            return null;
        }
        String serviceUuid = null;
        for (ParcelUuid uuid : uuids) {
            serviceUuid = uuid.getUuid().toString();
            if (TextUtils.isEmpty(serviceUuid))
                continue;
            break;
        }
        byte[] uuid = Arrays.copyOfRange(scanRecordBytes, 5, 21);
        String uuidStr = MokoUtils.bytesToHexString(uuid);
        StringBuilder stringBuilder = new StringBuilder(uuidStr);
        stringBuilder.insert(8, "-");
        stringBuilder.insert(13, "-");
        stringBuilder.insert(18, "-");
        stringBuilder.insert(23, "-");
        BeaconInfo beaconInfo = new BeaconInfo();
        beaconInfo.pid = deviceInfo.name;
        beaconInfo.rssi = deviceInfo.rssi;
        beaconInfo.uuid = stringBuilder.toString().toUpperCase();
        beaconInfo.mac = deviceInfo.mac;
        return beaconInfo;
    }
}
