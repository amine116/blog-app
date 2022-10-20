package com.amine.blog.interfaces;

import com.amine.blog.model.Article;

import java.util.ArrayList;

public interface OnReadUserArticleListener {
    void onReadUserArticle(ArrayList<Article> articles);
}
