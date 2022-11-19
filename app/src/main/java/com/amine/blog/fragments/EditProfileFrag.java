package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.MainActivity;
import com.amine.blog.R;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.People;
import com.amine.blog.model.UserBasicInfo;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class EditProfileFrag extends Fragment implements View.OnClickListener {

    private EditText edtProfileName, edtPhone, edtUniversity, edtProfession,
            edtHobby1, edtHobby2, edtHobby3, edtExpert1, edtExpert2, edtExpert3;
    private TextView txtPhoneSug;
    private ProgressBar pBar;
    private ScrollView sView;

    private String myUsername;

    private Context context;
    private OnWaitListener onWaitListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_profile_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        edtProfileName = view.findViewById(R.id.edtName);
        edtPhone = view.findViewById(R.id.edtEmail);
        edtUniversity = view.findViewById(R.id.edtUniversity);
        edtProfession = view.findViewById(R.id.edtProfession);
        edtHobby1 = view.findViewById(R.id.edtHobby1);
        edtHobby2 = view.findViewById(R.id.edtHobby2);
        edtHobby3 = view.findViewById(R.id.edtHobby3);
        edtExpert1 = view.findViewById(R.id.edtExpert1);
        edtExpert2 = view.findViewById(R.id.edtExpert2);
        edtExpert3 = view.findViewById(R.id.edtExpert3);
        pBar = view.findViewById(R.id.progress_editProfile);
        sView = view.findViewById(R.id.scroll_editProfile);
        txtPhoneSug = view.findViewById(R.id.txtPhoneSug);

        view.findViewById(R.id.btnSaveEdit).setOnClickListener(this);

        myUsername = MainActivity.userBasicInfo.getUserName();

        makeViewInvisible();
        readUserInfo();

        super.onViewCreated(view, savedInstanceState);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
    }

    private void makeViewInvisible(){
        sView.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }

    private void makeViewVisible(){
        pBar.setVisibility(View.GONE);
        sView.setVisibility(View.VISIBLE);
    }

    private void readUserInfo(){
        setPhoneNumberEditionSuggestion();
        Retrieve retrieve = new Retrieve(myUsername);
        retrieve.setOnStringArrayListener(stringArrayList -> {
            Retrieve retrieve1 = new Retrieve(myUsername);
            retrieve1.setOnStringArrayListener(stringArrayList1 ->
                    Retrieve.readPublicPhoneNumber(myUsername, (task, phone) -> {
                makeViewVisible();
                setOldData(stringArrayList, stringArrayList1, phone);
            }));
            retrieve1.getExpertiseList();
        });
        retrieve.getHobbyList();
    }

    private void setPhoneNumberEditionSuggestion() {
        String s = "1. If you want to change your phone number, just type new phone number.\n2. If you don't want to change, " +
                "don't change it";
        txtPhoneSug.setText(s);
    }

    private void setOldData(ArrayList<String> hobbyList, ArrayList<String> expertList, String phone) {
        edtProfileName.setText(MainActivity.userBasicInfo.getProfileName());
        if(phone != null){
            edtPhone.setText(phone);
        }
        String prof = MainActivity.userBasicInfo.getProfession();
        if(prof != null){
            edtProfession.setText(prof);
        }
        edtUniversity.setText(MainActivity.userBasicInfo.getUniversity());
        edtHobby1.setText(hobbyList.get(0));
        edtHobby2.setText(hobbyList.get(1));
        edtHobby3.setText(hobbyList.get(2));
        edtExpert1.setText(expertList.get(0));
        edtExpert2.setText(expertList.get(1));
        edtExpert3.setText(expertList.get(2));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSaveEdit){
            String email = edtPhone.getText().toString().trim(),
                    name = edtProfileName.getText().toString().trim(),
                    university = edtUniversity.getText().toString().trim(),
                    profession = edtProfession.getText().toString().trim(),

                    hobby1 = edtHobby1.getText().toString().trim(),
                    hobby2 = edtHobby2.getText().toString().trim(),
                    hobby3 = edtHobby3.getText().toString().trim(),

                    expert1 = edtExpert1.getText().toString().trim(),
                    expert2 = edtExpert2.getText().toString().trim(),
                    expert3 = edtExpert3.getText().toString().trim();


            // Checking if fields are empty
            if(email.equals("") || email.length() < 2) {
                edtPhone.requestFocus();
                edtPhone.setError("Provide your phone no., none can see your phone no.");
                return;
            }

            if(name.equals("")){
                edtProfileName.requestFocus();
                edtProfileName.setError("Provide your name");
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
                edtPhone.requestFocus();
                edtPhone.setSelection(ind);
                edtPhone.setError("Phone No. can not have space");
                return;
            }

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

            ArrayList<String> hobbies = new ArrayList<>(), expertise = new ArrayList<>();
            hobbies.add(hobby1);hobbies.add(hobby2);hobbies.add(hobby3);
            expertise.add(expert1);expertise.add(expert2);expertise.add(expert3);

            UserBasicInfo userBasicInfo =
                    new UserBasicInfo(name, myUsername, university, profession);

            new Save().saveUserPublicInfo(myUsername, userBasicInfo, hobbies, expertise);
            if(email.charAt(0) != '*' && email.charAt(1) != '*'){
                new Save().savePhoneNumb(myUsername, email);
            }

            String s = "Loves to- " + hobby1 + ", " + hobby2 + ", " + hobby3 + "\n\n" + "Good at- " +
                    expert1 + ", " + expert2 + ", " + expert3;

            People people = new People(myUsername, s, 0);
            Save.saveMyOverview(people, true);

            Toast.makeText(context, "Information saved", Toast.LENGTH_SHORT).show();
            onWaitListener.onWaitCallback(DataModel.MOVE_TO_MAIN_ACTIVITY_HOME);
        }
    }
}
