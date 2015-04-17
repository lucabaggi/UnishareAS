package it.android.unishare;

import it.android.unishare.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SearchFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "SearchFragment";
	
	private AdapterActivity activity;
	private View view;
	
	private OnBookSelectedListener bookListener;
	private OnCourseSelectedListener courseListener;
	
	//UI elements
	ListView listview;
	EditText searchForm;
	ProgressDialog dialog;
	ArrayAdapter<Entity> adapter;
	
	
	public interface OnBookSelectedListener {
        public void onBookSelected(String bookId, ProgressDialog dialog);
    }
	
	public interface OnCourseSelectedListener {
		public void onCourseSelected(String courseId, String courseName, ProgressDialog dialog);
	}

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(view == null){
    		if(this.activity instanceof BooksActivity)
    			view = inflater.inflate(R.layout.books_search_fragment, container, false);
    		else {
				view = inflater.inflate(R.layout.courses_search_fragment, container, false);
			}
            initializeUI(view);
    	}
    	return view;       
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof CoursesActivity){
			this.activity = (CoursesActivity) activity;
			Log.i(TAG, "fragment launched by CoursesActivity");
			try{
				this.courseListener = (OnCourseSelectedListener) activity;
			}
			catch(ClassCastException e){
				throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
			}
		}
		else {
			this.activity = (BooksActivity) activity;
			Log.i(TAG, "fragment launched by BooksActivity");
			try {
				this.bookListener = (OnBookSelectedListener) activity;
			}
			catch (ClassCastException e) {
				throw new ClassCastException(activity.toString() + " must implement OnBookSelectedListener");
			}
		}
    }
    
    
    @Override
	public void initializeUI(View view) {
    	adapter = activity.getAdapter();
    	Log.i(TAG, "l'adapter dell'activity ha dimensione " + activity.getAdapter().getCount());
    	listview = (ListView) view.findViewById(R.id.ListView1);
    	if(adapter.getCount() > 0){
    		listview.setAdapter(adapter);
    		
    		if(this.activity instanceof BooksActivity){
    			listview.setOnItemClickListener(new OnItemClickListener(){

    				@Override
    				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
    					Entity book = (Entity)parent.getItemAtPosition(position);
    					String bookId = book.get("id");
    					Log.i(TAG, "Clicked on book " + bookId);
    					dialog = new ProgressDialog(getActivity());
    		        	dialog.setTitle("Searching");
    		            dialog.setMessage("Please wait...");
    		            dialog.setIndeterminate(false);
    					SearchFragment.this.bookListener.onBookSelected(bookId, dialog);			
    				}
    					
    			});
    		}
    		else{
    			listview.setOnItemClickListener(new OnItemClickListener() {
    				
    				@Override
    				public void onItemClick(AdapterView<?> parent, View view, int position,	long id){
    					Entity course = (Entity)parent.getItemAtPosition(position);
    					String courseId = course.get("id");
    					String courseName = course.get("nome");
    					Log.i(TAG, "Clicked on course " + courseId);
    					dialog = new ProgressDialog(getActivity());
    		        	dialog.setTitle("Searching");
    		            dialog.setMessage("Please wait...");
    		            dialog.setIndeterminate(false);
    		            SearchFragment.this.courseListener.onCourseSelected(courseId, courseName, dialog);
    				}
				});
    		}
    	}    		
    	searchForm = (EditText) view.findViewById(R.id.opinionText);
    	Button btn = (Button) view.findViewById(R.id.insertOpinionButton);
        btn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View view) {
	        	clearList(adapter);
	        	dialog = new ProgressDialog(getActivity());
	        	dialog.setTitle("Searching");
	            dialog.setMessage("Please wait...");
	            dialog.setIndeterminate(false);
	        	activity.initializeFragmentUI(searchForm.getText().toString(), dialog);
	        }
        });
    	
	}

	public void displayResults(String tag) {
		adapter = activity.getAdapter();
		Log.i(TAG, "l'adapter dell'activity ha dimensione " + activity.getAdapter().getCount());
		fillList();
		MyApplication.alertMessage(activity, "Ricerca di '" + searchForm.getText().toString() + "'", (adapter.getCount()) + " risultati trovati");
	}
	
	private void fillList() {
		//adapter.addAll(result);	
		listview.setAdapter(adapter);
    	
		if(this.activity instanceof BooksActivity){
			listview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					Entity book = (Entity)parent.getItemAtPosition(position);
					String bookId = book.get("id");
					Log.i(TAG, "Clicked on book " + bookId);
					dialog = new ProgressDialog(getActivity());
		        	dialog.setTitle("Searching");
		            dialog.setMessage("Please wait...");
		            dialog.setIndeterminate(false);
					SearchFragment.this.bookListener.onBookSelected(bookId, dialog);			
				}
					
			});
		}
		else{
			listview.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id){
					Entity course = (Entity)parent.getItemAtPosition(position);
					String courseId = course.get("id");
					String courseName = course.get("nome");
					Log.i(TAG, "Clicked on course " + courseId);
					dialog = new ProgressDialog(getActivity());
		        	dialog.setTitle("Searching");
		            dialog.setMessage("Please wait...");
		            dialog.setIndeterminate(false);
		            SearchFragment.this.courseListener.onCourseSelected(courseId, courseName, dialog);
				}
			});
		}
	}
	
	public static void clearList(ArrayAdapter<Entity> adapter) {
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
