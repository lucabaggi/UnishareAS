package it.android.unishare;

import it.android.unishare.DatabaseContract.MyCoursesTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Unishare.db";
    
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MyCoursesTable.TABLE_NAME + " (" +
    		MyCoursesTable._ID + " INTEGER PRIMARY KEY, " + 
    		MyCoursesTable.COLUMN_COURSE_ID + " INTEGER, " +
    		MyCoursesTable.COLUMN_NAME + " VARCHAR(100)" + ")";
    
    private static final String SQL_DELETE_ENTRIES =
    		"DROP TABLE IF EXISTS " + MyCoursesTable.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public static void regenerateDatabase(SQLiteDatabase db) {
    	db.execSQL(SQL_DELETE_ENTRIES);
    	db.execSQL(SQL_CREATE_ENTRIES);
    }
}