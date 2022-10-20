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
import com.amine.blog.dialogs.SimpleDialog;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.repositories.Save;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class CreateAccFrag2 extends Fragment implements View.OnClickListener{

    // Values got from fragment1
    private String email, userName, password, name, university, profession;

    // private values of this fragment
    private Button btnCreate;
    private EditText edtHobby1, edtHobby2, edtHobby3, edtExpert1, edtExpert2, edtExpert3;
    private ArrayList<String> hobbies, expertise;

    private OnWaitListener onWaitListener;

    // Views of this fragment2
    private Context context;
    private ScrollView sv;
    private ProgressBar pBar;

    public CreateAccFrag2(String email, String userName, String password, String name, String university,
                          String profession) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.university = university;
        this.profession = profession;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_account_fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnCreate = view.findViewById(R.id.btnCrAcc2);
        btnCreate.setOnClickListener(this);

        edtHobby1 = view.findViewById(R.id.edtHobby1);
        edtHobby2 = view.findViewById(R.id.edtHobby2);
        edtHobby3 = view.findViewById(R.id.edtHobby3);
        edtExpert1 = view.findViewById(R.id.edtExpert1);
        edtExpert2 = view.findViewById(R.id.edtExpert2);
        edtExpert3 = view.findViewById(R.id.edtExpert3);

        sv = view.findViewById(R.id.scroll_crAcc2);
        pBar = view.findViewById(R.id.progress_crAcc2);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnCrAcc2){
            String hobby1 = edtHobby1.getText().toString(),
                    hobby2 = edtHobby2.getText().toString(),
                    hobby3 = edtHobby3.getText().toString(),

                    expert1 = edtExpert1.getText().toString(),
                    expert2 = edtExpert2.getText().toString(),
                    expert3 = edtExpert3.getText().toString();

            if(hobby1.equals("") && hobby2.equals("") && hobby3.equals("")){
                edtHobby1.setError("Provide at lease one hobby");
                edtHobby1.requestFocus();
                return;
            }
            if(expert1.equals("") && expert2.equals("") && expert3.equals("")){
                edtExpert1.setError("Provide at lease one hobby");
                edtExpert1.requestFocus();
                return;
            }

            hobbies = new ArrayList<>(); expertise = new ArrayList<>();
            hobbies.add(hobby1);hobbies.add(hobby2);hobbies.add(hobby3);
            expertise.add(expert1);expertise.add(expert2);
            expertise.add(expert3);

            UserBasicInfo userBasicInfo = new UserBasicInfo(name, userName, university, profession);

            inProgress();
            UserAccount userAccount = new UserAccount(email, userName, password, name, university, profession,
                    false, res -> {

                completeProgress();
                if(res == UserAccount.SUCCESS){
                    new Save().saveUserPublicInfo(userName, userBasicInfo, hobbies, expertise);
                    new Save().saveEmailAndPassword(userName, password, email);
                    Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show();
                    onWaitListener.onWaitCallback(DataModel.MOVE_TO_MAIN_ACTIVITY_HOME);
                }
                else if(res == UserAccount.USER_COLLIDE){
                    SimpleDialog sd =
                            new SimpleDialog(context,
                                    "User with username '" + userName + "' already Exists");
                    sd.setActionType(DataModel.STR_DISMISS);
                    sd.show();
                }
                else{
                    Toast.makeText(requireContext(),
                            "account creation failed; sign In Code: " + res, Toast.LENGTH_LONG).show();
                }
            });
            userAccount.create();
        }
    }

    private void inProgress(){
        sv.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }

    private void completeProgress(){
        pBar.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);
    }

}
