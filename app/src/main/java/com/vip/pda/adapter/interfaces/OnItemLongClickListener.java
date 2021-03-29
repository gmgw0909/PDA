package com.vip.pda.adapter.interfaces;


import com.vip.pda.adapter.ViewHolder;

public interface OnItemLongClickListener<T> {
    void onItemLongClick(ViewHolder viewHolder, T data, int position);
}
