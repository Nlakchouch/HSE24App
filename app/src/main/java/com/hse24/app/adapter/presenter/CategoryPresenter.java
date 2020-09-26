package com.hse24.app.adapter.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hse24.app.R;
import com.hse24.app.adapter.model.CategoryItem;
import com.lfm.rvgenadapter.ViewPresenter;

public class CategoryPresenter extends ViewPresenter<CategoryItem> {

    private View view;

    protected TextView titreView;
    protected View menuToggle;

    private int selection = 0;
    private CategoryItem.TYPE type;
    private View.OnClickListener onClickListener;
    private boolean isExpanded = false;

    @Override
    public void initViewPresenter(Context context, ViewGroup parent, Bundle params, View.OnClickListener onClickListener) {

        type = (CategoryItem.TYPE) params.getSerializable("type");
        this.view = LayoutInflater.from(context).inflate(getLayout(type), parent, false);

        titreView  = view.findViewById(R.id.menu_titre);
        menuToggle = view.findViewById(R.id.menu_toggle);
        this.onClickListener = onClickListener;

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void refresh() {
        if (view == null) {
            return;
        }

        CategoryItem data = getData();
        view.setTag(R.id.tag_content, data);
        view.setTag(R.id.tag_position, getPosition());

        if (data != null) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
            return;
        }

        if (type != CategoryItem.TYPE.TITLE) {
            view.setOnClickListener(onClickListener);
        } else if (menuToggle != null) {
            menuToggle.setSelected(isExpanded);
        }
        view.setSelected(data.getId() == selection);
        titreView.setText(data.getTitre());
    }

    @Override
    public void onNewParams(Bundle params) {
        super.onNewParams(params);
        if (params != null) {
            selection = params.getInt("selection");
            isExpanded = params.getBoolean("expanded", false);
        }
    }

    public static int getLayout(CategoryItem.TYPE type) {

        switch (type) {
            case MAIN:
                return R.layout.item_menu;
            case TITLE:
                return R.layout.item_menu_title;
            case SUB:
                return R.layout.item_menu_sub;
        }
        return R.layout.item_menu;
    }

}
