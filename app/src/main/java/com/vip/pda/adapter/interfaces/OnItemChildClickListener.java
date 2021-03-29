package com.vip.pda.adapter.interfaces;


import com.vip.pda.adapter.ViewHolder;

public interface OnItemChildClickListener<T> {
    void onItemChildClick(ViewHolder viewHolder, T data, int position);
}
