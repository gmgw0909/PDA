package com.vip.pda;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vip.pda.adapter.ViewHolder;
import com.vip.pda.adapter.interfaces.OnItemChildClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.mock)
    TextView mock;
    @BindView(R.id.et_barcode)
    EditText etBarcode;
    @BindView(R.id.rv)
    RecyclerView rv;
    StockAdapter adapter;
    List<String> list = new ArrayList<>();
    String titleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);
        titleText = getIntent().getStringExtra("title");
        title.setText(titleText);
        if ("入库作业".equals(titleText)) {
            mock.setVisibility(View.VISIBLE);
            list.addAll(SPUtils.getInstance().getList("in_list"));
        } else if ("出库作业".equals(titleText)) {
            mock.setVisibility(View.VISIBLE);
            list.addAll(SPUtils.getInstance().getList("out_list"));
        } else if ("入库删除".equals(titleText)) {
            list.addAll(SPUtils.getInstance().getList("in_list"));
        } else if ("出库删除".equals(titleText)) {
            list.addAll(SPUtils.getInstance().getList("out_list"));
        } else {
            list.addAll(SPUtils.getInstance().getList("in_list"));
            list.addAll(SPUtils.getInstance().getList("out_list"));
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StockAdapter(this, titleText.contains("删除"));
        adapter.setOnItemChildClickListener(R.id.delete, (viewHolder, data, position) -> {
            list.remove(data);
            if ("入库删除".equals(titleText)) {
                if (list.size() == 0) {
                    SPUtils.getInstance().remove("in_list");
                } else {
                    SPUtils.getInstance().put("in_list", list);
                }
            } else if ("出库删除".equals(titleText)) {
                if (list.size() == 0) {
                    SPUtils.getInstance().remove("out_list");
                } else {
                    SPUtils.getInstance().put("out_list", list);
                }
            }
            adapter.remove(position);
        });
        rv.setAdapter(adapter);
        adapter.setNewData(list);
        etBarcode.setOnKeyListener((view, keyCode, keyEvent) -> {
            Log.e("msg", "keyCode:" + keyCode + ",   keyEvent.getAction:" + keyEvent.getAction());
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                String barcode = etBarcode.getText().toString().replaceAll("\n", "");
                if ("".equals(barcode)) {
                    Toast.makeText(StockActivity.this, "扫码为空", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //在这里实现自己的逻辑代码
                adapter.insert(barcode);
            }
            return false;
        });
    }

    @OnClick(R.id.mock)
    public void onViewClicked() {
        Random random = new Random();
        String[] s = {"111122223333", "222233331111", "333311112222", "333322221111", "444422221111", "555522221111"};
        String data = s[random.nextInt(5)];
        list.add(data);
        if ("入库作业".equals(titleText)) {
            SPUtils.getInstance().put("in_list", list);
        } else if ("出库作业".equals(titleText)) {
            SPUtils.getInstance().put("out_list", list);
        }
        adapter.insert(data);
    }
}
