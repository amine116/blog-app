package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.interfaces.OnReadPeople;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.People;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class PeopleFrag extends Fragment implements View.OnClickListener {

    private TextView txtProfileName, txtArticleCount, txtFavourite, txtShowMore;
    private ProgressBar pBar, progressShowMore;
    private LinearLayout layoutPeople;
    private RelativeLayout layout_showMore;

    private DataSnapshot rootSnapshot;

    private ArrayList<People> peoples = new ArrayList<>();
    private int from = 1;
    private String lastReadingUsername = "0a";

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

        txtShowMore.setOnClickListener(this);

        getThePeople();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == txtShowMore.getId()){
            txtShowMore.setVisibility(View.GONE);
            progressShowMore.setVisibility(View.VISIBLE);

            Retrieve.readUserOverView(lastReadingUsername, false, context, (task, people) -> {

                progressShowMore.setVisibility(View.GONE);
                txtShowMore.setVisibility(View.VISIBLE);

                if(task == UserAccount.SUCCESS){
                    if(people.size() > 0){
                        lastReadingUsername = people.get(people.size() - 1).getUsername();
                    }
                    setPeoples(people);
                }
            });
        }
    }

    private void getThePeople(){
        inProgress();
        Retrieve.readUserOverView(lastReadingUsername, true, context, (task, people) -> {
            completeProgress();

            if(task == UserAccount.SUCCESS){
                if(people.size() > 0){
                    lastReadingUsername = people.get(people.size() - 1).getUsername();
                }
                setPeoples(people);
            }
        });
    }

    private void setPeoples(ArrayList<People> peoples){
        for(int i = 0; i < peoples.size(); i++){
            layoutPeople.addView(getPeopleView(peoples.get(i)));
        }
//        String s = "Show more...";
//        TextView txtShowMore = new TextView(context);
//        txtShowMore.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
//        txtShowMore.setGravity(Gravity.CENTER);
//        txtShowMore.setText(s);
//        txtShowMore.setTextColor(getResources().getColor(R.color.green));
//        txtShowMore.setOnClickListener(getClickListenerForShowMore(txtShowMore));
//        layoutPeople.addView(txtShowMore);
    }

    private View getPeopleView(People people){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_people_view, null);

        TextView txtProfileName = view.findViewById(R.id.txtProfileName),
                txtArticleCount = view.findViewById(R.id.txtArticleCount),
                txtFavourite = view.findViewById(R.id.txtFavourites);

        String s;
        long articleCount = people.getArticleCount();
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
}
