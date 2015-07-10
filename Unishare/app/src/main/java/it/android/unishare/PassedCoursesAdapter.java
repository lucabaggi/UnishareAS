package it.android.unishare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PassedCoursesAdapter extends ArrayAdapter<Entity> {

	public PassedCoursesAdapter(Context context, ArrayList<Entity> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		Entity entity = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passed_exams_row_layout, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.first_column);
        TextView author = (TextView) convertView.findViewById(R.id.second_column);
        TextView grade = (TextView) convertView.findViewById(R.id.third_column);
        // Populate the data into the template view using the data object
        title.setText(entity.get("nome"));
        author.setText(entity.get("professore"));
        String courseGrade;
        String courseGradeText = entity.get("valutazione");
        if(courseGradeText.equals("0"))
            courseGrade = "ND";
        else
            courseGrade = courseGradeText;
        if(entity.get("lode").equals("1"))
            courseGrade = courseGrade + "L";
        grade.setText(courseGrade);
            // Return the completed view to render on screen
            return convertView;

	}
	
	

}
