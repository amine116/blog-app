package com.amine.blog.model;

public class EditHistory {

    private String prevText, type, articleId, ownerUsername;
    private MyTime time;

    public EditHistory(){}

    public EditHistory(String prevText, String type, String articleId, String ownerUsername, MyTime time) {
        this.prevText = prevText;
        this.type = type;
        this.articleId = articleId;
        this.ownerUsername = ownerUsername;
        this.time = time;
    }

    public String getPrevText() {
        return prevText;
    }

    public void setPrevText(String prevText) {
        this.prevText = prevText;
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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public MyTime getTime() {
        return time;
    }

    public void setTime(MyTime time) {
        this.time = time;
    }
}
