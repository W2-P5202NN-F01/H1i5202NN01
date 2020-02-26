package com.moko.w2.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.moko.w2.R;
import com.moko.w2.utils.ToastUtils;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;

public class UUIDDialog extends BaseDialog<String> {

    public static final String UUID_PATTERN = "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}";
    @Bind(R.id.et_uuid)
    EditText etUUID;

    private Pattern pattern;
    

    public UUIDDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_uuid;
    }

    @Override
    protected void renderConvertView(View convertView, String pid) {
        pattern = Pattern.compile(UUID_PATTERN);
        //限制只输入大写，自动小写转大写
        etUUID.setTransformationMethod(new A2bigA());
        etUUID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().toUpperCase();
                if (!pattern.matcher(input).matches()) {
                    if (input.length() == 9 && !input.endsWith("-")) {
                        String show = input.substring(0, 8) + "-" + input.substring(8, input.length());
                        etUUID.setText(show);
                        etUUID.setSelection(show.length());
                    }
                    if (input.length() == 14 && !input.endsWith("-")) {
                        String show = input.substring(0, 13) + "-" + input.substring(13, input.length());
                        etUUID.setText(show);
                        etUUID.setSelection(show.length());
                    }
                    if (input.length() == 19 && !input.endsWith("-")) {
                        String show = input.substring(0, 18) + "-" + input.substring(18, input.length());
                        etUUID.setText(show);
                        etUUID.setSelection(show.length());
                    }
                    if (input.length() == 24 && !input.endsWith("-")) {
                        String show = input.substring(0, 23) + "-" + input.substring(23, input.length());
                        etUUID.setText(show);
                        etUUID.setSelection(show.length());
                    }
                }
            }
        });
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ensure:
                String uuid = etUUID.getText().toString().replaceAll("-", "");
                if (TextUtils.isEmpty(uuid)) {
                    ToastUtils.showToast(getContext(), "Data format incorrect!");
                    return;
                }
                if (uuid.length() != 32) {
                    ToastUtils.showToast(getContext(), "Data format incorrect!");
                    return;
                }
                dismiss();
                if (uuidClickListener != null)
                    uuidClickListener.onEnsureClicked(uuid);
                break;
        }
    }

    private UUIDClickListener uuidClickListener;

    public void setOnUUIDClicked(UUIDClickListener uuidClickListener) {
        this.uuidClickListener = uuidClickListener;
    }

    public interface UUIDClickListener {

        void onEnsureClicked(String uuid);
    }

    public void showKeyboard() {
        if (etUUID != null) {
            //设置可获得焦点
            etUUID.setFocusable(true);
            etUUID.setFocusableInTouchMode(true);
            //请求获得焦点
            etUUID.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) etUUID
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etUUID, 0);
        }
    }

    public class A2bigA extends ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = {'A', 'B', 'C', 'D', 'E', 'F'};
            return cc;
        }
    }
}
