package it.android.unishare;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class SellBookFragment extends Fragment implements ViewInitiator {

    public static final String TAG = "SellBookFragment";

    private View view;
    private EditText bookTitle;
    private EditText bookAuthor;
    private EditText bookPrice;

    private BooksActivity booksActivity;

    public SellBookFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
            view = inflater.inflate(R.layout.sell_book_fragment, container, false);
        initializeUI(view);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.booksActivity = (BooksActivity) activity;
    }

    @Override
    public void initializeUI(View view){

    }


}
