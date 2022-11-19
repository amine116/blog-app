package com.amine.blog.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.BuildConfig;
import com.amine.blog.R;

public class AboutMe extends Fragment implements View.OnClickListener {

    private TextView txtEmail, txtFacebook;
    private Context context;

    private String appName;

    public AboutMe(){}

    public void setContext(Context context) {
        this.context = context;
        appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_me_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtEmail = view.findViewById(R.id.txtEmail);
        txtFacebook = view.findViewById(R.id.txtFacebook);
        //txtFacebook.setOnClickListener(this);
        txtEmail.setOnClickListener(this);

        txtFacebook.setMovementMethod(LinkMovementMethod.getInstance());
        //txtEmail.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == txtEmail.getId()){

            String s = appName + "-\n" + "Version Code- " + BuildConfig.VERSION_CODE + "\n" +
                    "Version Name- " + BuildConfig.VERSION_NAME + "\n------write your opinion------\n\n";

            Intent i= new Intent(android.content.Intent.ACTION_SEND);
            i.setType("plain/text");
            i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{txtEmail.getText().toString()});
            i.putExtra(android.content.Intent.EXTRA_SUBJECT, appName + "- About Me");
            i.putExtra(android.content.Intent.EXTRA_TEXT, s);
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        }
    }
}
