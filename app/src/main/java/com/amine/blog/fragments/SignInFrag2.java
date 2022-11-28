package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.MainActivity;
import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.repositories.FireConstants;
import com.amine.blog.repositories.SignInFile;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;

public class SignInFrag2 extends Fragment implements View.OnClickListener, OnWaitListener {

    private TextView txtGuestSignIn;
    private EditText edtUsername, edtPass;
    private Button btnSignIn;
    private OnWaitListener onWaitListener;
    private Context context;
    private ProgressBar pBar;
    private RelativeLayout rl;

    private Animation slide_down, slide_up;


    public SignInFrag2(){}

    public void setWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_in_fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        txtGuestSignIn = view.findViewById(R.id.txtGuestSignIn);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtPass = view.findViewById(R.id.edtPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        pBar = view.findViewById(R.id.progress_sign_in);
        rl = view.findViewById(R.id.layout_sign_in_parent);


        txtGuestSignIn.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

        slide_down = AnimationUtils.loadAnimation(context, R.anim.animation_slide_down);
        slide_up = AnimationUtils.loadAnimation(context, R.anim.animation_slide_up);
        slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onWaitListener.onWaitCallback(DataModel.MOVE_TO_MAIN_ACTIVITY_HOME);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rl.startAnimation(slide_up);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnSignIn.getId()){
            String username = edtUsername.getText().toString(),
                    pass = edtPass.getText().toString();
            if(username.equals("")){
                edtUsername.requestFocus();
                edtUsername.setError("Your username");
                return;
            }
            if(pass.equals("") || pass.length() < 6){
                edtPass.requestFocus();
                edtPass.setError("Write your 6 character long password");
                return;
            }
            inProgress();

            btnSignIn.setEnabled(false);

            MainActivity.isGuest = false;
            UserAccount account = new UserAccount(username, pass);
            account.setWaitListener(this);
            account.signIn();

        }
        else if(view.getId() == txtGuestSignIn.getId()){
            MainActivity.isGuest = true;
            SignInFile signInFile = new SignInFile(context);
            signInFile.createFile();
            signInFile.writeFile(FireConstants.STR_GUEST_USER_USERNAME);

            MainActivity.userBasicInfo = new UserBasicInfo(FireConstants.STR_GUEST_USER_USERNAME,
                    FireConstants.STR_GUEST_USER_USERNAME, FireConstants.STR_GUEST_USER_USERNAME,
                    FireConstants.STR_GUEST_USER_USERNAME);
            rl.startAnimation(slide_down);
        }
    }

    @Override
    public void onWaitCallback(int task) {

        completeProgress();
        if(task == UserAccount.SUCCESS){
            rl.startAnimation(slide_down);
        }
        else if(task == UserAccount.FAIL){
            edtUsername.requestFocus();
            edtUsername.setError("Sign In Failed- error code: " + task);
            btnSignIn.setEnabled(true);
        }
    }

    private void completeProgress(){
        pBar.setVisibility(View.GONE);
        rl.setVisibility(View.VISIBLE);
    }

    private void inProgress(){
        rl.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }
}
