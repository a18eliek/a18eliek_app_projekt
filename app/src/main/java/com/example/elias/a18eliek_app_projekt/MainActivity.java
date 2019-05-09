package com.example.elias.a18eliek_app_projekt;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity {
    public ArrayList<Mountain> list = new ArrayList<>();
    public String Selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchData().execute(); //Starta utplock av json-data

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Selected = arg0.getSelectedItem().toString();

                MountainAdapter mountainAdapter = new MountainAdapter(getApplicationContext(), printFromDB());
                ListView listView = findViewById(R.id.my_listview);
                listView.setAdapter(mountainAdapter);

                Log.e("brom", Selected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
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
            MountainReaderDbHelper dbHelper = new MountainReaderDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(MountainReaderContract.SQL_DELETE_ENTRIES);
            db.execSQL(MountainReaderContract.SQL_CREATE); //App will crash if we don't have a database so let's create it.

            Toast.makeText(getApplicationContext(), "Dropping DB...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public ArrayList<Mountain> printFromDB() {
        list.clear();
        MountainReaderDbHelper dbHelper = new MountainReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = {
                MountainReaderContract.MountainEntry.COLUMN_NAME,
                MountainReaderContract.MountainEntry.COLUMN_LOCATION,
                MountainReaderContract.MountainEntry.COLUMN_HEIGHT,
                MountainReaderContract.MountainEntry.COLUMN_AUXDATA
        };

        String sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME + " ASC";

        if("Name A-Z".equalsIgnoreCase(Selected)) {
            sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME + " ASC";
        } else if("Name Z-A".equalsIgnoreCase(Selected)) {
            sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME + " DESC";
        } else if("Height Highest-Lowest".equalsIgnoreCase(Selected)) {
            sortOrder = MountainReaderContract.MountainEntry.COLUMN_HEIGHT + " DESC";
        } else if("Height Lowest-Highest".equalsIgnoreCase(Selected)) {
            sortOrder = MountainReaderContract.MountainEntry.COLUMN_HEIGHT + " ASC";
        }

        Cursor cursor = db.query(
                MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        while (cursor.moveToNext()) {
            Mountain m =  new Mountain(cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_LOCATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_HEIGHT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_AUXDATA))
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

            String jsonStr = null;

            try {
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

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
                    MountainReaderDbHelper dbHelper = new MountainReaderDbHelper(getApplicationContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    db.execSQL(MountainReaderContract.SQL_DELETE_ENTRIES);
                    db.execSQL(MountainReaderContract.SQL_CREATE);

                    JSONArray jsonArray = new JSONArray(o);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);

                        ContentValues values = new ContentValues();
                        values.put(MountainReaderContract.MountainEntry.COLUMN_NAME, object.getString("name"));
                        values.put(MountainReaderContract.MountainEntry.COLUMN_LOCATION, object.getString("location"));
                        values.put(MountainReaderContract.MountainEntry.COLUMN_HEIGHT, object.getInt("size"));
                        values.put(MountainReaderContract.MountainEntry.COLUMN_AUXDATA, object.getString("auxdata"));
                        db.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);

                        Log.e("brom","MountainReaderContract:"+values);
                    }
                }
            } catch (JSONException e) {
                Log.e("brom","E:"+e.getMessage());
            }

            //Skicka bergen till vår MountainAdapter
            MountainAdapter mountainAdapter = new MountainAdapter(getApplicationContext(), printFromDB());
            ListView listView = findViewById(R.id.my_listview);
            listView.setAdapter(mountainAdapter);

            //Lägger in en Toast vid klick på ett berg namn. Plats och namn visas.
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), list.get(position).info(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

