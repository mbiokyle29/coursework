package app.android.homeBase;

import java.util.List;
import android.util.Log;

/**
 * Created by kyle on 3/24/14.
 * Generic alert class for wrapping Chores, Messages, Groceries
 */
public class HomeBaseAlert {

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof HomeBaseAlert)
        {
            HomeBaseAlert comparedTo = (HomeBaseAlert) object;
            if(this.id.equals(comparedTo.id)) sameSame = true;
        }

        //Log.d("Comparing and returning: ", "" + sameSame);
        return sameSame;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    // These can only be set on creation
    private String title;
    private String id;
    private String type;
    private String description;
    private String creatorID;
    private List<String> responsibleUsers;
    private List<String> completedUsers;

    // Bill specific
    private Double amount;

    public HomeBaseAlert()
    {

    }

    public HomeBaseAlert(String title, String id, String type, List<String> responsibleUsers, List<String> completedUsers, String description, String creatorID)
    {
        this.title = title;
        this.id = id;
        this.type = type;
        this.description = description;
        this.creatorID = creatorID;
        this.responsibleUsers = responsibleUsers;
        this.completedUsers = completedUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<String> getResponsibleUsers() {
        return responsibleUsers;
    }

    public List<String> getCompletedUsers() {
        return completedUsers;
    }

    public void addResponsibleUser(String userID)
    {
        this.responsibleUsers.add(userID);
    }

    public boolean clearResponsibility(String userID)
    {
        if(this.responsibleUsers.contains(userID))
        {
            this.responsibleUsers.remove(userID);
            this.completedUsers.add(userID);
            return true;
        }
        else{
            return false;
        }
    }

    public String getDescription() {
        return description;
    }

    //Bill specific
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
