package it.android.unishare;

import it.android.unishare.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;

public class SearchFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "SearchFragment";
	
	private AdapterActivity activity;
	private View view;
	
	private OnBookSelectedListener bookListener;
	private OnCourseSelectedListener courseListener;
	
	//UI elements
	ListView listview;
	EditText searchForm;
	com.gc.materialdesign.widgets.ProgressDialog dialog;
	ArrayAdapter<Entity> adapter;
	
	
	public interface OnBookSelectedListener {
        public void onBookSelected(String bookId, com.gc.materialdesign.widgets.ProgressDialog dialog);
    }
	
	public interface OnCourseSelectedListener {
		public void onCourseSelected(String courseId, String courseName, com.gc.materialdesign.widgets.ProgressDialog dialog);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(activity instanceof CoursesActivity)
            registerForContextMenu(listview);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        Entity course = ((CoursesActivity) activity).getAdapter().getItem(info.position);
        String courseName = course.get("nome");
        menu.setHeaderTitle(courseName);
        menu.add(Menu.NONE, R.id.add_to_fav_item, Menu.NONE, "Aggiungi ai preferiti");
		menu.add(Menu.NONE, R.id.add_to_passed_item, Menu.NONE, "Aggiungi ai corsi superati");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

		//getting course info
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final Entity course = ((CoursesActivity) activity).getAdapter().getItem(info.position);
		final String courseName = course.get("nome");
		final int courseId = Integer.parseInt(course.get("id"));

        switch (item.getItemId()) {
            case R.id.add_to_fav_item:
                Log.i("ContextMenu", "Item addFav was chosen");
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.MyCoursesTable.COLUMN_NAME, courseName);
                values.put(DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID, courseId);
                try{
                    activity.getMyApplication().insertIntoDatabaseCatchingExceptions(DatabaseContract.MyCoursesTable.TABLE_NAME, values);
                    activity.getMyApplication().toastMessage(activity, courseName + " è stato aggiunto ai Preferiti");

                }
                catch (SQLiteConstraintException e){
                    Log.i(TAG, "Corso già presente nel db");
                    activity.getMyApplication().alertMessage("", "Corso già presente fra i Preferiti");
                }
                return true;
			case R.id.add_to_passed_item:
				Log.i("ContextMenu", "Item addPassed was chosen");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                String title = "";
                String message ="Con quale voto hai superato il corso?";
                DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int grade = Integer.parseInt(input.getText().toString());
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.PassedExams.COLUMN_NAME, courseName);
                        values.put(DatabaseContract.PassedExams.COLUMN_COURSE_ID, courseId);
                        values.put(DatabaseContract.PassedExams.GRADE, grade);
                        try{
                            activity.getMyApplication().insertIntoDatabaseCatchingExceptions(DatabaseContract.PassedExams.TABLE_NAME, values);
                            activity.getMyApplication().toastMessage(activity, courseName + " è stato aggiunto ai Corsi superati");

                        }
                        catch (SQLiteConstraintException e){
                            Log.i(TAG, "Corso già presente nel db");
                            activity.getMyApplication().alertMessage("", "Corso già presente fra gli esami superati");
                        }
                    }
                };
                DialogInterface.OnClickListener actionFalse = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                };
                activity.getMyApplication().alertDecision(title, message, input, actionTrue, actionFalse);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    
    @Override
	public void initializeUI(View view) {

        if(this.activity instanceof CoursesActivity){
            Button btn = (Button) view.findViewById(R.id.button_add_course);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CoursesActivity)activity).launchAddCourseFragment();
                }
            });
        }

    	adapter = activity.getAdapter();
    	Log.i(TAG, "l'adapter dell'activity ha dimensione " + activity.getAdapter().getCount());
    	listview = (ListView) view.findViewById(R.id.ListView1);
    	if(adapter.getCount() > 0){
    		listview.setAdapter(adapter);
    		
    		if(this.activity instanceof BooksActivity){
    			listview.setOnItemClickListener(new OnItemClickListener(){

    				@Override
    				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                        if(!Utilities.checkNetworkState(activity)){
                            String title = "Errore";
                            String message = "Verifica la tua connessione a Internet e riprova";
                            activity.getMyApplication().alertMessage(title, message);
                            return;
                        }
    					Entity book = (Entity)parent.getItemAtPosition(position);
    					String bookId = book.get("id");
    					Log.i(TAG, "Clicked on book " + bookId);
                        String title = "Searching";
    					dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
    					SearchFragment.this.bookListener.onBookSelected(bookId, dialog);			
    				}
    					
    			});
    		}
    		else{
    			listview.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!Utilities.checkNetworkState(activity)) {
                            String title = "Errore";
                            String message = "Verifica la tua connessione a Internet e riprova";
                            activity.getMyApplication().alertMessage(title, message);
                            return;
                        }
                        Entity course = (Entity) parent.getItemAtPosition(position);
                        String courseId = course.get("id");
                        String courseName = course.get("nome");
                        Log.i(TAG, "Clicked on course " + courseId);
                        String title = "Searching";
                        dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
                        SearchFragment.this.courseListener.onCourseSelected(courseId, courseName, dialog);
                    }
                });


    		}
    	}    		
    	searchForm = (EditText) view.findViewById(R.id.opinionText);
    	ButtonRectangle btn = (ButtonRectangle) view.findViewById(R.id.searchButton);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
				if(!searchForm.getText().toString().trim().equals("")){
					if (!Utilities.checkNetworkState(activity)) {
						String title = "Errore";
						String message = "Verifica la tua connessione a Internet e riprova";
						activity.getMyApplication().alertMessage(title, message);
						return;
					}
					clearList(adapter);
					String title = "Searching";
					dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
					activity.getMyApplication().hideKeyboard((Context) activity);
					activity.initializeFragmentUI(searchForm.getText().toString(), dialog);
					}
				}
        });

		if(this.activity instanceof BooksActivity){
			ButtonFloat sellBookButton = (ButtonFloat) view.findViewById(R.id.sellBookButtonFloat);
			sellBookButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((BooksActivity)activity).launchSellFragment();
				}
			});
		}
    	
	}

	public void displayResults(String tag) {
		adapter = activity.getAdapter();
		Log.i(TAG, "l'adapter dell'activity ha dimensione " + activity.getAdapter().getCount());
		fillList();
		activity.getMyApplication().alertMessage("Ricerca di '" + searchForm.getText().toString() + "'", (adapter.getCount()) + " risultati trovati");
	}
	
	private void fillList() {
		//adapter.addAll(result);	
		listview.setAdapter(adapter);
    	
		if(this.activity instanceof BooksActivity){
			listview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					if(!Utilities.checkNetworkState(activity)){
						String title = "Errore";
						String message = "Verifica la tua connessione a Internet e riprova";
						activity.getMyApplication().alertMessage(title, message);
                        return;
					}
					Entity book = (Entity)parent.getItemAtPosition(position);
					String bookId = book.get("id");
					Log.i(TAG, "Clicked on book " + bookId);
                    String title = "Searching";
                    dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
					SearchFragment.this.bookListener.onBookSelected(bookId, dialog);			
				}
					
			});
		}
		else{
			listview.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id){
                    if(!Utilities.checkNetworkState(activity)){
                        String title = "Errore";
                        String message = "Verifica la tua connessione a Internet e riprova";
                        activity.getMyApplication().alertMessage(title, message);
                        return;
                    }
					Entity course = (Entity)parent.getItemAtPosition(position);
					String courseId = course.get("id");
					String courseName = course.get("nome");
					Log.i(TAG, "Clicked on course " + courseId);
                    String title = "Searching";
                    dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
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
