package com.example.unserakus;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class LoadingAlert {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingAlert(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false); // Agar user tidak bisa cancel dengan klik luar

        dialog = builder.create();

        // Membuat background dialog transparan agar CardView terlihat membulat
        // Efek redup (dim) otomatis ditangani oleh sistem Android
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}