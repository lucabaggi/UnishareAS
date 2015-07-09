package it.android.unishare;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class UniversiAdapter extends ArrayAdapter<Entity> {

	public UniversiAdapter(Context context, ArrayList<Entity> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		Entity entity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.universi_row_layout, parent, false);
		}
		// Lookup view for data population
		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView except = (TextView) convertView.findViewById(R.id.except);
		ImageView image = (ImageView) convertView.findViewById(R.id.universiImageView);
		TextView link = (TextView) convertView.findViewById(R.id.universiLink);

		// Populate the data into the template view using the data object
        Ion.with(image)
                .placeholder(R.drawable.image_placeholder)
                .load(entity.get("immagine"));

		title.setText(entity.get("titolo"));
        except.setText(entity.get("testo"));

        String universiLink = "<a href=\"" + entity.get("url") + "\">Vai all'articolo completo</a>";
        link.setText(Html.fromHtml(universiLink));
        link.setMovementMethod(LinkMovementMethod.getInstance());
		// Return the completed view to render on screen
		return convertView;

	}
	
	

}
