package com.github.duc010298.gps_tracking.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.github.duc010298.gps_tracking.android.entity.LocationHistory;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GPS_Tracking";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE Location_History(time_tracking INTEGER, latitude TEXT, longitude TEXT)";
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

    public ArrayList<LocationHistory> getHistory() {
        ArrayList<LocationHistory> locationHistories = new ArrayList<>();

        String query = "SELECT time_tracking, latitude, longitude FROM Location_History";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                LocationHistory locationHistory = new LocationHistory();
                locationHistory.setTime(Long.parseLong(cursor.getString(0)));
                locationHistory.setLatitude(Double.parseDouble(cursor.getString(1)));
                locationHistory.setLongitude(Double.parseDouble(cursor.getString(2)));

                locationHistories.add(locationHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return locationHistories;
    }

    public LocationHistory getLatestLocation() {
        LocationHistory locationHistory = null;
        String getLatestLocationQuery = "SELECT time_tracking, latitude, longitude FROM Location_History ORDER BY time_tracking DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLatestLocationQuery, null);
        if(cursor.moveToFirst()) {
            locationHistory = new LocationHistory();
            locationHistory.setTime(Long.parseLong(cursor.getString(0)));
            locationHistory.setLatitude(Double.parseDouble(cursor.getString(1)));
            locationHistory.setLongitude(Double.parseDouble(cursor.getString(2)));
        }

        cursor.close();
        db.close();

        return locationHistory;
    }

    public void deleteLatest() {
        String getLatestTimeTracking = "SELECT time_tracking FROM Location_History ORDER BY time_tracking DESC LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLatestTimeTracking, null);

        if(cursor.moveToFirst()) {
            SQLiteDatabase dbDelete = this.getWritableDatabase();
            Long latestTimeTracking = Long.parseLong(cursor.getString(0));
            dbDelete.delete("Location_History", "time_tracking = ?", new String[] { String.valueOf(latestTimeTracking) });
            dbDelete.close();
        }
        cursor.close();
        db.close();
    }
}
