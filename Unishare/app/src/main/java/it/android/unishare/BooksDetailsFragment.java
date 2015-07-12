package it.android.unishare;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;

import java.net.URI;

public class BooksDetailsFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "BooksDetailsFragment";
	
	private static final String BOOK_ENTITY = "book_entity_key";
	
	private Entity book;
	private View view;

    private BooksActivity activity;
    private MyCoursesActivity myCoursesActivity;
	
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
        if(activity instanceof  BooksActivity)
            this.activity = (BooksActivity) activity;
        else
            this.myCoursesActivity = (MyCoursesActivity) activity;
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
        TextView editore = (TextView) view.findViewById(R.id.editoreTextView);
		TextView amazonLinkTextView = (TextView) view.findViewById(R.id.amazonLink);
        ImageView amazonImage = (ImageView) view.findViewById(R.id.amazonImage);
        ButtonRectangle button = (ButtonRectangle) view.findViewById(R.id.insertOpinionButton);
		
		text1.setText(book.get("titolo"));
		text2.setText("Autore: " + book.get("autore"));
		text3.setText("Prezzo: " + book.get("prezzo") + " euro");
        editore.setText("Editore: " + book.get("editore"));

        String imageUrl = book.get("immagine");
        if(imageUrl.length() > 0)
        {
            amazonImage.setTag(imageUrl);
            new DownloadImagesTask().execute(amazonImage);
        }
        else
        {
            amazonImage.setImageResource(R.drawable.book_cover);
        }


		String url = book.get("url");
        if(url.length() > 0) {
            String amazonLink;
            if(book.get("prezzo_amazon").length() > 0) {
                amazonLink = "<a href=\"" + url + "\">Questo libro e' disponibile anche su Amazon a partire da "+ book.get("prezzo_amazon") +"</a>";
            }
            else {
                amazonLink = "<a href=\"" + url + "\">Questo libro e' disponibile anche su Amazon</a>";
            }
            amazonLinkTextView.setText(Html.fromHtml(amazonLink));
            amazonLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }



		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                int bookId = Integer.parseInt(book.get("id"));
                if(activity != null)
				    activity.requestBook(bookId);
                else
                    myCoursesActivity.requestBook(bookId);
			}
		});
	}

}
