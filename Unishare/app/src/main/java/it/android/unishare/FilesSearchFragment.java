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

public class FilesSearchFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "FIlesSearchFragment";

	private FilesActivity activity;
	private View view;


	//UI elements
	ListView listview;
	EditText searchForm;
    LinearLayout searchHeader;
	com.gc.materialdesign.widgets.ProgressDialog dialog;
	ArrayAdapter<Entity> adapter;


    public FilesSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(view == null){
            view = inflater.inflate(R.layout.files_search_fragment, container, false);
            initializeUI(view);
    	}
    	return view;       
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        this.activity = (FilesActivity) activity;

    }

    
    @Override
	public void initializeUI(View view) {

        searchHeader = (LinearLayout) view.findViewById(R.id.list_header);
        searchHeader.setVisibility(View.INVISIBLE);


    	adapter = activity.getAdapter();
    	Log.i(TAG, "l'adapter dell'activity ha dimensione " + activity.getAdapter().getCount());
    	listview = (ListView) view.findViewById(R.id.filesListView);
    	if(adapter.getCount() > 0){
            searchHeader.setVisibility(View.VISIBLE);
    		listview.setAdapter(adapter);
    	}    		
    	searchForm = (EditText) view.findViewById(R.id.filesText);
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
	}
	
	public static void clearList(ArrayAdapter<Entity> adapter) {
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
}
