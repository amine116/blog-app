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

public class RulesAndStandard extends Fragment {

    private TextView txtRule1, txtRule2, txtRule3;

    public RulesAndStandard(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rules_and_standard_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtRule1 = view.findViewById(R.id.txtRule1);
        txtRule2 = view.findViewById(R.id.txtRule2);
        txtRule3 = view.findViewById(R.id.txtRule3);

        showRules();
    }


    private void showRules(){
        String rule1 = "You can write on any topic except sexuality or Nudity.\n",
                rule2 = "You can criticise anything except Religion.\n\n" +
                        "Criticise anyone's actions, not his/her personality.\n\n" +
                        "Also, you can criticise anyone's actions except the founder of any Religion.\n\n" +
                        "The article or portion of article will be considered as violation of rules if " +
                        "it criticise the entity of worship of any religion.\n",
                rule3 = "When you criticise something or someone's actions, do it with constructive words." +
                        " Do not use hate speech\n\n" +
                        "Use of insulting slang(s) will be considered as violation of rule.\n";
        txtRule1.setText(rule1);
        txtRule2.setText(rule2);
        txtRule3.setText(rule3);
    }
}
