package com.amine.blog.interfaces;

import com.amine.blog.model.People;

import java.util.ArrayList;

public interface OnReadPeople {
    void onReadPeople(int task, ArrayList<People> people);
}
