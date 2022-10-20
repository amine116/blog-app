package com.amine.blog.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
    }

    @Override
    public void selectAll() {

    }

    @Override
    public boolean isTextSelectable() {
        return false;
    }

    @Override
    public boolean hasSelection() {
        return false;
    }

    @Override
    public boolean didTouchFocusSelect() {
        return false;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public int getSelectionStart() {
        return super.getSelectionStart();
    }

    @Override
    public int getSelectionEnd() {
        return super.getSelectionEnd();
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Nullable
    @Override
    public Drawable getTextSelectHandle() {
        return null;
    }

    @Override
    public void setTextSelectHandle(int textSelectHandle) {

    }
}
