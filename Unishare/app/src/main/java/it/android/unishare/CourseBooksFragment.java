package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;

public class CourseBooksFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "CourseBooksFragment";

	private View view;
	private ListView listview;
    private ProgressDialog dialog;
    //private SwipeRefreshLayout swipeRefreshLayout;

    private OnBookSelectedListener bookListener;

	private MyCoursesActivity activity;
    private BooksAdapter booksAdapter;

	public CourseBooksFragment(){

	}

	public interface OnBookSelectedListener {
		public void onBookSelected(String bookId, ProgressDialog dialog);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
            view = inflater.inflate(R.layout.course_books_fragment, container, false);
        initializeUI(view);
        return view;
    }


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MyCoursesActivity) activity;
        try{
            this.bookListener = (OnBookSelectedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
        }
    }


	@Override
	public void initializeUI(View view) {
        listview = (ListView) view.findViewById(R.id.courseBooksListView);

        booksAdapter = activity.getBooksAdapter();
        listview.setAdapter(booksAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Utilities.checkNetworkState(activity)) {
                    String title = "Errore";
                    String message = "Verifica la tua connessione a Internet e riprova";
                    activity.getMyApplication().alertMessage(title, message);
                    return;
                }
                Entity book = (Entity) parent.getItemAtPosition(position);
                String bookId = book.get("id");
                Log.i(TAG, "Clicked on book " + bookId);
                String title = "Searching";
                dialog = new ProgressDialog(getActivity(), title);
                CourseBooksFragment.this.bookListener.onBookSelected(bookId, dialog);
            }
        });
	}

	

}
