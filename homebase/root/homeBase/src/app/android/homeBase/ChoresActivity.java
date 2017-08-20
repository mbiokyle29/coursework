package app.android.homeBase;


import android.content.Intent;
import android.support.v4.view.MotionEventCompat;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.app.ProgressDialog;
import android.view.MotionEvent;
import android.widget.Toast;
import android.util.Log;


import com.beardedhen.androidbootstrap.BootstrapButton;

public class ChoresActivity extends HomeBaseActivity {
    private ArrayList<BootstrapButton> choreContainers;
    private ArrayList<String> choreTitles;
    private HashMap<BootstrapButton, HomeBaseAlert> choreDescriptions;
    private LinearLayout layout;
    private boolean startCalled = false;
    private ApplicationManager mApplication;
    private BootstrapButton selectedFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "ChoresActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_chores);

        layout = (LinearLayout) findViewById(R.id.chores_choreContainer_button);
        layout.removeAllViews();
        choreContainers = new ArrayList<BootstrapButton>();
        choreTitles = new ArrayList<String>();
        choreDescriptions = new HashMap<BootstrapButton, HomeBaseAlert>();

        startCalled = true;
        selectedFilter = (BootstrapButton) findViewById(R.id.chores_allFilter_button);
        selectedFilter.setEnabled(false);

        mApplication.parse.getAlerts(this, "Chore");

        inflateProgressBar();

        layout.addView(loadingProgress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (startCalled) {
            startCalled = false;
            return;
        }

        mApplication.parse.refreshAlerts(this, "Chore");
    }

    public void onChoreContainerClick(View view)
    {
        BootstrapButton thisButton = (BootstrapButton) view.findViewById(R.id.alertContainer_container);

        Intent intent = new Intent(ChoresActivity.this, ChoreInfoActivity.class);
        intent.putExtra("caller", myClassName);
        intent.putExtra("title", choreDescriptions.get(thisButton).getTitle());
        intent.putExtra("info", choreDescriptions.get(thisButton).getDescription());
        intent.putExtra("creator", choreDescriptions.get(thisButton).getCreatorID());
        intent.putExtra("alertID", choreDescriptions.get(thisButton).getId());
        startActivity(intent);
    }

    public void onChoreAddClick(View view)
    {
        Intent intent = new Intent(ChoresActivity.this, ChoreCreateActivity.class);
        intent.putExtra("caller", myClassName);
        startActivity(intent);
    }


    public void onAllFilterClick(View view)
    {
        selectedFilter.setEnabled(true);

        BootstrapButton clicked = (BootstrapButton) view;
        clicked.setBootstrapButtonEnabled(false);

        selectedFilter = clicked;

        layout.removeAllViews();

        if (choreContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no chores at this time");
            header.setText("Welcome");
            return;
        }

        for(int i = 0; i < choreContainers.size(); i++) {
            BootstrapButton choreContainer = choreContainers.get(i);
            layout.addView(choreContainer);
        }
    }


    public void onCreatedFilterClick(View view)
    {
        selectedFilter.setEnabled(true);

        BootstrapButton clicked = (BootstrapButton) view;
        clicked.setBootstrapButtonEnabled(false);

        selectedFilter = clicked;

        layout.removeAllViews();

        if (choreContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no chores at this time");
            header.setText("Welcome");
            return;
        }

        for(int i = 0; i < choreContainers.size(); i++) {
            BootstrapButton choreContainer = choreContainers.get(i);
            if (choreDescriptions.get(choreContainer).getCreatorID().equals(mApplication.parse.getCurrentUser().getUsername().toString())) {
                layout.addView(choreContainer);
            }
        }
    }

    public void onResponsibleFilterClick(View view)
    {
        selectedFilter.setEnabled(true);

        BootstrapButton clicked = (BootstrapButton) view;
        clicked.setBootstrapButtonEnabled(false);

        selectedFilter = clicked;

        layout.removeAllViews();

        if (choreContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no chores at this time");
            header.setText("Welcome");
            return;
        }

        for(int i = 0; i < choreContainers.size(); i++) {
            BootstrapButton choreContainer = choreContainers.get(i);
            for (int j = 0; j < choreDescriptions.get(choreContainer).getResponsibleUsers().size(); j++) {
                if (choreDescriptions.get(choreContainer).getResponsibleUsers().get(j).equals(mApplication.parse.getCurrentUser().getUsername().toString())) {
                    layout.addView(choreContainer);
                }
            }
        }
    }

    @Override
    public void onGetAlertListByTypeSuccess(ArrayList<HomeBaseAlert> alerts)
    {
        layout.removeView(loadingProgress);
        if (alerts.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no chores at this time");
            header.setText("Welcome");
            return;
        } else {
            for (int i = 0; i < alerts.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

                BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
                BootstrapButton headerBar = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

                buttonCont.removeView(myButton);
                layout.addView(myButton);

                String title = alerts.get(i).getTitle();
                String information = alerts.get(i).getDescription();
                String creator = alerts.get(i).getCreatorID();
                ArrayList<String> responsibleUsers = new ArrayList<String>(alerts.get(i).getResponsibleUsers());

                headerBar.setText(title);
                headerBar.setBootstrapType("chore");
                myButton.setText(information);

                choreContainers.add(myButton);
                choreTitles.add(title);
                choreDescriptions.put(myButton, alerts.get(i));
            }
        }
    }

    @Override
    public void onGetAlertListByTypeFailure(String e)
    {

    }

    @Override
    public void onUpdateAlertListByTypeSuccess(ArrayList<HomeBaseAlert> alerts)
    {
        layout.removeAllViews();

        if (choreContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no chores at this time");
            header.setText("Welcome");
            return;
        }

        ArrayList<BootstrapButton> toDelete = new ArrayList<BootstrapButton>();
        for (int j = 0; j < choreContainers.size(); j++) {
            if (!alerts.contains(choreDescriptions.get(j))) {
                toDelete.add(choreContainers.get(j));
            }
        }

        for(BootstrapButton deleted : toDelete)
        {
            choreTitles.remove(choreDescriptions.get(deleted).getTitle());
            choreContainers.remove(deleted);
            choreDescriptions.remove(deleted);

        }

        for (int i = 0; i < alerts.size(); i++) {
            if (!choreTitles.contains(alerts.get(i).getTitle())) {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

                BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
                BootstrapButton headerBar = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

                buttonCont.removeView(myButton);
                //layout.addView(myButton);

                String title = alerts.get(i).getTitle();
                String information = alerts.get(i).getDescription();
                String creator = alerts.get(i).getCreatorID();
                ArrayList<String> responsibleUsers = new ArrayList<String>(alerts.get(i).getResponsibleUsers());

                headerBar.setText(title);
                headerBar.setBootstrapType("chore");
                myButton.setText(information);

                choreContainers.add(myButton);
                choreTitles.add(title);
                choreDescriptions.put(myButton, alerts.get(i));
            }
        }

        for (int k = 0; k < choreContainers.size(); k++) {
            layout.addView(choreContainers.get(k));
        }
    }

    @Override
    public void onUpdateAlertListByTypeFailure(String e)
    {

    }
}
