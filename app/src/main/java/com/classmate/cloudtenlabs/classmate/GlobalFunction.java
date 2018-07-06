package com.classmate.cloudtenlabs.classmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

public class GlobalFunction {

    private static final GlobalFunction ourInstance = new GlobalFunction();

    static GlobalFunction getInstance() {
        return ourInstance;
    }

    private ProgressDialog pd;

    private GlobalFunction() {

    }

    public void showAlertMessage(String title, String message, Context context) {
        AlertDialog.Builder alertDialg = new AlertDialog.Builder(context);
        alertDialg.setMessage(message);
        alertDialg.setTitle(title);
        alertDialg.setPositiveButton("Ok", null);
        alertDialg.setCancelable(true);
        alertDialg.create().show();
    }

    public void showProgressing(String title, String message, Context context) {
        pd = new ProgressDialog(context);
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.show();
    }

    public void hidProgressing() {
        pd.hide();
    }

}
