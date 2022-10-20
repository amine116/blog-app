package com.amine.blog.model;

public class ArticlesUnderTag {
    private String articleId, headLine;

    public ArticlesUnderTag(String articleId, String headLine) {
        this.articleId = articleId;
        this.headLine = headLine;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }
}
