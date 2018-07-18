package it.reply.timbrature.timbrature.view;

import it.reply.timbrature.timbrature.ErrorSet.NetworkError;

/**
 * Created by g.rucco on 24/01/2018.
 */

public interface MainView {

    void showProgress(final boolean show);
    void submit();
    void showMenu();
    void closeBottomSheet();
    void showCloseDialog();
    void hideCalendar();
    void showCalendar();
    void configureBottomSheet();
    void onNoNetwork();
    void displayNetworkError(NetworkError networkError);

}
