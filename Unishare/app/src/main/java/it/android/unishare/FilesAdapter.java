package it.android.unishare;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FilesAdapter extends ArrayAdapter<Entity> {

	public FilesAdapter(Context context, ArrayList<Entity> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		Entity entity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.files_row_layout, parent, false);
		}
		// Lookup view for data population
		TextView nome = (TextView) convertView.findViewById(R.id.first_column);
		TextView data = (TextView) convertView.findViewById(R.id.second_column);
		TextView dimensione = (TextView) convertView.findViewById(R.id.third_column);
		TextView url = (TextView) convertView.findViewById(R.id.fourth_column);
		// Populate the data into the template view using the data object
		nome.setText(entity.get("nome"));
		data.setText(entity.get("data"));
		dimensione.setText(entity.get("dimensione"));

		String urlString = entity.get("url");
		String fileLink = "<a href=\"" + urlString + "\">Download</a>";
		url.setText(Html.fromHtml(fileLink));
		url.setMovementMethod(LinkMovementMethod.getInstance());
		// Return the completed view to render on screen
		return convertView;

	}
	
	

}
