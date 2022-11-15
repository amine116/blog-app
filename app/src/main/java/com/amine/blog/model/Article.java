package com.amine.blog.model;

import androidx.annotation.NonNull;

import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class Article {
    private String headLine, text, ID, nameOfOwner, username, privacy;
    private int numberOfLikes;
    private MyTime time;
    private ArrayList<Opinion> opinions;
    //private ArrayList<HyperLink> hyperLinks;
    private ArrayList<String> tags;

    public Article(){}

    public Article(String headLine, String text, String ID, String nameOfOwner, String username, String privacy,
                   int numberOfLikes, MyTime time, ArrayList<Opinion> opinions,
            /*ArrayList<HyperLink> hyperLinks*/ ArrayList<String> tags) {
        this.headLine = headLine;
        this.text = text;
        this.ID = ID;
        this.nameOfOwner = nameOfOwner;
        this.username = username;
        this.privacy = privacy;
        this.numberOfLikes = numberOfLikes;
        this.time = time;
        this.opinions = opinions;
        //this.hyperLinks = hyperLinks;
        this.tags = tags;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNameOfOwner() {
        return nameOfOwner;
    }

    public void setNameOfOwner(String nameOfOwner) {
        this.nameOfOwner = nameOfOwner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPrivacy() {
        return privacy;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public MyTime getTime() {
        return time;
    }

    public void setTime(MyTime time) {
        this.time = time;
    }

    public ArrayList<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(ArrayList<Opinion> opinions) {
        this.opinions = opinions;
    }

    /*
    public ArrayList<HyperLink> getHyperLinks() {
        return hyperLinks;
    }

    public void setHyperLinks(ArrayList<HyperLink> hyperLinks) {
        this.hyperLinks = hyperLinks;
    }
     */

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getAuthor(){
        return DataModel.STR_AUTHOR + " - " + getNameOfOwner() + " ~ " + getUsername();
    }

    @NonNull
    @Override
    public String toString() {
        return "Article{" +
                "headLine='" + headLine + '\'' +
                ", text='" + text + '\'' +
                ", ID='" + ID + '\'' +
                ", nameOfOwner='" + nameOfOwner + '\'' +
                ", username='" + username + '\'' +
                ", privacy='" + privacy + '\'' +
                ", numberOfLikes=" + numberOfLikes +
                ", time=" + time +
                ", opinions=" + opinions +
                ", tags=" + tags +
                '}';
    }
}
