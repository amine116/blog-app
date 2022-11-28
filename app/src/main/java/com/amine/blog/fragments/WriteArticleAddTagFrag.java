package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.MainActivity;
import com.amine.blog.R;
import com.amine.blog.dialogs.SimpleDialog;
import com.amine.blog.interfaces.OnReadTagsListener;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.Article;
import com.amine.blog.model.ArticleTag;
import com.amine.blog.model.MyTime;
import com.amine.blog.model.Opinion;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class WriteArticleAddTagFrag extends Fragment implements View.OnClickListener {

    private TextView txtTag1, txtTag2, txtTag3, txtTagSug, txtSearch;
    private ImageView imgCancelTag1, imgCancelTag2, imgCancelTag3;
    private LinearLayout ll;
    private Button btnPost;
    private EditText edtSearchTags;
    private LinearLayout layoutTag1, layoutTag2, layoutTag3, layout_info;
    private ProgressBar progress;

    private Context context;
    private OnWaitListener onWaitListener;

    private String headLine, articleText, privacy;
    private final ArrayList<ArticleTag> tagList = new ArrayList<>();

    private final boolean[] isTagAdded = new boolean[3], isTagSuggested = new boolean[3];

    public WriteArticleAddTagFrag(){}

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.write_article_add_tag_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ll = view.findViewById(R.id.layout_dropDownListItems);
        txtTag1 = view.findViewById(R.id.txtTag1);
        txtTag2 = view.findViewById(R.id.txtTag2);
        txtTag3 = view.findViewById(R.id.txtTag3);
        txtTagSug = view.findViewById(R.id.txtTagSug);
        txtSearch = view.findViewById(R.id.txtSearch);
        imgCancelTag1 = view.findViewById(R.id.imgTag1Cancel);
        imgCancelTag2 = view.findViewById(R.id.imgTag2Cancel);
        imgCancelTag3 = view.findViewById(R.id.imgTag3Cancel);
        btnPost = view.findViewById(R.id.btnPost);
        edtSearchTags = view.findViewById(R.id.edtSearchTags);

        layoutTag1 = view.findViewById(R.id.layout_tag1);
        layoutTag2 = view.findViewById(R.id.layout_tag2);
        layoutTag3 = view.findViewById(R.id.layout_tag3);
        layout_info = view.findViewById(R.id.layout_info);
        progress = view.findViewById(R.id.progress);

        addListeners();


        super.onViewCreated(view, savedInstanceState);
    }

    private void readTags(){
        inProgress();
        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnListerForReadingTags(dataList -> {
            completeProgress();
            tagList.addAll(dataList);
            setDropDownMenu(tagList);
        });
        retrieve.getTagList();
    }

    private void addListeners(){
        imgCancelTag1.setOnClickListener(this);
        imgCancelTag2.setOnClickListener(this);
        imgCancelTag3.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        txtSearch.setOnClickListener(this);

        readTags();
    }

    private void setDropDownMenu(ArrayList<ArticleTag> tags){
        ll.removeAllViews();
        for(int i = 0; i < tags.size(); i++){
            View view = getViewForDropDownList(tags.get(i), i);
            ll.addView(view);
        }
    }

    private View getViewForDropDownList(ArticleTag tag, int ind){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_drop_down_menu_item_for_tag_list, null);

        TextView txtTagName = view.findViewById(R.id.txtTagName),
                txtTagCount = view.findViewById(R.id.txtTagCount),
                txtAddButton = view.findViewById(R.id.txtAddButton);

        String s;
        if(tag.getArticleCount() == 0) s = "No article written";
        else if(tag.getArticleCount() == 1) s = "1 article";
        else s = tag.getArticleCount() + " articles";

        txtTagName.setText(tag.getTagName());
        txtTagCount.setText(s);
        txtAddButton.setId(ind + 1);
        txtAddButton.setOnClickListener(getListenerForView(tag.getTagName()));

        return view;
    }

    private View.OnClickListener getListenerForView(String tagName){

        return view -> {
            addTagsToTheArticle(tagName, false);
        };
    }

    private void addTagsToTheArticle(String tagName, boolean suggested){
        if(!isTagAdded[0]){
            String tag2 = txtTag2.getText().toString(),
                    tag3 = txtTag3.getText().toString();

            if(!tagName.equals(tag2) && !tagName.equals(tag3)){
                isTagAdded[0] = true;
                layoutTag1.setVisibility(View.VISIBLE);
                txtTag1.setText(tagName);
                if (suggested){
                    isTagSuggested[0] = true;
                }
            }
        }
        else if(!isTagAdded[1]){

            String tag1 = txtTag1.getText().toString(),
                    tag3 = txtTag3.getText().toString();

            if(!tagName.equals(tag1) && !tagName.equals(tag3)) {
                isTagAdded[1] = true;
                layoutTag2.setVisibility(View.VISIBLE);
                txtTag2.setText(tagName);

                if (suggested){
                    isTagSuggested[1] = true;
                }
            }
        }
        else if(!isTagAdded[2]){

            String tag1 = txtTag1.getText().toString(),
                    tag2 = txtTag2.getText().toString();

            if(!tagName.equals(tag1) && !tagName.equals(tag2)) {
                isTagAdded[2] = true;
                layoutTag3.setVisibility(View.VISIBLE);
                txtTag3.setText(tagName);

                if (suggested){
                    isTagSuggested[2] = true;
                }
            }
        }
        else{
            Toast.makeText(context, "Only 3 tags are allowed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == imgCancelTag1.getId()){
            isTagAdded[0] = false;
            txtTag1.setText("");

            isTagSuggested[0] = false;

            layoutTag1.setVisibility(View.GONE);
        }

        if(view.getId() == imgCancelTag2.getId()){
            isTagAdded[1] = false;
            txtTag2.setText("");

            isTagSuggested[1] = false;

            layoutTag2.setVisibility(View.GONE);
        }

        if(view.getId() == imgCancelTag3.getId()){
            isTagAdded[2] = false;
            txtTag3.setText("");

            isTagSuggested[2] = false;

            layoutTag3.setVisibility(View.GONE);
        }

        if(view.getId() == txtSearch.getId()){
            String word = edtSearchTags.getText().toString().trim();
            searchForWord(word);
        }

        if(view.getId() == btnPost.getId()){

            String tag1 = txtTag1.getText().toString(),
                    tag2 = txtTag2.getText().toString(),
                    tag3 = txtTag3.getText().toString();


            ArrayList<Opinion> opinions = new ArrayList<>();
            ArrayList<String> tags = new ArrayList<>();
            MyTime time = new DataModel().getCurrentMyTime();

            if(tag1.equals("") && tag2.equals("") && tag3.equals("")){
                SimpleDialog sd =
                        new SimpleDialog(context, "Add at least one tag.\n(Search for the tag right hand side)");
                sd.setActionType(DataModel.STR_DISMISS);
                sd.show();
                return;
            }

            if(!tag1.equals("")){
                tags.add(tag1);
            }
            if(!tag2.equals("")){
                tags.add(tag2);
            }
            if(!tag3.equals("")){
                tags.add(tag3);
            }

            btnPost.setEnabled(false);

            UserBasicInfo userBasicInfo = MainActivity.userBasicInfo;

            Article article = new Article(headLine, articleText, new Retrieve("false").createUniqueIdForArticle(),
                    userBasicInfo.getProfileName(), userBasicInfo.getUserName(), privacy, 0, time,
                    opinions, tags);

            if(privacy.equals(DataModel.STR_PUBLIC)){
                Save.increaseArticleNumber(article.getUsername(), article.getTags());
                new Save().saveArticle(article, isTagSuggested);
            }
            else if (privacy.equals(DataModel.STR_ONLY_ME)){
                new Save().savePrivateArticle(article, isTagSuggested);
            }

            onWaitListener.onWaitCallback(DataModel.MOVE_TO_MAIN_ACTIVITY_HOME);

            btnPost.setEnabled(true);

        }
    }

    private void searchForWord(String word){
        word = word.trim().toLowerCase();
        if(word.isEmpty()){
            setDropDownMenu(tagList);
        }
        else{
            DataModel dataModel = new DataModel();
            dataModel.setSearchWord(word);
            ArrayList<ArticleTag> tags = dataModel.getDropDownSearchedTags(tagList);
            if(tags.size() > 0){
                setDropDownMenu(tags);
            }
            else{
                SimpleDialog sd = new SimpleDialog(context, word + " is not found in tag list in database\n" +
                        "Do you want to add this tag in database?");
                String finalWord = word;
                sd.setWaitListener(task -> {
                    if(task == DataModel.YES){
                        addTagsToTheArticle(finalWord, true);
                    }
                });
                sd.setActionType(DataModel.STR_CREATE_TAG);
                sd.show();
            }
        }
    }

    private void inProgress(){
        layout_info.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }
    private void completeProgress(){
        progress.setVisibility(View.GONE);
        layout_info.setVisibility(View.VISIBLE);
    }
}
