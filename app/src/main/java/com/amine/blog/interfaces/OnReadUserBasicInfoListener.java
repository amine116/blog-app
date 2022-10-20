package com.amine.blog.interfaces;

import com.amine.blog.model.UserBasicInfo;

public interface OnReadUserBasicInfoListener {
    void onReadBasicInfo(UserBasicInfo userBasicInfo, boolean isSignedIn);
}
