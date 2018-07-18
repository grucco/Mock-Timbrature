package it.reply.timbrature.timbrature;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.reply.timbrature.timbrature.DataSet.Timbratura;
import it.reply.timbrature.timbrature.ErrorSet.ErrorParser;
import it.reply.timbrature.timbrature.ErrorSet.MainError;
import it.reply.timbrature.timbrature.ErrorSet.NetworkError;
import it.reply.timbrature.timbrature.ErrorSet.NetworkErrorType;
import it.reply.timbrature.timbrature.Network.NetworkManager;
import it.reply.timbrature.timbrature.Network.TimbratureNetworkModel;
import it.reply.timbrature.timbrature.SupportClasses.Timbrature;
import it.reply.timbrature.timbrature.SupportClasses.TimbratureModule;
import it.reply.timbrature.timbrature.Utils.Config;
import it.reply.timbrature.timbrature.Utils.Constants;
import it.reply.timbrature.timbrature.Utils.Utils;
import it.reply.timbrature.timbrature.common.App;
import it.reply.timbrature.timbrature.model.NetworkCallBack;
import it.reply.timbrature.timbrature.view.TimbratureView;
import okhttp3.ResponseBody;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;
import stacksmashers.smp30connectionlib.smp.Header;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import javax.inject.Inject;

/**
//  TimbraturaActivity
//  Timbrature
//
//  Created by Raffaele Forgione @ Syskoplan Reply.
//  Copyright © 2017 Acea. All rights reserved.
*/

public class TimbraturaActivity extends AppCompatActivity implements TimbratureView {

    private static final String TAG = "TimbraturaActivity";
    // gps
    private String latitude;
    private String longitude;
    private String indirizzo;
    private String data;
    private String ora;

    // dialog
    private int whiteColor;
    private int grayColor;
    private int lightGrayColor;
    private Drawable warnIcon;
    private Drawable eeIcon;

    // altro
    private int count = 1;
    private long startMillis=0;

    private SharedPreferences settings;


    @Inject
    TimbratureNetworkModel timbratureNetworkModel;


    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.DateView)
    TextView dateView;
    @BindView(R2.id.HourChoices)
    TextView hourView;
    @BindView(R2.id.TimeChoices)
    Spinner entryOrExit;
    @BindView(R2.id.hud)
    ProgressBar hud;
    @BindView(R2.id.SendAndExit)
    Button send;
    @BindView(R2.id.AddressView)
    TextView addressText;
    @BindView(R2.id.ExitWithoutSending)
    ImageButton exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timbratura);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //necessario per inizializzare correttamente i vari oggetti che lavorano
        //per effettuare le richieste e ricevere le risposte
        ((App) getApplication()).getAppComponent()
                .plus(new TimbratureModule(this))
                .inject(this);

        // dati utente
        getDataFromIntent();
        addDataToUI();

        addressText.setText(indirizzo);

        // dialog
        lightGrayColor = ContextCompat.getColor(getApplicationContext(), R.color.superLightGrayColor);
        whiteColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        warnIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_warn);
        eeIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.easter_egg);
        grayColor = ContextCompat.getColor(getApplicationContext(), R.color.darkGrayColor);

    }

    @OnClick(R2.id.ExitWithoutSending)
    void exit() {
        finish();
    }

    @OnClick(R2.id.ClockButton)
    void setTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(TimbraturaActivity.this, R.style.Dialog_Theme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                formatAndShowTime(selectedHour, selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Scegli l'orario");
        mTimePicker.show();
    }

    @OnClick(R2.id.SendAndExit)
    void sendAndExit() {
        showSendDialog(entryOrExit.getSelectedItem().toString());
    }

    // Easter egg
    @Override
    @OnClick(R2.id.AddressView)
    public void showCredits() {

        //get system current milliseconds
        long time = System.currentTimeMillis();

        //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
        if (startMillis == 0 || (time - startMillis > 3000)) {
            startMillis = time;
            count = 1;
        }
        //it is not the first, and it has been  less than 3 seconds since the first
        else { //  time-startMillis< 3000
            count++;
        }

        if (count == 7) {
            Utils.showStatusMessage("Timbrature", "Applicazione sviluppata da Raffaele Forgione @Syskoplan Reply", grayColor, whiteColor, eeIcon, lightGrayColor, getApplicationContext());
            count = 1;
        }
    }

    private Timbratura createTimbratura() {

        Timbratura t = new Timbratura();

        // Entrata/Uscita
        String tipoText = entryOrExit.getSelectedItem().toString();
        // Codice Entrata/Uscita: P10/P20
        String satza = (tipoText.equals("Entrata")) ? "P10" : "P20";

        t.setTipotext(tipoText);
        t.setSatza(satza);
        t.setLatitudine(latitude);
        t.setLongitudine(longitude);
        t.setVia(indirizzo);

        return t;

    }

    private JsonObject timbraturaJSON() {

        JsonObject timbratura = new JsonObject();

        /* deprecato
        // timestamp data - SAP Format
        String day = String.valueOf(giorno);
        if(day.length() == 1){
            day = "0" + day;
        }

        String month = String.valueOf(mese + 1);
        if(month.length() == 1){
            month = "0" + month;
        }
        String ldate = Utils.backToLDate(day + month + String.valueOf(anno));
        // orario - SAP Format
        String ltime = Utils.backToLTime(hourView.getText().toString());
        */
        // Entrata/Uscita
        String tipoText = entryOrExit.getSelectedItem().toString();
        // Codice Entrata/Uscita: P10/P20
        String satza = (tipoText.equals("Entrata")) ? "P10" : "P20";

        settings = getApplicationContext().getSharedPreferences("settings", 0);


        //NB: L'ORDINE E' FONDAMENTALE PER LA RICOSTRUZIONE DELL'OGGETTO TIMBRATURA

        timbratura.addProperty("Satza", satza);
        timbratura.addProperty("Latitudine", latitude);
        timbratura.addProperty("Longitudine", longitude);
        timbratura.addProperty("Via", indirizzo);
        timbratura.addProperty("Nome", settings.getString("nome",""));
        timbratura.addProperty("Cognome", settings.getString("cognome",""));
        timbratura.addProperty("Uname", settings.getString("uname",""));
        timbratura.addProperty("Tipotext", tipoText);
        timbratura.addProperty("Societa", settings.getString("societa",""));
        timbratura.addProperty("Zausw", settings.getString("zausw",""));
        timbratura.addProperty("Pdsnr", settings.getString("pdsnr",""));
        timbratura.addProperty("Link", "");
        timbratura.addProperty("PdcUsrup", "776");
        timbratura.addProperty("Ldate", data);
        timbratura.addProperty("Ltime", ora);





        /*
        timbratura.addProperty("Link","");
        timbratura.addProperty("Societa", "");
        timbratura.addProperty("Pdsnr", "");
        timbratura.addProperty("Uname", "");
        timbratura.addProperty("Zausw", "");
        timbratura.addProperty("Nome", "");
        timbratura.addProperty("Cognome", "");
        timbratura.addProperty("Tipotext", "");
        timbratura.addProperty("PdcUsrup", "");
        timbratura.addProperty("Ldate", ldate);
        timbratura.addProperty("Ltime", ltime);
        */

        return timbratura;
    }

    private void formatAndShowTime(int selectedHour, int selectedMinute) {
        String chosenHour = String.valueOf(selectedHour);
        String chosenMinute = String.valueOf(selectedMinute);
        chosenHour = (chosenHour.length() == 1) ? "0" + chosenHour : chosenHour;
        chosenMinute = (chosenMinute.length() == 1) ? "0" + chosenMinute : chosenMinute;
        hourView.setText(chosenHour + ":" + chosenMinute);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            send.setVisibility(show ? View.GONE : View.VISIBLE);
            send.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    send.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            hud.setVisibility(show ? View.VISIBLE : View.GONE);
            hud.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    hud.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            hud.setVisibility(show ? View.VISIBLE : View.GONE);
            send.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        // abilito o disabilito il pulsante di chiusura
        exit.setEnabled(!show);
    }

    @Override
    public void showError(NetworkError result) {
        /*
        String errorString = "";
        if (result != null && result.getResult() != null && !(result.getResult()).has("ERROR")) {
            Gson gson = new Gson();
            MainError err = gson.fromJson(result.getResult().toString(), MainError.class);
            if (err.error != null) {
                errorString = err.error.getMessage().getValue();
                if (err.error.getCode().equals("DB/111")) {
                    logout();
                }
            }
        }
        */
        if(result==NetworkErrorType.NO_INTERNET)
            showDialog("Attenzione", "Controlla la tua connessione Internet", warnIcon);
        else
            showDialog("Attenzione", result.getLocalizedErrorMessageOrDefault(this), warnIcon);
        /*
        if(errorString.length() == 0) {
            Utils.showStatusMessage("Attenzione", "Si è verificato un errore in invio, attendi qualche istante e riprova", grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
        }
        else{
            Utils.showStatusMessage("Attenzione", errorString, grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
        }
        */
    }

    @Override
    public void showDialog(String title, String subtitle, Drawable icon) {
        showProgress(false);
        Utils.showStatusMessage(title, subtitle, grayColor, whiteColor, icon, lightGrayColor, getApplicationContext());
    }

    private void logout() {
        Context ctx = getApplicationContext();
        SharedPreferences settings = ctx.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(TimbraturaActivity.this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }

    private void getDataFromIntent() {

        Intent tItent = getIntent();

        // latitudine
        latitude = tItent.getStringExtra("latitudine");

        // longitudine
        longitude = tItent.getStringExtra("longitudine");

        // indirizzo
        indirizzo = tItent.getStringExtra("via");

        //@g.rucco: aggiunto io
        data = tItent.getStringExtra("ldate");
        ora = tItent.getStringExtra("ltime");

    }

    //DA MOCKARE
    public void getXCSRFTokenAndSend() {
        showProgress(true);
        Context ctx = getApplicationContext();

        /*try {
            Ion ion = Ion.getDefault(ctx);
            ion.getCookieMiddleware().clear();
            ion.with(this)
                    .load(NetworkManager.xCSRFTokenService(ctx))
                    .addHeader("Accept", "application/json")
                    .addHeader("X-CSRF-Token", "Fetch")
                    .basicAuthentication(Config.technicalUsername(ctx), Config.technicalPassword(ctx))
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                Headers response = result.getHeaders().getHeaders();*/
                                String xcsrfTokenString = Constants.TOKEN; //response.get("x-csrf-token");
                                sendTimbratura(xcsrfTokenString);
                            /*} catch (Exception e1) {
                                e1.printStackTrace();
                                showError(null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showError(null);
        }*/
    }

    @Override
    public void onNoNetwork() {
        Utils.showStatusMessage("Attenzione", "Per favore controlla la connessione Internet", grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
    }

    @Override
    public void displayNetworkError(NetworkError networkError) {
        Utils.showStatusMessage("Attenzione", networkError.getLocalizedErrorMessageOrDefault(this), grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
    }


    //DA MOCKARE
    public void sendTimbratura(String xcsrfToken) {

        Log.d(TAG,"sono nella sendTimbratura()");

        Context ctx = getApplicationContext();
        final SharedPreferences settings = ctx.getSharedPreferences("settings", 0);
        String tokenString = settings.getString("token", null);

        Header accept = new Header("Accept", "application/json");
        Header content = new Header("Content-Type", "application/json");
        Header tokenh = new Header("Token", tokenString);
        Header xcsrfTokenh = new Header("X-CSRF-Token", xcsrfToken);

        JsonObject timbJson = timbraturaJSON();

        String url = Uri.parse(NetworkManager.timbratureService(getApplicationContext()))
                .buildUpon()
                .build().toString();


        timbratureNetworkModel.sendTimbratura(timbJson, new NetworkCallBack<Timbratura>() {

            @Override
            public void onSuccess(Timbratura response) {

                Log.d(TAG, "sono nella onSuccess()");
                //Context ctx = getApplicationContext();
                //SharedPreferences settings = ctx.getSharedPreferences("timbrSettings", 0);
                //Set<String> timbrature = settings.getStringSet("timbrature", null);
                /*if(timbrature != null) {

                    //////in questo modo riusciamo ad invertire le timbrature, però l'ordine si ripercuote
                    //////anche sulle timbrature salvate nelle SharedPreferences
                    ////List list = new ArrayList(timbrature);
                    ////Collections.sort(list, Collections.reverseOrder());
                    ////Set resultSet = new LinkedHashSet(list);
                    ////Iterator iterator = resultSet.iterator();


                    Iterator iterator = timbrature.iterator();
                    String timbratura = iterator.next().toString(); //null;
                    //scorro tutto il Set in modo da ottenere l'ultima timbratura effettuata
                    //while(iterator.hasNext()) {
                    //    timbratura = iterator.next().toString();
                    //}
                    Log.d(TAG, "Timbratura ottenenuta dalle SharedPreferences: " + timbratura);
                } else
                    Log.d(TAG, "Non ho trovato timbrature nelle SharedPreferences");*/
                Log.d(TAG, "Timbratura ricevuta (quella che ho inviato): " +response.toString());

                showSuccessDialog();

            }

            @Override
            public void onError(Throwable t) {

                Log.d(TAG, "sono nella onError()");
                final NetworkErrorType errorType = ErrorParser.getErrorType(t);
                if(errorType==NetworkErrorType.NO_INTERNET)
                    onNoNetwork();
                else
                    displayNetworkError(errorType);
                showProgress(false);

                //showError(errorType);

            }

            @Override
            public void onErrorResponse(NetworkError networkError) {

                Log.d(TAG,"sono nella onErrorResponse()");
                //restituisci l'errore di rete ricevuto
                displayNetworkError(networkError);
                showProgress(false);

                //showError(networkError);

            }

        });

        /*try {
            Ion.with(this)
                    .load(url)
                    .setHeader(accept)
                    .setHeader(content)
                    .setHeader(tokenh)
                    .setHeader(xcsrfTokenh)
                    .basicAuthentication(Config.technicalUsername(ctx), Config.technicalPassword(ctx))
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                showSuccessDialog();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                showError(result);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showError(null);
        }*/
    }

    // MESSAGE DIALOGS
    @Override
    public void showSuccessDialog() {
        showProgress(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Timbratura inviata correttamente")
                .setTitle("");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blackColor));
            }
        });

        dialog.show();

    }

    private void showSendDialog(String tipo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stai inviando una timbratura di " + tipo + ".\n" + "Vuoi procedere?")
                .setTitle("Conferma invio");

        builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getXCSRFTokenAndSend();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blackColor));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blackColor));
            }
        });

        dialog.show();
    }

    private void addDataToUI() {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);


        String data = Utils.dayOfWeek(dayOfWeek) + " " + String.valueOf(day) + " " + Utils.monthOfYear(month) + " " + String.valueOf(year);
        dateView.setText(data);
        addressText.setText(indirizzo);
    }


    /*
    private void clearDataIfExpired() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("credentials", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        finish();
    }
    */

    /* -------------------------- VECCHI SERVIZI --------------------------
    // CHIAMATA POST
    public void sendTimbraturaOld(){

        // Ignore cookies during connection id retrieval
        try {
            Context ctx = getApplicationContext();
            ODataHttpClient oDataHttpClient = new ODataHttpClient(ctx, xsmpappcid,
                    username, password);
            oDataHttpClient.setTokenDelegate(new ODataHttpTokenClientCallback() {
                @Override
                public void onErrorCallback(Exception ex, Response response) {
                    // A network error happened. Use parameters "ex" and "response" to get more details
                    showError();

                    // token scaduto

                    //if(response!= null && response.getHeaders()!= null && response.getHeaders().code() == 401){
                    //    clearDataIfExpired();
                    //}

                }

                @Override
                public void onFetchXCSRFTokenSuccessCallback(String token){
                    System.out.println("X-CSRF-TOKEN: " + token);
                    postWithToken(token);
                }
            });
            oDataHttpClient.getXCSRFToken(NetworkManager.xCSRFTokenURL(getApplicationContext()));

        } catch (SmpExceptionInvalidInput ex) {
            // INVALID PARAMETERS ARE PROVIDED
            ex.printStackTrace();
            showError();
        }
    }

    public void postWithToken(String xcsrfToken){

        Header token = new Header("X-SMP-APPCID", xsmpappcid);
        Header accept = new Header("Accept", "application/json");
        Header content = new Header("Content-Type", "application/json");
        Header ctoken = new Header("X-CSRF-Token", xcsrfToken);

        JsonObject jsonObject = timbraturaJSON();

        // Ignore cookies during connection id retrieval
        try {
            Context ctx = getApplicationContext();
            ODataHttpClient oDataHttpClient = new ODataHttpClient(ctx, xsmpappcid,
                    username, password);
            oDataHttpClient.setPostDelegate(new ODataHttpPostClientCallback() {
                @Override
                public void onErrorCallback(Exception ex, Response response) {
                    // A network error happened. Use parameters "ex" and "response" to get more details
                    showError();

                    // token scaduto
                    //
                    //if(response!= null && response.getHeaders()!= null && response.getHeaders().code() == 401){
                    //    clearDataIfExpired();
                    //}

                }

                @Override
                public void onPostSuccessCallback(){
                    //Utils.showStatusMessage("Timbratura inviata correttamente", getApplicationContext());
                    showProgress(false);
                    showSuccessDialog();
                }
            });
            oDataHttpClient.sendODataEntitySet(NetworkManager.postURL(getApplicationContext()), jsonObject, token, accept, content, ctoken);

        } catch (SmpExceptionInvalidInput ex) {
            // INVALID PARAMETERS ARE PROVIDED
            ex.printStackTrace();
            showError();
        }
    }
    */

}
