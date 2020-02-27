package com.moko.w2.dialog;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.support.entity.AdvIntervalEnum;
import com.moko.w2.R;

import butterknife.Bind;
import butterknife.OnClick;

public class AdvIntervalDialog extends BaseDialog<Integer> implements SeekBar.OnSeekBarChangeListener {


    @Bind(R.id.sb_adv_interval)
    SeekBar sbAdvInterval;
    @Bind(R.id.tv_adv_interval)
    TextView tvAdvInterval;

    public AdvIntervalDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_adv_interval;
    }

    @Override
    protected void renderConvertView(View convertView, Integer advInterval) {
        sbAdvInterval.setOnSeekBarChangeListener(this);
        int AdvIntervalProgress = AdvIntervalEnum.fromAdvInterval(advInterval).ordinal();
        sbAdvInterval.setProgress(AdvIntervalProgress);
        tvAdvInterval.setText(String.format("%dms", advInterval));
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                dismiss();
                int AdvInterval = AdvIntervalEnum.fromOrdinal(sbAdvInterval.getProgress()).getAdvInterval();
                if (AdvIntervalClickListener != null)
                    AdvIntervalClickListener.onEnsureClicked(AdvInterval);
                break;
        }
    }

    private AdvIntervalDialog.AdvIntervalClickListener AdvIntervalClickListener;

    public void setOnAdvIntervalClicked(AdvIntervalDialog.AdvIntervalClickListener AdvIntervalClickListener) {
        this.AdvIntervalClickListener = AdvIntervalClickListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        AdvIntervalEnum advIntervalEnum = AdvIntervalEnum.fromOrdinal(progress);
        int advInterval = advIntervalEnum.getAdvInterval();
        tvAdvInterval.setText(String.format("%dms", advInterval));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface AdvIntervalClickListener {

        void onEnsureClicked(int AdvInterval);
    }
}
