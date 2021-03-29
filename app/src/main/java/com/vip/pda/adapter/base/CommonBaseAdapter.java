package com.vip.pda.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.vip.pda.adapter.ViewHolder;
import com.vip.pda.adapter.interfaces.OnItemChildClickListener;
import com.vip.pda.adapter.interfaces.OnItemClickListener;
import com.vip.pda.adapter.interfaces.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonBaseAdapter<T> extends BaseAdapter<T> {
    protected OnItemClickListener<T> mItemClickListener;
    private OnItemLongClickListener<T> mItemLongClickListener;

    private ArrayList<Integer> mItemChildIds = new ArrayList<>();
    private ArrayList<OnItemChildClickListener<T>> mItemChildListeners = new ArrayList<>();

    public CommonBaseAdapter(Context context, List<T> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    protected abstract void convert(ViewHolder holder, T data, int position);

    protected abstract int getItemLayoutId();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isCommonItemView(viewType)) {
            return ViewHolder.create(mContext, getItemLayoutId(), parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (isCommonItemView(viewType)) {
            bindCommonItem(holder, position - getHeaderCount());
        }
    }

    private void bindCommonItem(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.getConvertView().setOnClickListener(view -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(viewHolder, getAllData().get(position), position);
            }
        });

        convert(viewHolder, getAllData().get(position), position);

        viewHolder.getConvertView().setOnLongClickListener(view -> {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(viewHolder, getAllData().get(position), position);
            }
            return true;
        });

        for (int i = 0; i < mItemChildIds.size(); i++) {
            final int tempI = i;
            if (viewHolder.getConvertView().findViewById(mItemChildIds.get(i)) != null) {
                viewHolder.getConvertView()
                        .findViewById(mItemChildIds.get(i))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemChildListeners.get(tempI)
                                        .onItemChildClick(viewHolder, getAllData().get(position), position);
                            }
                        });
            }
        }
    }

    @Override
    protected int getViewType(int position, T data) {
        return TYPE_COMMON_VIEW;
    }

    public void setOnItemClickListener(OnItemClickListener<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public void setOnItemChildClickListener(int viewId, OnItemChildClickListener<T> itemChildClickListener) {
        mItemChildIds.add(viewId);
        mItemChildListeners.add(itemChildClickListener);
    }

}
