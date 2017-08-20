package app.android.homeBase;

import java.util.List;

/**
 * Created by kyle on 4/11/14.
 */
public class HomeBaseBill extends HomeBaseAlert
{
    private Double amount;
    // Constructor with Amount (Use this one most of the time)
    public HomeBaseBill(String title, String id, String type, Double amount, List<String> responsibleUsers, List<String> completedUsers, String description, String creatorID)
    {
        super(title, id, "bill", responsibleUsers, completedUsers, description, creatorID);
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
