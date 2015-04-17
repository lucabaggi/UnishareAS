package it.android.unishare;

import java.util.ArrayList;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BooksSearchFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "BooksSearchFragment";
	
	private BooksActivity activity;
	private View view;
	
	private OnBookSelectedListener bookListener;
	
	//UI elements
	ListView listview;
	EditText searchForm;
	ProgressDialog dialog;
	BooksAdapter adapter;
	
	
	public interface OnBookSelectedListener {
        public void onBookSelected(String bookId, ProgressDialog dialog);
    }

    public BooksSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(view == null){
    		view = inflater.inflate(R.layout.books_search_fragment, container, false);
            initializeUI(view);
    	}
    	return view;       
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BooksActivity) activity;
		try {
            this.bookListener = (OnBookSelectedListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnBookSelectedListener");
        }
    }
    
    
    @Override
	public void initializeUI(View view) {
    	adapter = activity.getAdapter();
    	listview = (ListView) view.findViewById(R.id.ListView1);
    	if(adapter.getCount() > 0){
    		listview.setAdapter(adapter);
    		
    		listview.setOnItemClickListener(new OnItemClickListener(){

    			@Override
    			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
    				Entity book = (Entity)parent.getItemAtPosition(position);
    				String bookId = book.get("id");
    				Log.i(TAG, "Clicked on " + bookId);
    				dialog = new ProgressDialog(getActivity());
    	        	dialog.setTitle("Searching");
    	            dialog.setMessage("Please wait...");
    	            dialog.setIndeterminate(false);
    				BooksSearchFragment.this.bookListener.onBookSelected(bookId, dialog);			
    			}
    				
    		});
    	}    		
    	searchForm = (EditText) view.findViewById(R.id.opinionText);
    	Button btn = (Button) view.findViewById(R.id.insertOpinionButton);
        btn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View view) {
	        	dialog = new ProgressDialog(getActivity());
	        	dialog.setTitle("Searching");
	            dialog.setMessage("Please wait...");
	            dialog.setIndeterminate(false);
	        	activity.initializeFragmentUI(searchForm.getText().toString(), dialog);
	        }
        });
    	
	}

	public void displayResults(ArrayList<Entity> result, String tag) {
		clearList(adapter);
		adapter = activity.getAdapter();
		fillList(result);
		MyApplication.alertMessage(activity, "Ricerca di '" + searchForm.getText().toString() + "'", (result.size()) + " risultati trovati");
	}
	
	private void fillList(ArrayList<Entity> result) {
		adapter.addAll(result);	
		listview.setAdapter(adapter);
    		
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				Entity book = (Entity)parent.getItemAtPosition(position);
				String bookId = book.get("id");
				Log.i(TAG, "Clicked on " + bookId);
				dialog = new ProgressDialog(getActivity());
	        	dialog.setTitle("Searching");
	            dialog.setMessage("Please wait...");
	            dialog.setIndeterminate(false);
				BooksSearchFragment.this.bookListener.onBookSelected(bookId, dialog);			
			}
				
		});
	}
	
	public static void clearList(BooksAdapter adapter) {
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
