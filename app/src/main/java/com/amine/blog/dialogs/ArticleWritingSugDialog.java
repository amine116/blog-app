package com.amine.blog.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.amine.blog.R;
import com.amine.blog.viewmodel.DataModel;

import androidx.annotation.NonNull;

public class ArticleWritingSugDialog extends Dialog implements View.OnClickListener {

    private int colorSubtitle, colorQuotation, bulletPointColor;
    private final Context context;


    public ArticleWritingSugDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void setColorSubtitle(int colorSubtitle) {
        this.colorSubtitle = colorSubtitle;
    }

    public void setColorQuotation(int colorQuotation) {
        this.colorQuotation = colorQuotation;
    }

    public void setBulletPointColor(int bulletPointColor) {
        this.bulletPointColor = bulletPointColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.article_writing_sug_dialog);


        TextView txt_sug_subheading_demo = findViewById(R.id.txt_sug_subheading_demo),
                txt_sug_quotation_demo = findViewById(R.id.txt_sug_quotation_demo),
                txt_sug_bullet_demo = findViewById(R.id.txt_sug_bullet_demo),
                txt_sug_hyperLink_demo = findViewById(R.id.txt_sug_hyperLink_demo);

        String demoTextSubheading = "*Black hole*\nA black hole is a region of spacetime where " +
                "gravity is so strong that nothing can escape from it.";
        DataModel.getSpannableArticle(
                demoTextSubheading, context, false, colorSubtitle, colorQuotation, bulletPointColor,
                new DataModel.CallbackForSpannable() {
                    @Override
                    public void onCallback(SpannableString content) {
                        txt_sug_subheading_demo.setText(content);
                        TextView txt = findViewById(R.id.txt_sug_subheading_sug3);
                        txt.setText(demoTextSubheading);
                    }
                });

        String quotationDemo = "\"We don't know why and how the Big Bang happened, but we do " +
                "know what happened after it.\"";
        DataModel.getSpannableArticle(
                quotationDemo, context,false, colorSubtitle, colorQuotation, bulletPointColor,
                new DataModel.CallbackForSpannable() {
                    @Override
                    public void onCallback(SpannableString content) {
                        txt_sug_quotation_demo.setText(content);
                        TextView txt = findViewById(R.id.txt_sug_quotation_sug3);
                        txt.setText(quotationDemo);
                    }
                });


        String bulletDemo = "Let me tell you some points-<1. This is first point.><2. This is second point." +
                "<a. This first sub point.><b. This is second sub point.><c. This is third sub point.>><" +
                "3. This is third point.><4. This fourth point.>";
        DataModel.getSpannableArticle(
                bulletDemo, context,false, colorSubtitle, colorQuotation, bulletPointColor,
                new DataModel.CallbackForSpannable() {
                    @Override
                    public void onCallback(SpannableString content) {
                        txt_sug_bullet_demo.setText(content);
                        TextView txt = findViewById(R.id.txt_sug_bullet_sug3);
                        txt.setText(bulletDemo);
                    }
                });

        String hyperLinkDemo = "To search anything on the internet visit ~[Google][google.com]~.";

        DataModel.getSpannableArticle(
                hyperLinkDemo, context,false, colorSubtitle, colorQuotation, bulletPointColor,
                new DataModel.CallbackForSpannable() {
                    @Override
                    public void onCallback(SpannableString content) {
                        txt_sug_hyperLink_demo.setText(content);
                        TextView txt = findViewById(R.id.txt_sug_hyperLink_sug3);
                        String s = hyperLinkDemo + "\nDo not put any space between first '~' and last '~'." +
                                " Use just required text. You can write anything in the place of 'Google'";
                        txt.setText(s);
                    }
                });

        findViewById(R.id.txtClose).setOnClickListener(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.txtClose){
            dismiss();
        }
    }
}
