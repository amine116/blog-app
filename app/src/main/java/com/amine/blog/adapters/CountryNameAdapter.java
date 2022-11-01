package com.amine.blog.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amine.blog.R;
import com.amine.blog.model.CountryCode;

import java.util.ArrayList;

public class CountryNameAdapter extends BaseAdapter {

    private final ArrayList<CountryCode> countryCodes;
    private final Context context;

    public CountryNameAdapter(ArrayList<CountryCode> countryCodes, Context context) {
        this.countryCodes = countryCodes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return countryCodes.size();
    }

    @Override
    public Object getItem(int i) {
        return countryCodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_dropdown_for_country_name, null);
        }

        TextView txtCountryName = view.findViewById(R.id.txtCountryName),
                txtCountryCode = view.findViewById(R.id.txtCountryCode);

        txtCountryName.setText(countryCodes.get(i).getName());
        txtCountryCode.setText(countryCodes.get(i).getDialCode());


        return view;
    }
}
