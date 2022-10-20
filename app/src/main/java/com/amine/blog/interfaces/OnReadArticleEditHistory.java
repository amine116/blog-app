package com.amine.blog.interfaces;

import com.amine.blog.model.EditHistory;

import java.util.ArrayList;

public interface OnReadArticleEditHistory {
    void onReadEdit(ArrayList<EditHistory> editHistories);
}
