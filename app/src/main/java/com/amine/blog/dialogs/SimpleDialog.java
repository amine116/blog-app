package com.amine.blog.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.interfaces.OnWaitListenerWithStringArrayInfo;
import com.amine.blog.viewmodel.DataModel;

public class SimpleDialog extends Dialog implements View.OnClickListener {

    private final String text;
    private String actionType;
    private OnWaitListener waitListener;

    public SimpleDialog(@NonNull Context context, String text) {
        super(context);
        this.text = text;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setWaitListener(OnWaitListener waitListener) {
        this.waitListener = waitListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.simple_dialog_text);
        this.findViewById(R.id.txtOkButton).setOnClickListener(this);
        setDialogText();
    }

    public void setDialogText(){

        TextView txtDialogText = this.findViewById(R.id.txtSimpleDialogText);
        txtDialogText.setText(text);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtOkButton){
            if(actionType.equals(DataModel.STR_DISMISS)){
                dismiss();
            }
            else if(actionType.equals(DataModel.STR_CREATE_TAG)){
                waitListener.onWaitCallback(DataModel.YES);
                dismiss();
            }
        }
    }
}
