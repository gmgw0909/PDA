package com.vip.pda;

import android.content.Context;
import android.view.View;

import com.vip.pda.adapter.ViewHolder;
import com.vip.pda.adapter.base.CommonBaseAdapter;

public class StockAdapter extends CommonBaseAdapter<String> {
    boolean delete;

    public StockAdapter(Context context, boolean delete) {
        super(context, null, false);
        this.delete = delete;
    }

    @Override
    protected void convert(ViewHolder holder, String data, int position) {
        holder.setText(R.id.tv1, position + 1 + "");
        holder.setText(R.id.tv2, data);
        holder.setVisibility(R.id.delete, delete ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_stock;
    }
}
