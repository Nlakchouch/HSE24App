package com.hse24.app.adapter.presenter;

import android.os.Bundle;
import android.view.View;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.lfm.rvgenadapter.ViewPresenter;

public class ChildPresenterHolder<E> extends ChildViewHolder {
    private final View view;
    private final ViewPresenter<E> viewPresenter;

    public ChildPresenterHolder(ViewPresenter<E> viewPresenter) {
        super(viewPresenter.getView());
        this.view = viewPresenter.getView();
        this.viewPresenter = viewPresenter;
    }

    public ViewPresenter<E> getViewPresenter() {
        return viewPresenter;
    }

    public void swapData(int position, E data) {
        viewPresenter.swapData(position, data);
    }

    public View getView() {
        return view;
    }

    public void setParams(Bundle params) {
        this.viewPresenter.onNewParams(params);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.viewPresenter.onNewOnClickListener(onClickListener);
    }
}
