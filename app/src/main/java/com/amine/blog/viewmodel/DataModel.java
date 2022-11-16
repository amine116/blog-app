package com.amine.blog.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.amine.blog.R;
import com.amine.blog.model.Article;
import com.amine.blog.model.ArticleTag;
import com.amine.blog.model.CountryCode;
import com.amine.blog.model.HyperLink;
import com.amine.blog.model.MyPair;
import com.amine.blog.model.MyTime;
import com.amine.blog.model.Opinion;
import com.amine.blog.model.RecentArticle;
import com.amine.blog.repositories.FireConstants;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DataModel {

    // public static values
    // Values for this class are going to be up to 10 and 51-60
    public static final int MOVE_TO_MAIN_ACTIVITY_HOME = 1, MOVE_TO_CREATE_ACCOUNT_FRAGMENT = 2,
            MOVE_TO_SIGN_IN_PAGE_2 = 3, YES = 4, NO = 5, MOVE_TO_PROFILE_ACTIVITY = 6,
            MOVE_TO_ADD_TAG_FRAGMENT = 7, MOVE_TO_RECOVER_ACCOUNT = 8, STR_MOVE_TO_SIGN_IN_PAGE = 9,
            PASSWORD_EMPTY = 10, PASSWORD_SHORT = 51;

    // Values that doesn't related to above condition
    public static final int SUB_HEADING_TEXT_SIZE = 60, QUOTATION_TEXT_SIZE = 50,
            HIGHLIGHT_SIZE = 30, MAXIMUM_DATA_QUERY_FIREBASE = 3, ARTICLE_SIZE_ON_HOME = 200;

    public static final String STR_CLICKED = "clicked", STR_AUTHOR = "Author", STR_PUBLIC = "public",
            STR_ONLY_ME = "only me", STR_DISMISS = "dismiss", STR_CREATE_TAG = "create tag",
            STR_ARTICLE = FireConstants.STR_ARTICLE, STR_OPINION = FireConstants.STR_OPINIONS,
            TIME_ZONE_ID = "Asia/Dhaka";

    public static final char SUB_HEAD_LINE_SIGN = '*', QUOTATION_SIGN = '"', HYPER_LINK_SIGN = '~',
            LEFT_BRACKET = '[', RIGHT_BRACKET = ']', HIGHLIGHT_SIGN = '#',
            CHOSEN_TEXT_SELECTION_SIGN = '@', BULLET_POINT_LEFT_SIGN = '<', BULLET_POINT_RIGHT_SIGN = '>';

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

        String date = new Date().toString();

        return getMyTimeFromDateString(date);
    }

    public static long calenderTimeInMill(){

        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE_ID);
        Calendar calendar = new GregorianCalendar(timeZone);

        return calendar.getTimeInMillis();
    }

    private static MyTime getMyTimeFromDateString(String date){
       String dayName = date.substring(0, 3), monthName = date.substring(4, 7),
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

    public RecentArticle formRecentArticle(DataSnapshot snapshot){
        RecentArticle article = null;
        if(snapshot != null){
            String headLine = snapshot.child(FireConstants.STR_ARTICLE_HEADLINE).getValue(String.class),
                    text = snapshot.child(FireConstants.STR_ARTICLE_TEXT).getValue(String.class),
                    id = snapshot.child(FireConstants.STR_ARTICLE_ID).getValue(String.class),
                    nameOfOwner =
                            snapshot.child(FireConstants.STR_NAME_OF_OWNER).getValue(String.class),
                    username = snapshot.child(FireConstants.STR_USERNAME).getValue(String.class),
                    privacy = snapshot.child(FireConstants.STR_PRIVACY).getValue(String.class);

            long timeInMill = snapshot.child(FireConstants.STR_TIME_IN_MILL).getValue(Long.class);
            int numberOfLikes =
                    snapshot.child(FireConstants.STR_NUMBER_OF_LIKES).getValue(Integer.class);
            MyTime time = snapshot.child(FireConstants.STR_TIME).getValue(MyTime.class);
            ArrayList<Opinion> opinions = getOpinions(snapshot.child(FireConstants.STR_OPINIONS));
            ArrayList<String> tags = getTags(snapshot.child(FireConstants.STR_TAGS));
            article = new RecentArticle(headLine, text, id, nameOfOwner, username, privacy, numberOfLikes,
                    time, opinions, tags, timeInMill);
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

                    content.setSpan(new StyleSpan(Typeface.BOLD),
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
                                        R.drawable.bullet_point), 15),
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
            if(http.equals(url.substring(0, http.length()))){
                return url;
            }
            else if(url.length() < https.length()){
                return https + url;
            }
            else if(https.equals(url.substring(0, https.length()))){
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

    private static String getJsonString(){
        return "{\"countries\" : [\n" +
                "    {\n" +
                "        \"name\": \"Afghanistan\",\n" +
                "        \"dialCode\": \"+93\",\n" +
                "        \"isoCode\": \"AF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/af.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Aland Islands\",\n" +
                "        \"dialCode\": \"+358\",\n" +
                "        \"isoCode\": \"AX\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ax.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Albania\",\n" +
                "        \"dialCode\": \"+355\",\n" +
                "        \"isoCode\": \"AL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/al.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Algeria\",\n" +
                "        \"dialCode\": \"+213\",\n" +
                "        \"isoCode\": \"DZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/dz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"American Samoa\",\n" +
                "        \"dialCode\": \"+1684\",\n" +
                "        \"isoCode\": \"AS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/as.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Andorra\",\n" +
                "        \"dialCode\": \"+376\",\n" +
                "        \"isoCode\": \"AD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ad.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Angola\",\n" +
                "        \"dialCode\": \"+244\",\n" +
                "        \"isoCode\": \"AO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ao.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Anguilla\",\n" +
                "        \"dialCode\": \"+1264\",\n" +
                "        \"isoCode\": \"AI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ai.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Antarctica\",\n" +
                "        \"dialCode\": \"+672\",\n" +
                "        \"isoCode\": \"AQ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/aq.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Antigua and Barbuda\",\n" +
                "        \"dialCode\": \"+1268\",\n" +
                "        \"isoCode\": \"AG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ag.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Argentina\",\n" +
                "        \"dialCode\": \"+54\",\n" +
                "        \"isoCode\": \"AR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ar.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Armenia\",\n" +
                "        \"dialCode\": \"+374\",\n" +
                "        \"isoCode\": \"AM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/am.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Aruba\",\n" +
                "        \"dialCode\": \"+297\",\n" +
                "        \"isoCode\": \"AW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/aw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ascension Island\",\n" +
                "        \"dialCode\": \"+247\",\n" +
                "        \"isoCode\": \"AC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ac.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Australia\",\n" +
                "        \"dialCode\": \"+61\",\n" +
                "        \"isoCode\": \"AU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/au.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Austria\",\n" +
                "        \"dialCode\": \"+43\",\n" +
                "        \"isoCode\": \"AT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/at.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Azerbaijan\",\n" +
                "        \"dialCode\": \"+994\",\n" +
                "        \"isoCode\": \"AZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/az.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bahamas\",\n" +
                "        \"dialCode\": \"+1242\",\n" +
                "        \"isoCode\": \"BS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bs.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bahrain\",\n" +
                "        \"dialCode\": \"+973\",\n" +
                "        \"isoCode\": \"BH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bh.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bangladesh\",\n" +
                "        \"dialCode\": \"+880\",\n" +
                "        \"isoCode\": \"BD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bd.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Barbados\",\n" +
                "        \"dialCode\": \"+1246\",\n" +
                "        \"isoCode\": \"BB\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bb.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Belarus\",\n" +
                "        \"dialCode\": \"+375\",\n" +
                "        \"isoCode\": \"BY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/by.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Belgium\",\n" +
                "        \"dialCode\": \"+32\",\n" +
                "        \"isoCode\": \"BE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/be.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Belize\",\n" +
                "        \"dialCode\": \"+501\",\n" +
                "        \"isoCode\": \"BZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Benin\",\n" +
                "        \"dialCode\": \"+229\",\n" +
                "        \"isoCode\": \"BJ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bj.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bermuda\",\n" +
                "        \"dialCode\": \"+1441\",\n" +
                "        \"isoCode\": \"BM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bhutan\",\n" +
                "        \"dialCode\": \"+975\",\n" +
                "        \"isoCode\": \"BT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bolivia\",\n" +
                "        \"dialCode\": \"+591\",\n" +
                "        \"isoCode\": \"BO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bo.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bosnia and Herzegovina\",\n" +
                "        \"dialCode\": \"+387\",\n" +
                "        \"isoCode\": \"BA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ba.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Botswana\",\n" +
                "        \"dialCode\": \"+267\",\n" +
                "        \"isoCode\": \"BW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Brazil\",\n" +
                "        \"dialCode\": \"+55\",\n" +
                "        \"isoCode\": \"BR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/br.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"British Indian Ocean Territory\",\n" +
                "        \"dialCode\": \"+246\",\n" +
                "        \"isoCode\": \"IO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/io.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Brunei Darussalam\",\n" +
                "        \"dialCode\": \"+673\",\n" +
                "        \"isoCode\": \"BN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Bulgaria\",\n" +
                "        \"dialCode\": \"+359\",\n" +
                "        \"isoCode\": \"BG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Burkina Faso\",\n" +
                "        \"dialCode\": \"+226\",\n" +
                "        \"isoCode\": \"BF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Burundi\",\n" +
                "        \"dialCode\": \"+257\",\n" +
                "        \"isoCode\": \"BI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bi.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cambodia\",\n" +
                "        \"dialCode\": \"+855\",\n" +
                "        \"isoCode\": \"KH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kh.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cameroon\",\n" +
                "        \"dialCode\": \"+237\",\n" +
                "        \"isoCode\": \"CM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Canada\",\n" +
                "        \"dialCode\": \"+1\",\n" +
                "        \"isoCode\": \"CA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ca.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cape Verde\",\n" +
                "        \"dialCode\": \"+238\",\n" +
                "        \"isoCode\": \"CV\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cv.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cayman Islands\",\n" +
                "        \"dialCode\": \"+1345\",\n" +
                "        \"isoCode\": \"KY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ky.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Central African Republic\",\n" +
                "        \"dialCode\": \"+236\",\n" +
                "        \"isoCode\": \"CF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Chad\",\n" +
                "        \"dialCode\": \"+235\",\n" +
                "        \"isoCode\": \"TD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/td.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Chile\",\n" +
                "        \"dialCode\": \"+56\",\n" +
                "        \"isoCode\": \"CL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"China\",\n" +
                "        \"dialCode\": \"+86\",\n" +
                "        \"isoCode\": \"CN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Christmas Island\",\n" +
                "        \"dialCode\": \"+61\",\n" +
                "        \"isoCode\": \"CX\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cx.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cocos (Keeling) Islands\",\n" +
                "        \"dialCode\": \"+61\",\n" +
                "        \"isoCode\": \"CC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Colombia\",\n" +
                "        \"dialCode\": \"+57\",\n" +
                "        \"isoCode\": \"CO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/co.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Comoros\",\n" +
                "        \"dialCode\": \"+269\",\n" +
                "        \"isoCode\": \"KM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/km.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Congo\",\n" +
                "        \"dialCode\": \"+242\",\n" +
                "        \"isoCode\": \"CG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cook Islands\",\n" +
                "        \"dialCode\": \"+682\",\n" +
                "        \"isoCode\": \"CK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ck.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Costa Rica\",\n" +
                "        \"dialCode\": \"+506\",\n" +
                "        \"isoCode\": \"CR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Croatia\",\n" +
                "        \"dialCode\": \"+385\",\n" +
                "        \"isoCode\": \"HR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/hr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cuba\",\n" +
                "        \"dialCode\": \"+53\",\n" +
                "        \"isoCode\": \"CU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Cyprus\",\n" +
                "        \"dialCode\": \"+357\",\n" +
                "        \"isoCode\": \"CY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cy.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Czech Republic\",\n" +
                "        \"dialCode\": \"+420\",\n" +
                "        \"isoCode\": \"CZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Democratic Republic of the Congo\",\n" +
                "        \"dialCode\": \"+243\",\n" +
                "        \"isoCode\": \"CD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/cd.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Denmark\",\n" +
                "        \"dialCode\": \"+45\",\n" +
                "        \"isoCode\": \"DK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/dk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Djibouti\",\n" +
                "        \"dialCode\": \"+253\",\n" +
                "        \"isoCode\": \"DJ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/dj.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Dominica\",\n" +
                "        \"dialCode\": \"+1767\",\n" +
                "        \"isoCode\": \"DM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/dm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Dominican Republic\",\n" +
                "        \"dialCode\": \"+1849\",\n" +
                "        \"isoCode\": \"DO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/do.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ecuador\",\n" +
                "        \"dialCode\": \"+593\",\n" +
                "        \"isoCode\": \"EC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ec.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Egypt\",\n" +
                "        \"dialCode\": \"+20\",\n" +
                "        \"isoCode\": \"EG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/eg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"El Salvador\",\n" +
                "        \"dialCode\": \"+503\",\n" +
                "        \"isoCode\": \"SV\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sv.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Equatorial Guinea\",\n" +
                "        \"dialCode\": \"+240\",\n" +
                "        \"isoCode\": \"GQ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gq.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Eritrea\",\n" +
                "        \"dialCode\": \"+291\",\n" +
                "        \"isoCode\": \"ER\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/er.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Estonia\",\n" +
                "        \"dialCode\": \"+372\",\n" +
                "        \"isoCode\": \"EE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ee.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Eswatini\",\n" +
                "        \"dialCode\": \"+268\",\n" +
                "        \"isoCode\": \"SZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ethiopia\",\n" +
                "        \"dialCode\": \"+251\",\n" +
                "        \"isoCode\": \"ET\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/et.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Falkland Islands (Malvinas)\",\n" +
                "        \"dialCode\": \"+500\",\n" +
                "        \"isoCode\": \"FK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/fk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Faroe Islands\",\n" +
                "        \"dialCode\": \"+298\",\n" +
                "        \"isoCode\": \"FO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/fo.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Fiji\",\n" +
                "        \"dialCode\": \"+679\",\n" +
                "        \"isoCode\": \"FJ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/fj.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Finland\",\n" +
                "        \"dialCode\": \"+358\",\n" +
                "        \"isoCode\": \"FI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/fi.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"France\",\n" +
                "        \"dialCode\": \"+33\",\n" +
                "        \"isoCode\": \"FR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/fr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"French Guiana\",\n" +
                "        \"dialCode\": \"+594\",\n" +
                "        \"isoCode\": \"GF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"French Polynesia\",\n" +
                "        \"dialCode\": \"+689\",\n" +
                "        \"isoCode\": \"PF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Gabon\",\n" +
                "        \"dialCode\": \"+241\",\n" +
                "        \"isoCode\": \"GA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ga.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Gambia\",\n" +
                "        \"dialCode\": \"+220\",\n" +
                "        \"isoCode\": \"GM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Georgia\",\n" +
                "        \"dialCode\": \"+995\",\n" +
                "        \"isoCode\": \"GE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ge.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Germany\",\n" +
                "        \"dialCode\": \"+49\",\n" +
                "        \"isoCode\": \"DE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/de.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ghana\",\n" +
                "        \"dialCode\": \"+233\",\n" +
                "        \"isoCode\": \"GH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gh.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Gibraltar\",\n" +
                "        \"dialCode\": \"+350\",\n" +
                "        \"isoCode\": \"GI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gi.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Greece\",\n" +
                "        \"dialCode\": \"+30\",\n" +
                "        \"isoCode\": \"GR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Greenland\",\n" +
                "        \"dialCode\": \"+299\",\n" +
                "        \"isoCode\": \"GL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Grenada\",\n" +
                "        \"dialCode\": \"+1473\",\n" +
                "        \"isoCode\": \"GD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gd.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guadeloupe\",\n" +
                "        \"dialCode\": \"+590\",\n" +
                "        \"isoCode\": \"GP\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gp.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guam\",\n" +
                "        \"dialCode\": \"+1671\",\n" +
                "        \"isoCode\": \"GU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guatemala\",\n" +
                "        \"dialCode\": \"+502\",\n" +
                "        \"isoCode\": \"GT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guernsey\",\n" +
                "        \"dialCode\": \"+44-1481\",\n" +
                "        \"isoCode\": \"GG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guinea\",\n" +
                "        \"dialCode\": \"+224\",\n" +
                "        \"isoCode\": \"GN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guinea-Bissau\",\n" +
                "        \"dialCode\": \"+245\",\n" +
                "        \"isoCode\": \"GW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Guyana\",\n" +
                "        \"dialCode\": \"+592\",\n" +
                "        \"isoCode\": \"GY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gy.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Haiti\",\n" +
                "        \"dialCode\": \"+509\",\n" +
                "        \"isoCode\": \"HT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ht.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Holy See (Vatican City State)\",\n" +
                "        \"dialCode\": \"+379\",\n" +
                "        \"isoCode\": \"VA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/va.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Honduras\",\n" +
                "        \"dialCode\": \"+504\",\n" +
                "        \"isoCode\": \"HN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/hn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Hong Kong\",\n" +
                "        \"dialCode\": \"+852\",\n" +
                "        \"isoCode\": \"HK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/hk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Hungary\",\n" +
                "        \"dialCode\": \"+36\",\n" +
                "        \"isoCode\": \"HU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/hu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Iceland\",\n" +
                "        \"dialCode\": \"+354\",\n" +
                "        \"isoCode\": \"IS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/is.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"India\",\n" +
                "        \"dialCode\": \"+91\",\n" +
                "        \"isoCode\": \"IN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/in.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Indonesia\",\n" +
                "        \"dialCode\": \"+62\",\n" +
                "        \"isoCode\": \"ID\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/id.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Iran\",\n" +
                "        \"dialCode\": \"+98\",\n" +
                "        \"isoCode\": \"IR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ir.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Iraq\",\n" +
                "        \"dialCode\": \"+964\",\n" +
                "        \"isoCode\": \"IQ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/iq.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ireland\",\n" +
                "        \"dialCode\": \"+353\",\n" +
                "        \"isoCode\": \"IE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ie.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Isle of Man\",\n" +
                "        \"dialCode\": \"+44-1624\",\n" +
                "        \"isoCode\": \"IM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/im.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Israel\",\n" +
                "        \"dialCode\": \"+972\",\n" +
                "        \"isoCode\": \"IL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/il.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Italy\",\n" +
                "        \"dialCode\": \"+39\",\n" +
                "        \"isoCode\": \"IT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/it.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ivory Coast / Cote d'Ivoire\",\n" +
                "        \"dialCode\": \"+225\",\n" +
                "        \"isoCode\": \"CI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ci.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Jamaica\",\n" +
                "        \"dialCode\": \"+1876\",\n" +
                "        \"isoCode\": \"JM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/jm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Japan\",\n" +
                "        \"dialCode\": \"+81\",\n" +
                "        \"isoCode\": \"JP\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/jp.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Jersey\",\n" +
                "        \"dialCode\": \"+44-1534\",\n" +
                "        \"isoCode\": \"JE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/je.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Jordan\",\n" +
                "        \"dialCode\": \"+962\",\n" +
                "        \"isoCode\": \"JO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/jo.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Kazakhstan\",\n" +
                "        \"dialCode\": \"+77\",\n" +
                "        \"isoCode\": \"KZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Kenya\",\n" +
                "        \"dialCode\": \"+254\",\n" +
                "        \"isoCode\": \"KE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ke.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Kiribati\",\n" +
                "        \"dialCode\": \"+686\",\n" +
                "        \"isoCode\": \"KI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ki.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Korea, Democratic People's Republic of Korea\",\n" +
                "        \"dialCode\": \"+850\",\n" +
                "        \"isoCode\": \"KP\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kp.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Korea, Republic of South Korea\",\n" +
                "        \"dialCode\": \"+82\",\n" +
                "        \"isoCode\": \"KR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Kosovo\",\n" +
                "        \"dialCode\": \"+383\",\n" +
                "        \"isoCode\": \"XK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/xk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Kuwait\",\n" +
                "        \"dialCode\": \"+965\",\n" +
                "        \"isoCode\": \"KW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Kyrgyzstan\",\n" +
                "        \"dialCode\": \"+996\",\n" +
                "        \"isoCode\": \"KG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Laos\",\n" +
                "        \"dialCode\": \"+856\",\n" +
                "        \"isoCode\": \"LA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/la.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Latvia\",\n" +
                "        \"dialCode\": \"+371\",\n" +
                "        \"isoCode\": \"LV\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lv.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Lebanon\",\n" +
                "        \"dialCode\": \"+961\",\n" +
                "        \"isoCode\": \"LB\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lb.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Lesotho\",\n" +
                "        \"dialCode\": \"+266\",\n" +
                "        \"isoCode\": \"LS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ls.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Liberia\",\n" +
                "        \"dialCode\": \"+231\",\n" +
                "        \"isoCode\": \"LR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Libya\",\n" +
                "        \"dialCode\": \"+218\",\n" +
                "        \"isoCode\": \"LY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ly.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Liechtenstein\",\n" +
                "        \"dialCode\": \"+423\",\n" +
                "        \"isoCode\": \"LI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/li.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Lithuania\",\n" +
                "        \"dialCode\": \"+370\",\n" +
                "        \"isoCode\": \"LT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Luxembourg\",\n" +
                "        \"dialCode\": \"+352\",\n" +
                "        \"isoCode\": \"LU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Macau\",\n" +
                "        \"dialCode\": \"+853\",\n" +
                "        \"isoCode\": \"MO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mo.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Madagascar\",\n" +
                "        \"dialCode\": \"+261\",\n" +
                "        \"isoCode\": \"MG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Malawi\",\n" +
                "        \"dialCode\": \"+265\",\n" +
                "        \"isoCode\": \"MW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Malaysia\",\n" +
                "        \"dialCode\": \"+60\",\n" +
                "        \"isoCode\": \"MY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/my.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Maldives\",\n" +
                "        \"dialCode\": \"+960\",\n" +
                "        \"isoCode\": \"MV\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mv.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mali\",\n" +
                "        \"dialCode\": \"+223\",\n" +
                "        \"isoCode\": \"ML\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ml.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Malta\",\n" +
                "        \"dialCode\": \"+356\",\n" +
                "        \"isoCode\": \"MT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Marshall Islands\",\n" +
                "        \"dialCode\": \"+692\",\n" +
                "        \"isoCode\": \"MH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mh.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Martinique\",\n" +
                "        \"dialCode\": \"+596\",\n" +
                "        \"isoCode\": \"MQ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mq.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mauritania\",\n" +
                "        \"dialCode\": \"+222\",\n" +
                "        \"isoCode\": \"MR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mauritius\",\n" +
                "        \"dialCode\": \"+230\",\n" +
                "        \"isoCode\": \"MU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mayotte\",\n" +
                "        \"dialCode\": \"+262\",\n" +
                "        \"isoCode\": \"YT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/yt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mexico\",\n" +
                "        \"dialCode\": \"+52\",\n" +
                "        \"isoCode\": \"MX\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mx.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Micronesia, Federated States of Micronesia\",\n" +
                "        \"dialCode\": \"+691\",\n" +
                "        \"isoCode\": \"FM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/fm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Moldova\",\n" +
                "        \"dialCode\": \"+373\",\n" +
                "        \"isoCode\": \"MD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/md.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Monaco\",\n" +
                "        \"dialCode\": \"+377\",\n" +
                "        \"isoCode\": \"MC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mongolia\",\n" +
                "        \"dialCode\": \"+976\",\n" +
                "        \"isoCode\": \"MN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Montenegro\",\n" +
                "        \"dialCode\": \"+382\",\n" +
                "        \"isoCode\": \"ME\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/me.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Montserrat\",\n" +
                "        \"dialCode\": \"+1664\",\n" +
                "        \"isoCode\": \"MS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ms.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Morocco\",\n" +
                "        \"dialCode\": \"+212\",\n" +
                "        \"isoCode\": \"MA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ma.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mozambique\",\n" +
                "        \"dialCode\": \"+258\",\n" +
                "        \"isoCode\": \"MZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Myanmar\",\n" +
                "        \"dialCode\": \"+95\",\n" +
                "        \"isoCode\": \"MM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Namibia\",\n" +
                "        \"dialCode\": \"+264\",\n" +
                "        \"isoCode\": \"NA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/na.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Nauru\",\n" +
                "        \"dialCode\": \"+674\",\n" +
                "        \"isoCode\": \"NR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/nr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Nepal\",\n" +
                "        \"dialCode\": \"+977\",\n" +
                "        \"isoCode\": \"NP\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/np.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Netherlands\",\n" +
                "        \"dialCode\": \"+31\",\n" +
                "        \"isoCode\": \"NL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/nl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Netherlands Antilles\",\n" +
                "        \"dialCode\": \"+599\",\n" +
                "        \"isoCode\": \"AN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/an.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"New Caledonia\",\n" +
                "        \"dialCode\": \"+687\",\n" +
                "        \"isoCode\": \"NC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/nc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"New Zealand\",\n" +
                "        \"dialCode\": \"+64\",\n" +
                "        \"isoCode\": \"NZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/nz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Nicaragua\",\n" +
                "        \"dialCode\": \"+505\",\n" +
                "        \"isoCode\": \"NI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ni.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Niger\",\n" +
                "        \"dialCode\": \"+227\",\n" +
                "        \"isoCode\": \"NE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ne.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Nigeria\",\n" +
                "        \"dialCode\": \"+234\",\n" +
                "        \"isoCode\": \"NG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ng.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Niue\",\n" +
                "        \"dialCode\": \"+683\",\n" +
                "        \"isoCode\": \"NU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/nu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Norfolk Island\",\n" +
                "        \"dialCode\": \"+672\",\n" +
                "        \"isoCode\": \"NF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/nf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"North Macedonia\",\n" +
                "        \"dialCode\": \"+389\",\n" +
                "        \"isoCode\": \"MK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Northern Mariana Islands\",\n" +
                "        \"dialCode\": \"+1670\",\n" +
                "        \"isoCode\": \"MP\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mp.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Norway\",\n" +
                "        \"dialCode\": \"+47\",\n" +
                "        \"isoCode\": \"NO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/no.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Oman\",\n" +
                "        \"dialCode\": \"+968\",\n" +
                "        \"isoCode\": \"OM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/om.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Pakistan\",\n" +
                "        \"dialCode\": \"+92\",\n" +
                "        \"isoCode\": \"PK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Palau\",\n" +
                "        \"dialCode\": \"+680\",\n" +
                "        \"isoCode\": \"PW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Palestine\",\n" +
                "        \"dialCode\": \"+970\",\n" +
                "        \"isoCode\": \"PS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ps.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Panama\",\n" +
                "        \"dialCode\": \"+507\",\n" +
                "        \"isoCode\": \"PA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pa.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Papua New Guinea\",\n" +
                "        \"dialCode\": \"+675\",\n" +
                "        \"isoCode\": \"PG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Paraguay\",\n" +
                "        \"dialCode\": \"+595\",\n" +
                "        \"isoCode\": \"PY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/py.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Peru\",\n" +
                "        \"dialCode\": \"+51\",\n" +
                "        \"isoCode\": \"PE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pe.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Philippines\",\n" +
                "        \"dialCode\": \"+63\",\n" +
                "        \"isoCode\": \"PH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ph.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Pitcairn\",\n" +
                "        \"dialCode\": \"+872\",\n" +
                "        \"isoCode\": \"PN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Poland\",\n" +
                "        \"dialCode\": \"+48\",\n" +
                "        \"isoCode\": \"PL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Portugal\",\n" +
                "        \"dialCode\": \"+351\",\n" +
                "        \"isoCode\": \"PT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Puerto Rico\",\n" +
                "        \"dialCode\": \"+1939\",\n" +
                "        \"isoCode\": \"PR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Qatar\",\n" +
                "        \"dialCode\": \"+974\",\n" +
                "        \"isoCode\": \"QA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/qa.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Reunion\",\n" +
                "        \"dialCode\": \"+262\",\n" +
                "        \"isoCode\": \"RE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/re.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Romania\",\n" +
                "        \"dialCode\": \"+40\",\n" +
                "        \"isoCode\": \"RO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ro.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Russia\",\n" +
                "        \"dialCode\": \"+7\",\n" +
                "        \"isoCode\": \"RU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ru.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Rwanda\",\n" +
                "        \"dialCode\": \"+250\",\n" +
                "        \"isoCode\": \"RW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/rw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Barthelemy\",\n" +
                "        \"dialCode\": \"+590\",\n" +
                "        \"isoCode\": \"BL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/bl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Helena, Ascension and Tristan Da Cunha\",\n" +
                "        \"dialCode\": \"+290\",\n" +
                "        \"isoCode\": \"SH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sh.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Kitts and Nevis\",\n" +
                "        \"dialCode\": \"+1869\",\n" +
                "        \"isoCode\": \"KN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/kn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Lucia\",\n" +
                "        \"dialCode\": \"+1758\",\n" +
                "        \"isoCode\": \"LC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Martin\",\n" +
                "        \"dialCode\": \"+590\",\n" +
                "        \"isoCode\": \"MF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/mf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Pierre and Miquelon\",\n" +
                "        \"dialCode\": \"+508\",\n" +
                "        \"isoCode\": \"PM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/pm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saint Vincent and the Grenadines\",\n" +
                "        \"dialCode\": \"+1784\",\n" +
                "        \"isoCode\": \"VC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/vc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Samoa\",\n" +
                "        \"dialCode\": \"+685\",\n" +
                "        \"isoCode\": \"WS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ws.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"San Marino\",\n" +
                "        \"dialCode\": \"+378\",\n" +
                "        \"isoCode\": \"SM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Sao Tome and Principe\",\n" +
                "        \"dialCode\": \"+239\",\n" +
                "        \"isoCode\": \"ST\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/st.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Saudi Arabia\",\n" +
                "        \"dialCode\": \"+966\",\n" +
                "        \"isoCode\": \"SA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sa.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Senegal\",\n" +
                "        \"dialCode\": \"+221\",\n" +
                "        \"isoCode\": \"SN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Serbia\",\n" +
                "        \"dialCode\": \"+381\",\n" +
                "        \"isoCode\": \"RS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/rs.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Seychelles\",\n" +
                "        \"dialCode\": \"+248\",\n" +
                "        \"isoCode\": \"SC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Sierra Leone\",\n" +
                "        \"dialCode\": \"+232\",\n" +
                "        \"isoCode\": \"SL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Singapore\",\n" +
                "        \"dialCode\": \"+65\",\n" +
                "        \"isoCode\": \"SG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Sint Maarten\",\n" +
                "        \"dialCode\": \"+1721\",\n" +
                "        \"isoCode\": \"SX\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sx.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Slovakia\",\n" +
                "        \"dialCode\": \"+421\",\n" +
                "        \"isoCode\": \"SK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Slovenia\",\n" +
                "        \"dialCode\": \"+386\",\n" +
                "        \"isoCode\": \"SI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/si.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Solomon Islands\",\n" +
                "        \"dialCode\": \"+677\",\n" +
                "        \"isoCode\": \"SB\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sb.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Somalia\",\n" +
                "        \"dialCode\": \"+252\",\n" +
                "        \"isoCode\": \"SO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/so.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"South Africa\",\n" +
                "        \"dialCode\": \"+27\",\n" +
                "        \"isoCode\": \"ZA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/za.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"South Georgia and the South Sandwich Islands\",\n" +
                "        \"dialCode\": \"+500\",\n" +
                "        \"isoCode\": \"GS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gs.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"South Sudan\",\n" +
                "        \"dialCode\": \"+211\",\n" +
                "        \"isoCode\": \"SS\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ss.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Spain\",\n" +
                "        \"dialCode\": \"+34\",\n" +
                "        \"isoCode\": \"ES\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/es.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Sri Lanka\",\n" +
                "        \"dialCode\": \"+94\",\n" +
                "        \"isoCode\": \"LK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/lk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Sudan\",\n" +
                "        \"dialCode\": \"+249\",\n" +
                "        \"isoCode\": \"SD\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sd.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Suriname\",\n" +
                "        \"dialCode\": \"+597\",\n" +
                "        \"isoCode\": \"SR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Svalbard and Jan Mayen\",\n" +
                "        \"dialCode\": \"+47\",\n" +
                "        \"isoCode\": \"SJ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sj.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Sweden\",\n" +
                "        \"dialCode\": \"+46\",\n" +
                "        \"isoCode\": \"SE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/se.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Switzerland\",\n" +
                "        \"dialCode\": \"+41\",\n" +
                "        \"isoCode\": \"CH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ch.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Syrian Arab Republic\",\n" +
                "        \"dialCode\": \"+963\",\n" +
                "        \"isoCode\": \"SY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/sy.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Taiwan\",\n" +
                "        \"dialCode\": \"+886\",\n" +
                "        \"isoCode\": \"TW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tw.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Tajikistan\",\n" +
                "        \"dialCode\": \"+992\",\n" +
                "        \"isoCode\": \"TJ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tj.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Tanzania, United Republic of Tanzania\",\n" +
                "        \"dialCode\": \"+255\",\n" +
                "        \"isoCode\": \"TZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Thailand\",\n" +
                "        \"dialCode\": \"+66\",\n" +
                "        \"isoCode\": \"TH\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/th.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Timor-Leste\",\n" +
                "        \"dialCode\": \"+670\",\n" +
                "        \"isoCode\": \"TL\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tl.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Togo\",\n" +
                "        \"dialCode\": \"+228\",\n" +
                "        \"isoCode\": \"TG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Tokelau\",\n" +
                "        \"dialCode\": \"+690\",\n" +
                "        \"isoCode\": \"TK\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tk.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Tonga\",\n" +
                "        \"dialCode\": \"+676\",\n" +
                "        \"isoCode\": \"TO\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/to.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Trinidad and Tobago\",\n" +
                "        \"dialCode\": \"+1868\",\n" +
                "        \"isoCode\": \"TT\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tt.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Tunisia\",\n" +
                "        \"dialCode\": \"+216\",\n" +
                "        \"isoCode\": \"TN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Turkey\",\n" +
                "        \"dialCode\": \"+90\",\n" +
                "        \"isoCode\": \"TR\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tr.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Turkmenistan\",\n" +
                "        \"dialCode\": \"+993\",\n" +
                "        \"isoCode\": \"TM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Turks and Caicos Islands\",\n" +
                "        \"dialCode\": \"+1649\",\n" +
                "        \"isoCode\": \"TC\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tc.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Tuvalu\",\n" +
                "        \"dialCode\": \"+688\",\n" +
                "        \"isoCode\": \"TV\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/tv.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Uganda\",\n" +
                "        \"dialCode\": \"+256\",\n" +
                "        \"isoCode\": \"UG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ug.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Ukraine\",\n" +
                "        \"dialCode\": \"+380\",\n" +
                "        \"isoCode\": \"UA\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ua.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"United Arab Emirates\",\n" +
                "        \"dialCode\": \"+971\",\n" +
                "        \"isoCode\": \"AE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ae.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"United Kingdom\",\n" +
                "        \"dialCode\": \"+44\",\n" +
                "        \"isoCode\": \"GB\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/gb.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"United States\",\n" +
                "        \"dialCode\": \"+1\",\n" +
                "        \"isoCode\": \"US\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/us.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"United States Minor Outlying Islands\",\n" +
                "        \"dialCode\": \"+246\",\n" +
                "        \"isoCode\": \"UMI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/umi.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Uruguay\",\n" +
                "        \"dialCode\": \"+598\",\n" +
                "        \"isoCode\": \"UY\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/uy.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Uzbekistan\",\n" +
                "        \"dialCode\": \"+998\",\n" +
                "        \"isoCode\": \"UZ\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/uz.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Vanuatu\",\n" +
                "        \"dialCode\": \"+678\",\n" +
                "        \"isoCode\": \"VU\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/vu.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Venezuela, Bolivarian Republic of Venezuela\",\n" +
                "        \"dialCode\": \"+58\",\n" +
                "        \"isoCode\": \"VE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ve.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Vietnam\",\n" +
                "        \"dialCode\": \"+84\",\n" +
                "        \"isoCode\": \"VN\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/vn.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Virgin Islands, British\",\n" +
                "        \"dialCode\": \"+1284\",\n" +
                "        \"isoCode\": \"VG\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/vg.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Virgin Islands, U.S.\",\n" +
                "        \"dialCode\": \"+1340\",\n" +
                "        \"isoCode\": \"VI\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/vi.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Wallis and Futuna\",\n" +
                "        \"dialCode\": \"+681\",\n" +
                "        \"isoCode\": \"WF\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/wf.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Yemen\",\n" +
                "        \"dialCode\": \"+967\",\n" +
                "        \"isoCode\": \"YE\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/ye.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Zambia\",\n" +
                "        \"dialCode\": \"+260\",\n" +
                "        \"isoCode\": \"ZM\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/zm.svg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Zimbabwe\",\n" +
                "        \"dialCode\": \"+263\",\n" +
                "        \"isoCode\": \"ZW\",\n" +
                "        \"flag\": \"https://cdn.kcak11.com/CountryFlags/countries/zw.svg\"\n" +
                "    }\n" +
                "] }";
    }

    public static ArrayList<CountryCode> getCountryCodes(){
        JSONArray jsonArray;
        ArrayList<CountryCode> countryCodes = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(DataModel.getJsonString());
            jsonArray = jsonObject.getJSONArray("countries");

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                CountryCode countryCode = new CountryCode(obj.getString("name")
                        , obj.getString("isoCode"),
                        obj.getString("dialCode"),
                        obj.getString("flag"));

                countryCodes.add(countryCode);
                //countryNames.add(obj.getString("name"));
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return countryCodes;
    }

    public static String getMyDeviceCountryIso(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();

//        switch (simState) {
//            case TelephonyManager.SIM_STATE_ABSENT:
//                String locale = context.getResources().getConfiguration().getLocales().toString();
//                break;
//            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
//                // do something
//                break;
//            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
//                // do something
//                break;
//            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
//                // do something
//                break;
//            case TelephonyManager.SIM_STATE_READY:
//                return tm.getNetworkCountryIso();
//            case TelephonyManager.SIM_STATE_UNKNOWN:
//                // do something
//                break;
//            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
//                break;
//            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
//                break;
//            case TelephonyManager.SIM_STATE_NOT_READY:
//                break;
//            case TelephonyManager.SIM_STATE_PERM_DISABLED:
//                break;
//        }

        return tm.getNetworkCountryIso();
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
