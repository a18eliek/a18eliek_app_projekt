package com.example.elias.a18eliek_app_projekt;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * TODO:
 * Skapa en about sida för info om appen
 * Tema inställning? Dark/Nightmode
 * Sharedpref för sort knappen, skapar problem... hjälp?
 */

public class MainActivity extends AppCompatActivity {
    public static ArrayList<SolarSystem> list = new ArrayList<>();
    public String Selected;
    public String sortDirection = "DESC";
    public static final String SPACEOBJ_NAME = "SPACEOBJ_NAME", SPACEOBJ_DISTANCE = "SPACEOBJ_DISTANCE", SPACEOBJ_RADIUS = "SPACEOBJ_RADIUS",  SPACEOBJ_CATEGORY = "SPACEOBJ_CATEGORY",  SPACEOBJ_PARENT = "SPACEOBJ_PARENT", SPACEOBJ_AUXDATA = "SPACEOBJ_AUXDATA";
    private final String SELECTED_KEY = "KEY";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.elias.a18eliek_app_projekt_sharedPrefFile_1";
    private int selectedPosition = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedPosition = mPreferences.getInt(SELECTED_KEY, 0);

        if(list.isEmpty()) {
            new FetchData().execute(); //Starta utplock av json-data
        }

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setSelection(selectedPosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Selected = arg0.getSelectedItem().toString();

                displayListView(false);

                selectedPosition = pos;

                Log.e("brom", Selected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

        final Button button = findViewById(R.id.sort_option);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button)v;
                String buttonText = b.getText().toString();

                if("DESC".equalsIgnoreCase(buttonText)) {
                    b.setText("ASC");
                } else if("ASC".equalsIgnoreCase(buttonText)) {
                    b.setText("DESC");
                }

                sortDirection = b.getText().toString();

                displayListView(false);

                Log.e("button", sortDirection);
            }
        });

        displayListView(true);

    }

    void displayListView(boolean enableOnItemClickListner) {
        //Skicka info till vår SolarSystemAdapter
        SolarSystemAdapter solarSystemAdapter = new SolarSystemAdapter(getApplicationContext(), printFromDB());
        ListView listView = findViewById(R.id.my_listview);
        listView.setAdapter(solarSystemAdapter);

        if(enableOnItemClickListner) {
            //Skicka all information vid klick till vår intent
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(view.getContext(), SolarSystemDetailsActivity.class);
                    myIntent.putExtra(SPACEOBJ_NAME, list.get(position).toString());
                    myIntent.putExtra(SPACEOBJ_DISTANCE, list.get(position).getDistance());
                    myIntent.putExtra(SPACEOBJ_RADIUS, list.get(position).getRadius());
                    myIntent.putExtra(SPACEOBJ_CATEGORY, list.get(position).getCategory());
                    myIntent.putExtra(SPACEOBJ_PARENT, list.get(position).getParent());

                    Log.e("getparent", list.get(position).getParent());
                    myIntent.putExtra(SPACEOBJ_AUXDATA, list.get(position).getAuxdata());
                    startActivity(myIntent);
                }
            });
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(SELECTED_KEY, selectedPosition);
        preferencesEditor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            list.clear(); //Ta bort all gamal data
            new FetchData().execute(); //Hämta ny data

            Toast.makeText(getApplicationContext(), "Refreshing data...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(id == R.id.action_dropdb) {
            SolarSystemReaderDbHelper dbHelper = new SolarSystemReaderDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(SolarSystemReaderContract.SQL_DELETE_ENTRIES);
            db.execSQL(SolarSystemReaderContract.SQL_CREATE); //App will crash if we don't have a database so let's create it.

            Toast.makeText(getApplicationContext(), "Dropping DB...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(id == R.id.action_about_app) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<SolarSystem> printFromDB() {
        list.clear();
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

        String sortOrder = "";

        String whereClause = null;
        String [] whereArgs = null;


        if("Name".equalsIgnoreCase(Selected)) {
            sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME + " " + sortDirection;
        } else if("Show Only Planets".equalsIgnoreCase(Selected)) {
            sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME + " " + sortDirection;
            whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=?";
            whereArgs = new String[]{"Planet"};
        } else if("Show Only Moons".equalsIgnoreCase(Selected)) {
            sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME + " " + sortDirection;
            whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=?";
            whereArgs = new String[]{"Moon"};
        } else if("Show Only Kuiper Belt Objects".equalsIgnoreCase(Selected)) {
            sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME + " " + sortDirection;
            whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=?";
            whereArgs = new String[]{"Kuiper belt object"};
        } else if("Show Only Asteroids".equalsIgnoreCase(Selected)) {
            sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME + " " + sortDirection;
            whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=?";
            whereArgs = new String[]{"belt asteroid"};
        } else if("Show Only Comets".equalsIgnoreCase(Selected)) {
            sortOrder = SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME + " " + sortDirection;
            whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=?";
            whereArgs = new String[]{"comet"};
        } else if("Distance from the Sun".equalsIgnoreCase(Selected)) {
            whereClause = SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=? OR " + SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=? OR " + SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=? OR " + SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY+"=?";
            sortOrder += SolarSystemReaderContract.SpaceobjEntry.COLUMN_DISTANCE + " " + sortDirection;
            whereArgs = new String[]{"Planet", "Kuiper belt object", "belt asteroid", "comet"};
        }

        Cursor cursor = db.query(
                SolarSystemReaderContract.SpaceobjEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                whereClause,              // The columns for the WHERE clause
                whereArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while (cursor.moveToNext()) {
            SolarSystem m =  new SolarSystem(
                    cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_DISTANCE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_RADIUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_PARENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SolarSystemReaderContract.SpaceobjEntry.COLUMN_AUXDATA))
            );

            list.add(m);

        }
        cursor.close();

        return list;
    }

    private class FetchData extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr;

            try {
                URL url = new URL("https://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=a18eliek");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Log.e("brom","onPostExecute:"+o);

            try {
                if (o != null) {
                    //Har vi data så kan vi tömma databasen och fylla på med den nya.
                    SolarSystemReaderDbHelper dbHelper = new SolarSystemReaderDbHelper(getApplicationContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    db.execSQL(SolarSystemReaderContract.SQL_DELETE_ENTRIES);
                    db.execSQL(SolarSystemReaderContract.SQL_CREATE);

                    JSONArray jsonArray = new JSONArray(o);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);

                        ContentValues values = new ContentValues();
                        values.put(SolarSystemReaderContract.SpaceobjEntry.COLUMN_NAME, object.getString("name"));
                        values.put(SolarSystemReaderContract.SpaceobjEntry.COLUMN_DISTANCE, object.getString("location"));
                        values.put(SolarSystemReaderContract.SpaceobjEntry.COLUMN_RADIUS, object.getInt("size"));
                        values.put(SolarSystemReaderContract.SpaceobjEntry.COLUMN_PARENT, object.getString("company"));
                        values.put(SolarSystemReaderContract.SpaceobjEntry.COLUMN_CATEGORY, object.getString("category"));
                        values.put(SolarSystemReaderContract.SpaceobjEntry.COLUMN_AUXDATA, object.getString("auxdata"));
                        db.insert(SolarSystemReaderContract.SpaceobjEntry.TABLE_NAME, null, values);

                        Log.e("brom","SolarSystemReaderContract:"+values);
                    }
                }
            } catch (JSONException e) {
                Log.e("brom","E:"+e.getMessage());
            }

            displayListView(false);

        }
    }
}

