package com.amine.blog.model;

import java.util.ArrayList;

public class Profile {

    // Public Information
    private UserBasicInfo basicInfo;
    private ArrayList<String> expertise, hobbies;
    private ArrayList<ArticleShortInfo> postedOrSharedArticles;

    // Private Information
    private ArrayList<String> favouriteArticles;

    public Profile(){}
    public Profile(UserBasicInfo basicInfo, ArrayList<String> expertise, ArrayList<String> hobbies,
                   ArrayList<ArticleShortInfo> postedOrSharedArticles, ArrayList<String> favouriteArticles) {
        this.basicInfo = basicInfo;
        this.expertise = expertise;
        this.hobbies = hobbies;
        this.postedOrSharedArticles = postedOrSharedArticles;
        this.favouriteArticles = favouriteArticles;
    }


    public UserBasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(UserBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public ArrayList<String> getExpertise() {
        return expertise;
    }

    public void setExpertise(ArrayList<String> expertise) {
        this.expertise = expertise;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<String> hobbies) {
        this.hobbies = hobbies;
    }

    public ArrayList<ArticleShortInfo> getPostedOrSharedArticles() {
        return postedOrSharedArticles;
    }

    public void setPostedOrSharedArticles(ArrayList<ArticleShortInfo> postedOrSharedArticles) {
        this.postedOrSharedArticles = postedOrSharedArticles;
    }

    public ArrayList<String> getFavouriteArticles() {
        return favouriteArticles;
    }

    public void setFavouriteArticles(ArrayList<String> favouriteArticles) {
        this.favouriteArticles = favouriteArticles;
    }
}
