package it.android.unishare;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gc.materialdesign.views.ButtonRectangle;


public class AddCourseFragment extends Fragment implements ViewInitiator {

    public static final String TAG = "AddCourseFragment";

    private View view;
    private EditText courseName;
    private EditText profName;
    private EditText language;
    private EditText cfu;
    private RadioGroup radioGroup;
    private com.gc.materialdesign.widgets.ProgressDialog dialog;

    private CoursesActivity activity;


    public AddCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (CoursesActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
            view = inflater.inflate(R.layout.add_course_fragment, container, false);
        initializeUI(view);
        return view;
    }


    @Override
    public void initializeUI(View view) {
        courseName = (EditText) view.findViewById(R.id.courseNameToAdd);
        profName = (EditText) view.findViewById(R.id.professorName);
        language = (EditText) view.findViewById(R.id.language);
        cfu = (EditText) view.findViewById(R.id.cfu);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        ButtonRectangle btn = (ButtonRectangle) view.findViewById(R.id.button_insert_course);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = AddCourseFragment.this.courseName.getText().toString().trim();
                String prof = profName.getText().toString().trim();
                String language = AddCourseFragment.this.language.getText().toString().trim();
                String cfuString = AddCourseFragment.this.cfu.getText().toString().trim();

                String title = "Inserting";
                dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);

                if(!courseName.equals("") && !prof.equals("") && !language.equals("") && !cfuString.equals("")
                        && radioGroup.getCheckedRadioButtonId() != -1){
                    activity.getMyApplication().hideKeyboard(activity);
                    if(!Utilities.checkNetworkState(activity)){
                        String dialogTitle = "Errore";
                        String message = "Verifica la tua connessione a Internet e riprova";
                        activity.getMyApplication().alertMessage(dialogTitle, message);
                        return;
                    }
                    float cfu = Float.valueOf(cfuString);
                    int index = radioGroup.getCheckedRadioButtonId();
                    Log.i(AddCourseFragment.TAG, "indice selezionato: " + index);
                    RadioButton btn = (RadioButton) AddCourseFragment.this.view.findViewById(index);
                    Log.i(AddCourseFragment.TAG, "selezione: " + btn.getText());
                    String radioValue = btn.getText().toString();
                    activity.insertNewCourse(courseName, prof, language, cfu, radioValue, dialog);
                }

            }
        });
    }
}
