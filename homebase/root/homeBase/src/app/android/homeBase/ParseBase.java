package app.android.homeBase;

import android.content.Context;
import android.util.Log;

import com.parse.*;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A custom class/wrapper for interacting with Parse
 * note that Parse.init should be called in every activity (I think) so every activity should make
 * a parse base in its onCreate()
 */
public class ParseBase
{
    // Default constructor
    public  ParseBase() { }

    /********************************************************************************************************
     *   GENERAL AND/OR PRIVATE METHODS
     **********************************************************************************************************/
    private List<String> convertJSON(JSONArray array)
    {
        List<String> list = new LinkedList<String>();
        for(int i = 0; i < array.length(); i++)
        {
            try {
                Object seenValue = array.get(i);
                list.add(seenValue.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return list;
    }

    /********************************************************************************************************
     *   USER BASED METHODS AND WRAPPERS
     **********************************************************************************************************/
    public boolean userLoggedIn()
    {
        ParseUser curUser = ParseUser.getCurrentUser();
        return (curUser != null);
    }

    public ParseUser getCurrentUser(){
        return ParseUser.getCurrentUser();
    }

    public String getCurrentHouseID() {
        return this.getCurrentUser().get("house").toString();
    }

    public void addUser(final String username, final String password, final String email, final HomeBaseActivity caller)
    {
        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseInstallation.getCurrentInstallation().put("user", user.getObjectId());
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    caller.onSignupSuccess(user);
                } else {
                    caller.onSignupError(e);
                }
            }
        });
    }

    public void getUsername(String userID, final HomeBaseActivity caller)
    {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("objectId", userID);
        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e == null)
                {
                    caller.onGetUsernameSuccess(parseUser.getUsername());
                } else {
                    caller.onGetHouseFailure(e.getMessage());
                }
            }
        });

    }
    /**
     * This is the method for checking that a username is not already in use on parse
     * @param username
     * @param caller
     *
     */
    public void checkUserName(final String username, final HomeBaseActivity caller)
    {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", username);
        userQuery.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e)
            {
                if (e == null)
                {
                    // Make sure we didn't have errors and we got an empty list back
                    if (parseUsers.isEmpty())
                    {
                        caller.onCheckUserSuccess();
                    }
                    else
                    {
                        caller.onCheckUserFailure();
                    }

                }
                else
                {
                    caller.onCheckUserError(e);
                }
            }
        });
    }

    public void loginUser(final String username, final  String password, final Context context, final HomeBaseActivity caller)
    {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                // Make sure we get a valid user back!
                if(e != null || parseUser == null) {
                    if (e != null) {
                        caller.onLoginError(e.getMessage());
                    } else {
                        caller.onLoginError("No account found");
                    }
                }
                else
                {
                    caller.onLoginSuccess();
                }
            }
        });

    }
    public void getUsersOfHouse(final HomeBaseActivity caller)
    {
        //get user's house
        ParseQuery<ParseObject> houseQuery = ParseQuery.getQuery("House");
        houseQuery.whereEqualTo("members", getCurrentUser().getObjectId());
        houseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseHouse, ParseException e) {
                if (e == null) {
                    List<String> members = convertJSON(parseHouse.getJSONArray("members"));
                    Log.d("MEMBERS", String.valueOf(members.size()));
                    //with members, check if home

                    for (int i = 0; i < members.size(); i++) {
                        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                        userQuery.whereEqualTo("objectId", members.get(i).toString());
                        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    // this should return all [client side should handle exclusions]
                                    //if (!user.getObjectId().equals(getCurrentUser().getObjectId())){
                                        caller.onGetHomeUsersSuccess(user.getUsername(), user.getBoolean("isHome"));
                                    //}
                                } else {
                                    caller.onGetHomeUsersFailure();
                                }
                            }
                        });
                    }
                    caller.onReturnUsersSuccess();
                } else {
                    caller.onReturnUsersFailure();
                }
            }
        });

    }

    public void getUsersOfHouse(final ApplicationManager caller)
    {
        if (getCurrentUser() == null) {
            caller.onGetHomeUsersFailure();
        }

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("house", getCurrentHouseID());
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null)
                {
                    caller.onGetHomeUsersSuccess(parseUsers);
                }
            }
        });
    }

    /******************************************************************************************************
     *
     * GPS Events and Wrappers for Parse
     ********************************************************************************************************/

    public void updateLocation(boolean home) {
        ParseUser jesus = getCurrentUser();
        jesus.put("isHome", home);
        jesus.saveEventually();

    }

    public boolean getUserLocation() {
        ParseUser jesus = this.getCurrentUser();
        Boolean isHome = false;
        try {
            isHome = jesus.getBoolean("isHome");
        } catch (Exception E){
            Log.d("Getting user Local","couldn't get isHome");
            jesus.put("isHome", false);
            return false;
        }
        return isHome;
    }

    /**************************************************************************************************************
     *  House Event and Wrappers for PARSE
     *************************************************************************************************************/

    public void createHouse(String housename, String userid, double lat, double lng, final HomeBaseActivity caller)
    {
        // Add the creator to the user list, also get added as admin in constructor
        List<String> memberList = new LinkedList<String>();
        memberList.add(userid);

        final HomeBaseHouse newHouse = new HomeBaseHouse(housename, userid, memberList, lat, lng);
        final ParseObject house = new ParseObject("House");

        house.put("admin", newHouse.getAdmin());
        house.put("members", newHouse.getMembers());
        house.put("housename", newHouse.getHousename());
        house.put("latitude", newHouse.getLatitude());
        house.put("longitude", newHouse.getLongitude());

        house.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    newHouse.setId(house.getObjectId());
                    caller.onCreateHouseSuccess(newHouse);
                } else {
                    caller.onCreateHouseFailure("Could not create house, please try again.");
                }
            }
        });
    }

    // Get via admin username
    public void getHouse(final String adminUsername, final HomeBaseActivity caller)
    {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", adminUsername);
        userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> houseQuery = ParseQuery.getQuery("House");
                    houseQuery.whereEqualTo("admin", parseUser.getObjectId());
                    houseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseHouse, ParseException e) {
                            if (e == null) {
                                String housename = parseHouse.getString("housename");
                                double lat = parseHouse.getDouble("latitude");
                                double longitude = parseHouse.getDouble("longitude");
                                String admin = parseHouse.getString("admin");
                                List<String> members = convertJSON(parseHouse.getJSONArray("members"));
                                String id = parseHouse.getObjectId();

                                // Create House instance
                                HomeBaseHouse house = new HomeBaseHouse(housename, admin, members, lat, longitude);
                                //house.setLatitude(lat);
                                //house.setLongitude(longitude);
                                house.setId(id);
                                caller.onGetHouseSuccess(house);
                            } else {
                                caller.onGetHouseFailure("Could not fetch house information, please try again");
                            }
                        }
                    });
                } else {
                    caller.onGetHouseFailure("Could not find a house associated with admin: " + adminUsername);
                }
            }
        });
    }


    public void getHouse(final GPSservice caller)
    {
        //get the current user, then the user part of that house

        ParseQuery<ParseObject> houseQuery = ParseQuery.getQuery("House");
        houseQuery.whereEqualTo("members", getCurrentUser().getObjectId());
        houseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseHouse, ParseException e) {
                if (e == null) {
                    String housename = parseHouse.getString("housename");
                    Log.d("HOUSE", "house found " + housename);
                    double lat = parseHouse.getDouble("latitude");
                    double longitude = parseHouse.getDouble("longitude");
                    String admin = parseHouse.getString("admin");
                    List<String> members = convertJSON(parseHouse.getJSONArray("members"));
                    String id = parseHouse.getObjectId();

                    // Create House instance
                    HomeBaseHouse house = new HomeBaseHouse(housename, admin, members, lat, longitude);
                    house.setId(id);
                    caller.onGetHouseSuccess(house);
                } else {
                    caller.onGetHouseFailure("Could not fetch house information, please try again");
                }
            }
        });

    }

    // Get via application model
    public void getHouse(HomeBaseHouse house, final HomeBaseActivity caller)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
        query.whereEqualTo("objectId", house.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseHouse, ParseException e) {
                if (e == null) {
                    String housename = parseHouse.getString("housename");
                    double lat = parseHouse.getDouble("latitude");
                    double longitude = parseHouse.getDouble("longitude");
                    String admin = parseHouse.getString("admin");
                    List<String> members = convertJSON(parseHouse.getJSONArray("members"));
                    String id = parseHouse.getObjectId();

                    // Create House instance
                    HomeBaseHouse house = new HomeBaseHouse(housename, admin, members, lat, longitude);
                    //house.setLatitude(lat);
                    //house.setLongitude(longitude);
                    house.setId(id);

                    caller.onGetHouseSuccess(house);
                } else {
                    caller.onGetHouseFailure("Could not fetch house information, please try again");
                }
            }
        });
    }

    // General Update
    public void updateHouse(final HomeBaseHouse house, final HomeBaseActivity caller)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
        query.whereEqualTo("objectId", house.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject parseHouse, ParseException outerException)
            {
                if(outerException == null)
                {
                    // Update al fields, for simplicity
                    parseHouse.put("housename", house.getHousename());
                    parseHouse.put("latitude", house.getLatitude());
                    parseHouse.put("longitude", house.getLongitude());
                    parseHouse.put("admin", house.getAdmin());
                    parseHouse.put("members", house.getMembers());

                    // Save and callback
                    parseHouse.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException innerException)
                        {
                            if(innerException == null)
                            {
                                caller.onUpdateHouseSuccess(house);
                            }
                            else
                            {
                                caller.onUpdateHouseFailure("Could not update house information");
                            }
                        }
                    });
                }
                else
                {
                    caller.onUpdateHouseFailure("Could not locate the requested house: "+house.getId());
                }
            }
        });
    }

    /***********************************************************************************************
     *  ALERT METHODS
     **********************************************************************************************/
    private HomeBaseAlert buildAlert(ParseObject alert, String passedType) {
        String type = alert.getString("type");
        String objectID = alert.getObjectId();
        String title = alert.getString("title");
        String description = alert.getString("description");
        String creator = alert.getString("creator");
        JSONArray responsibleArray = new JSONArray();
        JSONArray completedArray = new JSONArray();

        if(!type.equals("Supply")) {
            responsibleArray = alert.getJSONArray("responsibleUsers");
            completedArray = alert.getJSONArray("completedUsers");
        }

        List<String> responsibleUsers = convertJSON(responsibleArray);
        List<String> completedUsers = convertJSON(completedArray);

        if (type.equals("Default") || type.equals("Chore") || type.equals("Supply")) {
            return new HomeBaseAlert(title, objectID, type, responsibleUsers, completedUsers, description, creator);
        } else if (type.equals("Bill")) {
            return buildBill(alert, new HomeBaseAlert(title, objectID, type, responsibleUsers, completedUsers, description, creator));
        }

        return new HomeBaseAlert(title, objectID, type, responsibleUsers, completedUsers, description, creator);
    }

    private HomeBaseAlert buildBill(ParseObject alert, HomeBaseAlert alertBase) {
        Double amount = alert.getDouble("amount");
        alertBase.setAmount(amount);
        return alertBase;
    }

    public void createAlert(String title, final String type, String description, List<String> responsibleUsers, String creatorID, final HomeBaseActivity caller)
    {
        JSONArray responsibleArray = new JSONArray(responsibleUsers);
        JSONArray completedArray = new JSONArray();
        final ParseObject alert = new ParseObject("Alert");
        alert.put("title", title);
        alert.put("type", type);
        alert.put("description", description);
        alert.put("creator", creatorID);
        alert.put("responsibleUsers", responsibleArray);
        alert.put("completedUsers", completedArray);
        alert.put("house", this.getCurrentHouseID());
        alert.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    HomeBaseAlert hbAlert = buildAlert(alert, type);
                    caller.onCreateAlertSuccess(hbAlert);
                } else {
                    caller.onCreateAlertFailure(e.getMessage());
                }
            }
        });
    }

    public void createBill(String title, final String type, String description, Double amount, List<String> responsibleUsers, String creatorID, final HomeBaseActivity caller)
    {
        JSONArray responsibleArray = new JSONArray(responsibleUsers);
        JSONArray completedArray = new JSONArray();
        final ParseObject alert = new ParseObject("Alert");
        alert.put("title", title);
        alert.put("type", type);
        alert.put("description", description);
        alert.put("creator", creatorID);
        alert.put("responsibleUsers", responsibleArray);
        alert.put("completedUsers", completedArray);
        alert.put("amount", amount);
        alert.put("house", this.getCurrentHouseID());
        alert.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    HomeBaseAlert hbAlert = buildAlert(alert, type);
                    caller.onCreateAlertSuccess(hbAlert);
                } else {
                    caller.onCreateAlertFailure(e.getMessage());
                }
            }
        });
    }

    public void createSupply(String title, final String type, String description, String creator, final HomeBaseActivity caller)
    {
        final ParseObject alert = new ParseObject("Alert");
        alert.put("title", title);
        alert.put("type", type);
        alert.put("description", description);
        alert.put("creator", creator);
        alert.put("house", this.getCurrentHouseID());
        alert.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    HomeBaseAlert hbAlert = buildAlert(alert, type);
                    caller.onCreateAlertSuccess(hbAlert);
                } else {
                    caller.onCreateAlertFailure(e.getMessage());
                }
            }
        });
    }
    public void getAlert(String objectID, final HomeBaseActivity caller)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.whereEqualTo("objectId", objectID);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null)
                {
                    HomeBaseAlert hbAlert = buildAlert(parseObject, "Default");
                    caller.onGetAlertSuccess(hbAlert);
                }
                else
                {
                    caller.onGetAlertFailure(e.getMessage());
                }
            }
        });
    }


    public void getAlerts(final HomeBaseActivity caller)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<HomeBaseAlert> alertList = new ArrayList<HomeBaseAlert>();
                    for (int i = 0; i < objects.size(); i++) {
                        HomeBaseAlert alert = buildAlert(objects.get(i), objects.get(i).get("type").toString());
                        alertList.add(alert);
                    }
                    caller.onGetAlertListSuccess(alertList);
                } else {
                    caller.onGetAlertListFailure(e.getMessage());
                }
            }
        });
    }

    public void getAlerts(final HomeBaseActivity caller, final String type)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.whereEqualTo("type", type);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<HomeBaseAlert> alertList = new ArrayList<HomeBaseAlert>();
                    for (int i = 0; i < objects.size(); i++) {
                        HomeBaseAlert alert = buildAlert(objects.get(i), type);
                        alertList.add(alert);
                    }
                    caller.onGetAlertListByTypeSuccess(alertList);
                } else {
                    caller.onGetAlertListByTypeFailure(e.getMessage());
                }
            }
        });
    }

    public void refreshAlerts(final HomeBaseActivity caller)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<HomeBaseAlert> alertList = new ArrayList<HomeBaseAlert>();
                    for (int i = 0; i < objects.size(); i++) {
                        HomeBaseAlert alert = buildAlert(objects.get(i), objects.get(i).get("type").toString());
                        alertList.add(alert);
                    }
                    caller.onUpdateAlertListSuccess(alertList);
                } else {
                    caller.onUpdateAlertListFailure(e.getMessage());
                }
            }
        });
    }

    public void refreshAlerts(final HomeBaseActivity caller, final String type)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.whereEqualTo("type", type);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<HomeBaseAlert> alertList = new ArrayList<HomeBaseAlert>();
                    for (int i = 0; i < objects.size(); i++) {
                        HomeBaseAlert alert = buildAlert(objects.get(i), type);
                        alertList.add(alert);
                    }
                    caller.onUpdateAlertListByTypeSuccess(alertList);
                } else {
                    caller.onUpdateAlertListByTypeFailure(e.getMessage());
                }
            }
        });
    }

    public void deleteAlert(final HomeBaseAlert alert, final HomeBaseActivity caller)
    {
        ParseObject deleteAlert = ParseObject.createWithoutData("Alert", alert.getId());
        deleteAlert.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    caller.onDeleteAlertSuccess();
                } else {
                    caller.onDeleteAlertFailure(e.getMessage());
                }
            }
        });
    }
    
    //eventually, need to update alert by objectID - but this requires storing object ID across activities
    public void updateAlertResponsibleUsers(final String creatorID, final String title, final List<String> responsibleUsers, final List<String> completedUsers, final String type, final HomeBaseActivity caller) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.whereEqualTo("creator", creatorID);
        query.whereEqualTo("title", title);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    final ParseObject toUpdate = objects.get(0);
                    toUpdate.put("responsibleUsers", responsibleUsers);
                    toUpdate.put("completedUsers", completedUsers);
                    toUpdate.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //call completion
                                caller.onUpdateAlertSuccess(buildAlert(toUpdate, type));
                            } else {
                                //call error
                            }
                        }
                    });
                } else {
                    //call error
                }
            }
        });
    }

    public void getAlertResponsibleUsers(final String creatorID, final String title, final HomeBaseActivity caller) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.whereEqualTo("creator", creatorID);
        query.whereEqualTo("title", title);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    JSONArray responsibleArray = objects.get(0).getJSONArray("responsibleUsers");
                    List<String> responsibleUsers = convertJSON(responsibleArray);
                    caller.onGetAlertResponsibleUsersSuccess(responsibleUsers);
                } else {
                    caller.onGetAlertResponsibleUsersFailure(e.toString());
                }
            }
        });
    }
    
    public void getAlertCompletedUsers(final String creatorID, final String title, final HomeBaseActivity caller) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("house", this.getCurrentHouseID());
        query.whereEqualTo("creator", creatorID);
        query.whereEqualTo("title", title);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    JSONArray completedArray = objects.get(0).getJSONArray("completedUsers");
                    List<String> completedUsers = convertJSON(completedArray);
                    caller.onGetAlertCompletedUsersSuccess(completedUsers);
                } else {
                    caller.onGetAlertCompletedUsersFailure(e.toString());
                }
            }
        });
    }
}
