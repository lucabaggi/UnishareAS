package it.android.unishare;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProfileFragment extends Fragment implements ViewInitiator {

    public static final String TAG = "ProfileFragment";

    private ProfileActivity profileActivity;
    private View view;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
            view = inflater.inflate(R.layout.profile_fragment, container, false);
        initializeUI(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.profileActivity = (ProfileActivity) activity;
    }

    @Override
    public void initializeUI(View view) {
        Button myCoursesButton = (Button) view.findViewById(R.id.my_courses_button);
        myCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity.myCourses();
            }
        });

        Button passedExamsButton = (Button) view.findViewById(R.id.passed_courses_button);
        passedExamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity.passedExams();
            }
        });
    }
}
