package com.amine.blog.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amine.blog.R;

import java.util.ArrayList;

public class StringArrayAdapter extends BaseAdapter {

    private final ArrayList<String> stringArrayList;
    private final Context context;

    public StringArrayAdapter(ArrayList<String> stringArrayList, Context context) {
        this.stringArrayList = stringArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return stringArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return stringArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_dropdown_for_country_name, null);
        }

        TextView txtCountryName = view.findViewById(R.id.txtCountryName),
                txtCountryCode = view.findViewById(R.id.txtCountryCode);
        ImageView imgDropDownIcon = view.findViewById(R.id.imgDropDownIcon);

        imgDropDownIcon.setVisibility(View.GONE);
        txtCountryCode.setVisibility(View.GONE);

        txtCountryName.setText(stringArrayList.get(i));

        return view;
    }
}
