package com.amine.blog.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface OnReadListener {
    void onRead(DataSnapshot snapshot);
}