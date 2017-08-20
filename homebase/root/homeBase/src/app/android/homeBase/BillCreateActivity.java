package app.android.homeBase;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class BillCreateActivity extends HomeBaseActivity {

    private ApplicationManager mApplication;

    private BootstrapEditText headerBar;
    private BootstrapEditText infoContainer;
    private BootstrapEditText dollarAmountField;
    private BootstrapEditText centsAmountField;
    private BootstrapButton creatorField;

    private ArrayList<String> userNames;
    private HashMap<String, BootstrapButton> responsibleUsers;
    private HashMap<BootstrapButton, Boolean> selectedResponsibleUsers;

    private final String k_alertType = "Bill";

    private HomeBaseAlert createdAlert;
    private HashMap<String, ParseUser> usersObjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "BillCreateActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_bill_create);

        headerBar = (BootstrapEditText) this.findViewById(R.id.billCreate_header_field);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Resources resources = this.getResources();

        int navBarHeight = 0;

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        int statusBarHeight = 0;

        resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        int height = ((size.y - headerBar.getLayoutParams().height * 3) - navBarHeight - statusBarHeight);

        infoContainer = (BootstrapEditText) this.findViewById(R.id.billCreate_body_field);
        ViewGroup.LayoutParams rlp = infoContainer.getLayoutParams();
        rlp.height = height;
        infoContainer.setLayoutParams(rlp);

        //set up "responsible for" options
        userNames = mApplication.getRoommates();
        usersObjects = mApplication.usersObjects;
        responsibleUsers = new HashMap<String, BootstrapButton>();
        selectedResponsibleUsers = new HashMap<BootstrapButton, Boolean>();

        for (int i = 0; i < userNames.size(); i++) {
            Log.d("Users", userNames.get(i));
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout template = (LinearLayout) inflater.inflate(R.layout.user_select_template, null, false);
            RelativeLayout btnContainer = (RelativeLayout) template.findViewById(R.id.userSelection_template_container);
            template.removeView(btnContainer);
            BootstrapButton userBtn = (BootstrapButton)btnContainer.findViewById(R.id.userSelection_button);
            userBtn.setText(userNames.get(i));
            responsibleUsers.put(userNames.get(i), userBtn);
            selectedResponsibleUsers.put(userBtn, false);
            LinearLayout responsibleContainer = (LinearLayout) this.findViewById(R.id.billCreate_responsible_container);
            responsibleContainer.addView(btnContainer);
        }

        creatorField = (BootstrapButton) this.findViewById(R.id.billCreate_creator_field);
        creatorField.setText(mApplication.parse.getCurrentUser().getUsername());

        dollarAmountField = (BootstrapEditText) this.findViewById(R.id.billCreate_dollars_field);
        centsAmountField = (BootstrapEditText) this.findViewById(R.id.billCreate_cents_field);
    }

    public void onUserSelected(View view)
    {
        BootstrapButton button = (BootstrapButton)view;
        if (!selectedResponsibleUsers.get(button)) {
            button.setBootstrapType("info");
            selectedResponsibleUsers.put(button, true);
        } else {
            button.setBootstrapType("default");
            selectedResponsibleUsers.put(button, false);
        }
    }

    public void onBillCreateSubmitClick(View view)
    {
        String title = headerBar.getText().toString();
        String type = k_alertType;
        String desc = infoContainer.getText().toString();
        String dollarAmountStr = "0";
        String centsAmountStr = ".00";

        // Make sure everything is filled out
        List<String> fields = new LinkedList<String>(Arrays.asList(title, type, desc));
        for(String field : fields)
        {
            if(field.isEmpty())
            {
                Toast.makeText(BillCreateActivity.this, "Please fill out all fields", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (!dollarAmountField.getText().toString().equals("")) {
            dollarAmountStr = dollarAmountField.getText().toString();
        }
        if (!centsAmountField.getText().toString().equals("")) {
            centsAmountStr = centsAmountField.getText().toString();
        }
        String total = dollarAmountStr+"."+centsAmountStr;

        Double amount = Double.parseDouble(total);
        //round to 2 decimals
        amount = (double) Math.round(amount * 100) / 100;

        String creator = mApplication.parse.getCurrentUser().getUsername();
        List<String> responsibleUsers = new LinkedList<String>();

        for(String user : userNames) {
            BootstrapButton userButton = this.responsibleUsers.get(user);
            if (selectedResponsibleUsers.get(userButton)) {
                responsibleUsers.add(user);
            }
        }

        if (responsibleUsers.size() == 0){
            if(userNames.size() < 1){
                Toast.makeText(BillCreateActivity.this, "There are no users to assign the bill to", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(BillCreateActivity.this, "Please select an owner", Toast.LENGTH_LONG).show();
            }
        } else {
            mApplication.parse.createBill(title, type, desc, amount, responsibleUsers, creator, BillCreateActivity.this);
        }
    }

    public void onBillCreateCancelClick(View view)
    {
        onBackPressed();
    }

    @Override
    public void onCreateAlertSuccess(HomeBaseAlert alert)
    {
        createdAlert = alert;
        //int numUsers = createdAlert.getResponsibleUsers().size();
        int numUsers = mApplication.getHomeUsers().size();
        double splitAmount = (createdAlert.getAmount() / numUsers );
        if(splitAmount < 1.0)
        {
            splitAmount = 1.0;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String amountString = decimalFormat.format(splitAmount);

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");

        String[] emailsArr = new String[createdAlert.getResponsibleUsers().size()];
        int counter = 0;
        for(String user : createdAlert.getResponsibleUsers())
        {
           emailsArr[counter] = usersObjects.get(user).getEmail();
           counter++;
        }

        email.putExtra(Intent.EXTRA_EMAIL, emailsArr);
        email.putExtra(Intent.EXTRA_CC, new String[]{"request@square.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "$"+amountString);
        email.putExtra(Intent.EXTRA_TEXT, createdAlert.getTitle()+"\n"+createdAlert.getDescription());
        try {
            startActivity(Intent.createChooser(email, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BillCreateActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onCreateAlertFailure(String e)
    {
        Toast.makeText(BillCreateActivity.this, e, Toast.LENGTH_LONG).show();
    }
}
