package com.amine.blog.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface OnReadChatList {
    void onReadChatList(DataSnapshot snapshot, boolean isAdded);
}
