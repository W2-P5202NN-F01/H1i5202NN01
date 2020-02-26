package com.moko.w2.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.moko.w2.R;
import com.moko.w2.utils.ToastUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class AdvIntervalDialog extends BaseDialog<Integer> {


    @Bind(R.id.et_adv_interval)
    EditText etAdvInterval;

    public AdvIntervalDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_adv_interval;
    }

    @Override
    protected void renderConvertView(View convertView, Integer advInterval) {

    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                String advInterval = etAdvInterval.getText().toString();
                if (TextUtils.isEmpty(advInterval)) {
                    ToastUtils.showToast(getContext(), "Data format incorrect!");
                    return;
                }
                int advIntervalInt = Integer.parseInt(advInterval);
                if (advIntervalInt < 1 || advIntervalInt > 100) {
                    ToastUtils.showToast(getContext(), "The Adv Interval range is 1~100");
                    return;
                }
                dismiss();
                if (advIntervalClickListener != null)
                    advIntervalClickListener.onEnsureClicked(advIntervalInt);
                break;
        }
    }

    private AdvIntervalClickListener advIntervalClickListener;

    public void setOnAdvIntervalClicked(AdvIntervalClickListener advIntervalClickListener) {
        this.advIntervalClickListener = advIntervalClickListener;
    }

    public interface AdvIntervalClickListener {

        void onEnsureClicked(int advInterval);
    }

    public void showKeyboard() {
        if (etAdvInterval != null) {
            //设置可获得焦点
            etAdvInterval.setFocusable(true);
            etAdvInterval.setFocusableInTouchMode(true);
            //请求获得焦点
            etAdvInterval.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) etAdvInterval
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etAdvInterval, 0);
        }
    }
}
