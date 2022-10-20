package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.dialogs.ArticleWritingSugDialog;
import com.amine.blog.interfaces.OnWaitListenerWithStringArrayInfo;

import com.amine.blog.model.HyperLink;
import com.amine.blog.viewmodel.DataModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class WriteArticleFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText edtHeadLine, edtArticle;
    private Button btnNext;
    private Spinner spinner;
    private TextView txtPreviewedArticle;

    private Context context;
    private String privacy;

    private OnWaitListenerWithStringArrayInfo onWaitListener;

    public WriteArticleFrag(){}

    public void setOnWaitListenerWithStringArrayInfo(OnWaitListenerWithStringArrayInfo onWaitListener) {
        this.onWaitListener = onWaitListener;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        edtHeadLine = view.findViewById(R.id.edtHeadLine);
        edtArticle = view.findViewById(R.id.edtArticle);
        txtPreviewedArticle = view.findViewById(R.id.txtPreviewedArticle);

        spinner = view.findViewById(R.id.spinner_privacy);
        spinner.setOnItemSelectedListener(this);

        btnNext = view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);

        view.findViewById(R.id.txtWriteArticleSug).setOnClickListener(this);
        view.findViewById(R.id.txtPreview).setOnClickListener(this);

        setSpinnerAdapter();

        super.onViewCreated(view, savedInstanceState);
    }

    private void setSpinnerAdapter(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.privacy_items, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == btnNext.getId()){
            if(!privacy.isEmpty() && !privacy.equals("privacy")){
                String headLine = edtHeadLine.getText().toString().trim(),
                        text = edtArticle.getText().toString().trim();

                if(headLine.equals("")){
                    edtHeadLine.requestFocus();
                    edtHeadLine.setError("Make a Head line");
                    return;
                }
                if(text.equals("")){
                    edtArticle.requestFocus();
                    edtArticle.setError("Write a article");
                    return;
                }

                boolean isBulletAddedCorrectly = DataModel.isBulletPointAddedCorrectly(text);
                if(!isBulletAddedCorrectly){
                    edtArticle.requestFocus();
                    edtArticle.setError("Bullet points are not correctly placed");
                    return;
                }

                int indOfWrongHyperLink = DataModel.isHyperLinkFormationCorrect(text);
                if(indOfWrongHyperLink > -1){
                    edtArticle.requestFocus();
                    edtArticle.setSelection(indOfWrongHyperLink + 1);
                    edtArticle.setError("Invalid hyperlink formation");
                }
                else if(indOfWrongHyperLink == -1){
                    btnNext.setEnabled(false);
                    ArrayList<String> info = new ArrayList<>();
                    info.add(headLine); info.add(text); info.add(privacy);
                    onWaitListener.onWaitListenerWithStringArrayInfo(DataModel.MOVE_TO_ADD_TAG_FRAGMENT, info);
                    btnNext.setEnabled(true);
                }
            }
            else{
                Snackbar snackbar = Snackbar.make(spinner, getResources().getString(R.string.select_privacy),
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        else if(view.getId() == R.id.txtPreview){

            hideKeyBoard(view);

            String headLine = edtHeadLine.getText().toString().trim(),
                    text = edtArticle.getText().toString().trim();

            if(headLine.equals("")){
                edtHeadLine.requestFocus();
                edtHeadLine.setError("Make a Head line");
                return;
            }
            if(text.equals("")){
                edtArticle.requestFocus();
                edtArticle.setError("Write a article");
                return;
            }

            boolean isBulletAddedCorrectly = DataModel.isBulletPointAddedCorrectly(text);
            if(!isBulletAddedCorrectly){
                edtArticle.requestFocus();
                edtArticle.setError("Bullet points are not correctly placed");
                return;
            }

            int indOfWrongHyperLink = DataModel.isHyperLinkFormationCorrect(text);
            if(indOfWrongHyperLink > -1){
                edtArticle.requestFocus();
                edtArticle.setSelection(indOfWrongHyperLink + 1);
                edtArticle.setError("Invalid hyperlink formation");
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
        else if(view.getId() == R.id.txtWriteArticleSug){

            ArticleWritingSugDialog awt = new ArticleWritingSugDialog(context);
            awt.setColorQuotation(getResources().getColor(R.color.quotation));
            awt.setBulletPointColor(R.color.green);
            awt.setColorSubtitle(getResources().getColor(R.color.sub_head_line));
            awt.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        privacy = (String) adapterView.getItemAtPosition(i);
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
}
