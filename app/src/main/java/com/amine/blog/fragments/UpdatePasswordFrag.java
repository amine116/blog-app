package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.repositories.Save;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;

public class UpdatePasswordFrag extends Fragment implements View.OnClickListener {

    private EditText edtOldPass, edtNewPass, edtConfirmPass;
    private Button btnChange;
    private ProgressBar prChange;
    private ScrollView sView;

    private String myUsername;

    private Context context;
    private OnWaitListener onWaitListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_password_frag, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtOldPass = view.findViewById(R.id.edtOldPassword);
        edtNewPass = view.findViewById(R.id.edtNewPassword);
        edtConfirmPass = view.findViewById(R.id.edtNewPassword2);
        btnChange = view.findViewById(R.id.btnChangePassword);
        prChange = view.findViewById(R.id.prChange);
        sView = view.findViewById(R.id.scrollInfo);

        btnChange.setOnClickListener(this);

    }

    public void setMyUsername(String myUsername) {
        this.myUsername = myUsername;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnChange.getId()){

            String oldPass = edtOldPass.getText().toString().trim(),
                    newPass = edtNewPass.getText().toString().trim(),
                    confPass = edtConfirmPass.getText().toString().trim();

            if (isPasswordValid(oldPass, newPass, confPass)){
                if(confPass.equals(newPass)){

                    inProgress();

                    UserAccount.reAuthenticate(myUsername, oldPass, task -> {
                        if(task == UserAccount.SUCCESS){
                            UserAccount.updatePassword(newPass, task1 -> {

                                completeProgress();

                                if(task1 == UserAccount.SUCCESS){
                                    Toast.makeText(context, "You password has been updated",
                                            Toast.LENGTH_SHORT).show();

                                    Save.saveNewPass(myUsername, newPass);

                                    onWaitListener.onWaitCallback(DataModel.MOVE_TO_MAIN_ACTIVITY_HOME);

                                }
                                else if(task1 == UserAccount.FAIL){
                                    Toast.makeText(context, "Password Update failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(task1 == UserAccount.USER_NOT_SIGNED_IN){
                                    Toast.makeText(context, "You are not signed in", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context, "Password Update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if(task == UserAccount.FAIL){
                            completeProgress();
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                        else if(task == UserAccount.USER_NOT_SIGNED_IN){
                            completeProgress();
                            Toast.makeText(context, "You are not signed in", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            completeProgress();
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    edtConfirmPass.requestFocus();
                    edtConfirmPass.setError("Password did not match");
                }
            }
        }
    }

    private boolean isPasswordValid(String oldPass, String newPass, String confPass){
        if(oldPass.equals("")){
            edtOldPass.requestFocus();
            edtOldPass.setError("Choose a strong password");
            return false;
        }

        if(oldPass.length() < 6){
            edtOldPass.requestFocus();
            edtOldPass.setError("Password be at least 6 character long");
            return false;
        }

        if(newPass.equals("")){
            edtNewPass.requestFocus();
            edtNewPass.setError("Choose a strong password");
            return false;
        }

        if(newPass.length() < 6){
            edtNewPass.requestFocus();
            edtNewPass.setError("Password be at least 6 character long");
            return false;
        }

        if(confPass.equals("")){
            edtConfirmPass.requestFocus();
            edtConfirmPass.setError("Choose a strong password");
            return false;
        }

        if(confPass.length() < 6){
            edtConfirmPass.requestFocus();
            edtConfirmPass.setError("Password be at least 6 character long");
            return false;
        }

        return true;

    }

    private void inProgress(){
        sView.setVisibility(View.GONE);
        prChange.setVisibility(View.VISIBLE);
    }

    private void completeProgress(){
        prChange.setVisibility(View.GONE);
        sView.setVisibility(View.VISIBLE);
    }
}
