package com.hse24.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import com.hse24.app.adapter.presenter.CategoryPresenter;
import com.hse24.app.adapter.presenter.ChildPresenterHolder;
import com.hse24.app.adapter.presenter.ParentPresenterHolder;
import com.hse24.app.model.CategoryItem;

import java.util.List;

public class CategoryAdapter extends ExpandableRecyclerAdapter<ParentViewHolder, ChildViewHolder> {

    private final View.OnClickListener onClickListener;
    private final Context context;
    private final Bundle params;

    public CategoryAdapter(Context context, List<ParentObject> items, View.OnClickListener onClickListener) {
        super(context, items);
        this.context = context;
        this.onClickListener = onClickListener;
        params = new Bundle();
    }

    @Override
    public ParentViewHolder onCreateParentViewHolder(ViewGroup parent) {
        Bundle paramsType = (Bundle) params.clone();
        paramsType.putSerializable("type", CategoryItem.TYPE.TITLE);
        CategoryPresenter viewPresenter = new CategoryPresenter();
        viewPresenter.initViewPresenter(context, parent, paramsType, null);
        return new ParentPresenterHolder<>(viewPresenter);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            ParentViewHolder pvh = this.onCreateParentViewHolder(viewGroup);
            pvh.setParentItemClickListener(this);
            return pvh;
        } else if (viewType == 1) {
            return this.onCreateChildViewHolder(viewGroup);
        } else {
            return onCreateNormalViewHolder(viewGroup);
        }
    }

    private RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent) {
        Bundle paramsType = (Bundle) params.clone();
        paramsType.putSerializable("type", CategoryItem.TYPE.MAIN);
        CategoryPresenter viewPresenter = new CategoryPresenter();
        viewPresenter.initViewPresenter(context, parent, paramsType, onClickListener);
        return new ParentPresenterHolder<>(viewPresenter);
    }

    public int getItemViewType(int position) {
        switch (((CategoryItem) mItemList.get(position)).getType()) {
            case MAIN:
                return 2;
            case SUB:
                return 1;
            case TITLE:
                return 0;
            default:
                return 2;
        }
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent) {
        Bundle paramsType = (Bundle) params.clone();
        paramsType.putSerializable("type", CategoryItem.TYPE.SUB);
        CategoryPresenter viewPresenter = new CategoryPresenter();
        viewPresenter.initViewPresenter(context, parent, paramsType, onClickListener);
        return new ChildPresenterHolder<>(viewPresenter);
    }

    @Override
    public void onBindParentViewHolder(ParentViewHolder parentViewHolder, int position, Object o) {
        Bundle paramsType = (Bundle) params.clone();
        paramsType.putBoolean("expanded", parentViewHolder.isExpanded());
        ((ParentPresenterHolder) parentViewHolder).setParams(paramsType);
        ((ParentPresenterHolder) parentViewHolder).swapData(position, o);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int position, Object o) {
        ((ChildPresenterHolder) childViewHolder).setParams(params);
        ((ChildPresenterHolder) childViewHolder).swapData(position, o);

    }

    public void setSelection(int idCategory) {
        params.putInt("selection", idCategory);
        notifyDataSetChanged();
    }

    @Override
    public void onParentItemClickListener(int position) {
        super.onParentItemClickListener(position);
        notifyItemChanged(position);
    }
}