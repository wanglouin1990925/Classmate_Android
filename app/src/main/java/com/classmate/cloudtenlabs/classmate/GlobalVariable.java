package com.classmate.cloudtenlabs.classmate;

import java.util.ArrayList;

public class GlobalVariable {

    private static final GlobalVariable ourInstance = new GlobalVariable();

    public UserObject loggedInUser;

    static GlobalVariable getInstance() {
        return ourInstance;
    }

    private GlobalVariable() {

    }

}
