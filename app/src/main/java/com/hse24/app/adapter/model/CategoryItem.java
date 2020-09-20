package com.hse24.app.adapter.model;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

public class CategoryItem implements ParentObject {

    public void setType(TYPE type) {
        this.type = type;
    }

    public enum TYPE {
        MAIN, TITLE, SUB
    }

    private CategoryItem.TYPE type = TYPE.MAIN;
    private List<Object> list;

    @Override
    public List<Object> getChildObjectList() {
        return list;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        type = TYPE.TITLE;
        this.list = list;
        for (Object obj : list) {
            if (obj instanceof CategoryItem) {
                ((CategoryItem)obj).setType(TYPE.SUB);
            }
        }
    }

    private final String titre;
    private final int id;

    public CategoryItem(String titre, int id) {
        this.titre = titre;
        this.id = id;
    }


    public String getTitre() {
        return titre;
    }

    public int getId() {
        return id;
    }

    public CategoryItem.TYPE getType() {
        return type;
    }
}

