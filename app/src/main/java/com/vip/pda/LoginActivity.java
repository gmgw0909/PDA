package com.vip.pda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vip.pda.bean.LoginInfo;
import com.vip.pda.file.SPUtils;
import com.vip.pda.http.ApiDisposableObserver;
import com.vip.pda.http.BaseResponse;
import com.vip.pda.http.RetrofitClient;
import com.vip.pda.utils.Md5Utils;
import com.vip.pda.utils.ToastUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
//        et1.setText("430000");
//        et2.setText("1001");
//        et3.setText("123");
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
            //{"elsAccount":"307000","elsSubAccount":"1001","elsSubAccountPassword":"202cb962ac59075b964b07152d234b70"}
            Map<String, Object> map = new HashMap<>();
            map.put("elsAccount", user);
            map.put("elsSubAccount", zi);
            try {
                map.put("elsSubAccountPassword", Md5Utils.md5(pass));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            RetrofitClient.getApiService().login(map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiDisposableObserver<BaseResponse<LoginInfo>>() {
                        @Override
                        public void onResult(BaseResponse<LoginInfo> response) {
                            if (response.isSuccess()) {
                                SPUtils.getInstance().put("User", user);
                                SPUtils.getInstance().put("Token", response.getResult().getToken());
                                ToastUtils.showShort("登录成功");
                                RetrofitClient.reCreate();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                ToastUtils.showShort(response.getMessage());
                            }
                        }
                    });
        } else {
            ToastUtils.showShort("离线登录成功");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
