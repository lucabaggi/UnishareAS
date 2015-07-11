package it.android.unishare;

import it.android.unishare.DatabaseContract.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "Unishare.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.SQL_CREATE_COURSES_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_USER_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_PASSED_EXAMS_TABLE);
        Log.i("Database", "creazione");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseContract.SQL_DELETE_COURSES_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_NOTIFICATIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_USER_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_PASSED_EXAMS_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public static void regenerateDatabase(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.SQL_DELETE_COURSES_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_USER_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_NOTIFICATIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_DELETE_PASSED_EXAMS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_COURSES_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_USER_TABLE);
        db.execSQL(DatabaseContract.SQL_CREATE_PASSED_EXAMS_TABLE);
    }
}