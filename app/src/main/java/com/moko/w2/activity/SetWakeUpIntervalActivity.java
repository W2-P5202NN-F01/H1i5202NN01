package com.moko.w2.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.moko.w2.BeaconConstants;
import com.moko.w2.R;
import com.moko.w2.service.MokoService;
import com.moko.w2.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.entity.OrderType;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Date 2020/1/16
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.w2.activity.SetWakeUpIntervalActivity
 */
public class SetWakeUpIntervalActivity extends BaseActivity {


    @Bind(R.id.et_wake_up_interval)
    EditText etWakeUpInterval;
    private MokoService mMokoService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up_interval);
        ButterKnife.bind(this);
        bindService(new Intent(this, MokoService.class), mServiceConnection, BIND_AUTO_CREATE);
        String wakeupIntervalStr = getIntent().getStringExtra(BeaconConstants.EXTRA_KEY_DEVICE_WAKE_UP_INTERVAL);
        etWakeUpInterval.setText(wakeupIntervalStr);
        etWakeUpInterval.setSelection(wakeupIntervalStr.length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            if (intent != null) {
                String action = intent.getAction();
                if (MokoConstants.ACTION_CONNECT_DISCONNECTED.equals(action)) {
                    ToastUtils.showToast(SetWakeUpIntervalActivity.this, getString(R.string.alert_diconnected));
                    SetWakeUpIntervalActivity.this.setResult(BeaconConstants.RESULT_CONN_DISCONNECTED);
                    finish();
                }
                if (MokoConstants.ACTION_RESPONSE_TIMEOUT.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
                    switch (orderType) {
                        case writeAndNotify:
                            ToastUtils.showToast(SetWakeUpIntervalActivity.this, getString(R.string.read_data_failed));
                            finish();
                            break;
                    }
                }
                if (MokoConstants.ACTION_RESPONSE_SUCCESS.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
                    byte[] value = intent.getByteArrayExtra(MokoConstants.EXTRA_KEY_RESPONSE_VALUE);
                    switch (orderType) {
                        case writeAndNotify:
                            if ((value[0] & 0xFF) == 0xEB && (value[1] & 0xFF) == 0x79 && (value[4] & 0xFF) == 0xAA) {
                                int wakeupInterval = Integer.valueOf(etWakeUpInterval.getText().toString());
                                Intent i = new Intent();
                                i.putExtra(BeaconConstants.EXTRA_KEY_DEVICE_WAKE_UP_INTERVAL, wakeupInterval);
                                SetWakeUpIntervalActivity.this.setResult(RESULT_OK, i);
                                finish();
                            } else {
                                ToastUtils.showToast(SetWakeUpIntervalActivity.this, "Error");
                            }
                            break;
                    }
                }
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMokoService = ((MokoService.LocalBinder) service).getService();
            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_CONNECT_SUCCESS);
            filter.addAction(MokoConstants.ACTION_CONNECT_DISCONNECTED);
            filter.addAction(MokoConstants.ACTION_RESPONSE_SUCCESS);
            filter.addAction(MokoConstants.ACTION_RESPONSE_TIMEOUT);
            filter.addAction(MokoConstants.ACTION_RESPONSE_FINISH);
            filter.setPriority(300);
            registerReceiver(mReceiver, filter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @OnClick({R.id.tv_back, R.id.iv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_save:
                if (!MokoSupport.getInstance().isBluetoothOpen()) {
                    ToastUtils.showToast(this, "bluetooth is closed,please open");
                    return;
                }
                String wakeupIntervalStr = etWakeUpInterval.getText().toString();
                if (TextUtils.isEmpty(wakeupIntervalStr)) {
                    ToastUtils.showToast(this, getString(R.string.alert_data_cannot_null));
                    return;
                }
                int wakeupInterval = Integer.valueOf(wakeupIntervalStr);
                if (wakeupInterval < 1 || wakeupInterval > 86400) {
                    ToastUtils.showToast(this, getString(R.string.alert_measure_adv_period));
                    return;
                }
                mMokoService.sendOrder(mMokoService.setWakeUpInterval(wakeupInterval));
                break;

        }
    }
}
