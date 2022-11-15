package com.amine.blog.interfaces;

import com.amine.blog.model.ChatMessage;

import java.util.ArrayList;

public interface OnReadChatMessages {
    void onReadChatMessage(ArrayList<ChatMessage> chatMessages, int task);
}
