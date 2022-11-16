package com.amine.blog.model;

import java.util.ArrayList;

public class RecentArticle extends Article{

    private long timeInMill;

    public RecentArticle(){}

    public RecentArticle(long timeInMill) {
        this.timeInMill = timeInMill;
    }

    public RecentArticle(String headLine, String text, String ID, String nameOfOwner, String username,
                         String privacy, int numberOfLikes, MyTime time, ArrayList<Opinion> opinions,
                         ArrayList<String> tags, long timeInMill) {
        super(headLine, text, ID, nameOfOwner, username, privacy, numberOfLikes, time, opinions, tags);
        this.timeInMill = timeInMill;
    }

    public long getTimeInMill() {
        return timeInMill;
    }

    public void setTimeInMill(long timeInMill) {
        this.timeInMill = timeInMill;
    }
}
