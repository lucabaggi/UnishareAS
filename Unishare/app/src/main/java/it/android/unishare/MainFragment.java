package it.android.unishare;

import java.util.ArrayList;

import it.android.unishare.DatabaseContract.MyCoursesTable;
import it.android.unishare.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment implements ViewInitiator {
	
	public final static String TAG = "it.android.unishare.MainFragment";
	
	private Activity activity;
	private View view;
	

    public MainFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeUI(view);
        return view;
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
    }
    
    @Override
	public void initializeUI(View view) {
    	//Build view
        Button btn = (Button) view.findViewById(R.id.insertOpinionButton);
        btn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View view) {

	        	//Application.databaseCall(activity, "user.php?id=1", "userName", MainFragment.this);

	        	ContentValues values = new ContentValues();
	        	values.put(MyCoursesTable.COLUMN_COURSE_ID, 1);
	        	values.put(MyCoursesTable.COLUMN_NAME, "Corso di prova");
	        	//MyApplication.getInstance(activity).insertIntoDatabase(MyCoursesTable.TABLE_NAME,values);
	        	
	        	//RETRIEVAL
	        	String[] projection = {
	    		    MyCoursesTable.COLUMN_NAME
	    		};
	    		String sortOrder = MyCoursesTable.COLUMN_NAME + " ASC";
	    		
	    		Cursor cursor = MyApplication.getInstance(activity).queryDatabase(
	    			MyCoursesTable.TABLE_NAME,  // The table to query
	    		    projection,                               // The columns to return
	    		    null,                                // The columns for the WHERE clause
	    		    null,                            // The values for the WHERE clause
	    		    null,                                     // don't group the rows
	    		    null,                                     // don't filter by row groups
	    		    sortOrder                                 // The sort order
	    		);
	    		cursor.moveToFirst();
	    		MyApplication.alertMessage(activity,"Prova",cursor.getString(cursor.getColumnIndexOrThrow(MyCoursesTable.COLUMN_NAME)));
	        }
        });
        
        Button btn2 = (Button) view.findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View view) {
	        	MyApplication.getInstance(activity).newActivity(BooksActivity.class);
	        }
        });
        
        Button btn3 = (Button) view.findViewById(R.id.button3);
        btn3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyApplication.getInstance(activity).newActivity(CoursesActivity.class);			
			}
		});
        
	}

	public void displayResults(ArrayList<Entity> result, String tag) {
		Entity user = result.get(0);
		TextView textView = (TextView) view.findViewById(R.id.textView1);
		textView.setText(user.get("nickname"));
	}
    
}
