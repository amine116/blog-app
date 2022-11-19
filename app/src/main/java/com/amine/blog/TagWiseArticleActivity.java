package com.amine.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class TagWiseArticleActivity extends AppCompatActivity {

    private DataSnapshot rootTagSnapshot;
    private ArrayList<String> tags = new ArrayList<>();
    private int fromTag = 1, fromArticle = 1;
    private boolean isReadingTagDone = false, isReadingArticleDone = false;

    private String intentTag, intentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_wise_article);
        hideActionBar();
        getIntentData();

        if(intentType != null){
            if(intentType.equals(DataModel.STR_CLICKED)){
                findViewById(R.id.progress_readingArticles).setVisibility(View.VISIBLE);
                LinearLayout layout = findViewById(R.id.layout_articles);
                layout.removeAllViews();
                setArticleList(intentTag);
            }
        }
        initializeTagSnapShot();
    }

    private void getIntentData(){
        intentTag = getIntent().getStringExtra("TAG");
        intentType = getIntent().getStringExtra("INTENT_TYPE");
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void initializeTagSnapShot(){
        findViewById(R.id.progress_readingTags).setVisibility(View.VISIBLE);
        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnReadListener(snapshot -> {
            findViewById(R.id.progress_readingTags).setVisibility(View.GONE);
            rootTagSnapshot = snapshot;
            formTheTagList();
            setTagList();
        });
        retrieve.getTagsSnapshot();
    }

    private void formTheTagList(){

        DataSnapshot snapshot = rootTagSnapshot;
        if(snapshot != null){
            tags.clear();
            int start = 1;
            for(DataSnapshot tagNameSnapshot : snapshot.getChildren()){

                if(start >= this.fromTag){
                    String tagName = tagNameSnapshot.getKey();
                    tags.add(tagName);
                }
                start++;
                if(start >= fromTag + DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                    break;
                }

            }
            this.fromTag = start;

        }
    }

    private void setTagList(){

        LinearLayout layoutTags = findViewById(R.id.layout_tag_names);

        for(int i = 0; i < tags.size(); i++){
            layoutTags.addView(getView(tags.get(i), "tag"));
        }
        String s = "Show more...";
        TextView txtShowMore = new TextView(this);
        txtShowMore.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
        txtShowMore.setGravity(Gravity.CENTER);
        txtShowMore.setText(s);
        txtShowMore.setTextColor(getResources().getColor(R.color.green));
        txtShowMore.setOnClickListener(getListenerForShowMore(txtShowMore));
        layoutTags.addView(txtShowMore);
    }

    private View.OnClickListener getListenerForShowMore(TextView txtShowMore) {
        return view -> {
            txtShowMore.setVisibility(View.GONE);
            formTheTagList();
            setTagList();
        };
    }

    private View getView(String data, String type){

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_tag_list, null);

        TextView txtSomething = view.findViewById(R.id.txtSomething);
        txtSomething.setText(data);

        view.setOnClickListener(getListenerForView(data, type));
        return view;

    }

    private void setArticleList(String tagName){
        Retrieve retrieve = new Retrieve("false");
        retrieve.setArticleUnderATagListener(articlesUnderTags -> {
            findViewById(R.id.progress_readingArticles).setVisibility(View.GONE);
            TextView txtTagName = findViewById(R.id.txtTagName);
            txtTagName.setVisibility(View.VISIBLE);

            String s;
            if(articlesUnderTags.size() == 0 || articlesUnderTags.size() == 1){
                s = articlesUnderTags.size() + " article on " + tagName;
            }
            else{
                s = articlesUnderTags.size() + " articles on " + tagName;
            }
            txtTagName.setText(s);

            LinearLayout layout = findViewById(R.id.layout_articles);
            for(int i = 0; i < articlesUnderTags.size(); i++){
                layout.addView(
                        getView(articlesUnderTags.get(i).getHeadLine(), articlesUnderTags.get(i).getArticleId()));
            }
        });
        retrieve.readArticlesUnderATag(tagName);
    }

    private View.OnClickListener getListenerForView(String data, String type){
        if(type.equals("tag")){ // type = listener type
            return view -> {
                findViewById(R.id.txtTagName).setVisibility(View.GONE);
                LinearLayout layout = findViewById(R.id.layout_articles);
                layout.removeAllViews();
                findViewById(R.id.progress_readingArticles).setVisibility(View.VISIBLE);
                setArticleList(data);
            };
        }
        else{
            return view -> {
                LinearLayout layout = findViewById(R.id.layout_articles);
                layout.removeAllViews();
                findViewById(R.id.progress_readingArticles).setVisibility(View.VISIBLE);

                Retrieve retrieve = new Retrieve(type); // type = article id
                retrieve.setSingleArticleListener(article -> {
                    findViewById(R.id.progress_readingArticles).setVisibility(View.GONE);

                    if(article == null){
                        Toast.makeText(this, "Article not found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Intent intent = new Intent(TagWiseArticleActivity.this, ArticleDiscussion.class);
                        intent.putExtra("USER_NAME", article.getUsername());
                        intent.putExtra("ARTICLE_ID", article.getID());
                        intent.putExtra("PRIVACY", article.getPrivacy());
                        startActivity(intent);
                    }
                });
                retrieve.getSingleArticle();
            };
        }
    }

}