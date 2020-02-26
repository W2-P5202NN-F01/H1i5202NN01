package com.moko.w2.dialog;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.support.entity.TxPowerEnum;
import com.moko.w2.R;

import butterknife.Bind;
import butterknife.OnClick;

public class TxPowerDialog extends BaseDialog<Integer> implements SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.sb_tx_power)
    SeekBar sbTxPower;
    @Bind(R.id.tv_tx_power)
    TextView tvTxPower;

    public TxPowerDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_tx_power;
    }

    @Override
    protected void renderConvertView(View convertView, Integer txPower) {
        sbTxPower.setOnSeekBarChangeListener(this);
        int txPowerProgress = TxPowerEnum.fromTxPower(txPower).ordinal();
        sbTxPower.setProgress(txPowerProgress);
        tvTxPower.setText(String.format("%ddBm", txPower));
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                dismiss();
                int txPower = TxPowerEnum.fromOrdinal(sbTxPower.getProgress()).getTxPower();
                if (txPowerClickListener != null)
                    txPowerClickListener.onEnsureClicked(txPower);
                break;
        }
    }

    private TxPowerClickListener txPowerClickListener;

    public void setOnTxPowerClicked(TxPowerClickListener txPowerClickListener) {
        this.txPowerClickListener = txPowerClickListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TxPowerEnum txPowerEnum = TxPowerEnum.fromOrdinal(progress);
        int txPower = txPowerEnum.getTxPower();
        tvTxPower.setText(String.format("%ddBm", txPower));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface TxPowerClickListener {

        void onEnsureClicked(int txPower);
    }
}
