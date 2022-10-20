package com.amine.blog.model;

import androidx.annotation.NonNull;

public class UserBasicInfo {
    private String profileName, userName, university, profession;

    public UserBasicInfo(){}

    public UserBasicInfo(String profileName, String userName, String university, String profession) {
        this.profileName = profileName;
        this.userName = userName;
        this.university = university;
        this.profession = profession;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserBasicInfo{" +
                "profileName='" + profileName + '\'' +
                ", userName='" + userName + '\'' +
                ", university='" + university + '\'' +
                '}';
    }
}
