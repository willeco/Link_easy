package fr.willy.linky;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class CustomPopUp extends Dialog {

    //fields
    private String title;
    private String subtitle;
    private TextView confirm_text, cancel_text;
    private TextView titleView, subtitleView;
    private Spinner device_spinner;
    private Activity parent_activity;

    //constructor
    public CustomPopUp(Activity activity)
    {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.my_popup_device_adding_template);
        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.confirm_text = findViewById(R.id.confirm_text);
        this.cancel_text  = findViewById(R.id.cancel_text);
        this.titleView    = findViewById(R.id.device_popup_title);
        this.subtitleView = findViewById(R.id.device_popup_subtitle);
        this.device_spinner = findViewById(R.id.device_spinner);
        this.parent_activity = activity;
    }

    public void setTitle(String title){this.title = title;}

    public void setSubtitle(String subtitle){this.subtitle = subtitle;}

    public void getSpinnerData(){
        this.device_spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this.parent_activity);
    }

    public TextView getConfirm_text(){return confirm_text;}

    public TextView getCancel_text(){return cancel_text;}

    public void test_bluid(){
        show();
    }

}
