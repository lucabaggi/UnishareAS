package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainFragment extends Fragment implements ViewInitiator {
	
	public final static String TAG = "it.android.unishare.MainFragment";
	
	private MainActivity activity;
	private View view;

	private ArrayList<Entity> notifications;

	private Integer [] viewFlipperArray = {
			R.layout.viewflipper_opinions,
			R.layout.viewflipper_books,
			R.layout.viewflipper_files
	};
	private Class[] activityArray = {
			MyCoursesActivity.class,
			MyBooksActivity.class,
			FilesActivity.class
	};
	private ArrayList<Integer> viewFlipperElements;
	private ArrayList<Class> activityElements;

	public MainFragment() {
		viewFlipperElements = new ArrayList<Integer>(Arrays.asList(viewFlipperArray));
		activityElements = new ArrayList<Class>(Arrays.asList(activityArray));
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeUI(view);
        return view;
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
    }
    
    @Override
	public void initializeUI(View view) {

		ImageView campusImageView = (ImageView) view.findViewById(R.id.campusImageView);
		Utilities.loadImage(campusImageView, "campus.jpg", activity.getApplicationContext());

		ImageView universityLogoImageView = (ImageView) view.findViewById(R.id.universityLogoImage);
		Utilities.loadImage(universityLogoImageView, "universityLogo.jpg", activity.getApplicationContext());

		TextView dashNews = (TextView) view.findViewById(R.id.dashNews);
		//if(activity.getNews()!=null )dashNews.setText(activity.getNews());


		setHintsFlipper();
		setNotificationsFlipper();



	}

	private void setHintsFlipper() {
		LayoutInflater factory = LayoutInflater.from(activity);
		ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);

		Random rand = new Random();
		Collections.shuffle(viewFlipperElements, rand);
		Collections.shuffle(activityElements, rand);

		for(final Integer index : viewFlipperElements) {
			View newView = factory.inflate(index, null);
			/*
			newView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					activity.getMyApplication().newActivity(activityElements.get(viewFlipperElements.indexOf(index)));
				}
			});
			*/
			viewFlipper.addView(newView);
		}

		viewFlipper.setInAnimation(activity, R.anim.slide_in_from_left);
		viewFlipper.setOutAnimation(activity, R.anim.slide_out_to_right);
		viewFlipper.setFlipInterval(8000);
		viewFlipper.startFlipping();
	}

	private void setNotificationsFlipper() {
		LayoutInflater factory = LayoutInflater.from(activity);
		ViewFlipper notificationsFlipper = (ViewFlipper) view.findViewById(R.id.notificationsFlipper);

		Cursor cursor = activity.getNotifications();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			View newView = factory.inflate(R.layout.viewflipper_notifications, null);
			TextView textView = (TextView) newView.findViewById(R.id.textView);
			textView.setText(cursor.getString(1));
			ImageView imageView = (ImageView) newView.findViewById(R.id.imageView);
			switch(cursor.getInt(0)){
				case 0: imageView.setImageResource(R.drawable.commento_green); break;
				case 1: imageView.setImageResource(R.drawable.dialogo_green); break;
				case 2: imageView.setImageResource(R.drawable.libro_green); break;
				case 3: imageView.setImageResource(R.drawable.file_green); break;
				case 4: imageView.setImageResource(R.drawable.calendario_green); break;
			}
			notificationsFlipper.addView(newView);
			cursor.moveToNext();
		}

		notificationsFlipper.setInAnimation(activity, R.anim.slide_in_from_above);
		notificationsFlipper.setOutAnimation(activity, R.anim.slide_out_to_bottom);
		notificationsFlipper.setFlipInterval(4000);
		notificationsFlipper.startFlipping();
	}
    
}
