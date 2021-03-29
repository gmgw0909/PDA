package com.vip.pda.adapter.interfaces;


import com.vip.pda.adapter.ViewHolder;

public interface OnItemClickListener<T> {
    void onItemClick(ViewHolder viewHolder, T data, int position);
}
