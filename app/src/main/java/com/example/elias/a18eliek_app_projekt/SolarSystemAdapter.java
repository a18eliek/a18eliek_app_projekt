package com.example.elias.a18eliek_app_projekt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import java.io.InputStream;
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

        //Skriv ut namnet på berget
        TextView nameTextView = listItemView.findViewById(R.id.mountName);
        nameTextView.setText(currentSolarSystem.toString());

        //Skriv ut information om berget
        TextView numberTextView = listItemView.findViewById(R.id.mountInfo);
        numberTextView.setText(currentSolarSystem.info());

        try {
            spaceobjIMG = SolarSystem.splitAuxdata(currentSolarSystem.getAuxdata(), "img");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Ladda ner och visa bilden
        new DownloadImageTask((ImageView) listItemView.findViewById(R.id.list_item_icon)).execute(spaceobjIMG);

        return listItemView;
    }


    //Ladda bilder ifrån en URL
    //Tagen ifrån https://stackoverflow.com/a/9288544/3822307
    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}