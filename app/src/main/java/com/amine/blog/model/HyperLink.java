package com.amine.blog.model;

import androidx.annotation.NonNull;

public class HyperLink {

    private int textInit, textEnd;
    private String src;

    public HyperLink(){}

    public HyperLink(int textInit, int textEnd, String src) {
        this.textInit = textInit;
        this.textEnd = textEnd;
        this.src = src;
    }

    public int getTextInit() {
        return textInit;
    }

    public int getTextEnd() {
        return textEnd;
    }

    public void setTextEnd(int textEnd) {
        this.textEnd = textEnd;
    }

    public void setTextInit(int textInit) {
        this.textInit = textInit;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @NonNull
    @Override
    public String toString() {
        return "HyperLink{" +
                "textInit=" + textInit +
                ", textEnd=" + textEnd +
                ", src='" + src + '\'' +
                '}';
    }
}
