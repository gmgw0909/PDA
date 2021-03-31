package com.vip.pda;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vip.pda.file.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.et2)
    EditText et2;
    @BindView(R.id.et3)
    EditText et3;
    @BindView(R.id.check)
    CheckBox check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login)
    public void onViewClicked() {
        String user = et1.getText().toString();
        String zi = et2.getText().toString();
        String pass = et3.getText().toString();
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(zi) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "请输入账号信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!check.isChecked()) {
            Toast.makeText(this, "请选择离线登录", Toast.LENGTH_SHORT).show();
            return;
        }
        SPUtils.getInstance().put("User", user);
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
