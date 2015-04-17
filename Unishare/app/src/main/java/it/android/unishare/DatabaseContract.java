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
    }
    
    public static abstract class UserInfoTable implements BaseColumns {
        public static final String TABLE_NAME = "user_info";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NICKNAME = "nickname";
        public static final String COLUMN_UNIVERSITY_ID = "university_id";
        public static final String COLUMN_UNIVERSITY = "university";
        public static final String COLUMN_CAMPUS_ID = "faculty_id";
        public static final String COLUMN_CAMPUS = "faculty";
        public static final String COLUMN_FACULTY_ID = "faculty_id";
        public static final String COLUMN_FACULTY = "faculty";
        public static final String COLUMN_SPECIALIZATION_ID = "specialization_id";
        public static final String COLUMN_SPECIALIZATION = "specialization";
        public static final String COLUMN_LAST_ACCESS = "last_access";
    }
}
