package com.amine.blog.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.Article;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;
import com.google.android.material.snackbar.Snackbar;

public class EditArticleFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText edtHeadLine, edtArticleText;
    private Button btnSave;
    private Spinner spinner;
    private TextView txtPreviewedArticle;
    private ScrollView scroll_preview;

    private String newPrivacy, prevArticleText;
    private ArrayAdapter<CharSequence> adapter;
    private Article article;

    private OnWaitListener waitListener;
    private Context context;

    private boolean isPreviewClicked = false;

    public EditArticleFrag(){}

    public void setArticle(Article article) {
        this.article = article;
    }

    public void setWaitListener(OnWaitListener waitListener) {
        this.waitListener = waitListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.write_article_frag, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        edtHeadLine = view.findViewById(R.id.edtHeadLine);
        edtArticleText = view.findViewById(R.id.edtArticle);
        txtPreviewedArticle = view.findViewById(R.id.txtPreviewedArticle);
        scroll_preview = view.findViewById(R.id.scroll_preview);

        settingSpinner(view);

        btnSave = view.findViewById(R.id.btnNext);
        btnSave.setText(R.string.save);
        btnSave.setOnClickListener(this);
        view.findViewById(R.id.txtPreview).setOnClickListener(this);

        edtArticleText.setOnTouchListener((view1, motionEvent) -> {

            if (view1.getId() == R.id.edtArticle) {
                view1.getParent().requestDisallowInterceptTouchEvent(true);
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view1.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }

            return false;
        });


        edtHeadLine.setText(article.getHeadLine());
        edtArticleText.setText(article.getText());
        prevArticleText = article.getText();

        super.onViewCreated(view, savedInstanceState);
    }

    private void settingSpinner(View view){
        spinner = view.findViewById(R.id.spinner_privacy);
        spinner.setOnItemSelectedListener(this);
        settingSpinnerAdapter();

        if(article.getPrivacy() == null){
            return;
        }
        String pr = (String) adapter.getItem(0);
        if(article.getPrivacy().equals(pr)){
            spinner.setSelection(0);
        }
        pr = (String) adapter.getItem(1);
        if(article.getPrivacy().equals(pr)){
            spinner.setSelection(1);
        }
        pr = (String) adapter.getItem(2);
        if(article.getPrivacy().equals(pr)){
            spinner.setSelection(2);
        }
    }

    private void settingSpinnerAdapter(){
        adapter = ArrayAdapter.createFromResource(context,
                R.array.privacy_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == btnSave.getId()){
            if(newPrivacy != null && !newPrivacy.isEmpty() && !newPrivacy.equals(FireConstants.STR_PRIVACY)){
                String headLine = edtHeadLine.getText().toString().trim(),
                        articleText = edtArticleText.getText().toString().trim();
                if(headLine.isEmpty()){
                    edtHeadLine.requestFocus();
                    edtHeadLine.setError("Chose a head line for our article");
                    return;
                }
                if (articleText.isEmpty()){
                    edtArticleText.requestFocus();
                    edtArticleText.setError("Write an article");
                    return;
                }

                boolean isBulletAddedCorrectly = DataModel.isBulletPointAddedCorrectly(articleText);
                if(!isBulletAddedCorrectly){
                    edtArticleText.requestFocus();
                    edtArticleText.setError("Bullet points are not correctly placed");
                    return;
                }

                int indOfWrongHyperLink = DataModel.isHyperLinkFormationCorrect(articleText);
                if(indOfWrongHyperLink > -1){

                    edtArticleText.requestFocus();
                    edtArticleText.setSelection(indOfWrongHyperLink + 1);
                    edtArticleText.setError("Invalid hyperlink formation");

                }
                else if(indOfWrongHyperLink == -1){

                    article.setHeadLine(headLine);
                    article.setText(articleText);

                    new Save().saveEditedArticleText(article, newPrivacy);
                    Toast.makeText(context, "Information Saved", Toast.LENGTH_SHORT).show();

                    Save.saveEditHistory(article.getID(), article.getUsername(), prevArticleText,
                            FireConstants.STR_ARTICLE, article.getPrivacy());

                    waitListener.onWaitCallback(DataModel.MOVE_TO_MAIN_ACTIVITY_HOME);

                }
            }
            else {
                Snackbar snackbar = Snackbar.make(spinner, getResources().getString(R.string.select_privacy),
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        else if(view.getId() == R.id.txtPreview){
            hideKeyBoard(view);
            if(isPreviewClicked){
                isPreviewClicked = false;
                scroll_preview.setVisibility(View.GONE);
            }
            else{
                isPreviewClicked = true;
                setPreview();
                scroll_preview.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        newPrivacy = (String) adapterView.getItemAtPosition(i);
        TextView tv = (TextView) adapterView.getChildAt(0);
        tv.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void hideKeyBoard(View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setPreview(){
        String headLine = edtHeadLine.getText().toString().trim(),
                text = edtArticleText.getText().toString().trim();

        if(headLine.equals("")){
            edtHeadLine.requestFocus();
            edtHeadLine.setError("Make a Head line");
            return;
        }
        if(text.equals("")){
            edtArticleText.requestFocus();
            edtArticleText.setError("Write a article");
            return;
        }

        boolean isBulletAddedCorrectly = DataModel.isBulletPointAddedCorrectly(text);
        if(!isBulletAddedCorrectly){
            edtArticleText.requestFocus();
            edtArticleText.setError("Bullet points are not correctly placed");
            return;
        }

        int indOfWrongHyperLink = DataModel.isHyperLinkFormationCorrect(text);
        if(indOfWrongHyperLink > -1){
            edtArticleText.requestFocus();
            edtArticleText.setSelection(indOfWrongHyperLink + 1);
            edtArticleText.setError("Invalid hyperlink formation");
        }
        else if(indOfWrongHyperLink == -1){
            DataModel.getSpannableArticle(text, context, false,
                    getResources().getColor(R.color.sub_head_line), getResources().getColor(R.color.quotation),
                    getResources().getColor(R.color.bullet_point), new DataModel.CallbackForSpannable() {
                        @Override
                        public void onCallback(SpannableString content) {
                            txtPreviewedArticle.setText(content);
                        }
                    });
        }
    }
}
