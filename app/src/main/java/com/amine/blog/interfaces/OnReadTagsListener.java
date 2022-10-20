package com.amine.blog.interfaces;

import com.amine.blog.model.ArticleTag;

import java.util.ArrayList;

public interface OnReadTagsListener {
    void onReadTag(ArrayList<ArticleTag> dataList);
}
