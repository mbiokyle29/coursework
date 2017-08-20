package app.android.homeBase;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.app.ProgressDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;

public class BillsActivity extends HomeBaseActivity {
    private ArrayList<BootstrapButton> billContainers;
    private ArrayList<String> billIDs;
    private HashMap<BootstrapButton, HomeBaseAlert> billDescriptions;
    private LinearLayout layout;
    private boolean startCalled = false;
    private ApplicationManager mApplication;

    private BootstrapButton selectedFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Init parse
        super.onCreate(savedInstanceState);
        mApplication = (ApplicationManager)getApplicationContext();
        myIntent = getIntent();
        myClassName = "BillsActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_bills);

        layout = (LinearLayout) findViewById(R.id.bills_billContainer_button);
        billContainers = new ArrayList<BootstrapButton>();
        billIDs = new ArrayList<String>();
        billDescriptions = new HashMap<BootstrapButton, HomeBaseAlert>();

        startCalled = true;
        selectedFilter = (BootstrapButton) findViewById(R.id.bills_allFilter_button);
        selectedFilter.setEnabled(false);

        mApplication.parse.getAlerts(this, "Bill");

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

        mApplication.parse.refreshAlerts(this, "Bill");
    }

    public void onChoreContainerClick(View view)
    {
        BootstrapButton thisButton = (BootstrapButton) view.findViewById(R.id.alertContainer_container);
        Intent intent = new Intent(BillsActivity.this, BillInfoActivity.class);
        intent.putExtra("caller", myClassName);
        intent.putExtra("amount", billDescriptions.get(thisButton).getAmount().toString());
        intent.putExtra("creator", billDescriptions.get(thisButton).getCreatorID());
        intent.putExtra("title", billDescriptions.get(thisButton).getTitle());
        intent.putExtra("info", billDescriptions.get(thisButton).getDescription());
        intent.putExtra("alertID", billDescriptions.get(thisButton).getId());
        startActivity(intent);
    }

    public void onBillAddClick(View view)
    {
        Intent intent = new Intent(BillsActivity.this, BillCreateActivity.class);
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

        if (billContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no bills at this time");
            header.setText("Welcome");
            header.setBootstrapType("bill");
            return;
        }

        for(int i = 0; i < billContainers.size(); i++) {
            BootstrapButton billContainer = billContainers.get(i);
            layout.addView(billContainer);
        }
    }

    public void onCreatedFilterClick(View view)
    {
        selectedFilter.setEnabled(true);

        BootstrapButton clicked = (BootstrapButton) view;
        clicked.setBootstrapButtonEnabled(false);

        selectedFilter = clicked;

        layout.removeAllViews();

        if (billContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no bills at this time");
            header.setText("Welcome");
            header.setBootstrapType("bill");
            return;
        }

        for(int i = 0; i < billContainers.size(); i++) {
            BootstrapButton billContainer = billContainers.get(i);
            if (billDescriptions.get(billContainer).getCreatorID().equals(mApplication.parse.getCurrentUser().getUsername())) {
                layout.addView(billContainer);
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

        if (billContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no bills at this time");
            header.setText("Welcome");
            header.setBootstrapType("bill");
            return;
        }

        for(int i = 0; i < billContainers.size(); i++) {
            BootstrapButton billContainer = billContainers.get(i);
            for (int j = 0; j < billDescriptions.get(billContainer).getResponsibleUsers().size(); j++) {
                if (billDescriptions.get(billContainer).getResponsibleUsers().get(j).equals(mApplication.parse.getCurrentUser().getUsername())) {
                    layout.addView(billContainer);
                }
            }
        }
    }

    @Override
    public void onGetAlertListByTypeSuccess(ArrayList<HomeBaseAlert> alerts)
    {
        layout.removeView(loadingProgress);
        //if no alerts
        if (alerts.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no chores at this time");
            header.setText("Welcome");
            header.setBootstrapType("bill");
            return;
        } else {
            // Fetch all the bills from parse
            for (HomeBaseAlert alert : alerts)
            {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

                BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
                BootstrapButton headerBar = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

                buttonCont.removeView(myButton);
                layout.addView(myButton);

                String title = alert.getTitle();
                String information = alert.getDescription() + " [$" + alert.getAmount() + "]";
                String creator = alert.getCreatorID();
                Log.d("Creator: ", creator);

                headerBar.setText(title);
                headerBar.setBootstrapType("bill");
                myButton.setText(information);

                billContainers.add(myButton);
                billDescriptions.put(myButton, alert);
                billIDs.add(alert.getId());
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

        if (billContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("You have no bills at this time");
            header.setText("Welcome");
            header.setBootstrapType("bill");
            return;
        }

        List<BootstrapButton> toDelete = new LinkedList<BootstrapButton>();
        for(BootstrapButton existing : billDescriptions.keySet())
        {
            HomeBaseAlert existingAlert = billDescriptions.get(existing);
            if(!alerts.contains(existingAlert))
            {
                toDelete.add(existing);
            }
        }

        for(BootstrapButton deleted : toDelete)
        {
            billIDs.remove(billDescriptions.get(deleted).getId());
            billContainers.remove(deleted);
            billDescriptions.remove(deleted);

        }

        int pointer = 0;
        for (HomeBaseAlert alert : alerts)
        {
            if (!billIDs.contains(alert.getId()))
            {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

                BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
                BootstrapButton headerBar = (BootstrapButton) myButton.findViewById(R.id.alertContainer_header);

                buttonCont.removeView(myButton);

                String title = alert.getTitle();
                String information = alert.getDescription() + " [$" + alert.getAmount() + "]";
                String creator = alert.getCreatorID();

                headerBar.setText(title);
                headerBar.setBootstrapType("bill");
                myButton.setText(information);

                billContainers.add(pointer, myButton);
                pointer++;
                billDescriptions.put(myButton, alert);
                billIDs.add(alert.getId());
            }
        }

        for(int i = 0; i < billContainers.size(); i++)
        {
            BootstrapButton billContainer = billContainers.get(i);
            layout.addView(billContainer);
        }
    }

    @Override
    public void onUpdateAlertListByTypeFailure(String e)
    {

    }
}
