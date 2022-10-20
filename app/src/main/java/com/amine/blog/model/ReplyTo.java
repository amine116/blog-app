package com.amine.blog.model;

public class ReplyTo {
    private String type, articleId, opinionId, authorUsername, contextText;

    public ReplyTo(){}

    public ReplyTo(String type, String articleId, String opinionId, String authorUsername, String contextText) {
        this.type = type;
        this.articleId = articleId;
        this.opinionId = opinionId;
        this.authorUsername = authorUsername;
        this.contextText = contextText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(String opinionId) {
        this.opinionId = opinionId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getContextText() {
        return contextText;
    }

    public void setContextText(String contextText) {
        this.contextText = contextText;
    }

}
