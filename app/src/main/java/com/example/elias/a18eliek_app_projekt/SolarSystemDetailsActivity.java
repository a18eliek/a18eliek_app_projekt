package com.example.elias.a18eliek_app_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;

import static com.example.elias.a18eliek_app_projekt.SolarSystem.getFormattedNumber;

public class SolarSystemDetailsActivity extends AppCompatActivity {
    ImageView image;
    public static final String SPACEOBJ_URL = "SPACEOBJ_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solarsystemdetails);

        //Ta emot allt som blev skickat ifrån MainActivity
        Intent intent = getIntent();
        String spaceobjName = intent.getStringExtra(MainActivity.SPACEOBJ_NAME);
        String spaceobjDistance = intent.getStringExtra(MainActivity.SPACEOBJ_DISTANCE);
        String spaceobjRadius = intent.getStringExtra(MainActivity.SPACEOBJ_RADIUS);
        String spaceobjCategory = intent.getStringExtra(MainActivity.SPACEOBJ_CATEGORY);
        String spaceobjParent = intent.getStringExtra(MainActivity.SPACEOBJ_PARENT);
        String spaceobjAuxdata = intent.getStringExtra(MainActivity.SPACEOBJ_AUXDATA);

        String spaceobjURL = "", spaceobjIMG = "";

        try {
            spaceobjURL = SolarSystem.splitAuxdata(spaceobjAuxdata, "url");
        } catch (JSONException e) {
            Log.e("brom","URL Exception:"+e.getMessage());
        }

        try {
            spaceobjIMG = SolarSystem.splitAuxdata(spaceobjAuxdata, "img");
        } catch (JSONException e) {
            Log.e("brom","IMG Exception:"+e.getMessage());
        }

        Log.e("brom", spaceobjURL);

        //Ändra TextView för bergets namn
        TextView spaceobjNameTextView = findViewById(R.id.SPACEOBJ_NAME);
        spaceobjNameTextView.setText(spaceobjName);
        setTitle(spaceobjName); //Ändra lable text till bergnamnet

        //Ändra TextView för bergets plats
        TextView spaceobjDistanceTextView = findViewById(R.id.SPACEOBJ_DISTANCE);
        if("Moon".equalsIgnoreCase(spaceobjCategory)) {
            spaceobjDistanceTextView.setText("Distance from the " + spaceobjParent + ": " + getFormattedNumber(spaceobjDistance) + "km");
        } else {
            spaceobjDistanceTextView.setText("Distance from the Sun: " + getFormattedNumber(spaceobjDistance) + "km");
        }


        //Ändra TextView för bergets höjd
        TextView spaceobjRadiusTextView = findViewById(R.id.SPACEOBJ_RADIUS);
        spaceobjRadiusTextView.setText("Radius: " + getFormattedNumber(spaceobjRadius) + "km");

        TextView spaceobjLinkTextView = findViewById(R.id.SPACEOBJ_LINK);
        spaceobjLinkTextView.setContentDescription(spaceobjURL); //Vi sparar URL som en description

        //Visa bilen på berget
        new SolarSystemAdapter.DownloadImageTask((ImageView) findViewById(R.id.SpaceobjImageView)).execute(spaceobjIMG);

    }

    public void openSpaceobjLink(View v) {
        TextView tv = findViewById(R.id.SPACEOBJ_LINK);
        String url = (String) tv.getContentDescription();

        Intent myIntent = new Intent(v.getContext(), WebViewActivity.class);
        myIntent.putExtra(SPACEOBJ_URL, url);
        startActivity(myIntent);
    }

}