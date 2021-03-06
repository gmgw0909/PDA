package com.vip.pda.adapter.base;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vip.pda.adapter.ViewHolder;
import com.vip.pda.adapter.interfaces.OnItemChildClickListener;
import com.vip.pda.adapter.interfaces.OnMultiItemClickListeners;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiBaseAdapter<T> extends BaseAdapter<T> {
    protected OnMultiItemClickListeners<T> mItemClickListener;

    private ArrayList<Integer> mItemChildIds = new ArrayList<>();
    private ArrayList<OnItemChildClickListener<T>> mItemChildListeners = new ArrayList<>();

    public MultiBaseAdapter(Context context, List<T> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    protected abstract void convert(ViewHolder holder, T data, int position, int viewType);

    protected abstract int getItemLayoutId(int viewType);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isCommonItemView(viewType)) {
            return ViewHolder.create(mContext, getItemLayoutId(viewType), parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (isCommonItemView(viewType)) {
            bindCommonItem(holder, position - getHeaderCount(), viewType);
        }
    }

    private void bindCommonItem(RecyclerView.ViewHolder holder, final int position, final int viewType) {
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.getConvertView().setOnClickListener(view -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(viewHolder, getAllData().get(position), position, viewType);
            }
        });

        convert(viewHolder, getAllData().get(position), position, viewType);

        for (int i = 0; i < mItemChildIds.size(); i++) {
            final int tempI = i;
            if (viewHolder.getConvertView().findViewById(mItemChildIds.get(i)) != null) {
                viewHolder.getConvertView()
                        .findViewById(mItemChildIds.get(i))
                        .setOnClickListener(v -> mItemChildListeners.get(tempI)
                                .onItemChildClick(viewHolder, getAllData().get(position), position));
            }
        }
    }

    public void setOnMultiItemClickListener(OnMultiItemClickListeners<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setOnItemChildClickListener(int viewId, OnItemChildClickListener<T> itemChildClickListener) {
        mItemChildIds.add(viewId);
        mItemChildListeners.add(itemChildClickListener);
    }
}
