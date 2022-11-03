package com.amine.blog.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.R;
import com.amine.blog.adapters.CountryNameAdapter;
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.CountryCode;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.repositories.UserAccount;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RecoverAccountFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ProgressBar progressBar;
    private EditText edtUsername, edtPhone, edtOtp, edtPass;
    private TextView txtLastDigitsOfPhoneNumber, txtHeadlineOfSendingOtp, txtOtpPassHeadline ,txtFinalMessageToUser;
    private RelativeLayout layout_provideUsername, layout_providePhone, layout_otp, layout_providePass, layout_finalMessage;
    private Button btnNext, btnNext2, btnNext3, btnChange, txtOkayButton;
    private Spinner spinnerCountry;

    private String providedUsername, selectedCountryCode, providedPhone, verificationID, verificationCodeFromUser;

    private ArrayList<CountryCode> countryCodes = new ArrayList<>();

    private Context context;
    private Activity activity;

    private OnWaitListener waitListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
        layout_finalMessage = view.findViewById(R.id.layout_finalMessage);
        txtOkayButton = view.findViewById(R.id.txtOk);


        makeJsonArrayForCountry();

        btnChange.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnNext2.setOnClickListener(this);
        btnNext3.setOnClickListener(this);
        txtOkayButton.setOnClickListener(this);
        spinnerCountry.setOnItemSelectedListener(this);


        super.onViewCreated(view, savedInstanceState);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setWaitListener(OnWaitListener waitListener) {
        this.waitListener = waitListener;
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

            inProgress();
            providedPhone = selectedCountryCode + providedPhone;

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(Retrieve.getFirebaseAuth())
                            .setPhoneNumber(providedPhone)
                            .setTimeout(120L, TimeUnit.SECONDS)
                            .setActivity(activity)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    verificationCodeFromUser = phoneAuthCredential.getSmsCode();

                                    if(verificationCodeFromUser != null && !verificationCodeFromUser.isEmpty()) {
                                        verifyCode();
                                    }
                                    else{
                                        edtPhone.requestFocus();
                                        edtPhone.setError("Verification was not complete- ");
                                        completeProgress();
                                    }

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    edtPhone.requestFocus();
                                    edtPhone.setError("Verification failed- " + e.getMessage());
                                    completeProgress();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull
                                        PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);

                                    verificationID = s;

                                    String sug = "We have sent a verification code to " +
                                            providedPhone + "\nEnter the code below";
                                    txtHeadlineOfSendingOtp.setText(sug);
                                    completeProgress();
                                    showOtpLayout();

                                }
                            })
                            .build();

            PhoneAuthProvider.verifyPhoneNumber(options);

        }

        else if(view.getId() == btnNext3.getId()){
            verificationCodeFromUser = edtOtp.getText().toString().trim();
            if(verificationCodeFromUser.isEmpty()){
                edtOtp.requestFocus();
                edtOtp.setError("Enter verification code(OTP)");
                return;
            }
            inProgress();
            verifyCode();
        }

        else if(view.getId() == btnChange.getId()){
            String providedPass = edtPass.getText().toString().trim();
            if(providedPass.isEmpty()){
                edtPass.requestFocus();
                edtPass.setError("Provide a password");
                return;
            }

            if(providedPass.length() < 6){
                edtPass.requestFocus();
                edtPass.setError("Password must be at least 6 character long");
                return;
            }

            Save.requestAccountRecovery(providedUsername, providedPhone, providedPass);

            showFinalMessage();
            String s = "Password changed request has been sent to server. You password will be changed soon";
            txtFinalMessageToUser.setText(s);
        }

        else if(view.getId() == txtOkayButton.getId()){
            waitListener.onWaitCallback(DataModel.STR_MOVE_TO_SIGN_IN_PAGE);
        }
    }

    private void verifyCode() {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, verificationCodeFromUser);

        Save.signInWithPhoneAuthCredential(credential, task -> {
            completeProgress();
            if(task == UserAccount.SUCCESS){
                showProvidePassLayout();
                String s = "Provide new password";
                txtOtpPassHeadline.setText(s);
            }
            else{
                showOtpLayout();
                edtOtp.requestFocus();
                edtOtp.setError("OTP is not correct");
            }
        });

    }

    private void showUsernameLayout(){
        layout_provideUsername.setVisibility(View.VISIBLE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        layout_finalMessage.setVisibility(View.GONE);
    }

    private void showProvidePhoneLayout(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.VISIBLE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        layout_finalMessage.setVisibility(View.GONE);
    }

    private void showOtpLayout(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.VISIBLE);
        layout_providePass.setVisibility(View.GONE);
        layout_finalMessage.setVisibility(View.GONE);
    }

    private void showProvidePassLayout(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.VISIBLE);
        layout_finalMessage.setVisibility(View.GONE);
    }

    private void showFinalMessage(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        layout_finalMessage.setVisibility(View.VISIBLE);
    }

    private void inProgress(){
        layout_provideUsername.setVisibility(View.GONE);
        layout_providePhone.setVisibility(View.GONE);
        layout_otp.setVisibility(View.GONE);
        layout_providePass.setVisibility(View.GONE);
        layout_finalMessage.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void completeProgress(){
        progressBar.setVisibility(View.GONE);

        layout_provideUsername.setVisibility(View.VISIBLE);
        layout_providePhone.setVisibility(View.VISIBLE);
        layout_otp.setVisibility(View.VISIBLE);
        layout_providePass.setVisibility(View.VISIBLE);
        layout_finalMessage.setVisibility(View.VISIBLE);
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

        ImageView imgDropDownIcon = view.findViewById(R.id.imgDropDownIcon);
        imgDropDownIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
