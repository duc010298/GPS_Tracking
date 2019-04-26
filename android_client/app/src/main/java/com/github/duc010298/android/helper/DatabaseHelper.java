package com.github.duc010298.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GPS_Tracking";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE Location_History(time_tracking INTEGER, latitude REAL, longitude REAL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addLocationHistory(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("time_tracking", location.getTime());
        values.put("latitude", location.getLatitude());
        values.put("longitude", location.getLongitude());

        db.insert("Location_History", null, values);
        db.close();
    }

    public void cleanDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Location_History");
        db.close();
    }
}
