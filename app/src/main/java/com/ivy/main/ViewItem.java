package com.ivy.main;

import android.content.Intent;

/**
 * Created by ivy on 2017/10/25.
 * Description：
 */

public class ViewItem {
    private String text;
    private Class clazz;

    public ViewItem(String text, Class clazz) {
        this.text = text;
        this.clazz = clazz;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
