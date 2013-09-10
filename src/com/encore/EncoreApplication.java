package com.encore;

import android.app.Application;
import com.facebook.model.GraphUser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: babakpourkazemi
 * Date: 9/8/13
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class EncoreApplication extends Application {
    private List<GraphUser> selectedUsers;

    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> users) {
        this.selectedUsers = users;
    }
}
