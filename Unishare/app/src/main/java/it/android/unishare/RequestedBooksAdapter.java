package it.android.unishare;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RequestedBooksAdapter extends ArrayAdapter<Entity> {

	public RequestedBooksAdapter(Context context, ArrayList<Entity> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		Entity entity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.requested_book_row_layout, parent, false);
		}
		// Lookup view for data population
		TextView date = (TextView) convertView.findViewById(R.id.date);
		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView author = (TextView) convertView.findViewById(R.id.author);
		TextView price = (TextView) convertView.findViewById(R.id.price);
		TextView email = (TextView) convertView.findViewById(R.id.email);
		// Populate the data into the template view using the data object
		date.setText(entity.get("data"));
		title.setText(entity.get("titolo"));
		author.setText(entity.get("autore"));
		price.setText(entity.get("prezzo") + " €");
		String userInfo = "Email: " + entity.get("email");
		if(entity.getInt("fbid")>0) {
			userInfo = userInfo + " - " +(Html.fromHtml("<a href=\"https://www.facebook.com/" + (entity.get("fbid").length() > 11 ? "app_scoped_user_id/" : "") + entity.getInt("fbid") + "\">Vai al profilo Facebook</a>"));
			email.setMovementMethod(LinkMovementMethod.getInstance());
		}
		email.setText(userInfo);

		// Return the completed view to render on screen
		return convertView;

	}
	
	

}
