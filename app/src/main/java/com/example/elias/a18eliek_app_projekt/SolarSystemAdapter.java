package com.example.elias.a18eliek_app_projekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import java.util.ArrayList;


public class SolarSystemAdapter extends ArrayAdapter<SolarSystem> {

    public SolarSystemAdapter(Context context, ArrayList<SolarSystem> solarSystemList) {
        super(context, 0, solarSystemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        String spaceobjIMG = "";

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        SolarSystem currentSolarSystem = getItem(position);

        //Skriv ut namnet
        TextView nameTextView = listItemView.findViewById(R.id.mountName);
        nameTextView.setText(currentSolarSystem.toString());

        //Skriv ut information
        TextView numberTextView = listItemView.findViewById(R.id.mountInfo);
        numberTextView.setText(currentSolarSystem.info());

        try {
            spaceobjIMG = SolarSystem.splitAuxdata(currentSolarSystem.getAuxdata(), "img");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView imageView = listItemView.findViewById(R.id.list_item_icon);
        Glide.with(getContext()).load(spaceobjIMG).into(imageView);

        return listItemView;
    }

}