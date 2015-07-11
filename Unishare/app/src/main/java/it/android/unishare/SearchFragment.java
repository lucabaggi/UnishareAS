package it.android.unishare;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

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
    LinearLayout searchHeader;
	com.gc.materialdesign.widgets.ProgressDialog dialog;
	ArrayAdapter<Entity> adapter;
	
	
	public interface OnBookSelectedListener {
        public void onBookSelected(String bookId, com.gc.materialdesign.widgets.ProgressDialog dialog);
    }
	
	public interface OnCourseSelectedListener {
		public void onCourseSelected(String courseId, String courseName, Entity course, com.gc.materialdesign.widgets.ProgressDialog dialog);
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
        menu.add(Menu.NONE, R.id.add_to_fav_item, Menu.NONE, "Aggiungi ai corsi attuali");
		menu.add(Menu.NONE, R.id.add_to_passed_item, Menu.NONE, "Aggiungi ai corsi superati");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

		//getting course info
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final Entity course = ((CoursesActivity) activity).getAdapter().getItem(info.position);
		final String courseName = course.get("nome");
		final String professor = course.get("professore");
		final int courseId = Integer.parseInt(course.get("id"));

        switch (item.getItemId()) {
            case R.id.add_to_fav_item:
                Log.i("ContextMenu", "Item addFav was chosen");
                if(!inPassedExams(courseId)){
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.MyCoursesTable.COLUMN_NAME, courseName);
                    values.put(DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID, courseId);
                    values.put(DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR, professor);
                    try{
                        activity.getMyApplication().insertIntoDatabaseCatchingExceptions(DatabaseContract.MyCoursesTable.TABLE_NAME, values);
                        activity.getMyApplication().toastMessage(activity, courseName + " è stato aggiunto ai Preferiti");
                        ((CoursesActivity)activity).addToActualExams(courseId);
                    }
                    catch (SQLiteConstraintException e){
                        Log.i(TAG, "Corso già presente nel db");
                        activity.getMyApplication().alertMessage("", "Corso già presente fra i Preferiti");
                    }
                }
                else
                    activity.getMyApplication().toastMessage(activity, "Corso già presente tra gli esami sostenuti");
                return true;
			case R.id.add_to_passed_item:
				Log.i("ContextMenu", "Item addPassed was chosen");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
				final CheckBox checkbox = new CheckBox(getActivity());
				checkbox.setText("Lode");
                String title = "";
                String message ="Con quale voto hai superato il corso?";
                DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gradeString = input.getText().toString().trim();
                        boolean isChecked = checkbox.isChecked();
                        if(isValid(gradeString, isChecked, courseId)){
                            int grade = Integer.parseInt(input.getText().toString());
                            int lode = 0;
                            ContentValues values = new ContentValues();
                            values.put(DatabaseContract.PassedExams.COLUMN_NAME, courseName);
                            values.put(DatabaseContract.PassedExams.COLUMN_COURSE_ID, courseId);
                            values.put(DatabaseContract.PassedExams.COLUMN_PROFESSOR, professor);
                            values.put(DatabaseContract.PassedExams.COLUMN_GRADE, grade);
                            if(isChecked){
                                values.put(DatabaseContract.PassedExams.COLUMN_LAUDE, 1);
                                lode = 1;
                            }
                            else
                                values.put(DatabaseContract.PassedExams.COLUMN_LAUDE, 0);
                            try{
                                activity.getMyApplication().insertIntoDatabaseCatchingExceptions(DatabaseContract.PassedExams.TABLE_NAME, values);
                                activity.getMyApplication().toastMessage(activity, courseName + " è stato aggiunto ai Corsi superati");
                                ((CoursesActivity)activity).addToPassedExams(courseId, grade, lode);
                            }
                            catch (SQLiteConstraintException e){
                                Log.i(TAG, "Corso già presente nel db");
                                activity.getMyApplication().alertMessage("", "Corso già presente fra gli esami superati");
                            }
                        }
                        else
                            activity.getMyApplication().toastMessage(activity, "Verifica che i dati inseriti siano corretti");
                    }
                };
                DialogInterface.OnClickListener actionFalse = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                };
                activity.getMyApplication().alertDecision(title, message, input, checkbox, actionTrue, actionFalse);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private boolean inPassedExams(int courseId){
        String[] selectionArgs = {((Integer)courseId).toString()};
        Cursor cursor = activity.getMyApplication().queryDatabase(DatabaseContract.PassedExams.TABLE_NAME, null,
                DatabaseContract.PassedExams.COLUMN_COURSE_ID + " = ?", selectionArgs, null, null, null);
        if(cursor.getCount() > 0)
            return true;
        return false;
    }

    private boolean isValid(String gradeString, boolean isChecked, int courseId){
        if(isInteger(gradeString)){
            int grade = Integer.parseInt(gradeString);
            if(grade >= 18 && grade <= 30 && ((!isChecked) || (grade == 30))){
                if(coursePresentInCurrent(courseId))
                    deleteFromCurrent(courseId);
                return true;
            }
        }
        return false;
    }

    private boolean isInteger(String s){
        if(s == null || s.length()==0)
            return false;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c <= '/' || c >= ':')
                return false;
        }
        return true;
    }

    private boolean coursePresentInCurrent(int courseId){
        String[] selectionArgs = {((Integer)courseId).toString()};
        Cursor cursor = activity.getMyApplication().queryDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, null,
                DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", selectionArgs, null, null, null);
        if(cursor.getCount() > 0)
            return true;
        return false;
    }

    private void deleteFromCurrent(int courseId){
        String whereArgs[] = {((Integer)courseId).toString()};
        activity.getMyApplication().deleteFromTable(DatabaseContract.MyCoursesTable.TABLE_NAME,
                DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
    }
    
    
    @Override
	public void initializeUI(View view) {

        searchHeader = (LinearLayout) view.findViewById(R.id.list_header);
        searchHeader.setVisibility(View.INVISIBLE);


    	adapter = activity.getAdapter();
    	Log.i(TAG, "l'adapter dell'activity ha dimensione " + activity.getAdapter().getCount());
    	listview = (ListView) view.findViewById(R.id.ListView1);
    	if(adapter.getCount() > 0){
            searchHeader.setVisibility(View.VISIBLE);
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
                        SearchFragment.this.courseListener.onCourseSelected(courseId, courseName, course, dialog);
                    }
                });


    		}
    	}    		
    	searchForm = (EditText) view.findViewById(R.id.opinionText);
    	ImageButton btn = (ImageButton) view.findViewById(R.id.searchButton);
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
					activity.getMyApplication().hideKeyboard(activity);
					activity.initializeFragmentUI(searchForm.getText().toString(), dialog);
					}
				}
        });

        //Action buttons
        if(this.activity instanceof BooksActivity) {
            ButtonRectangle soldBtn = (ButtonRectangle) view.findViewById(R.id.booksSold);
            soldBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.getMyApplication().newActivity(MyBooksActivity.class);
                }
            });

            ButtonRectangle reqBtn = (ButtonRectangle) view.findViewById(R.id.booksRequested);
            reqBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.getMyApplication().newActivity(RequestedBooksActivity.class);
                }
            });
        }
        else if(this.activity instanceof CoursesActivity){
            ButtonRectangle addBtn = (ButtonRectangle) view.findViewById(R.id.button_add_course);
            addBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CoursesActivity)activity).launchAddCourseFragment();
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
        if(adapter.getCount() > 0)
            searchHeader.setVisibility(View.VISIBLE);
        else
            searchHeader.setVisibility(View.INVISIBLE);
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
		            SearchFragment.this.courseListener.onCourseSelected(courseId, courseName, course ,dialog);
				}
			});
		}
	}
	
	public static void clearList(ArrayAdapter<Entity> adapter) {
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
