package com.amine.blog.repositories;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amine.blog.interfaces.OnReadSingleArticle;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.AccountRecoverInfo;
import com.amine.blog.model.Article;
import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.model.ChatMessage;
import com.amine.blog.model.EditHistory;
import com.amine.blog.model.MyTime;
import com.amine.blog.model.Opinion;
import com.amine.blog.model.ReportToBlog;
import com.amine.blog.model.SharedArticle;
import com.amine.blog.model.SuggestedTag;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.viewmodel.DataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Save {

    // Own variables
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference;

    public Save(){}


    public void saveUserPublicInfo(String username, UserBasicInfo userBasicInfo, ArrayList<String>hobbies,
                                          ArrayList<String> expertise){

        reference = database.getReference().child(FireConstants.STR_USER).child(username)
                .child(FireConstants.STR_PUBLIC_INFO);
        saveUserBasicInfo(username, userBasicInfo);
        saveHobbies(username, hobbies);
        saveExpertise(username, expertise);

    }

    public void saveUserBasicInfo(String username, UserBasicInfo userBasicInfo){
        reference = database.getReference().child(FireConstants.STR_USER).child(username)
                .child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_BASIC_INFO);

        reference.setValue(userBasicInfo);
    }

    public void saveHobbies(String username, ArrayList<String> hobbies){
        reference = database.getReference().child(FireConstants.STR_USER).child(username)
                .child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_HOBBIES);

        reference.setValue(hobbies);
    }

    public void saveExpertise(String username, ArrayList<String> expertise){
        reference = database.getReference().child(FireConstants.STR_USER).child(username)
                .child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_EXPERTISE);

        reference.setValue(expertise);

    }

    public void saveEmailAndPassword(String username, String password, String email, String countryDialCode){
        reference = database.getReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_ACCOUNT_RECOVER_INFO).child(username);

        AccountRecoverInfo ari = new AccountRecoverInfo(username, password,
                "", email, "", countryDialCode);

        reference.setValue(ari);

        savePhoneNumb(username, email);
    }

    public static void saveNewPass(String myUsername, String newPass) {
        reference = database.getReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_ACCOUNT_RECOVER_INFO).child(myUsername).child(FireConstants.STR_OLD_PASSWORD);
        reference.setValue(newPass);

        reference = database.getReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_ACCOUNT_RECOVER_INFO).child(myUsername).child(FireConstants.STR_NEW_PASSWORD);
        reference.setValue("");
    }

    public void savePhoneNumb(String myUsername, String phoneNumb){
        reference = database.getReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_ACCOUNT_RECOVER_INFO).child(myUsername);
        reference.child(FireConstants.STR_OLD_PHONE).setValue(phoneNumb);

        reference = database.getReference().child(FireConstants.STR_USER).child(myUsername)
                .child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_EMAIL);
        String s = "***" + phoneNumb.substring(phoneNumb.length() - 2);
        reference.setValue(s);
    }

    public static void requestAccountRecovery(String myUsername, String newPhone, String newPass){
        reference = database.getReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_ACCOUNT_RECOVER_INFO).child(myUsername);

        reference.child(FireConstants.STR_NEW_PHONE).setValue(newPhone);
        reference.child(FireConstants.STR_NEW_PASSWORD).setValue(newPass);
    }

    public void saveArticle(Article article, boolean[] isTagSuggested){

        saveArticleInDirectory(Retrieve.getRootReference().child(FireConstants.STR_ARTICLE),
                article, isTagSuggested);

        setArticleAsRecent(Retrieve.getRootReference().child(FireConstants.STR_RECENT_ARTICLES), article,
                isTagSuggested);

        // saving last article
        Retrieve.getRootReference().child(FireConstants.STR_LAST_ARTICLE_ID).setValue(article.getID());

        // saving article id under tags
        for(int i = 0; i < article.getTags().size(); i++){
            if(!isTagSuggested[i]){
                saveInTags(article.getTags().get(i), article.getID(), article.getHeadLine());
            }
        }

        // saving article under my profile
        SharedArticle shArt = new SharedArticle(article.getUsername(), article.getUsername(), article.getID(),
                article.getTime());
        saveArticleInMyProfile(article.getUsername(), shArt);

        increaseArticleNumber();
    }

    private void saveArticleInDirectory(DatabaseReference reference, Article article, boolean[] isTagSuggested){

        reference.child(article.getID()).child(FireConstants.STR_ARTICLE_HEADLINE).setValue(article.getHeadLine());
        reference.child(article.getID()).child(FireConstants.STR_ARTICLE_ID).setValue(article.getID());
        reference.child(article.getID()).child(FireConstants.STR_NAME_OF_OWNER).setValue(article.getNameOfOwner());
        reference.child(article.getID()).child(FireConstants.STR_NUMBER_OF_LIKES).setValue(article.getNumberOfLikes());
        reference.child(article.getID()).child(FireConstants.STR_ARTICLE_TEXT).setValue(article.getText());
        reference.child(article.getID()).child(FireConstants.STR_TIME).setValue(article.getTime());
        reference.child(article.getID()).child(FireConstants.STR_USERNAME).setValue(article.getUsername());
        reference.child(article.getID()).child(FireConstants.STR_PRIVACY).setValue(article.getPrivacy());

        saveTags(reference, article, isTagSuggested);
        saveOpinions(reference, article);
    }

    // TODO
    //  Recent articles - reverse order :(
    public static void saveRecentArticleInReverseOrder(ArrayList<Article> articles, Context context){
        DatabaseReference ref = Retrieve.getRootReference().child(FireConstants.STR_RECENT_ARTICLES);
        //ref.removeValue();

        for(int i = 0; i < articles.size(); i++){
            Article article = articles.get(i);
            String revId = article.getID();

            /*
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){

                                        if(task.getException() != null){
                                            Toast.makeText(context, task.getException().toString(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(context, "Success",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
             */

            ref.child(revId).child(FireConstants.STR_ARTICLE_HEADLINE).setValue(article.getHeadLine());
            ref.child(revId).child(FireConstants.STR_ARTICLE_ID).setValue(article.getID());
            ref.child(revId).child(FireConstants.STR_NAME_OF_OWNER).setValue(article.getNameOfOwner());
            ref.child(revId).child(FireConstants.STR_NUMBER_OF_LIKES).setValue(article.getNumberOfLikes());
            ref.child(revId).child(FireConstants.STR_ARTICLE_TEXT).setValue(article.getText());
            ref.child(revId).child(FireConstants.STR_TIME).setValue(article.getTime());
            ref.child(revId).child(FireConstants.STR_USERNAME).setValue(article.getUsername());
            ref.child(revId).child(FireConstants.STR_PRIVACY).setValue(article.getPrivacy());

            for(int j = 0; j < article.getTags().size(); j++){
                String tag = article.getTags().get(j);
                ref.child(revId).child(FireConstants.STR_TAGS).child(tag).setValue(tag);
            }

            reference = ref.child(revId);

            for(int j = 0; j < article.getOpinions().size(); j++){
                reference.child(FireConstants.STR_OPINIONS).child(article.getOpinions().get(j).getId())
                        .setValue(article.getOpinions().get(j));
            }
        }

    }

    private void increaseArticleNumber(){
        reference = Retrieve.getRootReference().child(FireConstants.STR_NUMBER_OF_ARTICLE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int numOfArt = snapshot.getValue(Integer.class);
                    reference.setValue(numOfArt + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void decreaseArticleNumber(){
        reference = Retrieve.getRootReference().child(FireConstants.STR_NUMBER_OF_ARTICLE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int numOfArt = snapshot.getValue(Integer.class);
                    reference.setValue(numOfArt - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void savePrivateArticle(Article article, boolean[] isTagSuggested){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(article.getUsername()).child(FireConstants.STR_ARTICLE).child(article.getID());

        reference.child(FireConstants.STR_ARTICLE_HEADLINE).setValue(article.getHeadLine());
        reference.child(FireConstants.STR_ARTICLE_ID).setValue(article.getID());
        reference.child(FireConstants.STR_NAME_OF_OWNER).setValue(article.getNameOfOwner());
        reference.child(FireConstants.STR_NUMBER_OF_LIKES).setValue(article.getNumberOfLikes());
        reference.child(FireConstants.STR_ARTICLE_TEXT).setValue(article.getText());
        reference.child(FireConstants.STR_TIME).setValue(article.getTime());
        reference.child(FireConstants.STR_USERNAME).setValue(article.getUsername());
        reference.child(FireConstants.STR_PRIVACY).setValue(article.getPrivacy());

        savePrivateTags(article, isTagSuggested, reference);
        savePrivateOpinions(article, reference);

    }

    private void savePrivateTags(Article article, boolean[] isTagSuggested, DatabaseReference reference){
        for(int i = 0; i < article.getTags().size(); i++){
            String tag = article.getTags().get(i);
            if(!isTagSuggested[i]) {
                reference.child(FireConstants.STR_TAGS).child(tag).setValue(tag);
            }
            else{
                saveSuggestedTag(tag, article);
            }
        }
    }

    private void savePrivateOpinions(Article article, DatabaseReference reference){
        for(int i = 0; i < article.getOpinions().size(); i++){
            String opinionId = new Retrieve(article.getID()).createOpinionId();
            reference.child(FireConstants.STR_OPINIONS).child(opinionId).setValue(article.getOpinions().get(i));
        }
    }

    public void saveEditedArticleText(Article article, String newPrivacy){

        boolean[] isTagSuggested = new boolean[3];

        if (article.getPrivacy().equals(DataModel.STR_ONLY_ME) && newPrivacy.equals(DataModel.STR_PUBLIC)){
            reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                    .child(article.getUsername()).child(FireConstants.STR_ARTICLE).child(article.getID());
            reference.removeValue();
            article.setPrivacy(newPrivacy);
            saveArticle(article, isTagSuggested);
        }
        else if(article.getPrivacy().equals(DataModel.STR_PUBLIC) && newPrivacy.equals(DataModel.STR_ONLY_ME)){

            article.setPrivacy(newPrivacy);
            savePrivateArticle(article, isTagSuggested);

            reference = database.getReference().child(FireConstants.STR_ARTICLE).child(article.getID());
            reference.removeValue();
            reference = database.getReference().child(FireConstants.STR_RECENT_ARTICLES)
                    .child(article.getID());
            reference.removeValue();


            for(int i = 0; i < article.getTags().size(); i++){
                reference = database.getReference().child(FireConstants.STR_TAGS).child(article.getTags().get(i))
                        .child(article.getID());
                reference.removeValue();
            }

            reference = database.getReference().child(FireConstants.STR_USER).child(article.getUsername())
                    .child(FireConstants.STR_ARTICLE).child(article.getID());
            reference.removeValue();

            decreaseArticleNumber();
        }
        else if(article.getPrivacy().equals(DataModel.STR_ONLY_ME) && newPrivacy.equals(DataModel.STR_ONLY_ME)){

            article.setPrivacy(newPrivacy);
            savePrivateArticle(article, isTagSuggested);
        }
        else if(article.getPrivacy().equals(DataModel.STR_PUBLIC) && newPrivacy.equals(DataModel.STR_PUBLIC)){

            reference = FirebaseDatabase.getInstance().getReference()
                    .child(FireConstants.STR_ARTICLE).child(article.getID());
            reference.child(FireConstants.STR_ARTICLE_TEXT).setValue(article.getText());
            reference.child(FireConstants.STR_ARTICLE_HEADLINE).setValue(article.getHeadLine());
            reference.child(FireConstants.STR_PRIVACY).setValue(newPrivacy);

            for(int i = 0; i < article.getTags().size(); i++){
                reference = database.getReference().child(FireConstants.STR_TAGS).child(article.getTags().get(i))
                        .child(article.getID());
                reference.setValue(article.getHeadLine());
            }
        }

    }

    private void saveOpinions(DatabaseReference reference, Article article){
        reference = reference.child(article.getID());

        for(int i = 0; i < article.getOpinions().size(); i++){
            reference.child(FireConstants.STR_OPINIONS).child(article.getOpinions().get(i).getId())
                    .setValue(article.getOpinions().get(i));
        }
    }

    private void saveTags(DatabaseReference reference, Article article, boolean[] isTagSuggested){

        for(int i = 0; i < article.getTags().size(); i++){
            String tag = article.getTags().get(i);
            if(!isTagSuggested[i]) {
                reference.child(article.getID()).child(FireConstants.STR_TAGS).child(tag).setValue(tag);
            }
            else{
                saveSuggestedTag(tag, article);
            }
        }
    }

    private void saveSuggestedTag(String tag, Article article){
        DatabaseReference ref = database.getReference().child(FireConstants.STR_SUGGESTED_TAGS)
                .child(tag).child(article.getID());
        ref.setValue(new SuggestedTag(article.getID(), article.getHeadLine(), article.getUsername()));
    }

    public static void deleteSuggestedTag(String tagName){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_SUGGESTED_TAGS)
                .child(tagName);
        ref.removeValue();
    }

    public static void approveSuggestedTag(String username, String tagName, String articleId, String headLine,
                                           View view){
        DatabaseReference pubArtRef = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_ARTICLE)
                .child(articleId),
                prArtRef = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_ADMIN)
                        .child(FireConstants.STR_USER_PERSONAL_INFO).child(username)
                        .child(FireConstants.STR_ARTICLE).child(articleId),
                tagRef = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_TAGS)
                                .child(tagName);

        tagRef.child(articleId).setValue(headLine)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference()
                                        .child(FireConstants.STR_SUGGESTED_TAGS).child(tagName)
                                        .child(articleId).removeValue();
                                view.setVisibility(View.GONE);
                            }
                        });
        tagRef.child("article id").setValue("article id");

        pubArtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    pubArtRef.child(FireConstants.STR_TAGS).child(tagName).setValue(tagName);
                }
                else{
                    prArtRef.child(FireConstants.STR_TAGS).child(tagName).setValue(tagName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void setArticleAsRecent(DatabaseReference reference, Article article, boolean[] isTagSuggested){
        saveArticleInDirectory(reference, article, isTagSuggested);
    }

    public void saveInTags(String tagName, String articleId, String headLine){
        database.getReference().child(FireConstants.STR_TAGS).child(tagName).child(articleId).setValue(headLine);
    }

    public void saveArticleInMyProfile(String myUsername, SharedArticle article){
        database.getReference().child(FireConstants.STR_USER).child(myUsername).child(FireConstants.STR_ARTICLE)
                .child(article.getArticleID()).setValue(article);
    }

    public void saveFollowing(String myUsername, String followingUsername){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(myUsername).child(FireConstants.STR_FOLLOWING).child(followingUsername);

        reference.setValue(followingUsername);


        saveFollower(myUsername, followingUsername);
    }

    private void saveFollower(String myUsername, String followingUsername){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(followingUsername).child(FireConstants.STR_FOLLOWER).child(myUsername);

        reference.setValue(myUsername);
    }

    public void removeFollowing(String myUsername, String followingUsername){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(myUsername).child(FireConstants.STR_FOLLOWING).child(followingUsername);
        reference.removeValue();
        removeFollower(myUsername, followingUsername);
    }

    private void removeFollower(String myUsername, String followingUsername){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(followingUsername).child(FireConstants.STR_FOLLOWER).child(myUsername);
        reference.removeValue();
    }

    public void saveMyFavArticle(String username, String articleId){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(username).child(FireConstants.STR_FAV_POST).child(articleId);
        reference.setValue(articleId);

        // increasing number of likes
        DatabaseReference artRef = database.getReference().child(FireConstants.STR_ARTICLE).child(articleId)
                .child(FireConstants.STR_NUMBER_OF_LIKES);
        artRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int numOfLikes = snapshot.getValue(Integer.class);
                    artRef.setValue(numOfLikes + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeMyFavArticle(String username, String articleId){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(username).child(FireConstants.STR_FAV_POST).child(articleId);
        reference.removeValue();

        // decreasing number of likes
        DatabaseReference artRef = database.getReference().child(FireConstants.STR_ARTICLE).child(articleId)
                .child(FireConstants.STR_NUMBER_OF_LIKES);
        artRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int numOfLikes = snapshot.getValue(Integer.class);
                    artRef.setValue(Math.max(numOfLikes - 1, 0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void  saveMyOpinion(String articleId, Opinion opinion){
        reference = database.getReference().child(FireConstants.STR_ARTICLE)
                .child(articleId).child(FireConstants.STR_OPINIONS).child(opinion.getId());

        reference.setValue(opinion);

    }

    public void saveMyPrivateOpinion(String articleId, Opinion opinion){
        String opinionId = new Retrieve(articleId).createOpinionId();
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(opinion.getName()).child(FireConstants.STR_ARTICLE).child(articleId)
                .child(FireConstants.STR_OPINIONS).child(opinionId);
        reference.setValue(opinion);
    }

    public void saveMyMessage(String receiverUsername, String myUsername, ChatMessage chatMessage){
        // saving message to my end
        reference = database.getReference().child(FireConstants.STR_PERSONAL_CHAT)
                .child(myUsername).child(receiverUsername).child(chatMessage.getMessageId());
        reference.setValue(chatMessage);

        reference = Retrieve.getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(myUsername)
                .child(receiverUsername).child(FireConstants.STR_LAST_MESSAGE_TIME_IN_MILL);
        reference.setValue(chatMessage.getTimeInMill());

        reference = Retrieve.getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(myUsername)
                .child(receiverUsername).child(FireConstants.STR_LAST_MESSAGE);
        reference.setValue(chatMessage);


        // saving message to the receiver end
        reference = database.getReference().child(FireConstants.STR_PERSONAL_CHAT)
                .child(receiverUsername).child(myUsername).child(chatMessage.getMessageId());
        reference.setValue(chatMessage);

        reference = Retrieve.getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(receiverUsername)
                .child(myUsername).child(FireConstants.STR_LAST_MESSAGE_TIME_IN_MILL);
        reference.setValue(chatMessage.getTimeInMill());

        reference = Retrieve.getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(receiverUsername)
                .child(myUsername).child(FireConstants.STR_LAST_MESSAGE);
        reference.setValue(chatMessage);

        // sending receiver a notification
        reference = Retrieve.getRootReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_NEW_MESSAGE_STATUS).child(receiverUsername)
                .child(myUsername);

        reference.setValue(chatMessage.getText());

        // saving receiver to my chat list
        Retrieve.getRootReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_CHAT_LIST).child(receiverUsername).child(myUsername)
                .setValue(chatMessage.getText());
        Retrieve.getRootReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_CHAT_LIST).child(myUsername).child(receiverUsername)
                .setValue(chatMessage.getText());

    }

    public String getMessageId(String receiverUsername, String myUsername){
        return database.getReference().child(FireConstants.STR_PERSONAL_CHAT)
                .child(myUsername).child(receiverUsername).push().getKey();
    }

    public static void activeStatus(String username, boolean save){
        reference = database.getReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_ACTIVE_STATUS).child(username);
        if(save){
            reference.setValue(FireConstants.STR_ACTIVE);
        }
        else{
            reference.removeValue();
        }
    }

    public static void removeNewMessages(String myUsername, String friendsUsername){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_NEW_MESSAGE_STATUS).child(myUsername).child(friendsUsername);
        ref.removeValue();
    }

    public static void saveReport(ReportToBlog reportToBlog){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_USER_REPORT)
                .child(FireConstants.STR_REPORT).child(reportToBlog.getReportId());

        ref.setValue(reportToBlog);
    }

    public static void saveEditHistory(String articleId, String ownerUsername, String prevText, String type,
                                       String privacy){
        MyTime time = new DataModel().getCurrentMyTime();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(FireConstants.STR_ARTICLE_EDIT_HISTORY).child(FireConstants.STR_ARTICLE)
                .child(articleId).push();

        ref.setValue(new EditHistory(prevText, type, articleId, ownerUsername, time));

        /*
        else if(privacy.equals(DataModel.STR_ONLY_ME)){
            MyTime time = new DataModel().getCurrentMyTime();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_ADMIN)
                    .child(FireConstants.STR_USER_PERSONAL_INFO).child(ownerUsername)
                    .child(FireConstants.STR_ARTICLE_EDIT_HISTORY).child(FireConstants.STR_ARTICLE)
                    .child(articleId).push();

            ref.setValue(new EditHistory(prevText, type, articleId, ownerUsername, time));
        }
         */

    }

    public static void saveReportFeedback(ReportToBlog reportToBlog){
        //  Admin will give feedback who reported earlier.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_USER_REPORT)
                .child(FireConstants.STR_FEEDBACK).child(reportToBlog.getReporterUserName())
                .child(reportToBlog.getReportId());
    }
}
