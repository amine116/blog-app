package com.amine.blog.repositories;

import androidx.annotation.NonNull;

import com.amine.blog.interfaces.CallbackForSignIn;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.viewmodel.DataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

public class UserAccount {
    private FirebaseAuth fAuth;
    private String email, userName, pass, name, university, profession;
    private boolean isDummy;
    private CallbackForSignIn cbfsi;
    private OnWaitListener onWaitListener;


    // public static values
    // This values of this calss are from 21 - 30;
    public static int USER_COLLIDE = 21, SUCCESS = 22, FAIL = 23,
            USER_DO_NOT_EXIST = 24, UNKNOWN_REASON = 25, USER_NOT_SIGNED_IN = 26;

    public UserAccount(String userName, String pass){
        this.userName = userName;
        this.pass = pass;
        this.fAuth = FirebaseAuth.getInstance();
    }

    public UserAccount(String userName, String pass, boolean isDummy, CallbackForSignIn cbfsi){
        this.userName = userName;
        this.pass = pass;
        this.isDummy = isDummy;
        this.cbfsi = cbfsi;
        fAuth = FirebaseAuth.getInstance();
    }

    public UserAccount(String email, String userName, String pass, String name, String university, String profession,
                       boolean isDummy, CallbackForSignIn cbfsi){
        this.email = email;
        this.userName = userName;
        this.pass = pass;
        this.name = name;
        this.university = university;
        this.profession = profession;
        this.isDummy = isDummy;
        this.cbfsi = cbfsi;
        this.fAuth = FirebaseAuth.getInstance();
    }

    public void setWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public boolean isDummy(){
        return isDummy;
    }

    public void signIn(){
        fAuth.signInWithEmailAndPassword(DataModel.getEmailFromUsername(userName), pass)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        onWaitListener.onWaitCallback(SUCCESS);
                    }
                    else{
                        onWaitListener.onWaitCallback(FAIL);
                    }
                });
    }

    public static void signInWithPhoneAuthCredential(PhoneAuthCredential credential, OnWaitListener waitListener){

        FirebaseAuth fAuth = Retrieve.getFirebaseAuth();
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        fAuth.signOut();
                        FirebaseUser user = task.getResult().getUser();
                        if(user != null ) {
                            user.delete();
                        }

                        waitListener.onWaitCallback(UserAccount.SUCCESS);
                    }
                    else{
                        waitListener.onWaitCallback(UserAccount.FAIL);
                    }
                });

    }

    public static void signOut(String userName){
        Save.activeStatus(userName, false);
        FirebaseAuth.getInstance().signOut();
    }

    public void create(){
        fAuth.createUserWithEmailAndPassword(DataModel.getEmailFromUsername(userName), pass)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(isDummy){
                            //DataModel.deb("success dummy");
                            FirebaseUser user = fAuth.getCurrentUser();
                            if(user != null) user.delete();
                            fAuth.signOut();
                        }
                        cbfsi.callback(SUCCESS);
                    }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        //DataModel.deb("user collides");
                        cbfsi.callback(USER_COLLIDE);
                    }
                    else{
                        //DataModel.deb("account not created- Unknown reason");
                        /*
                        if(task.getException() != null){
                            DataModel.deb(task.getException().toString());
                        }
                         */
                        cbfsi.callback(UNKNOWN_REASON);
                    }
                });
    }

    public static void reAuthenticate(String username, String oldPassword, OnWaitListener onWait){
        AuthCredential credential = EmailAuthProvider.getCredential(DataModel.getEmailFromUsername(username), oldPassword);
        FirebaseUser user = Retrieve.getAuth().getCurrentUser();
        if(user != null){
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        onWait.onWaitCallback(UserAccount.SUCCESS);
                    }
                    else{
                        onWait.onWaitCallback(UserAccount.FAIL);
                    }
                }
            });
        }
        else{
            onWait.onWaitCallback(UserAccount.USER_NOT_SIGNED_IN);
        }
    }

    public static void updatePassword(String newPass, OnWaitListener onWait){
        FirebaseUser user = Retrieve.getAuth().getCurrentUser();
        if(user != null){
            user.updatePassword(newPass).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    onWait.onWaitCallback(UserAccount.SUCCESS);
                }
                else{
                    onWait.onWaitCallback(UserAccount.FAIL);
                }
            });
        }
        else {
            onWait.onWaitCallback(UserAccount.USER_NOT_SIGNED_IN);
        }
    }
}
