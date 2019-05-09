package com.example.elias.a18eliek_app_projekt;

import android.provider.BaseColumns;

public class SolarSystemReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    private SolarSystemReaderContract() {}

    // Inner class that defines the SolarSystem table contents
    public static class SpaceobjEntry implements BaseColumns {
        public static final String TABLE_NAME = "SpaceObjects";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_RADIUS = "radius";
        public static final String COLUMN_PARENT = "parent";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_AUXDATA = "auxdata";
    }

    public static final String SQL_CREATE  =
            "CREATE TABLE " + SpaceobjEntry.TABLE_NAME + " (" +
                    SpaceobjEntry._ID + "INTEGER PRIMARY KEY, " +
                    SpaceobjEntry.COLUMN_NAME + " TEXT," +
                    SpaceobjEntry.COLUMN_DISTANCE + " TEXT," +
                    SpaceobjEntry.COLUMN_RADIUS  + " TEXT," +
                    SpaceobjEntry.COLUMN_PARENT  + " TEXT," +
                    SpaceobjEntry.COLUMN_CATEGORY  + " TEXT," +
                    SpaceobjEntry.COLUMN_AUXDATA + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SpaceobjEntry.TABLE_NAME;

}
