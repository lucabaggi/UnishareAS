package it.android.unishare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerAdapter extends ArrayAdapter<String> {

	private ArrayList<Integer> drawables;

	public DrawerAdapter(Context context, ArrayList<String> items, ArrayList<Integer> drawables) {
		super(context, 0, items);
		this.drawables = drawables;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){

        String item = getItem(position);
		
		if (convertView == null) {
		  convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_row_layout, parent, false);
		}
		// Lookup view for data population
		TextView title = (TextView) convertView.findViewById(R.id.drawerRowtextView);
		title.setText(item.toString());
		ImageView image = (ImageView) convertView.findViewById(R.id.iconImageView);
		image.setImageResource(drawables.get(position));
		// Populate the data into the template view using the data object
		// Return the completed view to render on screen
		return convertView;

	}
	
	

}
