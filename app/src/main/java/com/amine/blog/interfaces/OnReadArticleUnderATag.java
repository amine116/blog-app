package com.amine.blog.interfaces;

import com.amine.blog.model.ArticlesUnderTag;

import java.util.ArrayList;

public interface OnReadArticleUnderATag {
    void onReadArticleUnderATag(ArrayList<ArticlesUnderTag> articlesUnderTags);
}
