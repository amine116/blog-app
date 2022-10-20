package com.amine.blog.model;


public class SuggestedTag {
    private String articleId, articleHeadLine, writerUsername;

    public SuggestedTag(){}

    public SuggestedTag(String articleId, String articleHeadLine, String writerUsername) {
        this.articleId = articleId;
        this.articleHeadLine = articleHeadLine;
        this.writerUsername = writerUsername;
    }


    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleHeadLine() {
        return articleHeadLine;
    }

    public void setArticleHeadLine(String articleHeadLine) {
        this.articleHeadLine = articleHeadLine;
    }

    public String getWriterUsername() {
        return writerUsername;
    }

    public void setWriterUsername(String writerUsername) {
        this.writerUsername = writerUsername;
    }
}
