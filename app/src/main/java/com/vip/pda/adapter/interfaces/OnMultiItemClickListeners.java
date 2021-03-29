package com.vip.pda.adapter.interfaces;


import com.vip.pda.adapter.ViewHolder;

public interface OnMultiItemClickListeners<T> {
    void onItemClick(ViewHolder viewHolder, T data, int position, int viewType);
}
