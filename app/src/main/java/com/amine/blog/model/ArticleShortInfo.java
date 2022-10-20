package com.amine.blog.model;

import java.util.ArrayList;

public class ArticleShortInfo {
    private String articleID, ownerName, nameWhoShares, headLine;
    private ArrayList<String> tags;
    private MyTime timeOfShare;

    public ArticleShortInfo(){}
    public ArticleShortInfo(String articleID, String ownerName, String nameWhoShares, String headLine,
                            ArrayList<String> tags, MyTime timeOfShare) {
        this.articleID = articleID;
        this.ownerName = ownerName;
        this.nameWhoShares = nameWhoShares;
        this.headLine = headLine;
        this.tags = tags;
        this.timeOfShare = timeOfShare;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getNameWhoShares() {
        return nameWhoShares;
    }

    public void setNameWhoShares(String nameWhoShares) {
        this.nameWhoShares = nameWhoShares;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public MyTime getTimeOfShare() {
        return timeOfShare;
    }

    public void setTimeOfShare(MyTime timeOfShare) {
        this.timeOfShare = timeOfShare;
    }
}
