package com.amine.blog.model;

public class SharedArticle {
    private String ownerUsername, userNameWhoShares, articleID;
    private MyTime timeOfShare;

    public SharedArticle(){}
    public SharedArticle(String ownerUsername, String userNameWhoShares, String articleID, MyTime timeOfShare) {
        this.ownerUsername = ownerUsername;
        this.userNameWhoShares = userNameWhoShares;
        this.articleID = articleID;
        this.timeOfShare = timeOfShare;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getUserNameWhoShares() {
        return userNameWhoShares;
    }

    public void setUserNameWhoShares(String userNameWhoShares) {
        this.userNameWhoShares = userNameWhoShares;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public MyTime getTimeOfShare() {
        return timeOfShare;
    }

    public void setTimeOfShare(MyTime timeOfShare) {
        this.timeOfShare = timeOfShare;
    }
}
