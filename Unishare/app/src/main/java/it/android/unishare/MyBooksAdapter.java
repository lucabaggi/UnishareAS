package it.android.unishare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyBooksAdapter extends ArrayAdapter<Entity> {

	public MyBooksAdapter(Context context, ArrayList<Entity> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		Entity entity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_books_row_layout, parent, false);
		}
		// Lookup view for data population
		TextView title = (TextView) convertView.findViewById(R.id.first_column);
		TextView author = (TextView) convertView.findViewById(R.id.second_column);
		TextView price = (TextView) convertView.findViewById(R.id.third_column);
		TextView requests = (TextView) convertView.findViewById(R.id.fourth_column);
		// Populate the data into the template view using the data object
		title.setText(entity.get("titolo"));
		author.setText(entity.get("autore"));
		String priceString = entity.get("prezzo") + " €";
		price.setText(priceString);
		requests.setText(entity.get("richieste"));
		// Return the completed view to render on screen
		return convertView;

	}
	
	

}
