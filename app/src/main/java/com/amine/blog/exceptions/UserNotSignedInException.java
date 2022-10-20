package com.amine.blog.exceptions;

public class UserNotSignedInException extends Exception{
    private String message;
    public UserNotSignedInException(String message){
        this.message = message;
    }
}
