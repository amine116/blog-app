package com.amine.blog.model;

public class Opinion {
    private String name, text, id;
    private MyTime time;
    private ReplyTo replyTo;

    public Opinion(){}

    public Opinion(String name, String text, String id, MyTime time, ReplyTo replyTo) {
        this.name = name;
        this.text = text;
        this.id = id;
        this.time = time;
        this.replyTo = replyTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyTime getTime() {
        return time;
    }

    public void setTime(MyTime time) {
        this.time = time;
    }

    public ReplyTo getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(ReplyTo replyTo) {
        this.replyTo = replyTo;
    }
}
