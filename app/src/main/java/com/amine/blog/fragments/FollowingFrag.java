package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.MainActivity;
import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.People;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class FollowingFrag extends Fragment {

    private TextView txtProfileName, txtArticleCount, txtFavourite;
    private ProgressBar pBar;
    private LinearLayout layoutPeople;

    private DataSnapshot rootSnapshot;

    private ArrayList<People> peoples = new ArrayList<>();
    private int from = 1;

    private Context context;

    private OnWaitListenerWithStringInfo waitWithInfoListener;

    public FollowingFrag(){}

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

        initializeSnapshot();
    }

    private void initializeSnapshot(){
        pBar.setVisibility(View.VISIBLE);
        Retrieve.getFollowingSnapshot(MainActivity.userBasicInfo.getUserName(), snapshot -> {
            pBar.setVisibility(View.GONE);
            rootSnapshot = snapshot;
            formThePeopleList();
            setPeoples();
        });
    }

    private void formThePeopleList(){

        DataSnapshot snapshot = rootSnapshot;
        if(snapshot != null){
            peoples.clear();
            int start = 1;
            for(DataSnapshot usernameSnapshot : snapshot.getChildren()){

                if(start >= this.from){
                    String username = usernameSnapshot.getKey(),
                            lovesTo = getHobbies(usernameSnapshot.child(FireConstants.STR_PUBLIC_INFO)
                                    .child(FireConstants.STR_HOBBIES)),
                            skills = getHobbies(usernameSnapshot.child(FireConstants.STR_PUBLIC_INFO)
                                    .child(FireConstants.STR_EXPERTISE));
                    String s = "Loves to- " + lovesTo + "\n\n" + "Good at- " + skills;

                    long articleCount = usernameSnapshot.child(FireConstants.STR_ARTICLE).getChildrenCount();
                    People people = new People(username, s, articleCount);
                    peoples.add(people);
                }
                start++;
                if(start >= this.from + FireConstants.READING_NODE_LIMIT){
                    break;
                }

            }
            this.from = start;

        }
    }

    private void setPeoples(){
        for(int i = 0; i < peoples.size(); i++){
            layoutPeople.addView(getPeopleView(peoples.get(i)));
        }

        String s = "Show more...";
        TextView txtShowMore = new TextView(context);
        txtShowMore.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
        txtShowMore.setGravity(Gravity.CENTER);
        txtShowMore.setText(s);
        txtShowMore.setTextColor(getResources().getColor(R.color.green));
        txtShowMore.setOnClickListener(getClickListenerForShowMore(txtShowMore));
        layoutPeople.addView(txtShowMore);
    }

    private View getPeopleView(People people){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_people_view, null);

        TextView txtProfileName = view.findViewById(R.id.txtProfileName),
                txtArticleCount = view.findViewById(R.id.txtArticleCount),
                txtFavourite = view.findViewById(R.id.txtFavourites);
        txtArticleCount.setVisibility(View.GONE);
        /*
        String s;
        long articleCount = people.getArticleCount();
        if(articleCount == 0 || articleCount == 1) s = articleCount + " article";
        else s = articleCount + " articles";
         */


        txtProfileName.setText(people.getUsername());
        //txtArticleCount.setText(s);
        txtFavourite.setText(people.getLovesTo());
        view.setOnClickListener(getClickListenerForView(people.getUsername()));

        return view;
    }

    private View.OnClickListener getClickListenerForView(String username){
        return view -> {
            waitWithInfoListener.onWaitWithInfo(DataModel.MOVE_TO_PROFILE_ACTIVITY, username);
        };
    }

    private View.OnClickListener getClickListenerForShowMore(TextView txtShowMore){
        return view -> {
            txtShowMore.setVisibility(View.GONE);
            formThePeopleList();
            setPeoples();
        };
    }

    private String getHobbies(DataSnapshot snapshot){
        StringBuilder sb = new StringBuilder();
        if(snapshot.exists()){
            for(DataSnapshot item : snapshot.getChildren()){
                String s = item.getValue(String.class);
                sb.append(s).append(", ");
            }
        }
        return sb.toString();
    }
}
