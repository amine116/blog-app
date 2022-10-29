package com.amine.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amine.blog.dialogs.EditHistoryDialog;
import com.amine.blog.dialogs.EditOpinionDialog;
import com.amine.blog.fragments.EditArticleFrag;
import com.amine.blog.interfaces.OnReadSingleOpinion;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.Article;
import com.amine.blog.model.CustomEditText;
import com.amine.blog.model.EditHistory;
import com.amine.blog.model.Opinion;
import com.amine.blog.model.ReplyTo;
import com.amine.blog.model.ReportToBlog;
import com.amine.blog.model.ReportingContext;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleDiscussion extends AppCompatActivity implements View.OnClickListener, OnReadSingleOpinion,
        MenuItem.OnMenuItemClickListener {

    private Article article;
    private LinearLayout ll;

    private final String MENU_ITEM_SELECT = "Select to reply";
    private String username, articleId, privacy;
    private ArrayList<EditHistory> editHistories = new ArrayList<>();

    private PopupMenu menu;
   // private ScrollView scrollOpinion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_discussion);

        ll = findViewById(R.id.layout_opinions);
        hideActionBar();
        getIntentData();
        makeViewInvisible();
        readArticle();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(MENU_ITEM_SELECT).setOnMenuItemClickListener(this);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void getIntentData(){

        username = getIntent().getStringExtra("USER_NAME");
        articleId = getIntent().getStringExtra("ARTICLE_ID");
        privacy = getIntent().getStringExtra("PRIVACY");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnPostTheComment){
            if(!MainActivity.isGuest){
                EditText edtOpinion = findViewById(R.id.edtOpinion);
                TextView txtChosenPortion = findViewById(R.id.txtChosenPortion);

                String strOpinion = edtOpinion.getText().toString().trim(),
                        strChosenPortion = txtChosenPortion.getText().toString();
                if(strOpinion.equals("")){
                    edtOpinion.requestFocus();
                    edtOpinion.setError("Write a opinion");
                    return;
                }
                ReplyTo replyTo = null;
                if(!strChosenPortion.isEmpty()){
                    replyTo = new ReplyTo(DataModel.STR_ARTICLE, article.getID(), "N/A",
                            article.getUsername(), strChosenPortion);
                }

                Opinion opinion = new Opinion(MainActivity.userBasicInfo.getUserName(), strOpinion,
                        new Retrieve(article.getID()).createOpinionId(), new DataModel().getCurrentMyTime(), replyTo);

                if(article.getPrivacy().equals(DataModel.STR_PUBLIC)){
                    new Save().saveMyOpinion(articleId, opinion);
                }
                else if(article.getPrivacy().equals(DataModel.STR_ONLY_ME)){
                    new Save().saveMyPrivateOpinion(articleId, opinion);
                }
                edtOpinion.setText("");
                cancelChoosingPortionOfTheArticle();
                hideKeyBoard(view);
            }
            else{
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == R.id.imgArticleMenu){
            createPopupMenu(view);
            setAuthorPopUpMenuView(menu);
            setOnClickListenerForPopUpMenu(menu);
            menu.show();
        }
        else if(view.getId() == R.id.txtCancelChoosing){

            cancelChoosingPortionOfTheArticle();
        }
        else if(view.getId() == R.id.txtChoosePortion){
            ChoosePortionOfText sa = new ChoosePortionOfText(this);
            sa.setWait((task, data) -> {
                if(task == DataModel.YES){
                    RelativeLayout layout_selectedText = findViewById(R.id.layout_chosen_portion);
                    TextView txtChosenPortion = findViewById(R.id.txtChosenPortion);
                    findViewById(R.id.txtChoosePortion).setVisibility(View.GONE);

                    layout_selectedText.setVisibility(View.VISIBLE);
                    txtChosenPortion.setText(data);
                    txtChosenPortion.setTextColor(darker(getResources().getColor(R.color.text_color)));
                }
            });
            //sa.getWindow().setAttributes(getWindowParams(sa, .8f, 0.8f));
            sa.show();
        }
    }

    private void createPopupMenu(View view){
        menu = new PopupMenu(this, view);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu_options_article, menu.getMenu());
    }

    private void cancelChoosingPortionOfTheArticle(){
        TextView txtChosenPortion = findViewById(R.id.txtChosenPortion);
        txtChosenPortion.setText("");

        findViewById(R.id.txtChoosePortion).setVisibility(View.VISIBLE);
        RelativeLayout layout_selectedText = findViewById(R.id.layout_chosen_portion);
        layout_selectedText.setVisibility(View.GONE);
    }

    private void setAuthorPopUpMenuView(PopupMenu menu){
        if (username.equals(MainActivity.userBasicInfo.getUserName())){
            menu.getMenu().findItem(R.id.menu_edit_article).setVisible(true);
            menu.getMenu().findItem(R.id.menu_delete_article).setVisible(true);
        }
        else{
            menu.getMenu().findItem(R.id.menu_edit_article).setVisible(false);
            menu.getMenu().findItem(R.id.menu_delete_article).setVisible(false);
        }
        menu.getMenu().findItem(R.id.menu_report_article).setVisible(true);
        menu.getMenu().findItem(R.id.menu_view_editHistory).setVisible(editHistories.size() > 0);
    }

    private void setOnClickListenerForPopUpMenu(PopupMenu menu) {
        menu.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();
            if(id == R.id.menu_edit_article){
                prepareForFragment();
                EditArticleFrag fr = new EditArticleFrag();
                fr.setArticle(article);
                fr.setContext(this);
                fr.setWaitListener(task -> {
                    Intent intent = new Intent(ArticleDiscussion.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
                addFragment(fr);
            }
            else if(id == R.id.menu_report_article){
                ReportingDialog rd = new ReportingDialog(this, article.getText());
                rd.setReportType(DataModel.STR_ARTICLE);

                rd.show();
            }
            else if(id == R.id.menu_view_editHistory){
                EditHistoryDialog ehd = new EditHistoryDialog(this, editHistories);
                ehd.setColorBullet(getResources().getColor(R.color.bullet_point));
                ehd.setColorQuot(getResources().getColor(R.color.quotation));
                ehd.setColorSubheading(getResources().getColor(R.color.sub_head_line));
                ehd.setTextColor(getResources().getColor(R.color.text_color));

                ehd.show();
                Window window = ehd.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

            return false;
        });
    }

    private void prepareForFragment(){
        RelativeLayout rl = findViewById(R.id.layout_article_discussion);
        FrameLayout fl = findViewById(R.id.frame_for_fragments);

        rl.setVisibility(View.GONE);
        fl.setVisibility(View.VISIBLE);
    }

    private void addFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_for_fragments, fragment)
                .commit();
    }

    private void readArticle(){

        if(privacy.equals(DataModel.STR_PUBLIC)){
            Retrieve retrieve = new Retrieve(articleId);
            retrieve.setSingleArticleListener(article -> {
                makeViewsVisible();
                ArticleDiscussion.this.article = article;
                showTheArticle();

                readEditHistory(article.getID(), article.getUsername(), article.getPrivacy());
            });
            retrieve.getSingleArticle();
        }
        else if(privacy.equals(DataModel.STR_ONLY_ME)){
            Retrieve retrieve = new Retrieve(articleId);
            retrieve.setSingleArticleListener(article -> {
                makeViewsVisible();
                ArticleDiscussion.this.article = article;
                showTheArticle();

                readEditHistory(article.getID(), article.getUsername(), article.getPrivacy());
            });
            retrieve.getSinglePrivateArticle(username);
        }


    }

    private void readEditHistory(String articleId, String ownerUsername, String privacy){
        Retrieve.readArticleEditHistory(articleId, ownerUsername, privacy, editHistories1 -> {

            editHistories.addAll(editHistories1);
            editHistories.add(new EditHistory(article.getText(), FireConstants.STR_ARTICLE,
                    articleId, article.getUsername(), article.getTime()));
        });
    }

    private void showTheArticle(){
        TextView txtProfileName = findViewById(R.id.txtProfileName),
                txtUsername = findViewById(R.id.txtUserName),
                txtHeadline = findViewById(R.id.txtHeadline),
                txtArticleText = findViewById(R.id.txtArticle);

        String s = "~ " + username;
        txtProfileName.setText(article.getNameOfOwner());
        txtUsername.setText(s);
        txtHeadline.setText(article.getHeadLine());

        DataModel.getSpannableArticle(article.getText(), this, false,
                getResources().getColor(R.color.sub_head_line),
                getResources().getColor(R.color.quotation),
                getResources().getColor(R.color.bullet_point),
                new DataModel.CallbackForSpannable() {
                    @Override
                    public void onCallback(SpannableString content) {
                        txtArticleText.setText(content);
                    }
                });
        txtArticleText.setMovementMethod(LinkMovementMethod.getInstance());

        setListeners();

        if(article.getPrivacy().equals(DataModel.STR_PUBLIC)){
            Retrieve retrieveSingleOpinion = new Retrieve(articleId);
            retrieveSingleOpinion.setOnReadSingleOpinionListener(this);
            retrieveSingleOpinion.readSingleOpinionOfTheArticle();
        }
        else if(article.getPrivacy().equals(DataModel.STR_ONLY_ME)){
            Retrieve retrieveSingleOpinion = new Retrieve(articleId);
            retrieveSingleOpinion.setOnReadSingleOpinionListener(this);
            retrieveSingleOpinion.readSinglePrivateOpinionOfTheArticle(
                    MainActivity.userBasicInfo.getUserName(), article.getID());
        }

        registerForContextMenu(txtArticleText);
    }

    private void setListeners(){
        findViewById(R.id.btnPostTheComment).setOnClickListener(this);
        findViewById(R.id.imgArticleMenu).setOnClickListener(this);
        findViewById(R.id.txtCancelChoosing).setOnClickListener(this);
        findViewById(R.id.txtChoosePortion).setOnClickListener(this);
    }

    private void makeDiscussionView(){
        RelativeLayout rl = findViewById(R.id.layout_article_discussion);
        FrameLayout fl = findViewById(R.id.frame_for_fragments);

        fl.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);

    }

    private void makeViewInvisible(){
        RelativeLayout rl = findViewById(R.id.layout_article_discussion);
        ProgressBar pBar = findViewById(R.id.progress_articleDiscussion);

        pBar.setVisibility(View.VISIBLE);
        rl.setVisibility(View.GONE);
    }

    private void makeViewsVisible(){
        RelativeLayout rl = findViewById(R.id.layout_article_discussion);
        ProgressBar pBar = findViewById(R.id.progress_articleDiscussion);

        pBar.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
    }

    private View getOpinionView(Opinion opinion){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_comment_section, null);

        TextView txtProfileName = view.findViewById(R.id.txtProfileName),
                txtOpinion = view.findViewById(R.id.txtOpinion),
                txtTime = view.findViewById(R.id.txtOpinionTime),
                txtPost = view.findViewById(R.id.txtPostButton),
                txtReply = view.findViewById(R.id.txtReply),
                txtReport = view.findViewById(R.id.txtReport),
                txtChosenPortion = view.findViewById(R.id.txtChosenPortion),
                txtReplyingTo = view.findViewById(R.id.txtReplyingTo),
                txtEdit = view.findViewById(R.id.txtEdit);

        if(isOwner(opinion.getName())){
            txtEdit.setVisibility(View.VISIBLE);
        }
        else{
            txtEdit.setVisibility(View.GONE);
        }

        EditText edtReply = view.findViewById(R.id.edtReply);
        LinearLayout layoutReply = view.findViewById(R.id.layout_reply);
        RelativeLayout layout_chosen_portion = view.findViewById(R.id.layout_chosen_portion);

        RelativeLayout rlProfilePic = view.findViewById(R.id.layout_profilePic);
        rlProfilePic.setTag(opinion.getName());

        rlProfilePic.setOnClickListener(getListenerForOpinionProfile());
        txtReport.setOnClickListener(getListenerForReport(opinion));
        txtReply.setOnClickListener(getListenerForReply(layoutReply));
        txtEdit.setOnClickListener(getListenerForEdit(txtOpinion, opinion));
        txtPost.setOnClickListener(getListenerForReplyButton(edtReply, txtPost, layoutReply,
                article.getUsername(), opinion));

        txtProfileName.setText(opinion.getName());
        DataModel.getSpannableArticle(opinion.getText(), this, false,
                getResources().getColor(R.color.sub_head_line),
                getResources().getColor(R.color.quotation),
                getResources().getColor(R.color.bullet_point), txtOpinion::setText);
        txtOpinion.setMovementMethod(LinkMovementMethod.getInstance());
        txtTime.setText(opinion.getTime().toString());

        if(opinion.getReplyTo() != null){
            layout_chosen_portion.setVisibility(View.VISIBLE);

            int brighter = brighter(getResources().getColor(R.color.partition_color)),
                    darker = darker(getResources().getColor(R.color.text_color));

            String s = "";
            if (opinion.getReplyTo().getType().equals(DataModel.STR_OPINION)){
                if(opinion.getReplyTo().getAuthorUsername().equals(opinion.getName())){
                    s = "Own opinion:";
                }
                else{
                    s = opinion.getReplyTo().getAuthorUsername() + "'s opinion:";
                }
            }
            else if (opinion.getReplyTo().getType().equals(DataModel.STR_ARTICLE)){
                s = "The paragraph:";
            }
            txtReplyingTo.setText(s);
            txtReplyingTo.setTextColor(darker);

            s = '"' + opinion.getReplyTo().getContextText() + '"';
            txtChosenPortion.setText(s);
            //txtChosenPortion.setBackgroundColor(brighter);
            txtChosenPortion.setTextColor(darker);
        }

        return view;
    }

    private View.OnClickListener getListenerForEdit(TextView txtOpinion, Opinion opinion) {

        return view -> {
            EditOpinionDialog epd = new EditOpinionDialog(ArticleDiscussion.this);
            epd.setOpinionText(opinion.getText());
            epd.setWaitListener((task, data) -> {
                opinion.setText(data);
                txtOpinion.setText(opinion.getText());
                new Save().saveMyOpinion(article.getID(), opinion);
            });
            epd.show();
        };
    }

    private int brighter(int color){
        return ColorUtils.blendARGB(color, Color.WHITE, .2f);
    }

    private int darker(int color){
        return ColorUtils.blendARGB(color, Color.BLACK, .2f);
    }

    private View.OnClickListener getListenerForReport(Opinion opinion){
        return view -> {

            if(MainActivity.isGuest){
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_SHORT).show();
                return;
            }

            ReportingDialog rd = new ReportingDialog(ArticleDiscussion.this, opinion.getText());
            rd.setReportType(DataModel.STR_OPINION);
            rd.setOpinionId(opinion.getId());
            rd.show();
        };
    }

    private View.OnClickListener getListenerForReplyButton(EditText edtReply, TextView txtPost,
                                                           LinearLayout layoutReply, String authorUsername,
                                                           Opinion opinion){
        return view -> {

            if(MainActivity.isGuest){
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_SHORT).show();
                return;
            }

            String replyText = edtReply.getText().toString().trim();
            if(!replyText.isEmpty()){
                ReplyTo replyTo = new ReplyTo(DataModel.STR_OPINION, article.getID(), opinion.getId(),
                        authorUsername, opinion.getText());


                Opinion opinion1 = new Opinion(MainActivity.userBasicInfo.getUserName(), replyText,
                        new Retrieve(article.getID()).createOpinionId(), new DataModel().getCurrentMyTime(),
                        replyTo);

                if(article.getPrivacy().equals(DataModel.STR_PUBLIC)){
                    new Save().saveMyOpinion(articleId, opinion1);
                }
                else if(article.getPrivacy().equals(DataModel.STR_ONLY_ME)){
                    new Save().saveMyPrivateOpinion(articleId, opinion1);
                }
                edtReply.setText("");
                layoutReply.setVisibility(View.GONE);
                hideKeyBoard(txtPost);
            }
        };

    }

    private void hideKeyBoard(View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private View.OnClickListener getListenerForReply(LinearLayout layoutReply){

        return view -> {

            if(MainActivity.isGuest){
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_SHORT).show();
                return;
            }

            if(layoutReply.getVisibility() == View.VISIBLE) {
                layoutReply.setVisibility(View.GONE);
            }
            else{
                layoutReply.setVisibility(View.VISIBLE);
            }
        };
    }

    private View.OnClickListener getListenerForOpinionProfile(){
        return view -> {

            if(MainActivity.isGuest){
                Toast.makeText(this, "Guest can't use this option", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = view.getTag().toString();
            Intent intent = new Intent(ArticleDiscussion.this, ProfileView.class);
            intent.putExtra("USER_NAME", username);
            startActivity(intent);
        };
    }

    @Override
    public void onReadSingleOpinion(Opinion opinion) {
        ll.addView(getOpinionView(opinion));
        //scrollOpinion.post(() -> scrollOpinion.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        if (menuItem.getTitle().equals(MENU_ITEM_SELECT)){
            ChoosePortionOfText sa = new ChoosePortionOfText(this);
            sa.setWait((task, data) -> {
                if(task == DataModel.YES){
                    RelativeLayout layout_selectedText = findViewById(R.id.layout_chosen_portion);
                    TextView txtChosenPortion = findViewById(R.id.txtChosenPortion);

                    layout_selectedText.setVisibility(View.VISIBLE);
                    txtChosenPortion.setText(data);
                    txtChosenPortion.setTextColor(darker(getResources().getColor(R.color.text_color)));
                }
            });
            //sa.getWindow().setAttributes(getWindowParams(sa, .8f, 0.8f));
            sa.show();
        }

        return false;
    }

    private class ChoosePortionOfText extends Dialog implements View.OnClickListener {

        private OnWaitListenerWithStringInfo wait;

        private EditText edtSelect;
        private int start = -1, end = -1;

        public ChoosePortionOfText(@NonNull Context context) {
            super(context);
        }

        public void setWait(OnWaitListenerWithStringInfo wait) {
            this.wait = wait;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(R.layout.choose_portion_of_text);
            initialize();
            super.onCreate(savedInstanceState);
        }

        private void initialize(){
            edtSelect = new CustomEditText(ArticleDiscussion.this);
            edtSelect.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            edtSelect.setBackgroundColor(getResources().getColor(R.color.background_color));
            edtSelect.setTextColor(getResources().getColor(R.color.text_color));
            edtSelect.setPadding(10, 10, 10, 10);

            LinearLayout ll = findViewById(R.id.layout_chooseText);
            ll.addView(edtSelect);

            TextView txtOk = findViewById(R.id.txtOk);
            txtOk.setOnClickListener(this);
            findViewById(R.id.txtClose).setOnClickListener(this);
            edtSelect.setText(article.getText());
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.txtOk){
                setStartAndEnd();
                if(start >= 0 && end >= 0 && start < end){

                    String chosenPortion = article.getText().substring(start, end - 1);
                    wait.onWaitWithInfo(DataModel.YES, chosenPortion);
                }
                dismiss();
            }
            else if(view.getId() == R.id.txtClose){
                dismiss();
            }
        }

        private void setStartAndEnd(){
            String string = edtSelect.getText().toString().trim();
            for (int i = 0; i < string.length(); i++){
                if (string.charAt(i) == DataModel.CHOSEN_TEXT_SELECTION_SIGN){
                    if(start >= 0){
                        end = i;
                        break;
                    }
                    else{
                        start = i;
                    }
                }
            }
        }
    }

    private class ReportingDialog extends Dialog implements View.OnClickListener{

        private final String post;
        private String reportType, opinionId;
        private int start = -1, end = -1;

        private CheckBox checkNudity, checkFalseInfo, checkHateSpeech, checkOffensive;
        private EditText edtSelect, edtExplanation;

        public ReportingDialog(@NonNull Context context, String post) {
            super(context);
            this.post = post;
        }

        public void setReportType(String reportType) {
            this.reportType = reportType;
        }

        public void setOpinionId(String opinionId) {
            this.opinionId = opinionId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(R.layout.reporting_dialog);
            super.onCreate(savedInstanceState);

            checkFalseInfo = findViewById(R.id.checkFalseInfo);
            checkNudity = findViewById(R.id.checkSexuality);
            checkHateSpeech = findViewById(R.id.checkHateSpeech);
            checkOffensive = findViewById(R.id.checkOffensive);

            findViewById(R.id.btnReport).setOnClickListener(this);
            findViewById(R.id.txtClose).setOnClickListener(this);
            checkNudity.setOnClickListener(this);
            checkFalseInfo.setOnClickListener(this);
            checkHateSpeech.setOnClickListener(this);
            checkOffensive.setOnClickListener(this);

            initialize();
        }

        private void initialize(){

            edtExplanation = findViewById(R.id.edtExplanation);

            edtSelect = new CustomEditText(ArticleDiscussion.this);
            edtSelect.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            edtSelect.setBackgroundColor(getResources().getColor(R.color.background_color));
            edtSelect.setTextColor(getResources().getColor(R.color.text_color));
            edtSelect.setPadding(10, 10, 10, 10);

            LinearLayout ll = findViewById(R.id.layout_chooseText);
            ll.addView(edtSelect);
            edtSelect.setText(post);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnReport){

                if(!checkNudity.isChecked() && !checkFalseInfo.isChecked() && !checkOffensive.isChecked() &&
                        !checkHateSpeech.isChecked()){
                    Toast.makeText(ArticleDiscussion.this, "Select at least one of the checkbox",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                setStartAndEnd();

                Map<String, String> contexts = new HashMap<>();

                if(checkNudity.isChecked()){
                    if(start >= 0 && end >= 0 && start < end){

                        String chosenPortion = post.substring(start, end - 1);
                        contexts.put(checkNudity.getText().toString(), chosenPortion);
                    }
                    else{
                        contexts.put(checkNudity.getText().toString(), "N/A");
                    }
                }
                if(checkFalseInfo.isChecked()){
                    if(start >= 0 && end >= 0 && start < end){

                        String chosenPortion = post.substring(start, end - 1);
                        contexts.put(checkFalseInfo.getText().toString(), chosenPortion);
                    }
                    else {
                        contexts.put(checkFalseInfo.getText().toString(), "N/A");
                    }
                }
                if(checkHateSpeech.isChecked()){
                    if(start >= 0 && end >= 0 && start < end){

                        String chosenPortion = post.substring(start, end - 1);
                        contexts.put(checkHateSpeech.getText().toString(), chosenPortion);
                    }
                    else{
                        contexts.put(checkHateSpeech.getText().toString(), "N/A");
                    }
                }
                if(checkOffensive.isChecked()){
                    if(start >= 0 && end >= 0 && start < end){

                        String chosenPortion = post.substring(start, end - 1);
                        contexts.put(checkOffensive.getText().toString(), chosenPortion);
                    }
                    else{
                        contexts.put(checkOffensive.getText().toString(), "N/A");
                    }
                }
                String explanation = edtExplanation.getText().toString().trim();
                ReportingContext reportingContext = new ReportingContext(contexts);
                if(reportType.equals(DataModel.STR_ARTICLE)){
                    opinionId = "N/A";
                }

                ReportToBlog reportToBlog = new ReportToBlog(MainActivity.userBasicInfo.getUserName(), articleId,
                        opinionId, Retrieve.createReportId(), reportType, explanation, new DataModel().getCurrentMyTime(),
                        reportingContext);

                Save.saveReport(reportToBlog);
                dismiss();
            }
            else if(view.getId() == R.id.txtClose){
                dismiss();
            }
            else if(view.getId() == R.id.checkSexuality){
                checkOffensive.setChecked(false);
                checkFalseInfo.setChecked(false);
                checkHateSpeech.setChecked(false);
            }
            else if(view.getId() == R.id.checkHateSpeech){
                checkOffensive.setChecked(false);
                checkFalseInfo.setChecked(false);
                checkNudity.setChecked(false);
            }
            else if(view.getId() == R.id.checkFalseInfo){
                checkOffensive.setChecked(false);
                checkNudity.setChecked(false);
                checkHateSpeech.setChecked(false);
            }
            else if(view.getId() == R.id.checkOffensive){
                checkNudity.setChecked(false);
                checkFalseInfo.setChecked(false);
                checkHateSpeech.setChecked(false);
            }
        }

        private void setStartAndEnd(){
            String string = edtSelect.getText().toString().trim();
            for (int i = 0; i < string.length(); i++){
                if (string.charAt(i) == DataModel.CHOSEN_TEXT_SELECTION_SIGN){
                    if(start >= 0){
                        end = i;
                        break;
                    }
                    else{
                        start = i;
                    }
                }
            }
        }
    }

    private boolean isOwner(String ownerUsername){
        String myEmail = Retrieve.getSignedInUserEmail();
        if(myEmail == null) return false;
        else{
            return DataModel.getUserNameFromEmail(myEmail).equals(ownerUsername);
        }
    }

    /*
    private WindowManager.LayoutParams getWindowParams(Dialog dialog, double width, double height){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * width);
        int dialogWindowHeight = (int) (displayHeight * height);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        return layoutParams;
    }
     */
}