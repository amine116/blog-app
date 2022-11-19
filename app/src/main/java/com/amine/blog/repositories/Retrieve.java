package com.amine.blog.repositories;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amine.blog.MainActivity;
import com.amine.blog.interfaces.ExistListener;
import com.amine.blog.interfaces.OnReadAllOpinionsListener;
import com.amine.blog.interfaces.OnReadArticleEditHistory;
import com.amine.blog.interfaces.OnReadArticleListener;
import com.amine.blog.interfaces.OnReadArticleUnderATag;
import com.amine.blog.interfaces.OnReadChatList;
import com.amine.blog.interfaces.OnReadChatMessages;
import com.amine.blog.interfaces.OnReadListener;
import com.amine.blog.interfaces.OnReadLongValue;
import com.amine.blog.interfaces.OnReadPeople;
import com.amine.blog.interfaces.OnReadSingleArticle;
import com.amine.blog.interfaces.OnReadSingleMessage;
import com.amine.blog.interfaces.OnReadSingleOpinion;
import com.amine.blog.interfaces.OnReadSinglePeople;
import com.amine.blog.interfaces.OnReadSuggestedTagsListener;
import com.amine.blog.interfaces.OnReadTagsListener;
import com.amine.blog.interfaces.OnReadUserBasicInfoListener;
import com.amine.blog.interfaces.OnReadStringArrayListener;
import com.amine.blog.interfaces.OnReadUserArticleListener;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.interfaces.OnWaitListenerWithStringArrayInfo;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.Article;
import com.amine.blog.model.ArticleTag;
import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.model.ChatMessage;
import com.amine.blog.model.EditHistory;
import com.amine.blog.model.Opinion;
import com.amine.blog.model.People;
import com.amine.blog.model.RecentArticle;
import com.amine.blog.model.SharedArticle;
import com.amine.blog.model.SuggestedTag;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Retrieve {

    // values for this class
    private final String node;
    private OnReadTagsListener tagListener;
    private OnReadUserBasicInfoListener basicInfoListener;
    private OnReadListener readListener;
    private OnReadStringArrayListener stringArrayListener;
    private OnReadUserArticleListener userArticleListener;
    private OnWaitListener waitListener;
    private OnReadAllOpinionsListener allOpinionsReadListener;
    private OnReadSingleMessage singleMessageListener;
    private OnReadSingleOpinion singleOpinionListener;
    private OnReadArticleUnderATag articleUnderATagListener;
    private OnReadSingleArticle singleArticleListener;
    private OnWaitListenerWithStringInfo waitWithInfoListener;

    private Context context;

    // Firebase
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private DatabaseReference reference;


    public Retrieve(String node){
        this.node = node;
    }

    public void setOnStringArrayListener(OnReadStringArrayListener stringArrayListener) {
        this.stringArrayListener = stringArrayListener;
    }

    public void setOnUserArticleListener(OnReadUserArticleListener userArticleListener) {
        this.userArticleListener = userArticleListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnListerForReadingTags(OnReadTagsListener tagListener){
        this.tagListener = tagListener;
    }

    public void setOnReadListener(OnReadListener articleListener){
        this.readListener = articleListener;
    }

    public void setOnSingleMessageListener(OnReadSingleMessage singleMessageListener) {
        this.singleMessageListener = singleMessageListener;
    }

    public void setOnListenerForReadingUserBasicInfo(OnReadUserBasicInfoListener basicInfoListener){
        this.basicInfoListener = basicInfoListener;
    }

    public void setOnWaitListener(OnWaitListener waitListener) {
        this.waitListener = waitListener;
    }

    public void setOnReadAllOpinionsListener(OnReadAllOpinionsListener allOpinionsReadListener) {
        this.allOpinionsReadListener = allOpinionsReadListener;
    }

    public void setOnReadSingleOpinionListener(OnReadSingleOpinion singleOpinionListener) {
        this.singleOpinionListener = singleOpinionListener;
    }

    public void setArticleUnderATagListener(OnReadArticleUnderATag articleUnderATagListener) {
        this.articleUnderATagListener = articleUnderATagListener;
    }

    public void setSingleArticleListener(OnReadSingleArticle singleArticleListener) {
        this.singleArticleListener = singleArticleListener;
    }

    public void setWaitWithInfoListener(OnWaitListenerWithStringInfo waitWithInfoListener) {
        this.waitWithInfoListener = waitWithInfoListener;
    }

    public void getSignedInUserBasicInfo(){
        FirebaseUser user = fAuth.getCurrentUser();
        if(user == null || user.getEmail() == null){

            UserBasicInfo userBasicInfo = new UserBasicInfo(FireConstants.STR_GUEST_USER_USERNAME,
                    FireConstants.STR_GUEST_USER_USERNAME, FireConstants.STR_GUEST_USER_USERNAME,
                    FireConstants.STR_GUEST_USER_USERNAME);
            SignInFile signInFile = new SignInFile(context);
            String guestName = signInFile.readFile();
            MainActivity.isGuest = true;
            basicInfoListener.onReadBasicInfo(userBasicInfo, !guestName.equals(""));

            //DataModel.deb("user null");
        }
        else{

            DatabaseReference r = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_USER)
                    .child(DataModel.getUserNameFromEmail(user.getEmail()))
                    .child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_BASIC_INFO);
            MainActivity.isGuest = false;

            r.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        //DataModel.deb("exist");
                        UserBasicInfo userBasicInfo = snapshot.getValue(UserBasicInfo.class);
                        basicInfoListener.onReadBasicInfo(userBasicInfo, true);
                    }
                    else{

                        UserBasicInfo userBasicInfo = new UserBasicInfo(FireConstants.STR_GUEST_USER_USERNAME,
                                FireConstants.STR_GUEST_USER_USERNAME, FireConstants.STR_GUEST_USER_USERNAME,
                                FireConstants.STR_GUEST_USER_USERNAME);
                        basicInfoListener.onReadBasicInfo(userBasicInfo, false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //DataModel.deb(error.getMessage());
                }
            });
        }
    }

    public void getAnyUserBasicInfo(){
        // node = username
        DatabaseReference r = database.getReference().child(FireConstants.STR_USER).child(node)
                .child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_BASIC_INFO);

        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserBasicInfo userBasicInfo = snapshot.getValue(UserBasicInfo.class);
                    basicInfoListener.onReadBasicInfo(userBasicInfo, true);
                }
                else{
                    UserBasicInfo userBasicInfo = new UserBasicInfo(FireConstants.STR_GUEST_USER_USERNAME,
                            FireConstants.STR_GUEST_USER_USERNAME, FireConstants.STR_GUEST_USER_USERNAME,
                            FireConstants.STR_GUEST_USER_USERNAME);
                    basicInfoListener.onReadBasicInfo(userBasicInfo, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static String getSignedInUserEmail(){
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        if(user != null){
            return user.getEmail();
        }
        else{
            return null;
        }
    }

    public static DatabaseReference getRootReference(){
        return FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseAuth getAuth(){
        return FirebaseAuth.getInstance();
    }

    public static FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    public String createUniqueIdForArticle(){
        return database.getReference().child(FireConstants.STR_ARTICLE).push().getKey();
    }

    public String createOpinionId(){
        // nod = article id
        reference = database.getReference().child(FireConstants.STR_ARTICLE).child(node)
                .child(FireConstants.STR_OPINIONS).push();

        return reference.getKey();

    }

    public void getTagList(){
        ArrayList<ArticleTag> tags = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(node);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        tags.add(new ArticleTag(item.getKey(), item.getChildrenCount() - 1));
                    }
                }
                tagListener.onReadTag(tags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getRecentArticleList(OnReadArticleListener onReadArticle, long startingArticleTimeInMill,
                                            boolean isFirstRead){

        ArrayList<RecentArticle> articles = new ArrayList<>();

        Query query;
        if (isFirstRead){
            query = getRootReference().child(FireConstants.STR_RECENT_ARTICLES)
                    .orderByChild(FireConstants.STR_TIME_IN_MILL).startAt(startingArticleTimeInMill)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }
        else{
            query = getRootReference().child(FireConstants.STR_RECENT_ARTICLES)
                    .orderByChild(FireConstants.STR_TIME_IN_MILL).startAfter(startingArticleTimeInMill)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }

//        DatabaseReference ref = getRootReference().child(FireConstants.STR_RECENT_ARTICLES);
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    for(DataSnapshot item : snapshot.getChildren()){
//                        if(item.getKey() != null){
//                            Article article = new DataModel().formArticle(snapshot.child(item.getKey()));
//                            articles.add(article);
//                        }
//                    }
//                    onReadArticle.onReadArticle(articles, UserAccount.SUCCESS);
//                }
//                else{
//                    onReadArticle.onReadArticle(articles, UserAccount.FAIL);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                onReadArticle.onReadArticle(null, UserAccount.FAIL);
//            }
//        });
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (item.getKey() != null && !item.getKey().equals(FireConstants.STR_LAST_ARTICLE_TIME_IN_MILL)){
                            RecentArticle article = new DataModel().formRecentArticle(snapshot.child(item.getKey()));
                            articles.add(article);
                        }
                    }
                    onReadArticle.onReadArticle(articles, UserAccount.SUCCESS);
                }
                else{
                    onReadArticle.onReadArticle(articles, UserAccount.FAIL);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadArticle.onReadArticle(null, UserAccount.FAIL);

            }
        });
    }

    public static void getFirstRecentArticles(OnReadArticleListener onReadArticle){
        ArrayList<RecentArticle> articles = new ArrayList<>();

        Query query = getRootReference().child(FireConstants.STR_RECENT_ARTICLES)
                        .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (item.getKey() != null && !item.getKey().equals(FireConstants.STR_LAST_ARTICLE_TIME_IN_MILL)){
                            RecentArticle article = new DataModel().formRecentArticle(snapshot.child(item.getKey()));
                            articles.add(article);
                        }
                    }
                    onReadArticle.onReadArticle(articles, UserAccount.SUCCESS);
                }
                else{
                    onReadArticle.onReadArticle(articles, UserAccount.FAIL);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadArticle.onReadArticle(null, UserAccount.FAIL);

            }
        });
    }

    public static void getLastArticleTimeInMill(OnWaitListenerWithStringInfo onWait){
        getRootReference().child(FireConstants.STR_RECENT_ARTICLES).child(FireConstants.STR_LAST_ARTICLE_TIME_IN_MILL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            long timeInMill = snapshot.getValue(Long.class);
                            onWait.onWaitWithInfo(UserAccount.SUCCESS, timeInMill + "");
                        }
                        else {
                            onWait.onWaitWithInfo(UserAccount.FAIL, null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onWait.onWaitWithInfo(UserAccount.FAIL, null);
                    }
                });
    }

    public void getSinglePrivateArticleList(String myUsername){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(myUsername).child(FireConstants.STR_ARTICLE);

        ArrayList<Article> articles = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        String articleId = item.getKey();
                        if(articleId != null){
                            Article article = new DataModel().formArticle(snapshot.child(articleId));
                            articles.add(article);
                        }
                    }
                }
                userArticleListener.onReadUserArticle(articles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getArticleOfTheUser(){
        // node = username;
        DatabaseReference articleRef = database.getReference().child(FireConstants.STR_ARTICLE),
                userArticleRef = database.getReference().child(FireConstants.STR_USER).child(node)
                        .child(FireConstants.STR_ARTICLE);
        articleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Article> articles = new ArrayList<>();
                userArticleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        if(snapshot1.exists()){
                            for(DataSnapshot item : snapshot1.getChildren()){
                                String key = item.getKey();
                                SharedArticle sa = item.getValue(SharedArticle.class);

                                if(sa != null && snapshot.child(sa.getArticleID()).exists()){
                                    Article article =
                                            new DataModel().formArticle(snapshot.child(sa.getArticleID()));
                                    articles.add(article);
                                }
                            }
                        }
                        userArticleListener.onReadUserArticle(articles);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getSingleArticle(){
        // node = article id;
        //DataModel.deb(node);
        reference = database.getReference().child(FireConstants.STR_ARTICLE).child(node);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Article article = new DataModel().formArticle(snapshot);
                    singleArticleListener.onReadSingleArticle(article);
                }
                else{
                    singleArticleListener.onReadSingleArticle(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSinglePrivateArticle(String myUsername){
        // node = article id;
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(myUsername).child(FireConstants.STR_ARTICLE).child(node);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Article article = new DataModel().formArticle(snapshot);
                    singleArticleListener.onReadSingleArticle(article);
                }
                else{
                    singleArticleListener.onReadSingleArticle(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getHobbyList(){
        // node = username
        reference = database.getReference().child(FireConstants.STR_USER).child(node).child(FireConstants.STR_PUBLIC_INFO)
                .child(FireConstants.STR_HOBBIES);
        ArrayList<String> expertise = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        expertise.add(item.getValue(String.class));
                    }
                }
                stringArrayListener.onReadStringArray(expertise);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getExpertiseList(){
        //node = username
        reference = database.getReference().child(FireConstants.STR_USER).child(node).child(FireConstants.STR_PUBLIC_INFO)
                .child(FireConstants.STR_EXPERTISE);
        ArrayList<String> hobbies = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        hobbies.add(item.getValue(String.class));
                    }
                }
                stringArrayListener.onReadStringArray(hobbies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void isFollowing(String myUsername, String personUsername){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(myUsername).child(FireConstants.STR_FOLLOWING).child(personUsername);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    waitListener.onWaitCallback(DataModel.YES);
                }
                else{
                    waitListener.onWaitCallback(DataModel.NO);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DataModel.deb("2. " + myUsername + " | " + error);
            }
        });
    }

    public void isFavouriteArticle(String articleId){
        // node = username
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(node).child(FireConstants.STR_FAV_POST).child(articleId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    waitListener.onWaitCallback(DataModel.YES);
                }
                else{
                    waitListener.onWaitCallback(DataModel.NO);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readSingleOpinionOfTheArticle(){
        // node = article id;
        reference = database.getReference().child(FireConstants.STR_ARTICLE).child(node)
                .child(FireConstants.STR_OPINIONS);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                singleOpinionListener.onReadSingleOpinion(snapshot.getValue(Opinion.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readSinglePrivateOpinionOfTheArticle(String myUsername, String articleId){
        reference = database.getReference().child(FireConstants.STR_ADMIN).child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(myUsername).child(FireConstants.STR_ARTICLE).child(articleId)
                .child(FireConstants.STR_OPINIONS);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                singleOpinionListener.onReadSingleOpinion(snapshot.getValue(Opinion.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void readMessages(String myUsername, String friendsUsername, long startAfter, boolean isFirstRead,
                                    OnReadChatMessages onReadMessage){
        Query query;
        if(isFirstRead){
            query = getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(myUsername)
                    .child(friendsUsername).orderByChild(FireConstants.STR_TIME_IN_MILL).startAt((double)startAfter)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }
        else{
            query = getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(myUsername)
                    .child(friendsUsername).orderByChild(FireConstants.STR_TIME_IN_MILL).startAfter((double)startAfter)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        if(item.getKey() != null){
                            if(!item.getKey().equals("last-message")){
                                ChatMessage chatMessage = item.getValue(ChatMessage.class);
                                chatMessages.add(chatMessage);
                            }
                        }
                    }
                    onReadMessage.onReadChatMessage(chatMessages, DataModel.YES);
                }
                else{
                    onReadMessage.onReadChatMessage(chatMessages, DataModel.NO);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadMessage.onReadChatMessage(chatMessages, DataModel.NO);
            }
        });

    }

    public static void readLastMessageTimeInMill(String myUsername, String friendsUsername,
                                                 OnReadLongValue onWait) {

        DatabaseReference ref = getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(myUsername)
                .child(friendsUsername).child(FireConstants.STR_LAST_MESSAGE_TIME_IN_MILL);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    onWait.onReadLongValue(snapshot.getValue(Long.class));
                }
                else{
                    onWait.onReadLongValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onWait.onReadLongValue(1);
            }
        });

    }

    public static void readInstantMessageSent(String myUsername, String friendsUsername,
                                              OnReadSingleMessage onReadSingleMessage){
        DatabaseReference reference = Retrieve.getRootReference().child(FireConstants.STR_PERSONAL_CHAT).child(myUsername)
                .child(friendsUsername).child(FireConstants.STR_LAST_MESSAGE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    onReadSingleMessage.onReadMessage(snapshot.getValue(ChatMessage.class));
                    reference.removeValue();
                }
                else{
                    onReadSingleMessage.onReadMessage(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadSingleMessage.onReadMessage(null);
            }
        });
    }

    public static void readOldChatList(String myUsername, OnReadChatList onReadChatList){
        // node = myUsername;
        DatabaseReference ref = getRootReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_CHAT_LIST).child(myUsername);

//        ref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                onReadChatList.onReadChatList(snapshot, true);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        ArrayList<ArticlesUnderTag> chatList = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        String username = item.getKey(),
                                lastMessage = item.getValue(String.class);

                        // its not related to article anyhow. just using this Class because it has to string value
                        // that is useful.
                        ArticlesUnderTag aut = new ArticlesUnderTag(username, lastMessage);
                        chatList.add(aut);
                    }
                }
                onReadChatList.onReadChatList(chatList, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadChatList.onReadChatList(chatList, true);
            }
        });
    }

    public static void readNewChatList(String myUsername, OnReadChatList onReadChatList){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(FireConstants.STR_CHAT_STATUSES).child(FireConstants.STR_NEW_MESSAGE_STATUS).child(myUsername);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                onReadChatList.onReadChatList(snapshot, true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                onReadChatList.onReadChatList(snapshot, true);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                onReadChatList.onReadChatList(snapshot, false);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTagsSnapshot(){
        reference = database.getReference().child(FireConstants.STR_TAGS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    readListener.onRead(snapshot);
                }
                else{
                    readListener.onRead(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getSuggestedTags(OnReadSuggestedTagsListener suggestedTagsListener){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_SUGGESTED_TAGS);

        Map<String, ArrayList<SuggestedTag>> suggestedTags = new HashMap<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        String tagName = item.getKey();
                        ArrayList<SuggestedTag> articleIds = new ArrayList<>();
                        if (tagName != null){
                            for (DataSnapshot articleItem : snapshot.child(tagName).getChildren()){
                                String articleId = articleItem.getKey();
                                if(articleId != null){
                                    SuggestedTag aut = articleItem.getValue(SuggestedTag.class);
                                    articleIds.add(aut);
                                }
                            }
                            suggestedTags.put(tagName, articleIds);
                        }
                    }
                }
                suggestedTagsListener.onReadSuggestedTags(suggestedTags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readArticlesUnderATag(String tagName){
        reference = database.getReference().child(FireConstants.STR_TAGS).child(tagName);
        ArrayList<ArticlesUnderTag> articlesUnderTags = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot item : snapshot.getChildren()){
                        String articleId = item.getKey();
                        assert articleId != null;
                        String articleHeadLine = snapshot.child(articleId).getValue(String.class);
                        if(!articleId.equals("article id")) {
                            articlesUnderTags.add(new ArticlesUnderTag(articleId, articleHeadLine));
                        }
                    }
                }
                articleUnderATagListener.onReadArticleUnderATag(articlesUnderTags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void readUserOverViewByUsername(String startAtOrAfter, boolean isFirst, Context context,
                                                  OnReadPeople onReadPeople){

        ArrayList<People> people = new ArrayList<>();
        Query query;

        if (isFirst){
            query = getRootReference().child(FireConstants.STR_USER_OVERVIEW).orderByChild(FireConstants.STR_USERNAME)
                    .startAt(startAtOrAfter).limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE + 1);
        }
        else {
            query = getRootReference().child(FireConstants.STR_USER_OVERVIEW).orderByChild(FireConstants.STR_USERNAME)
                    .startAfter(startAtOrAfter).limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Toast.makeText(context, snapshot.toString(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot item : snapshot.getChildren()){
                        if(item.getKey() != null && !item.getKey().equals("0a")){
                            People p = item.getValue(People.class);
                            people.add(p);
                        }
                    }
                    onReadPeople.onReadPeople(UserAccount.SUCCESS, people);
                }
                else{
                    //Toast.makeText(context, "snapshot empty", Toast.LENGTH_SHORT).show();
                    onReadPeople.onReadPeople(UserAccount.FAIL, people);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                onReadPeople.onReadPeople(UserAccount.FAIL, people);
            }
        });

    }

    public static void readUserOverViewByArticleCount(long startAtOrAfter, boolean isFirst, Context context,
                                                  OnReadPeople onReadPeople){

        // TODO
        //  Doesn't work perfectly
        //  Need to work on more
        //  Currently this function is not called, because the view is hidden

        ArrayList<People> people = new ArrayList<>();
        Query query = getRootReference().child(FireConstants.STR_USER_OVERVIEW).orderByChild(FireConstants.STR_ARTICLE_COUNT)
                .startAt(startAtOrAfter).endAt(startAtOrAfter + 1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Toast.makeText(context, snapshot.toString(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot item : snapshot.getChildren()){
                        if(item.getKey() != null && !item.getKey().equals("0a")){
                            People p = item.getValue(People.class);
                            people.add(p);
                        }
                    }
                    onReadPeople.onReadPeople(UserAccount.SUCCESS, people);
                }
                else{
                    //Toast.makeText(context, "snapshot empty", Toast.LENGTH_SHORT).show();
                    onReadPeople.onReadPeople(UserAccount.FAIL, people);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                onReadPeople.onReadPeople(UserAccount.FAIL, people);
            }
        });

    }

    public static void readUserOverViewBySearchWord(String username, OnReadPeople onReadPeople){

        ArrayList<People> people = new ArrayList<>();
        Query ref = getRootReference().child(FireConstants.STR_USER_OVERVIEW).orderByChild(FireConstants.STR_USERNAME)
                        .startAt(username).endAt(username + "\uf8ff");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item : snapshot.getChildren()){
                        if(item.getKey() != null && !item.getKey().equals("0a")){
                            people.add(item.getValue(People.class));
                        }
                    }
                    onReadPeople.onReadPeople(UserAccount.SUCCESS, people);
                }
                else{
                    onReadPeople.onReadPeople(UserAccount.FAIL, people);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                onReadPeople.onReadPeople(UserAccount.FAIL, people);
            }
        });

    }

    public static void hasMyNewMessages(String username, ExistListener existListener){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_NEW_MESSAGE_STATUS).child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    existListener.exist(true, snapshot.getChildrenCount());
                }
                else{
                    existListener.exist(false, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getMyFollowing(String username, String startAfter, boolean isFirst,
                                      OnReadArticleUnderATag onWait){
        Query query;
        ArrayList<ArticlesUnderTag> following = new ArrayList<>();
        // using 'ArticleUnderTag' because it has two string value.
        // article id = Username, headLine = Profile Name.
        if (isFirst){
            query = getRootReference().child(FireConstants.STR_ADMIN)
                    .child(FireConstants.STR_USER_PERSONAL_INFO)
                    .child(username).child(FireConstants.STR_FOLLOWING)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }
        else {
            query = getRootReference().child(FireConstants.STR_ADMIN)
                    .child(FireConstants.STR_USER_PERSONAL_INFO)
                    .child(username).child(FireConstants.STR_FOLLOWING).orderByKey().startAfter(startAfter)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        String username = item.getKey();
                        if(username != null && !username.isEmpty()){
                            String profileName = item.getValue(String.class);
                            following.add(new ArticlesUnderTag(username, profileName));
                        }
                    }
                    onWait.onReadArticleUnderATag(following);
                }
                else{
                    onWait.onReadArticleUnderATag(following);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onWait.onReadArticleUnderATag(following);
            }
        });

    }

    public static void readMyFollowingBySearchWord(String username, String searchWord, OnReadArticleUnderATag onWait){

        ArrayList<ArticlesUnderTag> following = new ArrayList<>();
        Query ref = getRootReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(username).child(FireConstants.STR_FOLLOWING).orderByKey()
                        .startAt(searchWord).endAt(searchWord + "\uf8ff");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        String username = item.getKey();
                        if(username != null && !username.isEmpty()){
                            String profileName = item.getValue(String.class);
                            following.add(new ArticlesUnderTag(username, profileName));
                        }
                    }
                    onWait.onReadArticleUnderATag(following);
                }
                else{
                    //DataModel.deb(snapshot.toString());
                    onWait.onReadArticleUnderATag(following);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onWait.onReadArticleUnderATag(following);
            }
        });

    }

    public static void getMyFollower(String username, String startAfter, boolean isFirst,
                                      OnReadArticleUnderATag onWait){
        Query query;
        ArrayList<ArticlesUnderTag> following = new ArrayList<>();
        // using 'ArticleUnderTag' because it has two string value.
        // article id = Username, headLine = Profile Name.
        if (isFirst){
            query = getRootReference().child(FireConstants.STR_ADMIN)
                    .child(FireConstants.STR_USER_PERSONAL_INFO)
                    .child(username).child(FireConstants.STR_FOLLOWER)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }
        else {
            query = getRootReference().child(FireConstants.STR_ADMIN)
                    .child(FireConstants.STR_USER_PERSONAL_INFO)
                    .child(username).child(FireConstants.STR_FOLLOWER).orderByKey().startAfter(startAfter)
                    .limitToFirst(DataModel.MAXIMUM_DATA_QUERY_FIREBASE);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        String username = item.getKey();
                        if(username != null && !username.isEmpty()){
                            String profileName = item.getValue(String.class);
                            following.add(new ArticlesUnderTag(username, profileName));
                        }
                    }
                    onWait.onReadArticleUnderATag(following);
                }
                else{
                    onWait.onReadArticleUnderATag(following);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onWait.onReadArticleUnderATag(following);
            }
        });

    }

    public static void readMyFollowerBySearchWord(String username, String searchWord, OnReadArticleUnderATag onWait){

        ArrayList<ArticlesUnderTag> following = new ArrayList<>();
        Query ref = getRootReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_USER_PERSONAL_INFO)
                .child(username).child(FireConstants.STR_FOLLOWER).orderByKey()
                .startAt(searchWord).endAt(searchWord + "\uf8ff");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        String username = item.getKey();
                        if(username != null && !username.isEmpty()){
                            String profileName = item.getValue(String.class);
                            following.add(new ArticlesUnderTag(username, profileName));
                        }
                    }
                    onWait.onReadArticleUnderATag(following);
                }
                else{
                    //DataModel.deb(snapshot.toString());
                    onWait.onReadArticleUnderATag(following);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onWait.onReadArticleUnderATag(following);
            }
        });

    }

    public static void getActivityStatus(String username, OnWaitListener waitListener){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_CHAT_STATUSES)
                .child(FireConstants.STR_ACTIVE_STATUS).child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    waitListener.onWaitCallback(DataModel.YES);
                }
                else{
                    waitListener.onWaitCallback(DataModel.NO);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static String createReportId(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_USER_REPORT)
                .child(FireConstants.STR_REPORT).push();

        return ref.getKey();
    }

    public static void readArticleEditHistory(String articleId, String ownerUsername,
                                              String privacy, OnReadArticleEditHistory onRead){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(FireConstants.STR_ARTICLE_EDIT_HISTORY).child(FireConstants.STR_ARTICLE)
                .child(articleId),
                refPrivate = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_ADMIN)
                        .child(FireConstants.STR_USER_PERSONAL_INFO).child(ownerUsername)
                        .child(FireConstants.STR_ARTICLE_EDIT_HISTORY).child(FireConstants.STR_ARTICLE)
                        .child(articleId);

        ArrayList<EditHistory> editHistories = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot editId : snapshot.getChildren()){
                        EditHistory editHistory = editId.getValue(EditHistory.class);
                        editHistories.add(editHistory);
                    }
                }
                onRead.onReadEdit(editHistories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRead.onReadEdit(editHistories);
            }
        });
        /*
        else if(privacy.equals(DataModel.STR_ONLY_ME)){
            refPrivate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot editId : snapshot.getChildren()){
                            EditHistory editHistory = editId.getValue(EditHistory.class);
                            editHistories.add(editHistory);
                        }
                    }
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot editId : snapshot.getChildren()){
                                    EditHistory editHistory = editId.getValue(EditHistory.class);
                                    editHistories.add(editHistory);
                                }
                            }
                            onRead.onReadEdit(editHistories);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            onRead.onReadEdit(editHistories);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot editId : snapshot.getChildren()){
                                    EditHistory editHistory = editId.getValue(EditHistory.class);
                                    editHistories.add(editHistory);
                                }
                            }
                            onRead.onReadEdit(editHistories);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            onRead.onReadEdit(editHistories);
                        }
                    });
                }
            });
        }
         */
    }

    public static void readAdminEmail(OnWaitListenerWithStringInfo onWait){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_ADMIN)
                .child(FireConstants.STR_ADMIN_ONLY).child(FireConstants.STR_ADMIN_EMAIL);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String email = snapshot.getValue(String.class);
                    if(email != null){
                        onWait.onWaitWithInfo(UserAccount.SUCCESS, email);
                    }
                    else{
                        onWait.onWaitWithInfo(UserAccount.FAIL, null);
                    }
                }
                else{
                    onWait.onWaitWithInfo(UserAccount.FAIL, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onWait.onWaitWithInfo(UserAccount.FAIL, null);
            }
        });
    }

    public static void readPublicPhoneNumber(String username, OnWaitListenerWithStringInfo waitListener){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FireConstants.STR_USER)
                .child(username).child(FireConstants.STR_PUBLIC_INFO).child(FireConstants.STR_EMAIL);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String phone = snapshot.getValue(String.class);
                    waitListener.onWaitWithInfo(UserAccount.SUCCESS, phone);
                }
                else{
                    waitListener.onWaitWithInfo(UserAccount.FAIL, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                waitListener.onWaitWithInfo(UserAccount.FAIL, null);
            }
        });
    }



}
