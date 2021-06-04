package com.example.foodrescue.model;

import android.graphics.Rect;

public class Box {
    private Rect rect;
    private String text;

    public Box(Rect rect, String text) {
        this.rect = rect;
        this.text = text;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
