package com.vip.pda.file;

import android.content.Context;

import com.vip.pda.R;
import com.vip.pda.adapter.ViewHolder;
import com.vip.pda.adapter.base.CommonBaseAdapter;

public class FileAdapter extends CommonBaseAdapter<String> {

    public FileAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    protected void convert(ViewHolder holder, String data, int position) {
        holder.setText(R.id.tv1, data);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_file;
    }
}
