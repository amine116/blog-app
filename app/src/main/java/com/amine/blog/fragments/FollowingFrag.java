package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
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
import com.amine.blog.interfaces.OnReadArticleUnderATag;
import com.amine.blog.interfaces.OnWaitListenerWithStringArrayInfo;
import com.amine.blog.interfaces.OnWaitListenerWithStringInfo;
import com.amine.blog.model.Article;
import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.model.People;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class FollowingFrag extends Fragment implements View.OnClickListener {

    private TextView txtShowMore;
    private ProgressBar pBar, prShowMore;
    private LinearLayout layoutPeople;
    private Context context;
    private ImageView imgSearch;
    private EditText edtSearch;

    private ArrayList<People> peoples = new ArrayList<>();
    private String lastReadingUsername;

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
        pBar = view.findViewById(R.id.progress_people);
        layoutPeople = view.findViewById(R.id.layout_people);
        txtShowMore = view.findViewById(R.id.txtShowMore);
        prShowMore = view.findViewById(R.id.progressShowMore);
        imgSearch = view.findViewById(R.id.imgSearchPeople);
        edtSearch = view.findViewById(R.id.edtSearchPeople);

        txtShowMore.setOnClickListener(this);
        imgSearch.setOnClickListener(this);

        initializeSnapshot();
    }

    private void initializeSnapshot(){
        pBar.setVisibility(View.VISIBLE);
        Retrieve.getMyFollowing(MainActivity.userBasicInfo.getUserName(), "No need here", true,
                following -> {
                    pBar.setVisibility(View.GONE);
                    if(following.size() > 0){
                        lastReadingUsername = following.get(following.size() - 1).getArticleId();
                    }
                    if(following.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                        lastReadingUsername = "";
                    }
                    setPeoples(following);
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
        txtArticleCount.setVisibility(View.GONE);
        /*
        String s;
        long articleCount = people.getArticleCount();
        if(articleCount == 0 || articleCount == 1) s = articleCount + " article";
        else s = articleCount + " articles";
         */


        txtProfileName.setText(people.getArticleId());
        txtFavourite.setText(people.getHeadLine());
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
                Retrieve.getMyFollowing(MainActivity.userBasicInfo.getUserName(), lastReadingUsername, false,
                        following -> {
                            prShowMore.setVisibility(View.GONE);
                            txtShowMore.setVisibility(View.VISIBLE);
                            if(following.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                                lastReadingUsername = "";
                            }
                            setPeoples(following);
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
                Retrieve.readMyFollowingBySearchWord(MainActivity.userBasicInfo.getUserName(), username, following -> {
                    pBar.setVisibility(View.GONE);
                    setPeoples(following);
                });
            }

        }
    }
}
