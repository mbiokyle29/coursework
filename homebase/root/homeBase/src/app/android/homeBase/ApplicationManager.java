package app.android.homeBase;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

public class ApplicationManager extends Application {
    public ArrayList<String> users;
    public ArrayList<String> roommates;
    public ParseBase parse;
    public HashMap<String, ParseUser> usersObjects = new HashMap<String, ParseUser>();
    public GPSservice gps;
    public LinkedList<Intent> forwardIntentQueue;
    public boolean traversingForwardIntentQueue = false;
    private ParseInstallation installation;
    private static ApplicationManager instance;
    private HomeBaseHouse house;

    public static ApplicationManager getInstance()
    {
        return instance;
    }

    @Override
    public Context getApplicationContext()
    {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Parse.initialize(instance, "dD0N7G0DiCBySn8gXbYtcOxfvM8OGKUZOBRPy8wl", "tt6FH3ugfJOhYY41bCiPb7URHrnzQtV8drwEKQDJ");
        instance.initialize();
        instance.installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    PushService.setDefaultPushCallback(instance, LoginActivity.class);
                }
            }
        });
    }

    private void initialize()
    {
        instance.users = new ArrayList<String>();
        instance.roommates = new ArrayList<String>();
        instance.parse = new ParseBase();
        instance.gps = new GPSservice(instance);
        instance.forwardIntentQueue = new LinkedList<Intent>();
        instance.installation = ParseInstallation.getCurrentInstallation();
    }

    public class SavedIntent {
        Intent intent;
    }

    public void setHouse(HomeBaseHouse house){
        this.house = house;
    }
    public HomeBaseHouse getHouse(){
        return this.house;
    }
    public boolean hasHouse(){
        return (house != null);
    }

    public void debugHomeUsers()
    {
        for(String user : users) {
            Log.d("User: ", user);
        }
    }

    public ArrayList<String> getHomeUsers()
    {
        return users;
    }
    public ArrayList<String> getRoommates() { return this.roommates; }

    public void onGetHomeUsersSuccess(List<ParseUser> parseUsers)
    {
        for(ParseUser user : parseUsers)
        {
            if(!instance.users.contains(user.getUsername())) {
                instance.users.add(user.getUsername());
                instance.usersObjects.put(user.getUsername(), user);
            }

            //list of only roommates
            if(!instance.roommates.contains(user.getUsername())
                    && !user.getUsername().equals(instance.parse.getCurrentUser().getUsername())) {
                    instance.roommates.add(user.getUsername());
            }

        }
    }

    public void onGetHomeUsersFailure()
    {

    }

    public void subscribeToHouseChannel(String houseID)
    {
        houseID = "house_" + houseID;
        PushService.subscribe(instance, houseID, NewsFeedActivity.class);
    }

    public void upsertHouseData()
    {
        instance.parse.getUsersOfHouse(this);
    }

    public boolean hasHouseData()
    {
        return (instance.users.size() > 0);
    }

    public void logout()
    {
        instance.roommates.clear();
        instance.users.clear();
        instance.forwardIntentQueue.clear();
        instance.usersObjects.clear();
        house = null;
    }

}