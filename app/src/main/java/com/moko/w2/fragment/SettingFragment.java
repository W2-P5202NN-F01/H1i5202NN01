package com.moko.w2.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moko.w2.R;
import com.moko.w2.activity.DeviceInfoActivity;
import com.moko.w2.dialog.AdvIntervalDialog;
import com.moko.w2.dialog.AlertMessageDialog;
import com.moko.w2.dialog.ModifyPasswordDialog;
import com.moko.w2.dialog.PidDialog;
import com.moko.w2.dialog.TxPowerDialog;
import com.moko.w2.dialog.UUIDDialog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    @Bind(R.id.rl_password)
    RelativeLayout rlPassword;
    @Bind(R.id.rl_reset_facotry)
    RelativeLayout rlResetFacotry;
    @Bind(R.id.tv_pid)
    TextView tvPid;
    @Bind(R.id.tv_uuid)
    TextView tvUuid;
    @Bind(R.id.tv_tx_power)
    TextView tvTxPower;
    @Bind(R.id.tv_adv_interval)
    TextView tvAdvInterval;

    private DeviceInfoActivity activity;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        return view;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @OnClick({R.id.rl_pid, R.id.rl_uuid, R.id.rl_tx_power, R.id.rl_adv_interval,
            R.id.rl_password, R.id.rl_update_firmware, R.id.rl_reset_facotry, R.id.iv_power})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_pid:
                final PidDialog pidDialog = new PidDialog(getActivity());
                pidDialog.setOnPidClicked(new PidDialog.PidClickListener() {
                    @Override
                    public void onEnsureClicked(String pid) {
//                        tvPid.setText(pid);
                        activity.setPid(pid);
                    }
                });
                pidDialog.show();
                Timer pidTimer = new Timer();
                pidTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pidDialog.showKeyboard();
                            }
                        });
                    }
                }, 200);
                break;
            case R.id.rl_uuid:
                final UUIDDialog uuidDialog = new UUIDDialog(getActivity());
                uuidDialog.setOnUUIDClicked(new UUIDDialog.UUIDClickListener() {
                    @Override
                    public void onEnsureClicked(String uuid) {
//                        StringBuilder stringBuilder = new StringBuilder(uuid);
//                        stringBuilder.insert(8, "-");
//                        stringBuilder.insert(13, "-");
//                        stringBuilder.insert(18, "-");
//                        stringBuilder.insert(23, "-");
//                        tvUuid.setText(stringBuilder.toString());
                        activity.setUUID(uuid);
                    }
                });
                uuidDialog.show();
                Timer uuidTimer = new Timer();
                uuidTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uuidDialog.showKeyboard();
                            }
                        });
                    }
                }, 200);
                break;
            case R.id.rl_tx_power:
                final TxPowerDialog txPowerDialog = new TxPowerDialog(getActivity());
                txPowerDialog.setOnTxPowerClicked(new TxPowerDialog.TxPowerClickListener() {
                    @Override
                    public void onEnsureClicked(int txPower) {
//                        tvTxPower.setText(String.format("%ddBm", txPower));
                        activity.setTxPower(txPower);
                    }
                });
                txPowerDialog.show();
                break;
            case R.id.rl_adv_interval:
                final AdvIntervalDialog advIntervalDialog = new AdvIntervalDialog(getActivity());
                advIntervalDialog.setOnAdvIntervalClicked(new AdvIntervalDialog.AdvIntervalClickListener() {
                    @Override
                    public void onEnsureClicked(int advInterval) {
//                        tvAdvInterval.setText(String.format("%dms", advInterval));
                        activity.setAdvInterval(advInterval);
                    }
                });
                advIntervalDialog.show();
                Timer advIntervalTimer = new Timer();
                advIntervalTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                advIntervalDialog.showKeyboard();
                            }
                        });
                    }
                }, 200);
                break;
            case R.id.rl_password:
                final ModifyPasswordDialog modifyPasswordDialog = new ModifyPasswordDialog(activity);
                modifyPasswordDialog.setOnModifyPasswordClicked(new ModifyPasswordDialog.ModifyPasswordClickListener() {
                    @Override
                    public void onEnsureClicked(String password) {
                        activity.modifyPassword(password);
                    }
                });
                modifyPasswordDialog.show();
                Timer modifyTimer = new Timer();
                modifyTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                modifyPasswordDialog.showKeyboard();
                            }
                        });
                    }
                }, 200);
                break;
            case R.id.rl_update_firmware:
                activity.chooseFirmwareFile();
                break;
            case R.id.rl_reset_facotry:
                final AlertMessageDialog resetDeviceDialog = new AlertMessageDialog();
                resetDeviceDialog.setMessage("Are you sure to reset the device？");
                resetDeviceDialog.setOnAlertConfirmListener(new AlertMessageDialog.OnAlertConfirmListener() {
                    @Override
                    public void onClick() {
                        activity.resetDevice();
                    }
                });
                resetDeviceDialog.show(activity.getSupportFragmentManager());
                break;
            case R.id.iv_power:
                final AlertMessageDialog powerAlertDialog = new AlertMessageDialog();
                powerAlertDialog.setMessage("Are you sure to turn off the device?Please make sure the device has a button to turn on!");
                powerAlertDialog.setOnAlertConfirmListener(new AlertMessageDialog.OnAlertConfirmListener() {
                    @Override
                    public void onClick() {
                        activity.setClose();
                    }
                });
                powerAlertDialog.show(activity.getSupportFragmentManager());
                break;
        }
    }

    public void setPid(String pid) {
        tvPid.setText(pid);
    }

    public void setUUID(String uuid) {
        tvUuid.setText(uuid);
    }

    public void setTxPower(String txPower) {
        tvTxPower.setText(txPower);
    }

    public void setAdvInterval(String advInterval) {
        tvAdvInterval.setText(advInterval);
    }
}
