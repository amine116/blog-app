package com.amine.blog.interfaces;

import com.amine.blog.model.RecentArticle;

import java.util.ArrayList;

public interface OnReadArticleListener {
    void onReadArticle(ArrayList<RecentArticle> articles, int task);
}
