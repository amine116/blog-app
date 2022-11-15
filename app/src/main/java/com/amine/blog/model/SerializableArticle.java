package com.amine.blog.model;

import java.util.ArrayList;

public class SerializableArticle extends Article{

    private long serial;

    public SerializableArticle(){}

    public SerializableArticle(long serial) {
        this.serial = serial;
    }

    public SerializableArticle(String headLine, String text, String ID, String nameOfOwner, String username,
                               String privacy, int numberOfLikes, MyTime time, ArrayList<Opinion> opinions,
                               ArrayList<String> tags, long serial) {
        super(headLine, text, ID, nameOfOwner, username, privacy, numberOfLikes, time, opinions, tags);
        this.serial = serial;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }
}
