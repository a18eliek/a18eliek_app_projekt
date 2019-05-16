package com.example.elias.a18eliek_app_projekt;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import java.util.ArrayList;
import static com.example.elias.a18eliek_app_projekt.SolarSystem.getFormattedNumber;

public class SolarSystemDetailsActivity extends AppCompatActivity implements MoonRecyclerViewAdapter.ItemClickListener{
    ImageView image;
    Menu optionsMenu;
    String URL = "";
    public static final String SPACEOBJ_URL = "SPACEOBJ_URL";
    public static ArrayList<String> MoonList = new ArrayList<>();
    public static ArrayList<String> MoonImgs = new ArrayList<>();
    public static ArrayList<String> MoonDistance = new ArrayList<>();
    private MoonRecyclerViewAdapter adapter;
    public String spaceobjName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solarsystemdetails);

        //Ta emot allt som blev skickat ifrån MainActivity
        Intent intent = getIntent();
        spaceobjName = intent.getStringExtra(MainActivity.SPACEOBJ_NAME);
        String spaceobjDistance = intent.getStringExtra(MainActivity.SPACEOBJ_DISTANCE);
        String spaceobjRadius = intent.getStringExtra(MainActivity.SPACEOBJ_RADIUS);
        String spaceobjCategory = intent.getStringExtra(MainActivity.SPACEOBJ_CATEGORY);
        String spaceobjParent = intent.getStringExtra(MainActivity.SPACEOBJ_PARENT);
        String spaceobjAuxdata = intent.getStringExtra(MainActivity.SPACEOBJ_AUXDATA);

        String spaceobjURL = "", spaceobjIMG = "";

        try {
            spaceobjURL = SolarSystem.splitAuxdata(spaceobjAuxdata, "url");
        } catch (JSONException e) {
            Log.e("brom", "URL Exception:" + e.getMessage());
        }

        try {
            spaceobjIMG = SolarSystem.splitAuxdata(spaceobjAuxdata, "imgFull");
        } catch (JSONException e) {
            try { //vi har endast hög upplösning på vissa objekt
                spaceobjIMG = SolarSystem.splitAuxdata(spaceobjAuxdata, "img");
            } catch (JSONException e1) {
                Log.e("brom", "IMG Exception:" + e1.getMessage());
            }

            Log.e("brom", "IMG Exception:" + e.getMessage());
        }

        Log.e("brom", spaceobjURL);

        //Ändra TextView för namn
        TextView spaceobjNameTextView = findViewById(R.id.SPACEOBJ_NAME);
        spaceobjNameTextView.setText(spaceobjName);
        setTitle(spaceobjName); //Ändra lable text till namn

        //Ändra TextView för distans
        TextView spaceobjDistanceTextView = findViewById(R.id.SPACEOBJ_DISTANCE);
        if ("Moon".equalsIgnoreCase(spaceobjCategory)) {
            spaceobjDistanceTextView.setText("Distance from " + spaceobjParent + ": " + getFormattedNumber(spaceobjDistance) + "km");
        } else {
            spaceobjDistanceTextView.setText("Distance from the Sun: " + getFormattedNumber(spaceobjDistance) + "km");
        }


        //Ändra TextView för radie
        TextView spaceobjRadiusTextView = findViewById(R.id.SPACEOBJ_RADIUS);
        spaceobjRadiusTextView.setText("Radius: " + getFormattedNumber(spaceobjRadius) + "km");

        //Licens
        TextView spaceobjLicenceTextView = findViewById(R.id.SPACEOBJ_LICENCE);
        try {
            Spanned licText = Html.fromHtml("Licence: <i>" + SolarSystem.splitAuxdata(spaceobjAuxdata, "licence") + "</i>");
            spaceobjLicenceTextView.setText(licText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URL = spaceobjURL;

        //Visa bild
        ImageView imageView = findViewById(R.id.SpaceobjImageView);
        Glide.with(getApplicationContext()).load(spaceobjIMG).into(imageView);


        //Hantera månar
        if(spaceobjParent != "") {
            SolarSystemReaderDbHelper dbHelper = new SolarSystemReaderDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            String[] projection = {
                    SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME,
                    SolarSystemReaderContract.SpaceobjEntry.COLUMN_DISTANCE,
                    SolarSystemReaderContract.SpaceobjEntry.COLUMN_RADIUS,
                    SolarSystemReaderContract.SpaceobjEntry.COLUMN_PARENT,
                    SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY,
                    SolarSystemReaderContract.SpaceobjEntry.COLUMN_AUXDATA
            };

            String whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_PARENT + "=?";
            String sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_DISTANCE + " DESC";
            String[] whereArgs = new String[]{spaceobjName};

            Cursor cursor = db.query(
                    SolarSystemReaderContract.SpaceobjEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    whereClause,              // The columns for the WHERE clause
                    whereArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            MoonList.clear();
            MoonImgs.clear();
            MoonDistance.clear();
            String moonDistance = null;
            while (cursor.moveToNext()) {
                String moonName = cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME));
                String moonAuxData = cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_AUXDATA));
                moonDistance = cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_DISTANCE));

                MoonList.add(moonName);
                MoonImgs.add(moonAuxData);
                MoonDistance.add(getFormattedNumber(moonDistance));
            }
            cursor.close();

            // Visa månar som RecyclerView
            RecyclerView recyclerView = findViewById(R.id.rvMoons);
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(SolarSystemDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            adapter = new MoonRecyclerViewAdapter(this, MoonImgs, MoonList, MoonDistance);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, adapter.getMoonName(position) + " is in orbit "+ adapter.getMoonDistance(position) + " km away from " + spaceobjName, Toast.LENGTH_SHORT).show();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.solarsystem_activity_menu, menu);
        optionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.SPACEOBJ_LINK) {
            openSpaceobjLink(findViewById(R.id.SPACEOBJ_LINK));
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSpaceobjLink(View v) {
        Intent myIntent = new Intent(v.getContext(), WebViewActivity.class);
        myIntent.putExtra(SPACEOBJ_URL, URL);
        startActivity(myIntent);
    }

}