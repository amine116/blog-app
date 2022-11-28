package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.viewmodel.DataModel;

public class SignInFrag extends Fragment implements View.OnClickListener {

    // views of this fragment
    private TextView txtSignIn, txtCreateAcc, txtRecoverAccount;
    private RelativeLayout layout_info;
    private Context context;
    private OnWaitListener onWaitListener;
    private Animation slide_down, slide_up;

    private char clickedButton;

    public SignInFrag(){}

    public void addWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_in_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        txtSignIn = view.findViewById(R.id.txtSignIn);
        txtCreateAcc = view.findViewById(R.id.txtCreateNewAcc);
        txtRecoverAccount = view.findViewById(R.id.txtRecoverAccount);
        layout_info = view.findViewById(R.id.layout_info);

        txtSignIn.setOnClickListener(this);
        txtCreateAcc.setOnClickListener(this);
        txtRecoverAccount.setOnClickListener(this);


        slide_down = AnimationUtils.loadAnimation(context, R.anim.animation_slide_down);
        slide_up = AnimationUtils.loadAnimation(context, R.anim.animation_slide_up);
        slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if(clickedButton == 's'){
                    onWaitListener.onWaitCallback(DataModel.MOVE_TO_SIGN_IN_PAGE_2);
                }
                else if(clickedButton == 'c'){
                    onWaitListener.onWaitCallback(DataModel.MOVE_TO_CREATE_ACCOUNT_FRAGMENT);
                }
                else if(clickedButton == 'r'){
                    onWaitListener.onWaitCallback(DataModel.MOVE_TO_RECOVER_ACCOUNT);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout_info.startAnimation(slide_up);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == txtSignIn.getId()){
            clickedButton = 's';
            layout_info.startAnimation(slide_down);

        }
        else if(view.getId() == txtCreateAcc.getId()){
            clickedButton = 'c';
            layout_info.startAnimation(slide_down);

        }
        else if(view.getId() == txtRecoverAccount.getId()){
            clickedButton = 'r';
            layout_info.startAnimation(slide_down);

        }
    }
}
