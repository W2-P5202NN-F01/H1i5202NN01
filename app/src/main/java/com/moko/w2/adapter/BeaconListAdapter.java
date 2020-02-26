package com.moko.w2.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moko.w2.R;
import com.moko.w2.entity.BeaconInfo;

/**
 * @Date 2020/2/22
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.w2.adapter.BeaconListAdapter
 */
public class BeaconListAdapter extends BaseQuickAdapter<BeaconInfo, BaseViewHolder> {

    public BeaconListAdapter() {
        super(R.layout.item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, BeaconInfo item) {
        helper.setText(R.id.tv_device_rssi, String.format("Rssi:%s", item.rssi));
        helper.setText(R.id.tv_device_pid, String.format("PID:%s", TextUtils.isEmpty(item.pid) ? "N/A" : item.pid));
        helper.setText(R.id.tv_device_uuid, String.format("UUID:%s", item.uuid));
        helper.addOnClickListener(R.id.tv_connect);
    }
}
