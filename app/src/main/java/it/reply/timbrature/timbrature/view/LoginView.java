package it.reply.timbrature.timbrature.view;

import it.reply.timbrature.timbrature.ErrorSet.NetworkError;

/**
 * Created by g.rucco on 24/01/2018.
 */

public interface LoginView {

    void showDialog(String message);
    void showErrorDialog();
    void dismissKeyboard();
    void showProgress(final boolean show);
    void onNoNetwork();
    void displayNetworkError(NetworkError networkError);

}
