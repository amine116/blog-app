package com.amine.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.model.SuggestedTag;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;

import java.util.ArrayList;
import java.util.Map;

public class ApproveTagsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_tags);
        hideActionBar();
        readSuggested();
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void readSuggested(){
        findViewById(R.id.progress_readSuggestedTag).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_suggested_tags).setVisibility(View.GONE);
        Retrieve.getSuggestedTags(suggestedTags -> {

            findViewById(R.id.progress_readSuggestedTag).setVisibility(View.GONE);
            findViewById(R.id.layout_suggested_tags).setVisibility(View.VISIBLE);

            for(Map.Entry<String, ArrayList<SuggestedTag>> entry : suggestedTags.entrySet()){
                displayTag(entry.getKey(), entry.getValue());
            }
        });
    }

    private void displayTag(String tagName, ArrayList<SuggestedTag> articleHeadLines){

        LinearLayout ll = findViewById(R.id.layout_suggested_tags);

        ll.addView(getViewForTag(tagName, articleHeadLines));
    }

    private View getViewForTag(String tagName, ArrayList<SuggestedTag> articleHeadLines){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_approve_tag_item, null);

        TextView txtTagName = view.findViewById(R.id.txtTagName),
                txtEditTagButton = view.findViewById(R.id.txtEditTagButton),
                txtAddTagButton = view.findViewById(R.id.txtAddTagButton);

        LinearLayout layout_sugg_tagItems = view.findViewById(R.id.layout_sugg_tagItems);
        EditText edtTagName = view.findViewById(R.id.edtTagName);
        txtTagName.setText(tagName);

        for (int i = 0; i < articleHeadLines.size(); i++){

            // TODO
            //  java.util.HashMap cannot be cast to com.amine.blog.model.ArticlesUnderTag
            //System.out.println(entry);
            layout_sugg_tagItems.addView(getViewForSubItem(articleHeadLines.get(i), (i + 1)));
        }

        txtEditTagButton.setOnClickListener(getListenerForEditButton(txtTagName, edtTagName));
        txtAddTagButton.setOnClickListener(getListenerForAddTagButton(tagName, articleHeadLines, view));

        return view;

    }

    private View getViewForSubItem(SuggestedTag suggestedTag, int ind){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_approve_tag_sub_item, null);

        TextView txtSerial = view.findViewById(R.id.txtSerial),
                txtArticleHeadLine = view.findViewById(R.id.txtArticleHeadLine);

        String serial = ind + ".";
        txtSerial.setText(serial);
        txtArticleHeadLine.setText(suggestedTag.getArticleHeadLine());

        return view;
    }

    private View.OnClickListener getListenerForEditButton(TextView txtTagName, EditText edtTagName){
        return view -> {
            TextView txtEditButton = (TextView) view;
            String done = getResources().getString(R.string.done), edit = getResources().getString(R.string.edit);
            if(txtTagName.getVisibility() == View.VISIBLE){
                txtTagName.setVisibility(View.GONE);
                edtTagName.setVisibility(View.VISIBLE);
                edtTagName.setText(txtTagName.getText());
                txtEditButton.setText(done);
            }
            else{

                if(edtTagName.getText().toString().trim().isEmpty()){
                    edtTagName.requestFocus();
                    edtTagName.setError("write tag");
                    return;
                }

                edtTagName.setVisibility(View.GONE);
                txtTagName.setVisibility(View.VISIBLE);
                txtTagName.setText(edtTagName.getText());
                txtEditButton.setText(edit);
            }
        };
    }

    private View.OnClickListener getListenerForAddTagButton(String tagName,
                                                            ArrayList<SuggestedTag> articleHeadLines, View viewParent){
        return view ->  {
            for (int i = 0; i < articleHeadLines.size(); i++){
                String articleId = articleHeadLines.get(i).getArticleId(),
                        articleHeadLine = articleHeadLines.get(i).getArticleHeadLine(),
                        username = articleHeadLines.get(i).getWriterUsername();

                Save.approveSuggestedTag(username, tagName, articleId, articleHeadLine, viewParent);
            }
        };
    }

    private View.OnClickListener getListenerForDeleteButton(String tagName){
        return view -> Save.deleteSuggestedTag(tagName);
    }
}