package com.moko.w2.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.moko.w2.R;
import com.moko.w2.utils.ToastUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class PidDialog extends BaseDialog<String> {
    @Bind(R.id.et_pid)
    EditText etPid;
    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";

    public PidDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_pid;
    }

    @Override
    protected void renderConvertView(View convertView, String pid) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!(source + "").matches(FILTER_ASCII)) {
                    return "";
                }

                return null;
            }
        };
        etPid.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7), filter});
        if (!TextUtils.isEmpty(pid)) {
            etPid.setText(pid);
            etPid.setSelection(pid.length());
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                String pid = etPid.getText().toString();
                if (TextUtils.isEmpty(pid)) {
                    ToastUtils.showToast(getContext(), "Data format incorrect!");
                    return;
                }
                if (pid.length() != 7) {
                    ToastUtils.showToast(getContext(), "Data format incorrect!");
                    return;
                }
                dismiss();
                if (pidClickListener != null)
                    pidClickListener.onEnsureClicked(pid);
                break;
        }
    }

    private PidClickListener pidClickListener;

    public void setOnPidClicked(PidClickListener pidClickListener) {
        this.pidClickListener = pidClickListener;
    }

    public interface PidClickListener {

        void onEnsureClicked(String pid);
    }

    public void showKeyboard() {
        if (etPid != null) {
            //设置可获得焦点
            etPid.setFocusable(true);
            etPid.setFocusableInTouchMode(true);
            //请求获得焦点
            etPid.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) etPid
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etPid, 0);
        }
    }
}
