package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.adapters.StringArrayAdapter;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.People;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class PeopleFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView txtProfileName, txtArticleCount, txtFavourite, txtShowMore;
    private ProgressBar pBar, progressShowMore;
    private LinearLayout layoutPeople;
    private RelativeLayout layout_showMore;
    private Spinner spinnerSortBy;
    private ImageView imgSearchPeople;
    private EditText edtSearchPeople;

    private final ArrayList<String> sortByItems = new ArrayList<>();
    private String lastReadingUsername, sortBy = "";
    private long lastReadingArticleCount = 0;

    private Context context;

    private OnWaitListenerWithStringInfo waitWithInfoListener;

    public PeopleFrag(){}

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnWaitWithInfoListener(OnWaitListenerWithStringInfo waitWithInfoListener) {
        this.waitWithInfoListener = waitWithInfoListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.people_view_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        txtProfileName = view.findViewById(R.id.txtProfileName);
        txtArticleCount = view.findViewById(R.id.txtArticleCount);
        txtFavourite = view.findViewById(R.id.txtFavourites);
        pBar = view.findViewById(R.id.progress_people);
        layoutPeople = view.findViewById(R.id.layout_people);
        txtShowMore = view.findViewById(R.id.txtShowMore);
        progressShowMore = view.findViewById(R.id.progressShowMore);
        layout_showMore = view.findViewById(R.id.layout_showMore);
        spinnerSortBy = view.findViewById(R.id.spinnerSortBy);
        imgSearchPeople = view.findViewById(R.id.imgSearchPeople);
        edtSearchPeople = view.findViewById(R.id.edtSearchPeople);

        imgSearchPeople.setOnClickListener(this);
        spinnerSortBy.setOnItemSelectedListener(this);
        txtShowMore.setOnClickListener(this);

        populateSpinner();
        //getThePeopleByUsername();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == txtShowMore.getId()){
            txtShowMore.setVisibility(View.GONE);
            progressShowMore.setVisibility(View.VISIBLE);

            if(sortBy.equals(FireConstants.STR_USERNAME)){
                // by username
                Retrieve.readUserOverViewByUsername(lastReadingUsername,
                        false, context, (task, people) -> {

                            progressShowMore.setVisibility(View.GONE);
                            txtShowMore.setVisibility(View.VISIBLE);

                            if(task == UserAccount.SUCCESS){
                                if(people.size() > 0){
                                    lastReadingUsername = people.get(people.size() - 1).getUsername();
                                }
                                if(people.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                                    lastReadingUsername = "";
                                    txtShowMore.setVisibility(View.GONE);
                                }
                                else{
                                    txtShowMore.setVisibility(View.VISIBLE);
                                }
                                setPeoples(people);
                            }
                        });
            }

//            else if(sortBy.equals(FireConstants.STR_ARTICLE_COUNT)){
//                // by article count
//                Retrieve.readUserOverViewByArticleCount(lastReadingArticleCount,
//                        false, context, (task, people) -> {
//
//                            progressShowMore.setVisibility(View.GONE);
//                            txtShowMore.setVisibility(View.VISIBLE);
//
//                            if(task == UserAccount.SUCCESS){
//                                if(people.size() > 0){
//                                    lastReadingArticleCount = people.get(people.size() - 1).getArticleCount();
//                                }
//                                setPeoples(people);
//                            }
//                        });
//            }

        }
        else if(view.getId() == imgSearchPeople.getId()){
            String username = edtSearchPeople.getText().toString().trim();
            if(username.isEmpty()){
                edtSearchPeople.requestFocus();
                edtSearchPeople.setError("Enter username");
            }
            else{
                layoutPeople.removeAllViews();
                getSearchedPeople(username);
            }
        }
    }

    private void getThePeopleByUsername(){
        inProgress();
        Retrieve.readUserOverViewByUsername("NO NEED", true, context, (task, people) -> {
            completeProgress();

            if(task == UserAccount.SUCCESS){
                if(people.size() > 0){
                    lastReadingUsername = people.get(people.size() - 1).getUsername();
                }
                if(people.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                    lastReadingUsername = "";
                    txtShowMore.setVisibility(View.GONE);
                }
                else{
                    txtShowMore.setVisibility(View.VISIBLE);
                }
                setPeoples(people);
            }
        });
    }

    private void getThePeopleByArticleCount(){
        inProgress();
        Retrieve.readUserOverViewByArticleCount(0, true, context, (task, people) -> {
            completeProgress();

            if(task == UserAccount.SUCCESS){
                lastReadingArticleCount++;
                setPeoples(people);
            }
        });
    }

    private void getSearchedPeople(String username){
        Retrieve.readUserOverViewBySearchWord(username, (task, people) -> {
            if(task == UserAccount.SUCCESS){
                setPeoples(people);
            }
        });
    }

    private void setPeoples(ArrayList<People> peoples){
        for(int i = 0; i < peoples.size(); i++){
            layoutPeople.addView(getPeopleView(peoples.get(i)));
        }
    }

    private View getPeopleView(People people){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_people_view, null);

        TextView txtProfileName = view.findViewById(R.id.txtProfileName),
                txtArticleCount = view.findViewById(R.id.txtArticleCount),
                txtFavourite = view.findViewById(R.id.txtFavourites);

        String s;
        long articleCount = -people.getArticleCount();
        if(articleCount == 0 || articleCount == 1) s = articleCount + " article";
        else s = articleCount + " articles";


        txtProfileName.setText(people.getUsername());
        txtArticleCount.setText(s);
        txtFavourite.setText(people.getLovesTo());
        view.setOnClickListener(getClickListenerForView(people.getUsername()));

        return view;
    }

    private View.OnClickListener getClickListenerForView(String username){
        return view -> {
            waitWithInfoListener.onWaitWithInfo(DataModel.MOVE_TO_PROFILE_ACTIVITY, username);
        };
    }

    private void populateSpinner(){

        sortByItems.add("Sort By");
        sortByItems.add("Username");
        sortByItems.add("Top 15 writer");
        StringArrayAdapter adapter = new StringArrayAdapter(sortByItems, context);
        spinnerSortBy.setAdapter(adapter);
    }

    private void inProgress(){
        layout_showMore.setVisibility(View.GONE);
        layoutPeople.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }

    private void completeProgress(){
        pBar.setVisibility(View.GONE);
        layoutPeople.setVisibility(View.VISIBLE);
        layout_showMore.setVisibility(View.VISIBLE);
    }

    private void removeSearchOption(){
        edtSearchPeople.setVisibility(View.INVISIBLE);
        imgSearchPeople.setVisibility(View.INVISIBLE);
    }

    private void addSearchOption(){
        edtSearchPeople.setVisibility(View.VISIBLE);
        imgSearchPeople.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String s = (String) adapterView.getItemAtPosition(i);
        /*
        sortByItems(0) = "Sort By";
        sortByItems(1) = "Username";
        sortByItems(2) = "Top 15 writer";
         */
        if(s.equals(sortByItems.get(0)) || s.equals(sortByItems.get(1))){
            sortBy = FireConstants.STR_USERNAME;
            addSearchOption();
        }
        else if(s.equals(sortByItems.get(2))){
            sortBy = FireConstants.STR_ARTICLE_COUNT;
            removeSearchOption();
        }

        ImageView imgDropDownIcon = view.findViewById(R.id.imgDropDownIcon);
        imgDropDownIcon.setVisibility(View.VISIBLE);

        layoutPeople.removeAllViews();
        getPeopleOverView();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void getPeopleOverView(){

        if(sortBy.equals(FireConstants.STR_USERNAME)){
            // by username
            getThePeopleByUsername();
        }
        else if(sortBy.equals(FireConstants.STR_ARTICLE_COUNT)){
            // by article count
            getThePeopleByArticleCount();
        }
    }
}
