package it.android.unishare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class RequestsAdapter extends ArrayAdapter<Entity> {

	public RequestsAdapter(Context context, ArrayList<Entity> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		Entity entity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.requests_row_layout, parent, false);
		}
		// Lookup view for data population
		TextView date = (TextView) convertView.findViewById(R.id.date);
		TextView user = (TextView) convertView.findViewById(R.id.user);
		TextView email = (TextView) convertView.findViewById(R.id.email);
		// Populate the data into the template view using the data object
		date.setText(entity.get("data"));
		user.setText(entity.get("name"));
		email.setText(entity.get("email"));
		// Return the completed view to render on screen
		return convertView;

	}
	
	

}
