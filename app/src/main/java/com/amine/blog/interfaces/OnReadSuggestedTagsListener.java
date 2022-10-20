package com.amine.blog.interfaces;

import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.model.SuggestedTag;

import java.util.ArrayList;
import java.util.Map;

public interface OnReadSuggestedTagsListener {
    void onReadSuggestedTags(Map<String, ArrayList<SuggestedTag>> suggestedTags);
}
