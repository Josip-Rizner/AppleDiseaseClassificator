package com.example.applediseaseclassificator;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private TextView tvMessage;
    private String message = "Please wait...";

    LoadingDialog(Activity myActivity, String message){
        activity = myActivity;
        this.message = message;
    }

    void showLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);
        builder.setView(view);
        builder.setCancelable(false);

        tvMessage = view.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        dialog = builder.create();
        dialog.show();
    }

    void dismissLoadingDialog(){
        dialog.dismiss();
    }
}
