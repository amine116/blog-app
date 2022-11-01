package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.adapters.CountryNameAdapter;
import com.amine.blog.model.ChatMessage;
import com.amine.blog.model.CountryCode;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecoverAccountFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ProgressBar progressBar;
    private EditText edtUsername, edtPhone, edtOtp, edtPass;
    private TextView txtLastDigitsOfPhoneNumber, txtHeadlineOfSendingOtp, txtOtpPassHeadline ,txtFinalMessageToUser;
    private RelativeLayout layout_provideUsername, layout_providePhone, layout_otp, layout_providePass;
    private Button btnNext, btnNext2, btnNext3, btnChange;
    private Spinner spinnerCountry;

    private String providedUsername, selectedCountryCode, providedPhone;

    private ArrayList<CountryCode> countryCodes = new ArrayList<>();

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recover_account_frag, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        progressBar = view.findViewById(R.id.progressBar);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtOtp = view.findViewById(R.id.edtOtp);
        edtPass = view.findViewById(R.id.edtPass);
        txtLastDigitsOfPhoneNumber = view.findViewById(R.id.txtLastDigitsOfPhoneNumber);
        txtHeadlineOfSendingOtp = view.findViewById(R.id.txtHeadlineOfSendingOtp);
        txtOtpPassHeadline = view.findViewById(R.id.txtOtpPassHeadline);
        txtFinalMessageToUser = view.findViewById(R.id.txtFinalMessageToUser);
        layout_provideUsername = view.findViewById(R.id.layout_provideUsername);
        layout_providePhone = view.findViewById(R.id.layout_providePhone);
        layout_otp = view.findViewById(R.id.layout_otp);
        layout_providePass = view.findViewById(R.id.layout_providePass);
        btnNext = view.findViewById(R.id.btnNext);
        btnNext2 = view.findViewById(R.id.btnNext2);
        btnNext3 = view.findViewById(R.id.btnNext3);
        btnChange = view.findViewById(R.id.btnChange);
        spinnerCountry = view.findViewById(R.id.spinner_country);

        makeJsonArrayForCountry();

        btnChange.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnNext2.setOnClickListener(this);
        btnNext3.setOnClickListener(this);
        spinnerCountry.setOnItemSelectedListener(this);


        super.onViewCreated(view, savedInstanceState);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == btnNext.getId()){
            providedUsername = edtUsername.getText().toString().trim();
            if(providedUsername.isEmpty()){
                edtUsername.requestFocus();
                edtUsername.setError("Enter your username");
                return;
            }

            inProgress();

            UserAccount ua = new UserAccount(providedUsername, "123456", true, res -> {

                if(res == UserAccount.USER_COLLIDE){

                    Retrieve.readPublicPhoneNumber(providedUsername, (task, data) -> {
                        if(task == UserAccount.SUCCESS){
                            populateCountrySpinner();
                            completeProgress();
                            showProvidePhoneLayout();
                            String s = "Enter your phone number matching with \n" + data;
                            txtLastDigitsOfPhoneNumber.setText(s);
                        }
                        else{
                            completeProgress();
                            edtUsername.requestFocus();
                            String s = "Accessing your phone was failed";
                            edtUsername.setError(s);
                        }
                    });
                }
                else{
                    completeProgress();
                    edtUsername.requestFocus();
                    String s = "User name not found! error code " + res;
                    edtUsername.setError(s);
                }
            });
            ua.create();


        }
        else if(view.getId() == btnNext2.getId()){
            providedPhone = edtPhone.getText().toString().trim();
            if(providedPhone.isEmpty()){
                edtPhone.requestFocus();
                edtPhone.setError("Give your phone number, Nobody can access your number.");
                return;
            }
            // TODO
            //   Send user a verification code
        }
    }

    private void showUsernameLayout(){
        layout_provideUsername.setVisibility(View.VISIBLE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        txtFinalMessageToUser.setVisibility(View.GONE);
    }

    private void showProvidePhoneLayout(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.VISIBLE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        txtFinalMessageToUser.setVisibility(View.GONE);
    }

    private void showOtpLayout(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.VISIBLE);
        layout_providePass.setVisibility(View.GONE);
        txtFinalMessageToUser.setVisibility(View.GONE);
    }

    private void showProvidePassLayout(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.VISIBLE);
        txtFinalMessageToUser.setVisibility(View.GONE);
    }

    private void showFinalMessage(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        txtFinalMessageToUser.setVisibility(View.VISIBLE);
    }

    private void inProgress(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        txtFinalMessageToUser.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void completeProgress(){
        progressBar.setVisibility(View.GONE);

        layout_provideUsername.setVisibility(View.VISIBLE);
        layout_providePhone.setVisibility(View.VISIBLE);
        layout_otp.setVisibility(View.VISIBLE);
        layout_providePass.setVisibility(View.VISIBLE);
        txtFinalMessageToUser.setVisibility(View.VISIBLE);
    }

    private void makeJsonArrayForCountry(){
        countryCodes = DataModel.getCountryCodes();
    }

    private void populateCountrySpinner(){
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(context,
//                        android.R.layout.simple_spinner_dropdown_item, countryNames);
//        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
//
//        spinnerCountry.setAdapter(adapter);

        CountryNameAdapter adapter = new CountryNameAdapter(countryCodes, context);
        spinnerCountry.setAdapter(adapter);

        String myDeviceIso = DataModel.getMyDeviceCountryIso(context);
        for(int i = 0; i < countryCodes.size(); i++){
            if(countryCodes.get(i).getIsoCode().equals(myDeviceIso.toUpperCase())){
                spinnerCountry.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCountryCode = countryCodes.get(i).getDialCode();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
