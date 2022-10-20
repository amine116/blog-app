package com.amine.blog.model;

public class ArticleTag {
    private String tagName;
    private long articleCount;
    public ArticleTag(){}

    public ArticleTag(String tagName, long articleCount) {
        this.tagName = tagName;
        this.articleCount = articleCount;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(long articleCount) {
        this.articleCount = articleCount;
    }
}
