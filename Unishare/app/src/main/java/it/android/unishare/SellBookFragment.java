package it.android.unishare;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonRectangle;


public class SellBookFragment extends Fragment implements ViewInitiator {

    public static final String TAG = "SellBookFragment";

    private View view;
    private EditText bookTitle;
    private EditText bookAuthor;
    private EditText bookPrice;
    private com.gc.materialdesign.widgets.ProgressDialog dialog;

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
        bookTitle = (EditText) view.findViewById(R.id.bookTitle);
        bookAuthor = (EditText) view.findViewById(R.id.bookAuthor);
        bookPrice = (EditText) view.findViewById(R.id.bookPrice);

        ButtonRectangle btn = (ButtonRectangle) view.findViewById(R.id.button_sell_book);
        String title = "Inserting";
        dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = SellBookFragment.this.bookTitle.getText().toString().trim();
                String author = bookAuthor.getText().toString().trim();
                String priceString = bookPrice.getText().toString().trim();
                if(bookTitle != null && !bookTitle.equals("") && author != null && !author.equals("")
                        && priceString != null && !priceString.equals("")){
                    float price = Float.valueOf(priceString);
                    booksActivity.getMyApplication().hideKeyboard(booksActivity);
                    if(!Utilities.checkNetworkState(booksActivity)){
                        String title = "Errore";
                        String message = "Verifica la tua connessione a Internet e riprova";
                        booksActivity.getMyApplication().alertMessage(title, message);
                        return;
                    }
                    booksActivity.sellBook(bookTitle, author, price, dialog);
                }

            }
        });
    }


}
