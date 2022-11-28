package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.MainActivity;
import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class FollowerFrag extends Fragment implements View.OnClickListener {
    private TextView txtShowMore, txtFollowerHead;
    private ProgressBar pBar, prShowMore;
    private LinearLayout layoutPeople;
    private Context context;
    private ImageView imgSearch;
    private EditText edtSearch;

    private String lastReadingUsername;

    private OnWaitListenerWithStringInfo waitWithInfoListener;

    public FollowerFrag(){}

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
        pBar = view.findViewById(R.id.progress_people);
        layoutPeople = view.findViewById(R.id.layout_people);
        txtShowMore = view.findViewById(R.id.txtShowMore);
        prShowMore = view.findViewById(R.id.progressShowMore);
        imgSearch = view.findViewById(R.id.imgSearchPeople);
        edtSearch = view.findViewById(R.id.edtSearchPeople);
        txtFollowerHead = view.findViewById(R.id.txtFollowerHead);
        view.findViewById(R.id.spinnerSortBy).setVisibility(View.GONE);

        txtShowMore.setOnClickListener(this);
        imgSearch.setOnClickListener(this);

        getFollowers();
    }

    private void getFollowers(){
        pBar.setVisibility(View.VISIBLE);
        Retrieve.getMyFollower(MainActivity.userBasicInfo.getUserName(), "No need here", true,
                followers -> {
                    pBar.setVisibility(View.GONE);
                    if(followers.size() > 0){
                        lastReadingUsername = followers.get(followers.size() - 1).getArticleId();
                    }
                    if(followers.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                        lastReadingUsername = "";
                        txtShowMore.setVisibility(View.GONE);
                    }
                    else{
                        txtShowMore.setVisibility(View.VISIBLE);
                    }

                    String followerHead = "Follower";
                    txtFollowerHead.setText(followerHead);

                    setPeoples(followers);
                });
    }

    private void setPeoples(ArrayList<ArticlesUnderTag> following){
        for(int i = 0; i < following.size(); i++){
            layoutPeople.addView(getPeopleView(following.get(i)));
        }

    }

    private View getPeopleView(ArticlesUnderTag people){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_people_view, null);

        TextView txtProfileName = view.findViewById(R.id.txtProfileName),
                txtArticleCount = view.findViewById(R.id.txtArticleCount),
                txtFavourite = view.findViewById(R.id.txtFavourites);
        txtArticleCount.setText(people.getHeadLine());
        txtProfileName.setText(people.getArticleId());
        txtFavourite.setVisibility(View.GONE);
        view.setOnClickListener(getClickListenerForView(people.getArticleId()));

        return view;
    }

    private View.OnClickListener getClickListenerForView(String username){
        return view -> {
            waitWithInfoListener.onWaitWithInfo(DataModel.MOVE_TO_PROFILE_ACTIVITY, username);
        };
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == txtShowMore.getId()){
            txtShowMore.setVisibility(View.GONE);
            prShowMore.setVisibility(View.VISIBLE);
            if(lastReadingUsername != null && !lastReadingUsername.isEmpty()){
                Retrieve.getMyFollower(MainActivity.userBasicInfo.getUserName(), lastReadingUsername, false,
                        followers -> {
                            prShowMore.setVisibility(View.GONE);
                            txtShowMore.setVisibility(View.VISIBLE);
                            if(followers.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                                lastReadingUsername = "";
                                txtShowMore.setVisibility(View.GONE);
                            }
                            else{
                                txtShowMore.setVisibility(View.VISIBLE);
                            }
                            setPeoples(followers);
                        });
            }
            else{
                txtShowMore.setVisibility(View.VISIBLE);
                prShowMore.setVisibility(View.GONE);
            }
        }
        else if(view.getId() == imgSearch.getId()){
            String username = edtSearch.getText().toString().trim();
            if(username.isEmpty()){
                edtSearch.requestFocus();
                edtSearch.setError("Enter username");
            }
            else{
                layoutPeople.removeAllViews();
                pBar.setVisibility(View.VISIBLE);
                lastReadingUsername = "";
                Retrieve.readMyFollowerBySearchWord(MainActivity.userBasicInfo.getUserName(), username, followers -> {
                    pBar.setVisibility(View.GONE);
                    setPeoples(followers);
                });
            }

        }
    }
}
