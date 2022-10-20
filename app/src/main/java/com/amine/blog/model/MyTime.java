package com.amine.blog.model;

import androidx.annotation.NonNull;

public class MyTime {
    private String dayName, monthName, dayInt, hourMinSEc, gmt, year;

    public MyTime(){}

    public MyTime(String dayName, String monthName, String dayInt, String hourMinSEc, String gmt, String year) {
        this.dayName = dayName;
        this.monthName = monthName;
        this.dayInt = dayInt;
        this.hourMinSEc = hourMinSEc;
        this.gmt = gmt;
        this.year = year;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getDayInt() {
        return dayInt;
    }

    public void setDayInt(String dayInt) {
        this.dayInt = dayInt;
    }

    public String getHourMinSEc() {
        return hourMinSEc;
    }

    public void setHourMinSEc(String hourMinSEc) {
        this.hourMinSEc = hourMinSEc;
    }

    public String getGmt() {
        return gmt;
    }

    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @NonNull
    @Override
    public String toString() {
        return getDayInt() + " " + getMonthName() + ", " + getYear() + " | " + getHourMinSEc().substring(0, 5);
    }
}
