package com.amine.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
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
import com.amine.blog.fragments.RecoverAccountFrag;
import com.amine.blog.fragments.SignInFrag;
import com.amine.blog.fragments.SignInFrag2;
import com.amine.blog.fragments.UpdatePasswordFrag;
import com.amine.blog.fragments.WriteArticleAddTagFrag;
import com.amine.blog.fragments.WriteArticleFrag;
import com.amine.blog.interfaces.CallbackForFr2;
import com.amine.blog.interfaces.ExistListener;
import com.amine.blog.interfaces.OnReadArticleListener;
import com.amine.blog.interfaces.OnReadListener;
import com.amine.blog.interfaces.OnReadUserBasicInfoListener;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.Article;
import com.amine.blog.model.RecentArticle;
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
        OnReadUserBasicInfoListener, OnWaitListener, OnWaitListenerWithStringInfo,
        ExistListener, OnReadArticleListener {

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static UserBasicInfo userBasicInfo;
    public static boolean isGuest;
    private final int runThreadFor = 30, MAXIMUM_NUMBER_OF_FRAGMENT = 15;
    private boolean isMessageAnimationRunning = false, isActive = true;
    private long lastReadArticleTimeInMill = 1;

    private int currentFragment = -1;
    private final Fragment[] fragments = new Fragment[MAXIMUM_NUMBER_OF_FRAGMENT];

    private ProgressBar moreArticleReadInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        //testingDrawableMargin();
        getReadingMoreArticleProgress();
        readMyBasicInfo();

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
        findViewById(R.id.layout_privacyAndSecurity).setOnClickListener(this);

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
        else if(view.getId() == R.id.layout_privacyAndSecurity){

            DrawerLayout dr = findViewById(R.id.drawerLayout);
            dr.close();

            if(isGuest){
                Toast.makeText(this, "Guest user can't use this option", Toast.LENGTH_SHORT).show();
            }
            else{
                prepareForFragment();
                UpdatePasswordFrag upf = new UpdatePasswordFrag();
                upf.setMyUsername(userBasicInfo.getUserName());
                upf.setContext(this);
                upf.setOnWaitListener(this);
                addFragmentToTheFrameLayout(upf);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        if(fm == null) fm = getSupportFragmentManager();
//        ft = fm.beginTransaction();
//        Fragment fragment = fm.findFragmentById(R.id.activity_main_frameLayout);
//        if(fragment != null){
//            ft.remove(fragment);
//            //fm.popBackStack();
//            ft.commit();
//
//            // Go to home if the last fragment is removed
//            /*
//            ft = getSupportFragmentManager().beginTransaction();
//            Fragment f = getSupportFragmentManager().findFragmentById(R.id.activity_main_frameLayout);
//            if(f == null){
//                makeViewsInvisible();
//                readArticles();
//            }
//             */
//
//        }
//        else{
//            Save.activeStatus(userBasicInfo.getUserName(), false);
//            isActive = false;
//            super.onBackPressed();
//        }

        if(!removeCurrentFragment()){
            Save.activeStatus(userBasicInfo.getUserName(), false);
            isActive = false;
            super.onBackPressed();
        }
    }

    private boolean removeCurrentFragment(){

        if(currentFragment < 0){
            return false;
        }
        else{
            if(fm == null) fm = getSupportFragmentManager();
            ft = fm.beginTransaction();

            ft.remove(fragments[currentFragment--]);
            ft.commit();
            if(currentFragment < 0){
                readMyBasicInfo();
            }
            return true;
        }
    }

    private void showArticles(ArrayList<RecentArticle> articles){

        ScrollView sv = findViewById(R.id.scroll_info);
        FrameLayout fl = findViewById(R.id.activity_main_frameLayout);

        fl.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);


        LinearLayout ll = findViewById(R.id.layout_info);
        //ll.removeAllViews();
        ll.removeView(moreArticleReadInProgress);

        for(int i = 0; i < articles.size(); i++){

            View view = getView(articles.get(i), i, articles.size());
            ll.addView(view);

            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20));
            v.setBackgroundColor(getResources().getColor(R.color.partition_color));
            ll.addView(v);
        }

        View v = getMoreArticleView();
        ll.addView(v);
        v.setOnClickListener(view -> {
            ll.removeView(v);
            ll.addView(moreArticleReadInProgress);
            if (lastReadArticleTimeInMill != 1){
                Retrieve.getRecentArticleList(MainActivity.this, lastReadArticleTimeInMill, false);
            }
            else{
                ll.removeView(moreArticleReadInProgress);
            }
        });

    }

    private void getReadingMoreArticleProgress(){
        moreArticleReadInProgress = new ProgressBar(this);
        moreArticleReadInProgress.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 100));
    }

    private View getMoreArticleView(){
        TextView v = new TextView(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
        v.setTextColor(getResources().getColor(R.color.partition_color));
        v.setGravity(Gravity.CENTER);
        v.setPadding(0, 0, 0, 20);
        String s = "More articles...";
        v.setText(s);

        return v;
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

        if(article.getText().length() > DataModel.ARTICLE_SIZE_ON_HOME){

            DataModel.getSpannableArticle(article.getText().substring(
                    0, DataModel.ARTICLE_SIZE_ON_HOME), this,true,
                    getResources().getColor(R.color.partition_color),
                    getResources().getColor(R.color.gray),
                    getResources().getColor(R.color.bullet_point),
                    txtText::setText);
            txtShowMore.setVisibility(View.VISIBLE);
        }
        else{

            DataModel.getSpannableArticle(article.getText(), this, false,
                    getResources().getColor(R.color.partition_color),
                    getResources().getColor(R.color.gray),
                    getResources().getColor(R.color.bullet_point),
                    txtText::setText);

            txtShowMore.setVisibility(View.GONE);
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
        fragments[++currentFragment] = fragment;
    }

    @Override
    public void callback(String email, String userName, String password, String name,
                         String university, String profession, String countryDialCode) {
        CreateAccFrag2 cf = new CreateAccFrag2(email, userName, password, name, university,
                profession, countryDialCode);
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

        Retrieve.getLastArticleTimeInMill((task, data) -> {
            //Toast.makeText(this, data + "", Toast.LENGTH_SHORT).show();
            if(task == UserAccount.SUCCESS){
                long timeInMill = Long.parseLong(data);
                Retrieve.getRecentArticleList(MainActivity.this, timeInMill, true);
            }

        });

        //Retrieve.getFirstRecentArticles(this);
    }

    @Override
    public void onReadArticle(ArrayList<RecentArticle> articles, int task) {
        if (articles.size() > 0){
            lastReadArticleTimeInMill = articles.get(articles.size() - 1).getTimeInMill();
        }
        makeViewsVisible();
        showArticles(articles);

        //Save.reSaveRecentArticle(articles, this);
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
    public void onWaitCallback(int task) {
        if(task == DataModel.MOVE_TO_MAIN_ACTIVITY_HOME){

            removeAllFragment();
            makeViewsInvisible();
            readMyBasicInfo();
        }
        else if(task == DataModel.MOVE_TO_CREATE_ACCOUNT_FRAGMENT){
            prepareForFragment();
            CreateAccFrag1 caf = new CreateAccFrag1(this);
            caf.setContext(this);
            addFragmentToTheFrameLayout(caf);
            /*
        Or above line could be written like this:
        FrCreateAcc1 frCreateAcc1 = new FrCreateAcc1(new CallbackForFr2() {
            @Override
            public void callback(String email, String userName, String password, String name, String university) {

            }
        });
         */
        }
        else if(task == DataModel.STR_MOVE_TO_SIGN_IN_PAGE){
            goToSignInPage();
        }
        else if(task == DataModel.MOVE_TO_SIGN_IN_PAGE_2){
            prepareForFragment();
            SignInFrag2 fr = new SignInFrag2();
            fr.setWaitListener(this);
            fr.setContext(this);
            addFragmentToTheFrameLayout(fr);
        }
        else if(task == DataModel.MOVE_TO_RECOVER_ACCOUNT){
            prepareForFragment();
            RecoverAccountFrag raf = new RecoverAccountFrag();
            raf.setActivity(this);
            raf.setContext(this);
            raf.setWaitListener(this);
            addFragmentToTheFrameLayout(raf);
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