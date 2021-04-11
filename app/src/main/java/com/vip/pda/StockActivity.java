package com.vip.pda;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vip.pda.file.SPUtils;
import com.vip.pda.file.SharePopup;
import com.vip.pda.http.ApiDisposableObserver;
import com.vip.pda.http.BaseResponse;
import com.vip.pda.http.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StockActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.mock)
    TextView mock;
    @BindView(R.id.et_barcode)
    EditText etBarcode;
    @BindView(R.id.tv_dh)
    TextView tvDh;
    @BindView(R.id.rv)
    RecyclerView rv;
    StockAdapter adapter;
    List<String> list = new ArrayList<>();
    String titleText, userKey;
    SharePopup popup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);
        popup = new SharePopup(this);
        titleText = getIntent().getStringExtra("title");
        userKey = SPUtils.getInstance().getString("User");
        title.setText(titleText);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StockAdapter(this, titleText.contains("删除"));
        adapter.setOnItemChildClickListener(R.id.delete, (viewHolder, data, position) -> {
            list.remove(data);
            adapter.remove(position);
        });
        rv.setAdapter(adapter);
        Map<String, ?> map = SPUtils.getInstance(userKey).getAll();
        List<String> keys = new ArrayList<>(map.keySet());
        List<String> files = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).contains(titleText)) {
                files.add(keys.get(i).split("\\|")[1]);
            }
        }
        popup.setData(files);
        popup.setOnItemClick((viewHolder, data, position) -> {
            StockBean bean = (StockBean) SPUtils.getInstance(userKey).getObject(titleText + "|" + data);
            tvDh.setText(bean.getDh());
            adapter.setNewData(list = bean.getList());
            popup.dismiss();
        });
        etBarcode.setOnKeyListener((view, keyCode, keyEvent) -> {
            Log.e("msg", "keyCode:" + keyCode + ",   keyEvent.getAction:" + keyEvent.getAction());
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                String barcode = etBarcode.getText().toString().replaceAll("\n", "");
                if ("".equals(barcode)) {
                    Toast.makeText(StockActivity.this, "扫码为空", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //在这里实现自己的逻辑代码
                if (TextUtils.isEmpty(tvDh.getText().toString())) {
                    tvDh.setText(barcode);
                } else {
                    list.add(barcode);
                    adapter.insert(barcode);
                }
            }
            return false;
        });
    }

    @OnClick({R.id.mock, R.id.add, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mock:
                Random random = new Random();
                String[] c = {"111122223333", "222233331111", "333311112222", "333322221111", "444422221111", "555522221111"};
                String[] s = {"EAN-13/8 UPC-A/E8", "ITF 14 EAN/UCC 12", "SDF-13/8 UPC-A/", "34-/;EAN/UCC 12", "UY/?34-/;EAN/UCC 12", "OPD;/PDA-/;EAN/UCC 12"};
                if (TextUtils.isEmpty(tvDh.getText().toString())) {
                    tvDh.setText(c[random.nextInt(5)]);
                } else {
                    list.add(s[random.nextInt(5)]);
                    adapter.insert(s[random.nextInt(5)]);
                }
                break;
            case R.id.add:
                popup.showAtLocation(title, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.commit:
                if (!TextUtils.isEmpty(tvDh.getText().toString()) && list.size() > 0) {
                    StockBean bean = new StockBean();
                    bean.setDh(tvDh.getText().toString());
                    bean.setList(list);
                    SPUtils.getInstance(userKey).putObject(titleText + "|" + tvDh.getText().toString(), bean);
                } else {
                    Toast.makeText(this, "请先扫描", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void scanIn() {
        Map<String, Object> map = new HashMap<>();
        map.put("elsAccount", "307000");
        RetrofitClient.getApiService().saveStockDetailList(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiDisposableObserver<BaseResponse>() {
                    @Override
                    public void onResult(BaseResponse response) {

                    }
                });
    }
}
