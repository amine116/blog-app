package com.amine.blog.model;

public class People {

    private String username, lovesTo;
    private long articleCount;

    public People(){}

    public People(String username, String lovesTo, long articleCount) {
        this.username = username;
        this.lovesTo = lovesTo;
        this.articleCount = articleCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLovesTo() {
        return lovesTo;
    }

    public void setLovesTo(String lovesTo) {
        this.lovesTo = lovesTo;
    }

    public long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(long articleCount) {
        this.articleCount = articleCount;
    }
}
