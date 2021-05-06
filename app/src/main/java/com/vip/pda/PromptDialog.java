package com.vip.pda;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class PromptDialog extends Dialog {
    TextView tvTitle, tvMessage, btnCancel, btnOk;
    View line;
    private String title, message;

    public PromptDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_prompt);
        tvTitle = findViewById(R.id.title);
        tvMessage = findViewById(R.id.content);
        btnOk = findViewById(R.id.btn_ok);
        btnCancel = findViewById(R.id.btn_cancel);
        line = findViewById(R.id.line);
        btnCancel.setOnClickListener(v -> dismiss());
    }

    /**
     * 设备提示内容
     *
     * @param message 内容
     */
    public PromptDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 设备提示标题
     *
     * @param title 标题
     */
    public PromptDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设备提示标题
     *
     * @param text 标题
     */
    public PromptDialog setButText(String text) {
        btnOk.setText(text);
        return this;
    }

    /**
     * 设置点击好的按钮监听器
     *
     * @param l
     */
    public void setOnOkClickListener(View.OnClickListener l) {
        btnOk.setOnClickListener(l);
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    public void single() {
        btnCancel.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
    }

    private void refreshView() {
        tvTitle.setText(title);
        tvMessage.setText(message);
    }
}
