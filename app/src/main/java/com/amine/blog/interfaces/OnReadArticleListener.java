package com.amine.blog.interfaces;

import com.amine.blog.model.Article;

import java.util.ArrayList;

public interface OnReadArticleListener {
    void onReadArticle(ArrayList<Article> articles, int task);
}
