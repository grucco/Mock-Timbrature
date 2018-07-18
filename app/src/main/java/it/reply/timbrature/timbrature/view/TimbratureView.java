package it.reply.timbrature.timbrature.view;

import android.graphics.drawable.Drawable;

import it.reply.timbrature.timbrature.ErrorSet.NetworkError;

/**
 * Created by g.rucco on 25/01/2018.
 */

public interface TimbratureView {

    void showCredits();
    void showProgress(boolean show);
    void showError(NetworkError e);
    void showDialog(String d, String t, Drawable w);
    void showSuccessDialog();
    void onNoNetwork();
    void displayNetworkError(NetworkError networkError);

}
