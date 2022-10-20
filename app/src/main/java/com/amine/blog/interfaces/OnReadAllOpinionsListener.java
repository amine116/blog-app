package com.amine.blog.interfaces;

import com.amine.blog.model.Opinion;

import java.util.ArrayList;

public interface OnReadAllOpinionsListener {
    void onReadAllOpinions(ArrayList<Opinion> opinions);
}
