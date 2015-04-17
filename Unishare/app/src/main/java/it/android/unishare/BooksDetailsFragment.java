package it.android.unishare;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BooksDetailsFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "BooksDetailsFragment";
	
	private static final String BOOK_ENTITY = "book_entity_key";
	
	private Entity book;
	private View view;
	
	public BooksDetailsFragment(){
		
	}
	
	public BooksDetailsFragment (Entity book){
		this.book = book;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null)
			book = savedInstanceState.getParcelable(BOOK_ENTITY);
        view = inflater.inflate(R.layout.books_details_fragment, container, false);
        initializeUI(view);
        return view;
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
    }
	
	@Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	outState.putParcelable(BOOK_ENTITY, book);
    }
    

	@Override
	public void initializeUI(View view) {
		TextView text1 = (TextView) view.findViewById(R.id.textView1);
		TextView text2 = (TextView) view.findViewById(R.id.textView2);
		TextView text3 = (TextView) view.findViewById(R.id.textView3);
		TextView text4 = (TextView) view.findViewById(R.id.textView4);
		Button button = (Button) view.findViewById(R.id.insertOpinionButton);
		
		text1.setText("Titolo: " + book.get("titolo"));
		text2.setText("Autore: " + book.get("autore"));
		text3.setText("Prezzo: " + book.get("prezzo") + " euro");	
	}
	
	

}
