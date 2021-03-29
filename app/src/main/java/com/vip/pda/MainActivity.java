package com.vip.pda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.in_work, R.id.out_work, R.id.login, R.id.all, R.id.in_delete, R.id.out_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.in_work:
                startActivity(new Intent(this, StockActivity.class)
                        .putExtra("title", "入库作业"));
                break;
            case R.id.out_work:
                startActivity(new Intent(this, StockActivity.class)
                        .putExtra("title", "出库作业"));
                break;
            case R.id.login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.all:
                startActivity(new Intent(this, StockActivity.class)
                        .putExtra("title", "库存盘点"));
                break;
            case R.id.in_delete:
                startActivity(new Intent(this, StockActivity.class)
                        .putExtra("title", "入库删除"));
                break;
            case R.id.out_delete:
                startActivity(new Intent(this, StockActivity.class)
                        .putExtra("title", "出库删除"));
                break;
        }
    }
}