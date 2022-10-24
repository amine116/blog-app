package com.amine.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DrawableMarginSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amine.blog.fragments.CreateAccFrag1;
import com.amine.blog.fragments.CreateAccFrag2;
import com.amine.blog.fragments.EditProfileFrag;
import com.amine.blog.fragments.FollowingFrag;
import com.amine.blog.fragments.PeopleFrag;
import com.amine.blog.fragments.SignInFrag;
import com.amine.blog.fragments.SignInFrag2;
import com.amine.blog.fragments.WriteArticleAddTagFrag;
import com.amine.blog.fragments.WriteArticleFrag;
import com.amine.blog.interfaces.CallbackForFr2;
import com.amine.blog.interfaces.ExistListener;
import com.amine.blog.interfaces.OnReadListener;
import com.amine.blog.interfaces.OnReadUserBasicInfoListener;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.Article;
import com.amine.blog.model.MyPair;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.repositories.SignInFile;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CallbackForFr2,
        OnReadUserBasicInfoListener, OnReadListener, OnWaitListener, OnWaitListenerWithStringInfo,
        ExistListener {

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static UserBasicInfo userBasicInfo;
    public static boolean isGuest;
    private final int runThreadFor = 30;
    private boolean isMessageAnimationRunning = false, isActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        //testingDrawableMargin();
        readMyBasicInfo();

    }

    private void testingDrawableMargin(){
        String s = "Hey dear, how are you? let me tell you some important points-" +
                "<Never insult anyone.<May be they will be your friend one day.>Of course it is possible. just try" +
                "for a while, you will see the inevitable reality of human psychology.<>" +
                "<You might need him one day>><Trust everyone. <Doing an action cross check first.><Don't depend.>>" +
                "<Never do the wrong things.><Always smile.> Now this is the time to flourish friendship, right?";

        DataModel.getBulletAdded2(s, (articleText, bulletPoints) -> {
            SpannableString tempSpan = new SpannableString(articleText);
            for(int i = 0; i < bulletPoints.size(); i++){

                if(bulletPoints.get(i).getFirst() < bulletPoints.get(i).getSecond() + 1){
                    tempSpan.setSpan(new DrawableMarginSpan(ContextCompat.getDrawable(MainActivity.this,
                                    R.drawable.round_shape_green), 15),
                            bulletPoints.get(i).getFirst(), bulletPoints.get(i).getSecond() + 1,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE);


                }
            }
        });
    }

    private void readMyBasicInfo(){
        makeViewsInvisible();
        Retrieve retrieve = new Retrieve("false");
        retrieve.setContext(this);
        retrieve.setOnListenerForReadingUserBasicInfo(this);
        retrieve.getSignedInUserBasicInfo();
    }

    private void makeViewsInvisible(){
        ProgressBar pBar = findViewById(R.id.progress_main);
        ScrollView sv = findViewById(R.id.scroll_info);
        sv.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }
    private void makeViewsVisible(){
        ProgressBar pBar = findViewById(R.id.progress_main);
        ScrollView sv = findViewById(R.id.scroll_info);
        pBar.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);
    }

    private void initializeListeners(){
        findViewById(R.id.ic_menu).setOnClickListener(this);
        findViewById(R.id.txtUtilityButton).setOnClickListener(this);
        findViewById(R.id.layout_write_article).setOnClickListener(this);
        findViewById(R.id.imgHome).setOnClickListener(this);
        findViewById(R.id.layout_signOut).setOnClickListener(this);
        findViewById(R.id.imgMyProfile).setOnClickListener(this);
        findViewById(R.id.layout_browse_article).setOnClickListener(this);
        findViewById(R.id.imgChat).setOnClickListener(this);
        findViewById(R.id.imgPeople).setOnClickListener(this);
        findViewById(R.id.layout_go_following).setOnClickListener(this);
        findViewById(R.id.layout_editProfile).setOnClickListener(this);
        findViewById(R.id.layout_suggestedTags).setOnClickListener(this);

    }

    private void removeAllFragment(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.ic_menu){
            // Open Drawer layout
            DrawerLayout dl = findViewById(R.id.drawerLayout);
            dl.openDrawer(GravityCompat.START);
            // Actions and visibility of buttons of drawer layout
        }
        // Commented where menu items would be hidden by click
        /*
        else if(view.getId() == R.id.txtUtilityButton){
            RelativeLayout rl = findViewById(R.id.layout_utility_writeArticle),
                    rlCAc = findViewById(R.id.layout_createAcc);
            TextView tv = findViewById(R.id.txtUtilityButton);


            Drawable dUp = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_drop_up),
                    dDown = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_drop_up);



            if(rl.getVisibility() == View.GONE) {
                rl.setVisibility(View.VISIBLE);
                rlCAc.setVisibility(View.VISIBLE);

                tv.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_arrow_drop_up, 0);
            }
            else{
                rl.setVisibility(View.GONE);
                rlCAc.setVisibility(View.GONE);
                tv.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_arrow_drop_down, 0);
            }


        }
         */

        else if(view.getId() == R.id.imgHome){
            removeAllFragment();
            makeViewsInvisible();
            readMyBasicInfo();
        }

        else if(view.getId() == R.id.layout_write_article){

            if(!isGuest){
                DrawerLayout dl = findViewById(R.id.drawerLayout);
                dl.close();

                prepareForFragment();
                WriteArticleFrag fr = new WriteArticleFrag();
                fr.setContext(this);
                fr.setOnWaitListenerWithStringArrayInfo((task, info) -> {
                    if(task == DataModel.MOVE_TO_ADD_TAG_FRAGMENT){
                        readTags(info);
                    }
                });
                addFragmentToTheFrameLayout(fr);
            }
            else{
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_LONG).show();
            }
        }

        else if(view.getId() == R.id.layout_signOut){

            DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
                if (i == DialogInterface.BUTTON_POSITIVE){
                    DrawerLayout dl = findViewById(R.id.drawerLayout);
                    dl.close();
                    signOut();
                }
                dialogInterface.dismiss();
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Are you sure to Sign out?")
                    .setPositiveButton("Yes", listener)
                    .setNegativeButton("No", listener);
            builder.show();
        }

        else if(view.getId() == R.id.imgMyProfile){
            if(!isGuest){
                Intent intent = new Intent(MainActivity.this, ProfileView.class);
                intent.putExtra("USER_NAME", userBasicInfo.getUserName());
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_LONG).show();
            }
        }

        else if(view.getId() == R.id.layout_browse_article){
            Intent intent = new Intent(MainActivity.this, TagWiseArticleActivity.class);
            startActivity(intent);
        }

        else if(view.getId() == R.id.imgChat){

            if(!isGuest){

                isMessageAnimationRunning = false;
                TextView imgChat = findViewById(R.id.imgChat);
                imgChat.setText("");

                //Save.removeNewMessages(userBasicInfo.getUserName());
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("INTENT_TYPE", ChatActivity.CHAT_LIST);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_LONG).show();
            }
        }

        else if(view.getId() == R.id.imgPeople){
            if(!isGuest){
                prepareForFragment();
                PeopleFrag peopleFrag = new PeopleFrag();
                peopleFrag.setContext(this);
                peopleFrag.setOnWaitWithInfoListener(this);
                addFragmentToTheFrameLayout(peopleFrag);
            }
            else{
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_LONG).show();
            }
        }

        else if(view.getId() == R.id.layout_go_following){
            if(!isGuest){
                DrawerLayout dl = findViewById(R.id.drawerLayout);
                dl.close();

                prepareForFragment();
                FollowingFrag followingFrag = new FollowingFrag();
                followingFrag.setContext(this);
                followingFrag.setOnWaitWithInfoListener(this);
                addFragmentToTheFrameLayout(followingFrag);
            }
            else{
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_LONG).show();
            }
        }

        else if(view.getId() == R.id.layout_editProfile){
            if(!isGuest){
                DrawerLayout dr = findViewById(R.id.drawerLayout);
                dr.close();

                prepareForFragment();
                EditProfileFrag fr = new EditProfileFrag();
                fr.setContext(this);
                fr.setOnWaitListener(this);
                addFragmentToTheFrameLayout(fr);
            }
            else{
                Toast.makeText(this, "Guest user can't use this option", Toast.LENGTH_SHORT).show();
            }
        }

        else if(view.getId() == R.id.layout_suggestedTags){
            Intent i = new Intent(MainActivity.this, ApproveTagsActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        if(fm == null) fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentById(R.id.activity_main_frameLayout);
        if(fragment != null){
            ft.remove(fragment);
            //fm.popBackStack();
            ft.commit();

            // Go to home if the last fragment is removed
            /*
            ft = getSupportFragmentManager().beginTransaction();
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.activity_main_frameLayout);
            if(f == null){
                makeViewsInvisible();
                readArticles();
            }
             */

        }
        else{
            Save.activeStatus(userBasicInfo.getUserName(), false);
            isActive = false;
            super.onBackPressed();
        }
    }

    private void showArticles(ArrayList<Article> articles){

        ScrollView sv = findViewById(R.id.scroll_info);
        FrameLayout fl = findViewById(R.id.activity_main_frameLayout);

        fl.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);


        LinearLayout ll = findViewById(R.id.layout_info);
        ll.removeAllViews();

        for(int i = 0; i < articles.size(); i++){

            View view = getView(articles.get(i), i, articles.size());
            ll.addView(view, 0);


            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20));
            v.setBackgroundColor(getResources().getColor(R.color.partition_color));
            ll.addView(v);
        }
    }

    private View getView(Article article, int i, int size){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_article_view, null);

        TextView txtName = view.findViewById(R.id.txt_model_article_name),
                txtText = view.findViewById(R.id.txt_model_article_text),
                txtTime = view.findViewById(R.id.txt_model_article_time),
                txtTag1 = view.findViewById(R.id.txt_model_article_tag1),
                txtTag2 = view.findViewById(R.id.txt_model_article_tag2),
                txtTag3 = view.findViewById(R.id.txt_model_article_tag3),
                txtTag4 = view.findViewById(R.id.txt_model_article_tag4),
                txtTag = view.findViewById(R.id.txt_model_article_tag),
                txtNumberOfLikes = view.findViewById(R.id.txt_model_article_makeFavouriteButton),
                txtHeadLine = view.findViewById(R.id.txt_model_article_headLine),
                txtOpinion = view.findViewById(R.id.txt_model_article_opinionButton),
                txtShowMore = view.findViewById(R.id.txtShowMore),
                txtSpread = view.findViewById(R.id.txt_model_article_shareButton);

        RelativeLayout articleLayout = view.findViewById(R.id.layout_model_article_text);

        txtOpinion.setOnClickListener(getListenerForArticleDiscussion(
                article.getID(), article.getUsername(), article.getPrivacy()));
        articleLayout.setOnClickListener(getListenerForArticleDiscussion(
                article.getID(), article.getUsername(), article.getPrivacy()));


        txtName.setTag(article.getUsername());
        txtName.setOnClickListener(getListenerForProfile());

        String s = article.getAuthor();
        SpannableString content = new SpannableString(s);
        content.setSpan(new UnderlineSpan(), DataModel.STR_AUTHOR.length() + 3, s.length(), 0);
        txtName.setText(content);

        if(article.getText().length() > 200){

            DataModel.getSpannableArticle(article.getText().substring(0, 200), this,true,
                    getResources().getColor(R.color.partition_color),
                    getResources().getColor(R.color.gray),
                    getResources().getColor(R.color.bullet_point),
                    new DataModel.CallbackForSpannable() {
                        @Override
                        public void onCallback(SpannableString content) {
                            txtText.setText(content);
                        }
                    });
            txtShowMore.setVisibility(View.VISIBLE);
        }
        else{

            DataModel.getSpannableArticle(article.getText(), this, false,
                    getResources().getColor(R.color.partition_color),
                    getResources().getColor(R.color.gray),
                    getResources().getColor(R.color.bullet_point),
                    new DataModel.CallbackForSpannable() {
                        @Override
                        public void onCallback(SpannableString content) {
                            txtText.setText(content);
                        }
                    });
        }

        s = article.getTime().toString();

        txtTime.setText(s);
        s = article.getNumberOfLikes() + "";
        if(article.getNumberOfLikes() > 0) txtNumberOfLikes.setText(s);
        txtHeadLine.setText(article.getHeadLine());

        if(article.getTags().size() > 0){
            txtTag.setVisibility(View.VISIBLE);
        }

        for(int j = 0; j < article.getTags().size(); j++){
            if(j == 0){
                s = article.getTags().get(j) + " ";
                txtTag1.setText(s);
                txtTag1.setVisibility(View.VISIBLE);
                txtTag1.setOnClickListener(getListenerForTags(txtTag1));
            }
            if(j == 1){
                s = article.getTags().get(j) + " ";
                txtTag2.setText(s);
                txtTag2.setVisibility(View.VISIBLE);
                txtTag2.setOnClickListener(getListenerForTags(txtTag2));
            }
            if(j == 2){
                s = article.getTags().get(j) + " ";
                txtTag3.setText(s);
                txtTag3.setVisibility(View.VISIBLE);
                txtTag3.setOnClickListener(getListenerForTags(txtTag3));
            }
            if(j == 3){
                s = article.getTags().get(j);
                txtTag4.setText(s);
                txtTag4.setVisibility(View.VISIBLE);
            }
        }

        txtName.setTextColor(getResources().getColor(R.color.text_color));
        txtText.setTextColor(getResources().getColor(R.color.text_color));
        txtTime.setTextColor(getResources().getColor(R.color.text_color));

        Retrieve retrieve = new Retrieve(userBasicInfo.getUserName());
        retrieve.setOnWaitListener(task -> {
            txtNumberOfLikes.setTag(article.getID());
            txtNumberOfLikes.setId(i + size);
            if(task == DataModel.YES){
                txtNumberOfLikes.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_favorite_red, 0, 0, 0);
            }
            else{
                txtNumberOfLikes.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_favorite, 0, 0, 0);
            }
            txtNumberOfLikes.setOnClickListener(getListenerForLikeButton());
        });
        retrieve.isFavouriteArticle(article.getID());

        view.setId(i);

        return view;

        /*
        two view's id are set
        1. view = i;
        2. txtNumberOfLikes = (i + size) // where size = articles.size()
         */
    }

    private View.OnClickListener getListenerForTags(TextView txtTag){
        //  TODO
        //   Doesn't work :(
        return view -> {
            String tag = txtTag.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, TagWiseArticleActivity.class);
            intent.putExtra("INTENT_TYPE", DataModel.STR_CLICKED);
            intent.putExtra("TAG", tag);
            startActivity(intent);
        };
    }

    private View.OnClickListener getListenerForArticleDiscussion(
            String articleId, String username, String privacy ){
        return view -> {
            Intent intent = new Intent(MainActivity.this, ArticleDiscussion.class);
            intent.putExtra("USER_NAME", username);
            intent.putExtra("ARTICLE_ID", articleId);
            intent.putExtra("PRIVACY", privacy);
            startActivity(intent);
        };
    }

    private View.OnClickListener getListenerForLikeButton(){
        return view -> {
            String articleId = view.getTag().toString();
            TextView txtNumberOfLikes = findViewById(view.getId());

            Save save = new Save();

            Retrieve retrieve = new Retrieve(userBasicInfo.getUserName());
            retrieve.setOnWaitListener(task -> {

                if(task == DataModel.YES){
                    txtNumberOfLikes.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_favorite, 0, 0, 0);

                    save.removeMyFavArticle(userBasicInfo.getUserName(), articleId);

                    String s = txtNumberOfLikes.getText().toString();
                    if(!s.equals("")) {
                        int min = Math.max(Integer.parseInt(s) - 1, 0);
                        if(min == 0) s = "";
                        else s = min + "";
                        txtNumberOfLikes.setText(s);
                    }
                }
                else{
                    txtNumberOfLikes.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_favorite_red, 0, 0, 0);

                    save.saveMyFavArticle(userBasicInfo.getUserName(), articleId);
                    String s = txtNumberOfLikes.getText().toString();
                    if(s.equals("")) {
                        s = "1";
                    }
                    else{
                        s = (Integer.parseInt(s) + 1) + "";
                    }
                    txtNumberOfLikes.setText(s);
                }
            });
            retrieve.isFavouriteArticle(articleId);
        };
    }

    private View.OnClickListener getListenerForProfile(){
        return view -> {
            String username = view.getTag().toString();
            Intent intent = new Intent(MainActivity.this, ProfileView.class);
            intent.putExtra("USER_NAME", username);
            startActivity(intent);
        };
    }

    private void prepareForFragment(){

        ProgressBar pBar = findViewById(R.id.progress_main);
        ScrollView sv = findViewById(R.id.scroll_info);
        FrameLayout fl = findViewById(R.id.activity_main_frameLayout);

        pBar.setVisibility(View.GONE);
        sv.setVisibility(View.GONE);
        fl.setVisibility(View.VISIBLE);

    }

    private void addFragmentToTheFrameLayout(Fragment fragment){
        if(fm == null) fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.activity_main_frameLayout, fragment);
        ft.commit();
    }

    @Override
    public void callback(String email, String userName, String password, String name,
                         String university, String profession) {
        CreateAccFrag2 cf = new CreateAccFrag2(email, userName, password, name, university, profession);
        cf.setContext(this);
        cf.addWaitListener(this);
        addFragmentToTheFrameLayout(cf);
    }

    @Override
    public void onReadBasicInfo(UserBasicInfo userBasicInfo, boolean isSignedIn) {
        MainActivity.userBasicInfo = userBasicInfo;
        //printUserBasicInfo(userBasicInfo);

        if(isSignedIn){
            if(!userBasicInfo.getUserName().equals(FireConstants.STR_GUEST_USER_USERNAME)){
                getMyNewMessageStatus();
                Save.activeStatus(userBasicInfo.getUserName(), true);
                makeAdminView();
            }
            initializeListeners();
            readArticles();
        }
        else{
            goToSignInPage();
        }
    }

    private void makeAdminView(){

        Retrieve.readAdminEmail((task, data) -> {
            if(task == UserAccount.SUCCESS && data != null && data.equals(Retrieve.getSignedInUserEmail())){
                findViewById(R.id.layout_admin).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.layout_admin).setVisibility(View.GONE);
            }
        });
    }

    private void getMyNewMessageStatus(){
        Retrieve.hasMyNewMessages(userBasicInfo.getUserName(), this);
    }

    private void goToSignInPage(){
        prepareForFragment();
        SignInFrag signInFrag = new SignInFrag();
        signInFrag.addWaitListener(this);
        addFragmentToTheFrameLayout(signInFrag);
    }

    private void readArticles(){

        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnReadListener(this);
        retrieve.getArticleList();

    }

    private void readTags(ArrayList<String> info){
        Retrieve retrieve = new Retrieve(FireConstants.STR_TAGS);
        retrieve.setOnListerForReadingTags(tagList -> {
            WriteArticleAddTagFrag fr = new WriteArticleAddTagFrag();
            fr.setHeadLine(info.get(0));
            fr.setArticleText(info.get(1));
            fr.setPrivacy(info.get(2));
            fr.setContext(MainActivity.this);
            fr.setOnWaitListener(MainActivity.this);
            fr.setTagList(tagList);
            addFragmentToTheFrameLayout(fr);
        });

        ProgressBar pBar = findViewById(R.id.progress_main);
        ScrollView sv = findViewById(R.id.scroll_info);
        sv.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);

        retrieve.getTagList(); // getTagList() method will trigger the 'WriteArticleFrag.java' to be open.
    }

    @Override
    public void onRead(DataSnapshot snapshot) {

        ArrayList<Article> articles = new ArrayList<>();

        if(snapshot != null){
            for(DataSnapshot item : snapshot.getChildren()){
                if(item.getKey() != null){
                    Article article = new DataModel().formArticle(snapshot.child(item.getKey()));
                    articles.add(article);

                }
            }
        }
        makeViewsVisible();
        showArticles(articles);
    }

    @Override
    public void onWaitCallback(int task) {
        if(task == DataModel.MOVE_TO_MAIN_ACTIVITY_HOME){

            removeAllFragment();
            makeViewsInvisible();
            readMyBasicInfo();
        }
        else if(task == DataModel.MOVE_TO_CREATE_ACCOUNT_FRAGMENT){
            prepareForFragment();
            addFragmentToTheFrameLayout(new CreateAccFrag1(this));
            /*
        Or above line could be written like this:
        FrCreateAcc1 frCreateAcc1 = new FrCreateAcc1(new CallbackForFr2() {
            @Override
            public void callback(String email, String userName, String password, String name, String university) {

            }
        });
         */
        }
        else if(task == DataModel.MOVE_TO_SIGN_IN_PAGE_2){
            prepareForFragment();
            SignInFrag2 fr = new SignInFrag2();
            fr.setWaitListener(this);
            fr.setContext(this);
            addFragmentToTheFrameLayout(fr);
        }
    }

    private void signOut(){
        Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show();
        UserAccount.signOut(userBasicInfo.getUserName());
        userBasicInfo = null;
        new SignInFile(this).deleteFile();
        goToSignInPage();
    }

    @Override
    public void onWaitWithInfo(int task, String data) {
        if(task == DataModel.MOVE_TO_PROFILE_ACTIVITY){
            Intent intent = new Intent(MainActivity.this, ProfileView.class);
            intent.putExtra("USER_NAME", data);
            startActivity(intent);
        }
    }

    @Override
    public void exist(boolean isExist, long messageCount) {
        if(isExist){
            TextView imgChat = findViewById(R.id.imgChat);
            String s = messageCount + "";
            imgChat.setText(s);
            animateChatIcon();
        }
    }

    private void animateChatIcon(){

        isMessageAnimationRunning = true;

        new Thread(){
            @Override
            public void run() {
                int limit = runThreadFor;

                while (limit-- > 0 && isMessageAnimationRunning){
                    TextView imgChat = findViewById(R.id.imgChat);

                    runOnUiThread(() ->
                            imgChat.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_chat_red, 0, 0, 0));

                    try {
                        sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> imgChat.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_chat, 0, 0, 0));

                    try {
                        sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        if(!isActive){
            Save.activeStatus(userBasicInfo.getUserName(), false);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(userBasicInfo != null){
            Save.activeStatus(userBasicInfo.getUserName(), true);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Save.activeStatus(userBasicInfo.getUserName(), false);
        super.onDestroy();
    }
}