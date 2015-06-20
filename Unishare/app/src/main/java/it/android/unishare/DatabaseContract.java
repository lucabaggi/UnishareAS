package it.android.unishare;

import android.provider.BaseColumns;

public class DatabaseContract {
	// To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class MyCoursesTable implements BaseColumns {
        public static final String TABLE_NAME = "my_courses";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PROFESSOR = "professor";
    }

    public static abstract class PassedExams implements BaseColumns{
        public static final String TABLE_NAME = "passed_exams";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PROFESSOR = "professor";
        public static final String COLUMN_GRADE = "grade";
        public static final String COLUMN_LAUDE = "laude";
    }

    
    public static abstract class UserInfoTable implements BaseColumns {
        public static final String TABLE_NAME = "user_info";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NICKNAME = "nickname";
        public static final String COLUMN_UNIVERSITY_ID = "university_id";
        public static final String COLUMN_UNIVERSITY = "university";
        public static final String COLUMN_CAMPUS_ID = "campus_id";
        public static final String COLUMN_CAMPUS = "campus";
        /*
        public static final String COLUMN_FACULTY_ID = "faculty_id";
        public static final String COLUMN_FACULTY = "faculty";
        */
        public static final String COLUMN_SPECIALIZATION_ID = "specialization_id";
        public static final String COLUMN_SPECIALIZATION = "specialization";
        public static final String COLUMN_LAST_ACCESS = "last_access";
        public static final String COLUMN_PROFILE_IMAGE_PATH = "image_path";
    }

    //Strings used to create tables

    public static final String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + MyCoursesTable.TABLE_NAME + " (" +
            MyCoursesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MyCoursesTable.COLUMN_COURSE_ID + " INTEGER, " +
            MyCoursesTable.COLUMN_NAME + " VARCHAR(100), " +
            MyCoursesTable.COLUMN_PROFESSOR + " VARCHAR(100), " +
            "UNIQUE(" + MyCoursesTable.COLUMN_COURSE_ID + ")" + ")";

    public static final String SQL_CREATE_PASSED_EXAMS_TABLE = "CREATE TABLE " + PassedExams.TABLE_NAME + " (" +
            PassedExams._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PassedExams.COLUMN_COURSE_ID + " INTEGER, " +
            PassedExams.COLUMN_NAME + " VARCHAR(100), " +
            PassedExams.COLUMN_PROFESSOR + " VARCHAR(100), " +
            PassedExams.COLUMN_GRADE + " INTEGER, " +
            PassedExams.COLUMN_LAUDE + " INTEGER, " +
            "UNIQUE(" + PassedExams.COLUMN_COURSE_ID + ")" + ")";


    public static final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserInfoTable.TABLE_NAME + " (" +
            UserInfoTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            UserInfoTable.COLUMN_USER_ID + " INTEGER, " +
            UserInfoTable.COLUMN_NICKNAME + " VARCHAR(100), " +
            UserInfoTable.COLUMN_CAMPUS + " VARCHAR(100), " +
            UserInfoTable.COLUMN_CAMPUS_ID + " INTEGER, " +
            UserInfoTable.COLUMN_UNIVERSITY_ID + " INTEGER, " +
            UserInfoTable.COLUMN_UNIVERSITY + " VARCHAR(100), " +
            UserInfoTable.COLUMN_SPECIALIZATION_ID + " INTEGER, " +
            UserInfoTable.COLUMN_SPECIALIZATION + " VARCHAR(100), " +
            UserInfoTable.COLUMN_PROFILE_IMAGE_PATH + " VARCHAR(100), " +
            UserInfoTable.COLUMN_LAST_ACCESS + " INTEGER)";

    public static final String SQL_DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS " + MyCoursesTable.TABLE_NAME;
    public static final String SQL_DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + UserInfoTable.TABLE_NAME;
    public static final String SQL_DELETE_PASSED_EXAMS_TABLE = "DROP TABLE IF EXISTS " + PassedExams.TABLE_NAME;

}
