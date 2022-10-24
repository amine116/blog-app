package com.amine.blog.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.amine.blog.R;
import com.amine.blog.model.Article;
import com.amine.blog.model.ArticleTag;
import com.amine.blog.model.HyperLink;
import com.amine.blog.model.MyPair;
import com.amine.blog.model.MyTime;
import com.amine.blog.model.Opinion;
import com.amine.blog.repositories.FireConstants;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class DataModel {

    // public static values
    // Values for this class are going to be up to 10
    public static final int MOVE_TO_MAIN_ACTIVITY_HOME = 1, MOVE_TO_CREATE_ACCOUNT_FRAGMENT = 2,
            MOVE_TO_SIGN_IN_PAGE_2 = 3, YES = 4, NO = 5, MOVE_TO_PROFILE_ACTIVITY = 6,
            MOVE_TO_ADD_TAG_FRAGMENT = 7;

    // Values that doesn't related to above condition
    public static final int SUB_HEADING_TEXT_SIZE = 50;
    public static final int QUOTATION_TEXT_SIZE = 50;

    public static final String STR_CLICKED = "clicked", STR_AUTHOR = "Author", STR_PUBLIC = "public",
            STR_ONLY_ME = "only me", STR_DISMISS = "dismiss", STR_CREATE_TAG = "create tag",
            // TODO should delete this admin email. find other way to do this.
            STR_ARTICLE = FireConstants.STR_ARTICLE, STR_OPINION = FireConstants.STR_OPINIONS;

    public static final char SUB_HEAD_LINE_SIGN = '*', QUOTATION_SIGN = '"', HYPER_LINK_SIGN = '~',
            LEFT_BRACKET = '[', RIGHT_BRACKET = ']', HIGHLIGHT_SIGN = '#',
            CHOSEN_TEXT_SELECTION_SIGN = '/', BULLET_POINT_LEFT_SIGN = '<', BULLET_POINT_RIGHT_SIGN = '>';

    private String searchWord;

    public DataModel(){}

    public void setSearchWord(String searchWord){
        this.searchWord = searchWord;
    }

    public static void deb(String s){
        Log.i("test", s);
    }
    public static void deb(int i){
        Log.i("test", i + "");
    }

    public static String getUserNameFromEmail(String email){
        int ind = 0;
        for(int i = 0; i < email.length(); i++){
            if(email.charAt(i) == '@'){
                ind = i;
                break;
            }
        }

        return email.substring(0, ind);
    }

    public static String getEmailFromUsername(String username){
        return username + "@gmail.com";
    }

    public ArrayList<ArticleTag> getDropDownSearchedTags(ArrayList<ArticleTag> tagList){
        ArrayList<ArticleTag> tags = new ArrayList<>();
        int lenWord = searchWord.length();

        for(int i = 0; i < tagList.size(); i++){
            String tag = tagList.get(i).getTagName();
            int lenTag = tag.length();
            if(lenTag > lenWord){
                for(int j = 0; j <= lenTag - lenWord; j++){
                    //if(j + lenWord > ) break;

                    String substringTag = tag.substring(j, j + lenWord);
                    //DataModel.deb(tag + " | " + substringTag + " (" + j + ", " + (j + lenWord) + ")" + searchWord);
                    if(substringTag.equals(searchWord)){
                        tags.add(tagList.get(i));
                        break;
                    }
                }
            }
            else if(tag.length() == searchWord.length() && tag.equals(searchWord)){
                tags.add(tagList.get(i));
            }
        }

        return tags;
    }

    public MyTime getCurrentMyTime(){
        // Thu Sep 01 23:19:10 GMT+06:00 2022

        String date = new Date().toString(), dayName = date.substring(0, 3), monthName = date.substring(4, 7),
                dayInt = date.substring(8, 10), hourMinSEc = date.substring(11, 19), gmt = date.substring(20, 29),
                year = date.substring(30, 34);

        return new MyTime(dayName, monthName, dayInt, hourMinSEc, gmt, year);
    }

    public Article formArticle(DataSnapshot snapshot){
        Article article = null;
        if(snapshot != null){
            String headLine = snapshot.child(FireConstants.STR_ARTICLE_HEADLINE).getValue(String.class),
                    text = snapshot.child(FireConstants.STR_ARTICLE_TEXT).getValue(String.class),
                    id = snapshot.child(FireConstants.STR_ARTICLE_ID).getValue(String.class),
                    nameOfOwner =
                            snapshot.child(FireConstants.STR_NAME_OF_OWNER).getValue(String.class),
                    username = snapshot.child(FireConstants.STR_USERNAME).getValue(String.class),
                    privacy = snapshot.child(FireConstants.STR_PRIVACY).getValue(String.class);
            int numberOfLikes =
                    snapshot.child(FireConstants.STR_NUMBER_OF_LIKES).getValue(Integer.class);
            MyTime time = snapshot.child(FireConstants.STR_TIME).getValue(MyTime.class);
            ArrayList<Opinion> opinions = getOpinions(snapshot.child(FireConstants.STR_OPINIONS));
            ArrayList<String> tags = getTags(snapshot.child(FireConstants.STR_TAGS));
            article = new Article(headLine, text, id, nameOfOwner, username, privacy, numberOfLikes, time, opinions, tags);
        }
        return article;
    }

    private ArrayList<String> getTags(DataSnapshot snapshot){
        ArrayList<String> tags = new ArrayList<>();

        for(DataSnapshot item : snapshot.getChildren()){
            assert item.getKey() != null;
            String tag = snapshot.child(item.getKey()).getValue(String.class);
            tags.add(tag);
        }
        return tags;
    }

    private ArrayList<Opinion> getOpinions(DataSnapshot snapshot){
        ArrayList<Opinion> opinions = new ArrayList<>();

        for(DataSnapshot item : snapshot.getChildren()){
            assert item.getKey() != null;
            Opinion opinion = snapshot.child(item.getKey()).getValue(Opinion.class);
            opinions.add(opinion);
        }

        return opinions;
    }

    public int hasChar(String data, char ch){
        for(int i = 0; i < data.length(); i++){
            if(data.charAt(i) == ch){
                return (i + 1);
            }
        }
        return -1;
    }

    public int isUsernameCorrect(String data){
        for(int i = 0; i < data.length(); i++){
            if(!(data.charAt(i) >= 'a' && data.charAt(i) <= 'z')){
                if (!(data.charAt(i) >= '0' && data.charAt(i) <= '9')){
                    if(data.charAt(i) != '_' && data.charAt(i) != '-'){
                        return (i + 1);
                    }
                }
            }
        }
        return -1;
    }

    public static void getSpannableArticle(String articleText, Context context, boolean isCut, int colorSubtitle,
                                           int colorQuotation, int bulletPointColor, CallbackForSpannable callback){
        ArrayList<Integer> subHeadLineIndex = new ArrayList<>(),
                quotationIndex = new ArrayList<>(),
                highLightIndex = new ArrayList<>();

        getHyperLinkExtracted(articleText, (invalidIndex, newArticleText, hyperLinks) -> {

            getBulletAdded2(newArticleText, (articleText1, bulletPoints) -> {
                StringBuilder sb = new StringBuilder(articleText1);
                for(int i = 0; i < sb.length(); i++){
                    if(sb.charAt(i) == SUB_HEAD_LINE_SIGN){
                        subHeadLineIndex.add(i);
                        sb.setCharAt(i, ' ');
                    }
                    else if(sb.charAt(i) == QUOTATION_SIGN){
                        quotationIndex.add(i);
                    }
                    else if(sb.charAt(i) == HIGHLIGHT_SIGN){
                        highLightIndex.add(i);
                        sb.setCharAt(i, ' ');
                    }
                }
                SpannableString content = new SpannableString(sb.toString());

                // making sub head lines
                for(int i = 0; i < subHeadLineIndex.size() - 1; i += 2){

                    if(subHeadLineIndex.get(i + 1) >= sb.length()){
                        break;
                    }

                    content.setSpan(new StyleSpan(Typeface.BOLD),
                            subHeadLineIndex.get(i), subHeadLineIndex.get(i + 1), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    content.setSpan(new BackgroundColorSpan(colorSubtitle),
                            subHeadLineIndex.get(i), subHeadLineIndex.get(i + 1), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    content.setSpan(new AbsoluteSizeSpan(SUB_HEADING_TEXT_SIZE),
                            subHeadLineIndex.get(i), subHeadLineIndex.get(i + 1), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                // making quotation
                for(int i = 0; i < quotationIndex.size() - 1; i += 2){

                    if(quotationIndex.get(i + 1) >= sb.length()){
                        break;
                    }

                    content.setSpan(new StyleSpan(Typeface.ITALIC),
                            quotationIndex.get(i), quotationIndex.get(i + 1) + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    content.setSpan(new BackgroundColorSpan(colorQuotation),
                            quotationIndex.get(i), quotationIndex.get(i + 1) + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    content.setSpan(new AbsoluteSizeSpan(QUOTATION_TEXT_SIZE),
                            quotationIndex.get(i), quotationIndex.get(i + 1) + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                // making bullets
                for(int i = 0; i < bulletPoints.size(); i++){

                    if(bulletPoints.get(i).getFirst() < bulletPoints.get(i).getSecond() + 1){
                        content.setSpan(new DrawableMarginSpan(ContextCompat.getDrawable(context,
                                        R.drawable.round_shape_green), 15),
                                bulletPoints.get(i).getFirst(), bulletPoints.get(i).getSecond() + 1,
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                }

                // making hyper link clickable
                for(int i = 0; i < hyperLinks.size(); i++){

                    int finalI = i;
                    content.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            String httpUrl = attachHTTPInURL(hyperLinks.get(finalI).getSrc());
                            Uri uri = Uri.parse(httpUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(intent);
                        }
                    }, hyperLinks.get(i).getTextInit(),
                            hyperLinks.get(i).getTextEnd(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                callback.onCallback(content);
            });

        });
    }

    private static String attachHTTPInURL(String url){
        String http = "http://", https = "https://";
        if(url.length() < http.length()){
            return https + url;
        }
        else{
            if(http.equals(url.substring(0, 7))){
                return url;
            }
            else if(url.length() < https.length()){
                return https + url;
            }
            else if(https.equals(url.substring(0, 8))){
                return url;
            }
            else{
                return https + url;
            }
        }
    }

    private static void getHyperLinkExtracted(String articleText, CallbackToExtractLink callback){

        StringBuilder sb = new StringBuilder(articleText);

        ArrayList<HyperLink> hyperLinks = new ArrayList<>();

        int textInit = -1, textEnd = -1, srcInit = -1, srcEnd = -1;

        for(int i = 0; i + 2 < sb.length(); ){
            boolean isValid = false;

            if(sb.charAt(i) == HYPER_LINK_SIGN && sb.charAt(i + 1) == LEFT_BRACKET){
                textInit = i + 2;
                int j;
                for (j = textInit; j < sb.length(); j++){
                    if(sb.charAt(j) == RIGHT_BRACKET){
                        textEnd = j - 1;

                        sb.delete(i, i + 2);
                        i -= 2;
                        j -= 2;
                        textInit -= 2;
                        break;
                    }
                }

                int temp = j;
                j++;
                if(sb.charAt(j) == LEFT_BRACKET && j + 1 < sb.length() && sb.charAt(j + 1) != ' '  && textEnd > -1){
                    srcInit = j + 1;
                    for(j = srcInit; j + 1 < sb.length(); j++){
                        if(sb.charAt(j) == RIGHT_BRACKET && sb.charAt(j + 1) == HYPER_LINK_SIGN){
                            srcEnd = j - 1;

                            String src = sb.substring(srcInit, srcEnd + 1);

                            sb.setCharAt(j + 1, ' ');
                            sb.delete(temp, j + 1);

                            j -= src.length() + 3;

                            HyperLink hyperLink = new HyperLink(textInit, textEnd, src);
                            hyperLinks.add(hyperLink);
                            isValid = true;

                            i = j + 1;

                            break;
                        }
                    }
                    if(!isValid){
                        callback.onCallback(i, null, null);
                        return;
                    }
                }
                else{
                    callback.onCallback(i, null, null);
                    return;
                }

            }
            else{
                i++;
            }
        }

        callback.onCallback(-1, sb.toString(), hyperLinks);
    }

    private static void getBulletAdded(String articleText, CallbackToGetBulletPoints callback){

        ArrayList<MyPair> bulletIndex = new ArrayList<>();
        StringBuilder sb = new StringBuilder(articleText);
        int numbOfLeftBullet = 0, numOfRightBullet = 0;

        int[] emptySecondIndex = new int[articleText.length()];
        int curEmptyInd = -1;

        for(int i = 0; i < sb.length(); i++){
            if(sb.charAt(i) == BULLET_POINT_LEFT_SIGN){
                numbOfLeftBullet++;
                numOfRightBullet = 1;
                sb.setCharAt(i, '\n');
                sb.insert(i + 1, '\t');
                for(int j = 0; j < numbOfLeftBullet; j++){
                    sb.insert(i + j + 2, '\t');
                }
                bulletIndex.add(new MyPair(i + numbOfLeftBullet + 1, -1));
                curEmptyInd++;
                emptySecondIndex[curEmptyInd] = bulletIndex.size() - 1;
            }
            else if(sb.charAt(i) == BULLET_POINT_RIGHT_SIGN){
                MyPair p = bulletIndex.get(emptySecondIndex[curEmptyInd]);
                p.setSecond(i - 1);
                bulletIndex.set(emptySecondIndex[curEmptyInd], p);
                curEmptyInd--;

                numbOfLeftBullet--;
                if(numOfRightBullet == 1){
                    sb.setCharAt(i, '\n');
                    numOfRightBullet = 0;
                }
                else{
                    sb.setCharAt(i, ' ');
                }
            }
        }

        /*
        for(int i = 0; i < bulletIndex.size(); i++){
            System.out.println(bulletIndex.get(i));
        }
         */

       callback.onCallback(sb.toString(), bulletIndex);
    }

    public static void getBulletAdded2(String articleText, CallbackToGetBulletPoints callback){

        ArrayList<MyPair> bulletIndex = new ArrayList<>();
        StringBuilder sb = new StringBuilder(articleText);
        int[] emptySecondIndex = new int[articleText.length()];
        int curEmptyInd = -1, numOfRightBullet = 0;

        for(int i = 0; i < sb.length(); i++){
            if(sb.charAt(i) == BULLET_POINT_LEFT_SIGN){
                bulletIndex.add(new MyPair(i + 1, -1));
                curEmptyInd++;
                emptySecondIndex[curEmptyInd] = bulletIndex.size() - 1;
                sb.setCharAt(i, '\n');
                numOfRightBullet = 1;

            }
            else if(sb.charAt(i) == BULLET_POINT_RIGHT_SIGN){
                MyPair p = bulletIndex.get(emptySecondIndex[curEmptyInd]);
                p.setSecond(i - 1);
                bulletIndex.set(emptySecondIndex[curEmptyInd], p);
                curEmptyInd--;

                //numbOfLeftBullet--;
                if(numOfRightBullet == 1){
                    sb.setCharAt(i, '\n');
                    numOfRightBullet = 0;
                }
                else{
                    sb.setCharAt(i, ' ');
                }
            }
        }

        /*
        for(int i = 0; i < bulletIndex.size(); i++){
            System.out.println(bulletIndex.get(i));
        }
         */

        callback.onCallback(sb.toString(), bulletIndex);
    }

    public static boolean isBulletPointAddedCorrectly(String articleText){
        short numOfLeftBulletSign = 0, numOfRightBulletSign = 0;
        for(int i = 0; i < articleText.length(); i++){
            if(articleText.charAt(i) == BULLET_POINT_LEFT_SIGN){
                numOfLeftBulletSign++;
            }
            else if(articleText.charAt(i) == BULLET_POINT_RIGHT_SIGN){
                numOfRightBulletSign++;
                if(numOfLeftBulletSign < numOfRightBulletSign){
                    return false;
                }
            }
        }

        return numOfLeftBulletSign == numOfRightBulletSign;
    }

    public static int isHyperLinkFormationCorrect(String text) {

        ArrayList<HyperLink> hyperLinks = new ArrayList<>();

        int textInit = -1, textEnd = -1, srcInit = -1, srcEnd = -1;

        for(int i = 0; i + 2 < text.length(); ){
            boolean isValid = false;

            if(text.charAt(i) == '~' && text.charAt(i + 1) == '['){
                textInit = i + 2;
                int j;
                for (j = i + 2; j < text.length(); j++){
                    if(text.charAt(j) == ']'){
                        textEnd = j - 1;
                        break;
                    }
                }
                j++;
                if(text.charAt(j) == '[' && j + 1 < text.length() && textEnd > -1){
                    if(text.charAt(j + 1) == ' '){
                        return j + 1;
                    }
                    srcInit = j + 1;
                    for(j = srcInit; j + 1 < text.length(); j++){
                        if(text.charAt(j) == ']' && text.charAt(j + 1) == '~'){
                            if(text.charAt(j - 1) == ' '){
                                return j - 1;
                            }
                            srcEnd = j - 1;
                            //HyperLink hyperLink = new HyperLink(textInit, textEnd, srcInit, srcEnd);
                            //hyperLinks.add(hyperLink);
                            isValid = true;
                            i = j + 2;
                            break;
                        }
                    }
                    if(!isValid){
                        return i;
                    }
                }
                else{
                    return i;
                }

            }
            else{
                if(text.charAt(i) == '~' && (i + 1) < text.length() && text.charAt(i + 1) == ' '){
                    return i + 1;
                }
                i++;
            }
        }

        return -1;
    }

    public interface CallbackToExtractLink {
        void onCallback(int invalidIndex, String newArticleText, ArrayList<HyperLink> hyperLinks);
    }

    public interface CallbackForSpannable{
        void onCallback(SpannableString content);
    }

    public interface CallbackToGetBulletPoints{
        void onCallback(String articleText, ArrayList<MyPair> bulletPoints);
    }
}
