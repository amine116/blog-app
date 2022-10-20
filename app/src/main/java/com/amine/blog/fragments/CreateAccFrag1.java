package com.amine.blog.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.interfaces.CallbackForFr2;
import com.amine.blog.interfaces.CallbackForSignIn;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;

public class CreateAccFrag1 extends Fragment implements View.OnClickListener, CallbackForSignIn {

    private final CallbackForFr2 cbfr2;
    private EditText edtEmail, edtUsername, edtPassword, edtConfirmPass, edtName, edtUniversity, edtProfession;
    private UserAccount userAccount;
    private ProgressBar prBar;
    private ScrollView sv;
    private ImageView imgEye;
    private Button btnCreateAcc1;

    private boolean passwordShown = false;

    public CreateAccFrag1(CallbackForFr2 cbfr2){
        this.cbfr2 = cbfr2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.create_account_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        edtEmail = view.findViewById(R.id.edtEmail);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPass = view.findViewById(R.id.edtConfirmPass);
        edtName = view.findViewById(R.id.edtName);
        edtUniversity = view.findViewById(R.id.edtUniversity);
        edtProfession = view.findViewById(R.id.edtProfession);
        imgEye = view.findViewById(R.id.imgEye);
        btnCreateAcc1 = view.findViewById(R.id.btnCrAcc1);

        prBar = view.findViewById(R.id.progress_crAcc1);
        sv = view.findViewById(R.id.scroll_crAcc1);

        // Listeners
        btnCreateAcc1.setOnClickListener(this);
        imgEye.setOnClickListener(this);
        //edtUsername.setOnKeyListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnCrAcc1){

            String email = edtEmail.getText().toString().trim(),
                    userName = edtUsername.getText().toString().trim(),
                    password = edtPassword.getText().toString(),
                    confirmPass = edtConfirmPass.getText().toString(),
                    name = edtName.getText().toString().trim(),
                    university = edtUniversity.getText().toString().trim(),
                    profession = edtProfession.getText().toString().trim();


            // Checking if fields are empty
            if(email.equals("")) {
                edtEmail.requestFocus();
                edtEmail.setError("Provide your phone, none can see your phone");
                return;
            }
            if(userName.equals("")){
                edtUsername.requestFocus();
                edtUsername.setError("Choose a unique username");
                return;
            }
            if(password.equals("")){
                edtPassword.requestFocus();
                edtPassword.setError("Choose a strong password");
                return;
            }
            if(confirmPass.equals("")){
                edtConfirmPass.requestFocus();
                edtConfirmPass.setError("Confirm password");
                return;
            }
            if(!confirmPass.equals(password)){
                edtConfirmPass.requestFocus();
                edtConfirmPass.setError("Password didn't match");
                return;
            }
            if(password.length() < 6){
                edtPassword.requestFocus();
                edtPassword.setError("Password be at least 6 character long");
                return;
            }
            if(email.length() < 2){
                edtEmail.requestFocus();
                edtEmail.setError("Phone no. can't be less than two digits");
                return;
            }
            if(name.equals("")){
                edtName.requestFocus();
                edtName.setError("Provide your name");
                return;
            }
            if(university.equals("")){
                edtUniversity.requestFocus();
                edtUniversity.setError("Write where you studied");
                return;
            }
            if(profession.isEmpty()){
                edtProfession.requestFocus();
                edtProfession.setError("Profession must be given");
                return;
            }

            // checking if they are field correctly
            int ind = new DataModel().hasChar(email , ' ');
            if(ind > -1) {
                edtEmail.requestFocus();
                edtEmail.setSelection(ind);
                edtEmail.setError("Phone No. can not have space");
                return;
            }
            ind = new DataModel().hasChar(userName, ' ');
            if(ind > -1){
                edtUsername.requestFocus();
                edtUsername.setSelection(ind);
                edtUsername.setError("Username can not have space");
                return;
            }

            ind = new DataModel().isUsernameCorrect(userName);
            if(ind > -1){
                edtUsername.requestFocus();
                edtUsername.setSelection(ind);
                edtUsername.setError("Username can only contain small letters, numbers, underscore, and hyphen");
                return;
            }

            btnCreateAcc1.setEnabled(false);
            inProgress();
            userAccount = new UserAccount(email, userName, password, name,
                    university, profession, true, this);
            userAccount.create();

        }
        if(view.getId() == imgEye.getId()){
            if(passwordShown){
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edtConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgEye.setImageResource(R.drawable.ic_visibility);
                passwordShown = false;
            }
            else{
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                edtConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT);
                imgEye.setImageResource(R.drawable.ic_visibility_off);
                passwordShown = true;
            }
        }
    }

    @Override
    public void callback(int res) {
        completeProgress();
        btnCreateAcc1.setEnabled(true);
        if(res == UserAccount.USER_COLLIDE){
            edtUsername.setError("Username is taken");
            edtUsername.requestFocus();
        }
        else if(res == UserAccount.SUCCESS && userAccount.isDummy()){
            cbfr2.callback(userAccount.getEmail(), userAccount.getUserName(),
                    userAccount.getPass(), userAccount.getName(),
                    userAccount.getUniversity(), userAccount.getProfession());

        }

    }

    private void inProgress(){
        sv.setVisibility(View.GONE);
        prBar.setVisibility(View.VISIBLE);
    }
    private void completeProgress(){
        prBar.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);
    }

}
