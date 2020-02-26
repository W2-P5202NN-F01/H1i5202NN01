package com.moko.w2.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;
import com.moko.w2.AppConstants;
import com.moko.w2.R;
import com.moko.w2.adapter.BeaconListAdapter;
import com.moko.w2.dialog.LoadingDialog;
import com.moko.w2.dialog.LoadingMessageDialog;
import com.moko.w2.dialog.PasswordDialog;
import com.moko.w2.entity.BeaconInfo;
import com.moko.w2.service.MokoService;
import com.moko.w2.utils.BeaconInfoParseableImpl;
import com.moko.w2.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Date 2020/2/22
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.w2.activity.MainActivity
 */
public class MainActivity extends BaseActivity implements MokoScanDeviceCallback, BaseQuickAdapter.OnItemChildClickListener {

    @Bind(R.id.rv_device_list)
    RecyclerView rvDeviceList;
    @Bind(R.id.iv_refresh)
    ImageView ivRefresh;
    @Bind(R.id.tv_devices_title)
    TextView tvDevicesTitle;


    private Animation animation = null;
    private BeaconListAdapter mAdapter;
    private ArrayList<BeaconInfo> mBeaconInfos;
    private MokoService mMokoService;
    private boolean mReceiverTag = false;
    private HashMap<String, BeaconInfo> beaconMap;
    private BeaconInfoParseableImpl beaconInfoParseable;
    private boolean mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mBeaconInfos = new ArrayList<>();
        beaconMap = new HashMap<>();
        mAdapter = new BeaconListAdapter();
        mAdapter.openLoadAnimation();
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.replaceData(mBeaconInfos);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceList.setAdapter(mAdapter);
        bindService(new Intent(this, MokoService.class), mServiceConnection, BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMokoService = ((MokoService.LocalBinder) service).getService();
            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_ORDER_RESULT);
            filter.addAction(MokoConstants.ACTION_ORDER_TIMEOUT);
            filter.addAction(MokoConstants.ACTION_ORDER_FINISH);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(100);
            registerReceiver(mReceiver, filter);
            mReceiverTag = true;
            if (!MokoSupport.getInstance().isBluetoothOpen()) {
                // 蓝牙未打开，开启蓝牙
                MokoSupport.getInstance().enableBluetooth();
            } else {
                if (animation == null) {
                    startScan();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        String action = event.getAction();
        if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
            mPassword = "";
            // 设备断开，通知页面更新
            dismissLoadingProgressDialog();
            dismissLoadingMessageDialog();
            if (!mInputPassword && animation == null) {
                ToastUtils.showToast(MainActivity.this, "Disconnected");
                startScan();
            } else {
                mInputPassword = false;
            }
        }
        if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            // 设备连接成功，通知页面更新
            dismissLoadingProgressDialog();
            showLoadingMessageDialog();
            mMokoService.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mPassword)) {
                        MokoSupport.getInstance().sendOrder(mMokoService.getLockState());
                    } else {
                        LogModule.i("锁定状态，获取unLock，解锁");
                        MokoSupport.getInstance().sendOrder(mMokoService.getUnLock());
                    }
                }
            }, 1000);
        }
    }

    private String unLockResponse;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                    OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
                    OrderType orderType = response.orderType;
                    int responseType = response.responseType;
                    byte[] value = response.responseValue;
                    switch (orderType) {
                        case lockState:
                            String valueStr = MokoUtils.bytesToHexString(value);
                            if ("00".equals(valueStr)) {
                                dismissLoadingMessageDialog();
                                if (TextUtils.isEmpty(unLockResponse)) {
                                    mInputPassword = true;
                                    MokoSupport.getInstance().disConnectBle();
                                    // 弹出密码框
                                    final PasswordDialog dialog = new PasswordDialog(MainActivity.this);
                                    dialog.setData(mSavedPassword);
                                    dialog.setOnPasswordClicked(new PasswordDialog.PasswordClickListener() {
                                        @Override
                                        public void onEnsureClicked(String password) {
                                            if (!MokoSupport.getInstance().isBluetoothOpen()) {
                                                // 蓝牙未打开，开启蓝牙
                                                MokoSupport.getInstance().enableBluetooth();
                                                return;
                                            }
                                            LogModule.i(password);
                                            mPassword = password;
                                            if (animation != null) {
                                                mMokoService.mHandler.removeMessages(0);
                                                MokoSupport.getInstance().stopScanDevice();
                                            }
                                            showLoadingProgressDialog();
                                            ivRefresh.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mMokoService.connectBluetoothDevice(mSelectedBeaconXMac);
                                                }
                                            }, 2000);
                                        }

                                        @Override
                                        public void onDismiss() {
                                            unLockResponse = "";
                                            MokoSupport.getInstance().disConnectBle();
                                        }
                                    });
                                    dialog.show();
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {

                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.showKeyboard();
                                                }
                                            });
                                        }
                                    }, 200);
                                } else {
                                    dismissLoadingMessageDialog();
                                    unLockResponse = "";
                                    MokoSupport.getInstance().disConnectBle();
                                    ToastUtils.showToast(MainActivity.this, "Password error");
                                }
                            } else if ("02".equals(valueStr)) {
                                // 不需要密码验证
                                dismissLoadingMessageDialog();
                                Intent deviceInfoIntent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                                deviceInfoIntent.putExtra(AppConstants.EXTRA_KEY_PASSWORD, mPassword);
                                startActivityForResult(deviceInfoIntent, AppConstants.REQUEST_CODE_DEVICE_INFO);
                            } else {
                                // 解锁成功
                                dismissLoadingMessageDialog();
                                LogModule.i("解锁成功");
                                unLockResponse = "";
                                mSavedPassword = mPassword;
                                Intent deviceInfoIntent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                                deviceInfoIntent.putExtra(AppConstants.EXTRA_KEY_PASSWORD, mPassword);
                                startActivityForResult(deviceInfoIntent, AppConstants.REQUEST_CODE_DEVICE_INFO);
                            }
                            break;
                        case unLock:
                            if (responseType == OrderTask.RESPONSE_TYPE_READ) {
                                unLockResponse = MokoUtils.bytesToHexString(value);
                                LogModule.i("返回的随机数：" + unLockResponse);
                                MokoSupport.getInstance().sendOrder(mMokoService.setConfigNotify(), mMokoService.setUnLock(mPassword, value));
                            }
                            if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
                                MokoSupport.getInstance().sendOrder(mMokoService.getLockState());
                            }
                            break;
                    }
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            if (animation != null) {
                                mMokoService.mHandler.removeMessages(0);
                                MokoSupport.getInstance().stopScanDevice();
                                onStopScan();
                            }
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (animation == null) {
                                startScan();
                            }
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CODE_DEVICE_INFO:
                    mPassword = "";
                    if (animation == null) {
                        startScan();
                    }
                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.iv_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                if (isWindowLocked())
                    return;
                if (!MokoSupport.getInstance().isBluetoothOpen()) {
                    // 蓝牙未打开，开启蓝牙
                    MokoSupport.getInstance().enableBluetooth();
                    return;
                }
                if (animation == null) {
                    startScan();
                } else {
                    mMokoService.mHandler.removeMessages(0);
                    MokoSupport.getInstance().stopScanDevice();
                }
                break;
        }
    }

    private void startScan() {
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
            return;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        findViewById(R.id.iv_refresh).startAnimation(animation);
        beaconInfoParseable = new BeaconInfoParseableImpl();
        MokoSupport.getInstance().startScanDevice(this);
        mMokoService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MokoSupport.getInstance().stopScanDevice();
            }
        }, 1000 * 60);
    }

    @Override
    public void onStartScan() {
        beaconMap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (animation != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.replaceData(mBeaconInfos);
                            tvDevicesTitle.setText(getString(R.string.device_list_title_num, mBeaconInfos.size()));
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateDevices();
                }
            }
        }).start();
    }

    @Override
    public void onScanDevice(DeviceInfo device) {
        BeaconInfo beaconInfo = beaconInfoParseable.parseDeviceInfo(device);
        if (beaconInfo == null) {
            return;
        }
        beaconMap.put(beaconInfo.mac, beaconInfo);
    }

    @Override
    public void onStopScan() {
        findViewById(R.id.iv_refresh).clearAnimation();
        animation = null;
    }

    private void updateDevices() {
        mBeaconInfos.clear();
        mBeaconInfos.addAll(beaconMap.values());
        if (!mBeaconInfos.isEmpty()) {
            Collections.sort(mBeaconInfos, new Comparator<BeaconInfo>() {
                @Override
                public int compare(BeaconInfo lhs, BeaconInfo rhs) {
                    if (lhs.rssi > rhs.rssi) {
                        return -1;
                    } else if (lhs.rssi < rhs.rssi) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
    }

    private LoadingDialog mLoadingDialog;

    private void showLoadingProgressDialog() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingProgressDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismissAllowingStateLoss();
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    private void showLoadingMessageDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Verifying..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingMessageDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    private String mPassword;
    private String mSavedPassword;
    private String mSelectedBeaconXMac;

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
            return;
        }
        final BeaconInfo beaconInfo = (BeaconInfo) adapter.getItem(position);
        if (beaconInfo != null && !isFinishing()) {
            LogModule.i(beaconInfo.toString());
            if (animation != null) {
                mMokoService.mHandler.removeMessages(0);
                MokoSupport.getInstance().stopScanDevice();
            }

            mSelectedBeaconXMac = beaconInfo.mac;
            showLoadingProgressDialog();
            ivRefresh.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMokoService.connectBluetoothDevice(beaconInfo.mac);
                }
            }, 2000);
        }
    }
}
