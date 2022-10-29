package com.amine.blog.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.viewmodel.DataModel;

public class EditOpinionDialog extends Dialog implements View.OnClickListener {

    private String opinionText;
    private EditText edtEditOpinion;
    private OnWaitListenerWithStringInfo waitListener;

    public EditOpinionDialog(@NonNull Context context) {
        super(context);
    }

    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    public void setWaitListener(OnWaitListenerWithStringInfo waitListener) {
        this.waitListener = waitListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.edit_comment_dialog);

        findViewById(R.id.txtSave).setOnClickListener(this);
        edtEditOpinion = findViewById(R.id.edtEditOpinion);

        edtEditOpinion.setText(opinionText);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.txtSave){
            String opinion = edtEditOpinion.getText().toString().trim();
            if(opinion.isEmpty()){
                edtEditOpinion.requestFocus();
                edtEditOpinion.setError("Opinion can't be empty");
                return;
            }

            waitListener.onWaitWithInfo(DataModel.YES, opinion);
            dismiss();
        }
        else if(view.getId() == R.id.txtClose){
            dismiss();
        }
    }
}
