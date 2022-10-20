package com.amine.blog.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amine.blog.R;
import com.amine.blog.model.EditHistory;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class EditHistoryDialog extends Dialog {
    private ArrayList<EditHistory> editHistories;
    private Context context;
    private int colorSubheading, colorQuot, colorBullet, textColor;
    public EditHistoryDialog(@NonNull Context context, ArrayList<EditHistory> editHistories) {
        super(context);
        this.editHistories = editHistories;
        this.context = context;
    }

    public void setColorBullet(int colorBullet) {
        this.colorBullet = colorBullet;
    }

    public void setColorQuot(int colorQuot) {
        this.colorQuot = colorQuot;
    }

    public void setColorSubheading(int colorSubheading) {
        this.colorSubheading = colorSubheading;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.edit_history_dialog);

        LinearLayout ll = findViewById(R.id.layout_editInfo);

        for(int i = editHistories.size() - 1; i >= 0; i--){
            String s = editHistories.get(i).getTime().toString() + "\n" + editHistories.get(i).getPrevText();

            DataModel.getSpannableArticle(s, context, false, colorSubheading, colorQuot, colorBullet,
                    content -> {
                        TextView txt = new TextView(context);
                        txt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        txt.setTextColor(textColor);
                        txt.setText(content);

                        View v = new View(context);
                        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                8));
                        v.setBackgroundColor(colorQuot);

                        ll.addView(txt);
                        ll.addView(v);
                    });
        }
    }
}
