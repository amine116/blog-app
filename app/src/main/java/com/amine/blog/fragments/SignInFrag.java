package com.amine.blog.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    // values of this fragment
    private OnWaitListener onWaitListener;

    public SignInFrag(){}

    public void addWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
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

        txtSignIn.setOnClickListener(this);
        txtCreateAcc.setOnClickListener(this);
        txtRecoverAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == txtSignIn.getId()){
            onWaitListener.onWaitCallback(DataModel.MOVE_TO_SIGN_IN_PAGE_2);
        }
        else if(view.getId() == txtCreateAcc.getId()){
            onWaitListener.onWaitCallback(DataModel.MOVE_TO_CREATE_ACCOUNT_FRAGMENT);
        }
        else if(view.getId() == txtRecoverAccount.getId()){
            onWaitListener.onWaitCallback(DataModel.MOVE_TO_RECOVER_ACCOUNT);
        }
    }
}
