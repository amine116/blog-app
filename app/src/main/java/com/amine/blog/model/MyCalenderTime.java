package com.amine.blog.model;

import androidx.annotation.NonNull;

public class MyCalenderTime{

    private long year, month, day, hourOfTheDay, minutesOfTheHour, secondOfTheMinute, millisecond;

    public MyCalenderTime(){}

    public MyCalenderTime(long year, long month, long day, long hourOfTheDay, long minutesOfTheHour,
                          long secondOfTheMinute, long millisecond) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hourOfTheDay = hourOfTheDay;
        this.minutesOfTheHour = minutesOfTheHour;
        this.secondOfTheMinute = secondOfTheMinute;
        this.millisecond = millisecond;
    }


    public long getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getHourOfTheDay() {
        return hourOfTheDay;
    }

    public void setHourOfTheDay(int hourOfTheDay) {
        this.hourOfTheDay = hourOfTheDay;
    }

    public long getMinutesOfTheHour() {
        return minutesOfTheHour;
    }

    public void setMinutesOfTheHour(int minutesOfTheHour) {
        this.minutesOfTheHour = minutesOfTheHour;
    }

    public long getSecondOfTheMinute() {
        return secondOfTheMinute;
    }

    public void setSecondOfTheMinute(int secondOfTheMinute) {
        this.secondOfTheMinute = secondOfTheMinute;
    }

    public long getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    public long getTimeInMillisecond(){
        return getYear() + getMonth() + getDay() + getHourOfTheDay() + getMinutesOfTheHour() + getSecondOfTheMinute() +
                getMillisecond();
    }

    @NonNull
    @Override
    public String toString() {
        return "MyCalenderTime{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hourOfTheDay=" + hourOfTheDay +
                ", minutesOfTheHour=" + minutesOfTheHour +
                ", secondOfTheMinute=" + secondOfTheMinute +
                ", millisecond=" + millisecond +
                '}';
    }
}
