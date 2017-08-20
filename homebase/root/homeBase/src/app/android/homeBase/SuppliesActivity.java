package app.android.homeBase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class SuppliesActivity extends HomeBaseActivity {
    private ArrayList<BootstrapButton> supplyContainers;
    private ArrayList<String> supplyIDs;
    private HashMap<BootstrapButton, HomeBaseAlert> supplyDescriptions;
    private HashMap<BootstrapButton, BootstrapButton> confirmButtons;
    private LinearLayout layout;
    private ApplicationManager mApplication;
    private BootstrapButton selectedFilter;
    boolean startCalled = false;
    private BootstrapButton clickedButton;
    private AlertDialog createDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (ApplicationManager)getApplicationContext();
        myIntent = getIntent();
        myClassName = "SuppliesActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_supplies);

        layout = (LinearLayout) findViewById(R.id.supply_supplyContainer_button);
        supplyContainers = new ArrayList<BootstrapButton>();
        supplyIDs = new ArrayList<String>();
        supplyDescriptions = new HashMap<BootstrapButton, HomeBaseAlert>();
        confirmButtons = new HashMap<BootstrapButton, BootstrapButton>();

        selectedFilter = (BootstrapButton) findViewById(R.id.supply_allFilter_button);
        selectedFilter.setEnabled(false);
        startCalled = true;

        mApplication.parse.getAlerts(this, "Supply");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (startCalled) {
            startCalled = false;
            return;
        }
        mApplication.parse.refreshAlerts(this, "Supply");
    }

    public void onAllFilterClick(View view)
    {
        selectedFilter.setEnabled(true);

        BootstrapButton clicked = (BootstrapButton) view;
        clicked.setBootstrapButtonEnabled(false);

        selectedFilter = clicked;

        layout.removeAllViews();

        if (supplyContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("No supplies are needed");
            header.setText("Welcome");
            header.setBootstrapType("supply");
            return;
        }

        for(int i = 0; i < supplyContainers.size(); i++) {
            BootstrapButton supplyContainer = supplyContainers.get(i);
            layout.addView(supplyContainer);
        }
    }

    public void onSupplyAddClick(View view)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final HomeBaseActivity wrappingInstance = this;

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        alertDialogBuilder.setView(inflater.inflate(R.layout.activity_supply_create, null, false));

        // set dialog message
        alertDialogBuilder
                .setCancelable(false);



        // create alert dialog
        createDialog = alertDialogBuilder.create();

        // show it
        createDialog.show();

        BootstrapButton cancel = (BootstrapButton)createDialog.findViewById(R.id.supplyInfo_cancel_button);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                createDialog.dismiss();
            }
        });

        BootstrapButton submit = (BootstrapButton)createDialog.findViewById(R.id.supplyInfo_confirm_button);

        final BootstrapEditText headerBar = (BootstrapEditText) createDialog.findViewById(R.id.supplyCreate_header_field);
        final BootstrapEditText infoContainer = (BootstrapEditText) createDialog.findViewById(R.id.supplyCreate_body_field);

        BootstrapButton creatorField = (BootstrapButton) createDialog.findViewById(R.id.supplyCreate_creator_field);
        creatorField.setText(mApplication.parse.getCurrentUser().getUsername());

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = headerBar.getText().toString();
                String type = "Supply";
                String desc = infoContainer.getText().toString();

                // Make sure everything is filled out
                List<String> fields = new LinkedList<String>(Arrays.asList(title, type, desc));
                for(String field : fields)
                {
                    if(field.isEmpty())
                    {
                        Toast.makeText(wrappingInstance, "Please fill out all fields", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                String creator = mApplication.parse.getCurrentUser().getUsername();
                List<String> placeHolder = new LinkedList<String>();
                mApplication.parse.createAlert(title, type, desc, placeHolder, creator, wrappingInstance);
            }
        });
    }

    public void onCreatedFilterClick(View view)
    {
        selectedFilter.setEnabled(true);

        BootstrapButton clicked = (BootstrapButton) view;
        clicked.setBootstrapButtonEnabled(false);

        selectedFilter = clicked;

        layout.removeAllViews();

        if (supplyContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("No supplies are needed");
            header.setText("Welcome");
            header.setBootstrapType("supply");
            return;
        }

        for(int i = 0; i < supplyContainers.size(); i++) {
            BootstrapButton supplyContainer = supplyContainers.get(i);
            if (supplyDescriptions.get(supplyContainer).getCreatorID().equals(mApplication.parse.getCurrentUser().getUsername())) {
                layout.addView(supplyContainer);
            }
        }
    }

    @Override
    public void onGetAlertListByTypeSuccess(ArrayList<HomeBaseAlert> alerts)
    {
        if (alerts.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("No supplies are needed");
            header.setText("Welcome");
            header.setBootstrapType("supply");
            return;
        }

        // Fetch all the bills from parse
        for (HomeBaseAlert alert : alerts)
        {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.supply_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.supplyContainer_container);
            BootstrapButton headerBar = (BootstrapButton) myButton.findViewById(R.id.supplyContainer_header);
            BootstrapButton confirmButton = (BootstrapButton) myButton.findViewById(R.id.supplyContainer_button);

            buttonCont.removeView(myButton);
            layout.addView(myButton);

            String title = alert.getTitle();
            String information = alert.getDescription();

            headerBar.setText(title);
            headerBar.setBootstrapType("supply");
            myButton.setText(information);

            supplyContainers.add(myButton);
            supplyDescriptions.put(myButton, alert);
            supplyIDs.add(alert.getId());
            confirmButtons.put(confirmButton, myButton);
        }
    }

    @Override
    public void onGetAlertListByTypeFailure(String e)
    {

    }

    @Override
    public void onUpdateAlertListByTypeSuccess(ArrayList<HomeBaseAlert> alerts)
    {
        if (supplyContainers.size() == 0){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton header = (BootstrapButton)myButton.findViewById(R.id.alertContainer_header);

            buttonCont.removeView(myButton);
            myButton.setClickable(false);
            layout.addView(myButton);

            myButton.setText("No supplies are needed");
            header.setText("Welcome");
            header.setBootstrapType("supply");
            return;
        }

        for (HomeBaseAlert alert: alerts) {
            if (!supplyIDs.contains(alert.getId())) {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.supply_container, null, false);

                BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.supplyContainer_container);
                BootstrapButton headerBar = (BootstrapButton) myButton.findViewById(R.id.supplyContainer_header);
                BootstrapButton confirmButton = (BootstrapButton) myButton.findViewById(R.id.supplyContainer_button);

                buttonCont.removeView(myButton);
                layout.addView(myButton);

                String title = alert.getTitle();
                String information = alert.getDescription();

                headerBar.setText(title);
                headerBar.setBootstrapType("supply");
                myButton.setText(information);

                supplyContainers.add(myButton);
                supplyDescriptions.put(myButton, alert);
                supplyIDs.add(alert.getId());
                confirmButtons.put(confirmButton, myButton);
            }
        }
    }

    public void onChoreContainerClick(View v)
    {
        clickedButton = (BootstrapButton) v.findViewById(R.id.supplyContainer_container);
        mApplication.parse.getUsername(supplyDescriptions.get(clickedButton).getCreatorID(), SuppliesActivity.this);
    }

    public void onSupplyObtained(View view)
    {
        BootstrapButton clickedButton = (BootstrapButton) view.findViewById(R.id.supplyContainer_button);
        BootstrapButton container = confirmButtons.get(clickedButton);
        mApplication.parse.deleteAlert(supplyDescriptions.get(container), this);

        supplyContainers.remove(container);
        supplyDescriptions.remove(container);
        layout.removeView(container);
        confirmButtons.remove(clickedButton);
    }

    @Override
    public void onGetUsernameSuccess(String username)
    {
        Intent intent = new Intent(SuppliesActivity.this, SupplyInfoActivity.class);
        intent.putExtra("caller", myClassName);
        intent.putExtra("title", supplyDescriptions.get(clickedButton).getTitle());
        intent.putExtra("info", supplyDescriptions.get(clickedButton).getDescription());
        intent.putExtra("creator", username);
        intent.putExtra("id", supplyDescriptions.get(clickedButton).getId());
        startActivity(intent);
    }

    @Override
    public void onGetUsernameFailure(String e)
    {
        Toast.makeText(SuppliesActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteAlertFailure(String error){
        Log.d("Delete Alert", "Failed: " + error);
    }

    @Override
    public void onDeleteAlertSuccess()
    {
        //finish();
    }

    @Override
    public void onCreateAlertSuccess(HomeBaseAlert alert)
    {
        // Close dialog
        createDialog.dismiss();
        mApplication.parse.refreshAlerts(this, "Supply");
    }

    @Override
    public void onCreateAlertFailure(String e)
    {
        Toast.makeText(SuppliesActivity.this, e, Toast.LENGTH_LONG).show();
    }
}
