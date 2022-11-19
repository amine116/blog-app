package com.amine.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amine.blog.interfaces.OnReadSingleArticle;
import com.amine.blog.model.Article;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class ProfileView extends AppCompatActivity implements View.OnClickListener {

    private String username, profileName, myUsername;
    private boolean amIFollowing = false, basicInfoClicked = true,
            hobbiesClicked = true, expertiseClicked = true;

    // Views
    private TextView txtFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        hideActionBar();
        setInformation();
        isFollowing();
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void setInformation(){
        Intent intent = getIntent();
        username = intent.getStringExtra("USER_NAME");
        myUsername = MainActivity.userBasicInfo.getUserName();
        String myName = MainActivity.userBasicInfo.getProfileName(),
                myUniversity = MainActivity.userBasicInfo.getUniversity(),
                myProfession = MainActivity.userBasicInfo.getProfession();

        txtFollow = findViewById(R.id.txtFollowButton);

        if(myUsername.equals(username)){
            makeItMyProfile();
            profileName = MainActivity.userBasicInfo.getProfileName();
            setBasicInfo(myName, myUsername, myUniversity, myProfession);
            setExpertise(myUsername);
            setHobbies(myUsername);
            setArticles(myUsername);
            setPrivateArticle(myUsername);
        }
        else if(username != null){
            inProgressUserBasicInfo();
            inProgressHobby();
            inProgressExpertise();
            inProgressArticle();
            readBasicInfo(username);
        }

    }

    private void makeItMyProfile(){
        RelativeLayout rlF = findViewById(R.id.layout_follow),
                rlM = findViewById(R.id.layout_message);
        rlF.setVisibility(View.INVISIBLE);
        rlM.setVisibility(View.INVISIBLE);
    }

    private void setListeners(){
        findViewById(R.id.layout_follow).setOnClickListener(this);
        findViewById(R.id.layout_message).setOnClickListener(this);
        findViewById(R.id.txtBasicInfo).setOnClickListener(this);
        findViewById(R.id.txtExpertise).setOnClickListener(this);
        findViewById(R.id.txtHobbies).setOnClickListener(this);
    }

    private void readBasicInfo(String userName){
        Retrieve retrieve = new Retrieve(userName);
        retrieve.setOnListenerForReadingUserBasicInfo((userBasicInfo, isSignedIn) -> {
            completeProgressUserBasicInfo();

            setBasicInfo(userBasicInfo.getProfileName(), userBasicInfo.getUserName(),
                    userBasicInfo.getUniversity(), userBasicInfo.getProfession());

            profileName = userBasicInfo.getProfileName();

            setExpertise(userName);
            setHobbies(userName);
            setArticles(userName);
        });
        retrieve.getAnyUserBasicInfo();
    }

    private void setBasicInfo(String pName, String userName, String university, String profession){

        TextView txtProfileName = findViewById(R.id.txtProfileName),
                txtUsername = findViewById(R.id.txtUserName),
                txtUniversity = findViewById(R.id.txtUniversity),
                txtProfession = findViewById(R.id.txtProfession),
                txtProfileHeadName = findViewById(R.id.txtProfileHeadName);

        if(pName != null){
            txtProfileHeadName.setText(pName);
            pName = "Name: " + pName;
            txtProfileName.setText(pName);
        }
        if(userName != null) {
            String name = "Username: " + userName;
            txtUsername.setText(name);
        }
        if(university != null) {
            university = "Education: " + university;
            txtUniversity.setText(university);
        }
        if(profession != null){
            profession = "Profession: " + profession;
            txtProfession.setText(profession);
        }
    }

    private void setExpertise(String username){
        inProgressExpertise();
        Retrieve retrieve = new Retrieve(username);
        retrieve.setOnStringArrayListener(stringArrayList -> {
            completeProgressExpertise();
            TextView txtExpertiseList = findViewById(R.id.txtExpertiseList);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < stringArrayList.size(); i++){
                sb.append(i + 1).append(". ");
                sb.append(stringArrayList.get(i));
                sb.append("\n");
            }
            txtExpertiseList.setText(sb.toString());
        });
        retrieve.getExpertiseList();
    }

    private void setHobbies(String username){
        inProgressHobby();
        Retrieve retrieve = new Retrieve(username);
        retrieve.setOnStringArrayListener(stringArrayList -> {
            completeProgressHobby();
            TextView txtHobbyList = findViewById(R.id.txtHobbyList);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < stringArrayList.size(); i++){
                sb.append(i + 1).append(". ");
                sb.append(stringArrayList.get(i));
                sb.append("\n");
            }
            txtHobbyList.setText(sb.toString());
        });
        retrieve.getHobbyList();
    }

    private void setArticles(String username){
        inProgressArticle();
        Retrieve retrieve = new Retrieve(username);
        retrieve.setOnUserArticleListener(articles -> {
            completeProgressArticle();
            showArticles(articles);
        });
        retrieve.getArticleOfTheUser();
    }

    private void setPrivateArticle(String username){
        findViewById(R.id.progress_article_only_me).setVisibility(View.VISIBLE);
        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnUserArticleListener(articles -> {
            findViewById(R.id.progress_article_only_me).setVisibility(View.GONE);
            findViewById(R.id.layout_article_only_me).setVisibility(View.VISIBLE);
            findViewById(R.id.txtArticleOnlyMe).setVisibility(View.VISIBLE);

            showPrivateArticles(articles);
        });
        retrieve.getSinglePrivateArticleList(username);
    }

    private void inProgressUserBasicInfo(){
        ProgressBar pBar = findViewById(R.id.progress_basicInfo);
        RelativeLayout rl = findViewById(R.id.layout_basicInfoChild);

        rl.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }

    private void completeProgressUserBasicInfo(){
        ProgressBar pBar = findViewById(R.id.progress_basicInfo);
        RelativeLayout rl = findViewById(R.id.layout_basicInfoChild);

        pBar.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
    }

    private void inProgressHobby(){
        ProgressBar pBar = findViewById(R.id.progress_hobbies);
        TextView txtHobbyList = findViewById(R.id.txtHobbyList);

        txtHobbyList.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }
    private void completeProgressHobby(){
        ProgressBar pBar = findViewById(R.id.progress_hobbies);
        TextView txtHobbyList = findViewById(R.id.txtHobbyList);

        pBar.setVisibility(View.GONE);
        txtHobbyList.setVisibility(View.VISIBLE);
    }

    private void inProgressExpertise(){
        ProgressBar pBar = findViewById(R.id.progress_expertise);
        TextView txtExpertiseList = findViewById(R.id.txtExpertiseList);

        txtExpertiseList.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }
    private void completeProgressExpertise(){
        ProgressBar pBar = findViewById(R.id.progress_expertise);
        TextView txtExpertiseList = findViewById(R.id.txtExpertiseList);

        pBar.setVisibility(View.GONE);
        txtExpertiseList.setVisibility(View.VISIBLE);
    }

    private void inProgressArticle(){
        ProgressBar pBar = findViewById(R.id.progress_article);
        LinearLayout ll = findViewById(R.id.layout_articles);

        ll.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }
    private void completeProgressArticle(){
        ProgressBar pBar = findViewById(R.id.progress_article);
        LinearLayout ll = findViewById(R.id.layout_articles);

        pBar.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);
    }

    private void isFollowing(){
        //DataModel.deb("1. " + myUsername + " | " + username);
        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnWaitListener(task -> {
            //DataModel.deb("3. " + myUsername + " | " + username);
            setListeners();
            amIFollowing = task == DataModel.YES;
            if(amIFollowing){
                String s = "Following";
                txtFollow.setText(s);
                txtFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }
            else{
                String s = "Follow";
                txtFollow.setText(s);
                txtFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
            }
        });
        retrieve.isFollowing(myUsername, username);
    }

    private void showArticles(ArrayList<Article> articles){
        LinearLayout ll = findViewById(R.id.layout_articles);

        TextView txtArticle = findViewById(R.id.txtArticles);
        String s;
        if(articles.size() < 2){
            s = "Total Article: " + articles.size();
        }
        else {
            s = "Total Articles: " + articles.size();
        }
        txtArticle.setText(s);

        for(int i = 0; i < articles.size(); i++){
            ll.addView(getView(articles.get(i)), 0);
        }
    }

    private void showPrivateArticles(ArrayList<Article> articles){
        LinearLayout ll = findViewById(R.id.layout_article_only_me);
        for(int i = 0; i < articles.size(); i++){
            ll.addView(getView(articles.get(i)), 0);
        }
    }

    private View getView(Article article){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_profile_article_view, null);

        String s;
        if(article.getUsername().equals(username)){
            s = article.getHeadLine();
        }
        else{
            s = article.getHeadLine() + "\n\tShared from " + article.getUsername();
        }

        TextView txtArticleHeadLine = view.findViewById(R.id.txt_model_profile_article_headLine);
        txtArticleHeadLine.setText(s);
        txtArticleHeadLine.setOnClickListener(getOnClickListenerForArticleHeadLine(article));

        return view;

    }

    private View.OnClickListener getOnClickListenerForArticleHeadLine(Article article){
        return view -> {
            Intent intent = new Intent(ProfileView.this, ArticleDiscussion.class);

            intent.putExtra("USER_NAME", article.getUsername());
            intent.putExtra("ARTICLE_ID", article.getID());
            intent.putExtra("PRIVACY", article.getPrivacy());
            startActivity(intent);
        };
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_message){
            Intent intent = new Intent(ProfileView.this, ChatActivity.class);
            intent.putExtra("INTENT_TYPE", ChatActivity.CHAT_BOX);
            intent.putExtra("RECEIVE_USER_NAME", username);
            intent.putExtra("RECEIVER_NAME", profileName);
            startActivity(intent);
        }
        else if(view.getId() == R.id.layout_follow){
            if(amIFollowing){

                new Save().removeFollowing(myUsername, username);
                amIFollowing = false;

                String s = "Follow";
                txtFollow.setText(s);
                txtFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
            }
            else{
                new Save().saveFollowing(myUsername, MainActivity.userBasicInfo.getProfileName(), username, profileName);
                amIFollowing = true;

                String s = "Following";
                txtFollow.setText(s);
                txtFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }
        }
        else if(view.getId() == R.id.txtBasicInfo){
            RelativeLayout rl = findViewById(R.id.layout_basicInfo);
            TextView tv = findViewById(R.id.txtBasicInfo);
            if(basicInfoClicked){
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                rl.setVisibility(View.GONE);
                basicInfoClicked = false;
            }
            else{
                rl.setVisibility(View.VISIBLE);
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                basicInfoClicked = true;
            }
        }
        else if(view.getId() == R.id.txtHobbies){
            RelativeLayout rl = findViewById(R.id.layout_hobbies);
            TextView tv = findViewById(R.id.txtHobbies);
            if(hobbiesClicked){
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                rl.setVisibility(View.GONE);
                hobbiesClicked = false;
            }
            else{
                rl.setVisibility(View.VISIBLE);
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                hobbiesClicked = true;
            }
        }
        else if(view.getId() == R.id.txtExpertise){
            RelativeLayout rl = findViewById(R.id.layout_expertise);
            TextView tv = findViewById(R.id.txtExpertise);
            if(expertiseClicked){
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                rl.setVisibility(View.GONE);
                expertiseClicked = false;
            }
            else{
                rl.setVisibility(View.VISIBLE);
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                expertiseClicked = true;
            }
        }
    }


}