package com.vip.pda;

import android.Manifest;
import android.content.Intent;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.king.zxing.CameraConfig;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DefaultCameraScan;
import com.king.zxing.analyze.MultiFormatAnalyzer;
import com.vip.pda.bean.BarcodeInfo;
import com.vip.pda.bean.StockBean;
import com.vip.pda.file.SPUtils;
import com.vip.pda.file.SharePopup;
import com.vip.pda.http.ApiDisposableObserver;
import com.vip.pda.http.BaseResponse;
import com.vip.pda.http.RetrofitClient;
import com.vip.pda.utils.CommonUtils;
import com.vip.pda.utils.ToastUtils;

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
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

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
        adapter = new StockAdapter(this, true);
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
                String barcode = etBarcode.getText().toString().replaceAll("\n", "").trim();
                if (!TextUtils.isEmpty(barcode)) {
                    etBarcode.setText("");
                    //????????????????????????????????????
                    if (titleText.contains("??????") && TextUtils.isEmpty(tvDh.getText().toString())) {
                        if (barcode.startsWith("DO") || barcode.startsWith("BDO")) {
                            tvDh.setText(barcode);
                        } else {
                            ToastUtils.showShort("????????????????????????");
                        }
                    } else if (titleText.contains("??????") && TextUtils.isEmpty(tvDh.getText().toString())) {
                        if (barcode.startsWith("DO") || barcode.startsWith("BDO")) {
                            tvDh.setText(barcode);
                        } else {
                            ToastUtils.showShort("????????????????????????");
                        }
                    } else {
                        if (barcode.startsWith("(")) {
                            if (!list.contains(barcode)) {
                                list.add(barcode);
                                adapter.insert(barcode);
                                if (list.size() > 3) rv.smoothScrollToPosition(list.size() - 1);
                            } else {
                                ToastUtils.showShort("??????????????????");
                            }
                        } else {
                            ToastUtils.showShort("????????????????????????");
                        }
                    }
                } else {
                    ToastUtils.showShort("????????????????????????");
                }
            }
            return false;
        });
    }

    /**
     * ??????????????????
     */
    private void checkCameraPermissions() {
        if (CommonUtils.checkHasPermission(this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(this, CaptureActivity.class);
            ActivityCompat.startActivityForResult(this, intent, REQUEST_CODE_SCAN, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @OnClick({R.id.mock, R.id.add, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mock:
//                Random random = new Random();
//                String[] c = {"BDO111122223333", "BDO222233331111", "BDO333311112222",
//                        "BDO333322221111", "BDO444422221111", "BDO555522221111"};
//                String[] s = {"(01)90.GN.2001B.5033.C000003;(03)1;(07)OA01000398;(05)21040900002",
//                        "(01)90.GN.2001B.9107.C000002;(03)1;(07)OA01000398;(05)21040900003",
//                        "(01)90.GN.2001B.1762.C000003;(03)1;(07)OA01000398;(05)21040900004",
//                        "(01)90.GN.2001B.9007.C000001;(03)1;(07)OA01000398;(05)21040900005",
//                        "(01)90.GN.2001B.5033.C000003;(03)1;(07)OA01000398;(05)21040900006",
//                        "(01)90.GN.2001B.9007.C000001;(03)1;(07)OA01000398;(05)21040900007"};
//                String result = s[random.nextInt(5)];
//                list.add(result);
//                adapter.insert(result);
                checkCameraPermissions();
                break;
            case R.id.add:
                popup.showAtLocation(title, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.commit:
                if (list.size() > 0) {
                    scanIn(list);
                } else {
                    ToastUtils.showShort("????????????");
                }
                break;
        }
    }

    private void scanIn(List<String> list) {
        if (titleText.equals("????????????")) {
            RetrofitClient.getApiService().saveStockDetailList(list).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(apiDisposableObserver);
        } else if (titleText.equals("????????????")) {
            List<BarcodeInfo> list_ = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                list_.add(new BarcodeInfo(list.get(i)));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("fbk1", tvDh.getText().toString());
            map.put("itemList", list_);
            RetrofitClient.getApiService().outStockDetailList(map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(apiDisposableObserver);
        } else if (titleText.equals("????????????")) {
            RetrofitClient.getApiService().deleteInStockDetailList(list).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(apiDisposableObserver);
        } else if (titleText.equals("????????????")) {
            RetrofitClient.getApiService().deleteOutStockDetailList(list).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(apiDisposableObserver);
        } else if (titleText.equals("????????????")) {
            List<BarcodeInfo> list_ = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                list_.add(new BarcodeInfo(list.get(i)));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("receiptNumber", tvDh.getText().toString());
            map.put("itemList", list_);
            RetrofitClient.getApiService().saveInventoryHead(map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(apiDisposableObserver);
        }
    }

    ApiDisposableObserver apiDisposableObserver = new ApiDisposableObserver<BaseResponse>() {
        @Override
        public void onResult(BaseResponse response) {
            if (!response.isSuccess()) {
                commitFailed(response);
            } else {
                ToastUtils.showShort("????????????");
            }
            finish();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            commitFailed(null);
            finish();
        }
    };

    private void commitFailed(BaseResponse response) {
        String re = response != null && !TextUtils.isEmpty(response.getMessage()) ? ("\n???????????????" + response.getMessage()) : "";
        ToastUtils.showLong("????????????,?????????????????????: " + tvDh.getText().toString() + re);
        StockBean bean = new StockBean();
        bean.setDh(tvDh.getText().toString());
        bean.setList(list);
        SPUtils.getInstance(userKey).putObject(titleText + "|" + tvDh.getText().toString(), bean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    String result = CameraScan.parseScanResult(data);
                    if (!TextUtils.isEmpty(result)) {
                        if (titleText.contains("??????") && TextUtils.isEmpty(tvDh.getText().toString())) {
                            if (result.startsWith("DO") || result.startsWith("BDO")) {
                                tvDh.setText(result);
                            } else {
                                ToastUtils.showShort("????????????????????????");
                            }
                        } else if (titleText.contains("??????") && TextUtils.isEmpty(tvDh.getText().toString())) {
                            if (result.startsWith("DO") || result.startsWith("BDO")) {
                                tvDh.setText(result);
                            } else {
                                ToastUtils.showShort("????????????????????????");
                            }
                        } else {
                            if (result.startsWith("(")) {
                                if (!list.contains(result)) {
                                    list.add(result);
                                    adapter.insert(result);
                                    if (list.size() > 3) rv.smoothScrollToPosition(list.size() - 1);
                                } else {
                                    ToastUtils.showShort("??????????????????");
                                }
                            } else {
                                ToastUtils.showShort("????????????????????????");
                            }
                        }
                    } else {
                        ToastUtils.showShort("????????????????????????");
                    }
                    break;
            }
        }
    }
}
