package com.amine.blog.interfaces;

import com.amine.blog.model.ArticlesUnderTag;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface OnReadChatList {
    void onReadChatList(ArrayList<ArticlesUnderTag> chatList, boolean isAdded);
    void onReadChatList(DataSnapshot snapshot, boolean isAdded);
    //void onReadChatList(DataSnapshot snapshot, boolean isAdded);
}
