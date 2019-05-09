package com.example.elias.a18eliek_app_projekt;

import android.provider.BaseColumns;

public class SolarSystemReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    private SolarSystemReaderContract() {}

    // Inner class that defines the SolarSystem table contents
    public static class MountainEntry implements BaseColumns {
        public static final String TABLE_NAME = "mountains";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_RADIUS = "radius";
        public static final String COLUMN_PARENT = "parent";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_AUXDATA = "auxdata";
    }

    public static final String SQL_CREATE  =
            "CREATE TABLE " + MountainEntry.TABLE_NAME + " (" +
                    MountainEntry._ID + "INTEGER PRIMARY KEY, " +
                    MountainEntry.COLUMN_NAME + " TEXT," +
                    MountainEntry.COLUMN_DISTANCE + " TEXT," +
                    MountainEntry.COLUMN_RADIUS  + " TEXT," +
                    MountainEntry.COLUMN_PARENT  + " TEXT," +
                    MountainEntry.COLUMN_CATEGORY  + " TEXT," +
                    MountainEntry.COLUMN_AUXDATA + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MountainEntry.TABLE_NAME;

}
