package it.reply.timbrature.timbrature;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import java.util.List;

import it.reply.timbrature.timbrature.DataSet.Employee;
import it.reply.timbrature.timbrature.ErrorSet.ErrorParser;
import it.reply.timbrature.timbrature.ErrorSet.MainError;
import it.reply.timbrature.timbrature.ErrorSet.NetworkError;
import it.reply.timbrature.timbrature.ErrorSet.NetworkErrorType;
import it.reply.timbrature.timbrature.Mock.MockServer;
import it.reply.timbrature.timbrature.Network.NetworkManager;
import it.reply.timbrature.timbrature.Network.TimbratureNetworkModel;
import it.reply.timbrature.timbrature.SupportClasses.TimbratureModule;
import it.reply.timbrature.timbrature.TokenSet.MainToken;
import it.reply.timbrature.timbrature.TokenSet.Token;
import it.reply.timbrature.timbrature.Utils.Config;
import it.reply.timbrature.timbrature.Utils.Constants;
import it.reply.timbrature.timbrature.Utils.Utils;
import it.reply.timbrature.timbrature.ValidationSet.MainValidation;
import it.reply.timbrature.timbrature.ValidationSet.Validation;
import it.reply.timbrature.timbrature.common.App;
import it.reply.timbrature.timbrature.model.NetworkCallBack;
import it.reply.timbrature.timbrature.view.LoginView;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;


/**
//  LoginActivity
//  Timbrature
//
//  Created by Raffaele Forgione @ Syskoplan Reply.
//  Copyright © 2017 Acea. All rights reserved.
*/

//NB: This is the real Main Activity (@grucco)
public class LoginActivity extends AppCompatActivity implements LoginView {


    // UI references.
    private EditText mUsernameView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences settings;
    private Button mEmailSignInButton;

    private String token;
    private String verificationCode;
    private String inserted;

    @Inject
    TimbratureNetworkModel timbratureNetworkModel;

    private boolean verified = false;

    private boolean authenticated = false;

    // dialog
    private int whiteColor;
    private int grayColor;
    private int lightGrayColor;
    private Drawable warnIcon;
    private Drawable mailIcon;

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;



    public void checkCanDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Context ctx = getApplicationContext();

        ((App) getApplication()).getAppComponent()
                .plus(new TimbratureModule(this))
                .inject(this);

        //timbratureNetworkModel = new TimbratureNetworkModel();


        //start the mock server here
        //startMockServer();

        checkCanDrawOverlays();


        // dialog
        lightGrayColor = ContextCompat.getColor(getApplicationContext(), R.color.superLightGrayColor);
        whiteColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        warnIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_warn);
        mailIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mail);
        grayColor = ContextCompat.getColor(getApplicationContext(), R.color.darkGrayColor);

        // controllo se la login è stata lanciata dalle altre activity in seguito alla scadenza della password
        settings =  ctx.getSharedPreferences("settings", 0);

        //@g.rucco: faccio in modo che l'utente deve autenticarsi ogni volta inserendo la matricola di prova ABC123
        //per farlo devo commentare ste righe
        String username = settings.getString("username", null);
        token = settings.getString("token", null);

        //@g.rucco: faccio in modo che l'utente deve autenticarsi ogni volta inserendo la matricola di prova ABC123
        //per farlo devo commentare ste righe
        verified = settings.getBoolean("verified", false);


        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissKeyboard();
                attemptLogin();
            }
        });

        Log.d("LoginActivity","verified: "+verified);
        Log.d("LoginActivity","authenticated: "+authenticated);

        // disattivo il pulsante per ottenere il verificationCode se già l'ho richiesto
        if(verified == true){
            mEmailSignInButton.setBackgroundColor(lightGrayColor);
            mEmailSignInButton.setEnabled(false);
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Se sto in build o in mock popolo le credenziali con quelle predefinite nella classe Config
        /*if(Config.isTesting(ctx)){
            username = Config.technicalUsername(ctx);
        }

        mUsernameView.setText(username);*/
        //mPasswordView.setText(password);

        // Se ho già effettuato il login in una precedente sessione ed è andato a buon fine, lo faccio partire in automatico
        //@g.rucco: se la matricola inserita è quella corretta, allora possiamo lanciare la MainActivity
        //if(token != null && token.length() > 0){
        if(authenticated || verified) {
            Log.d("LoginActivity","Sto per lanciare la MainActivity");
            intentToMainActivity();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        inserted = username;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("Campo obbligatorio");
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            //getToken(username, password, token);

            //DA MOCKARE
            getValidationCode(username);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onNoNetwork() {
        Utils.showStatusMessage("Attenzione", "Per favore controlla la connessione Internet", grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
    }

    @Override
    public void displayNetworkError(NetworkError networkError) {
        Utils.showStatusMessage("Attenzione", networkError.getLocalizedErrorMessageOrDefault(this), grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
    }

    private void intentToMainActivity(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        showProgress(false);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }

    public void dismissKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUsernameView.getWindowToken(), 0);
    }

    public void getValidationCode(final String matricola) {

        showProgress(true);
        //final Type type = new TypeToken<ArrayList<Error>>(){}.getType();

        timbratureNetworkModel.getVerificationCode(matricola, new NetworkCallBack<Employee>() {


            //se la ricezione del verication code va a buon fine, occorre ottenere il token associato
            @Override
            public void onSuccess(Employee response) {

                //ottieni l'authentication token associato al verification code ottenuto
                Log.d("getVerificationCode()","sono nella onSuccess()");
                Log.d("getVerificationCode()","mat: "+response.getMatricola()+", ver_code: "+response.getVerCode());
                Log.d("getVerificationCode()","mat inserita: "+inserted);
                manageValidationSuccess(response.getMatricola());

            }

            @Override
            public void onError(Throwable t) {

                Log.d("getVerificationCode()","sono nella onError()");

                final NetworkErrorType errorType = ErrorParser.getErrorType(t);
                if(errorType==NetworkErrorType.NO_INTERNET)
                    onNoNetwork();
                else
                    displayNetworkError(errorType);
                showProgress(false);


                //se si è verificato un errore, verifica il tipo di errore e mostralo
                //showErrorDialog();

            }

            @Override
            public void onErrorResponse(NetworkError networkError) {

                Log.d("getVerificationCode()","sono nella onErrorResponse()");
                //restituisci l'errore di rete ricevuto
                displayNetworkError(networkError);
                showProgress(false);

                //showErrorDialog();

            }
        });



        /*
        final String uriToParse = NetworkManager.validationCodeService(getApplicationContext()) + "(Matricola='" + matricola + "')";

        String url = Uri.parse(uriToParse)
                .buildUpon()
                .build().toString();

        try {
            Context ctx = getApplicationContext();

            Ion ion = Ion.getDefault(ctx);
            ion.getCookieMiddleware().clear();
            ion.with(this)
                    .load(url)
                    .addHeader("Accept", "application/json")
                    .basicAuthentication(Config.technicalUsername(ctx), Config.technicalPassword(ctx))
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                manageValidationSuccess(result);
                            } catch (Exception e1) {
                                System.out.println("errore");
                                manageErrors(result);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog();
        }
        */
    }

    private void getToken(final String validationCode){

        final String uriToParse = NetworkManager.tokenService(getApplicationContext()) + "(Token='" + validationCode + "')";

        String url = Uri.parse(uriToParse)
                .buildUpon()
                .build().toString();

        try {
            Context ctx = getApplicationContext();

            /*Ion ion = Ion.getDefault(ctx);
            ion.getCookieMiddleware().clear();
            ion.with(this)
                    .load(url)
                    .addHeader("Accept", "application/json")
                    .basicAuthentication(Config.technicalUsername(ctx), Config.technicalPassword(ctx))
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);*/
                                manageTokenSuccess(/*result*/);
                                // salvo le impostazioni
                            } catch (Exception e1) {
                                System.out.println("errore");
                                showErrorDialog();
                                manageErrors(/*result*/);
                            }
       //                 }
                    //);
       /* } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog();
        }*/
    }

    public void showErrorDialog(){
        showProgress(false);
        Utils.showStatusMessage("Attenzione", "Si è verificato un errore", grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
    }

    public void showDialog(String message){
        showProgress(false);
        Utils.showStatusMessage(message, this);
        //Utils.showStatusMessage("Attenzione", message, grayColor, whiteColor, warnIcon, lightGrayColor, getApplicationContext());
    }

    private void getDataFromIntent(){
        Intent tItent = getIntent();
        verificationCode = tItent.getStringExtra("verificationCode");
        String token = settings.getString("token", null);
        if(token != null && token.length() > 0){
            intentToMainActivity();
        }
        else if(verificationCode != null && verificationCode.length() > 0){
            mEmailSignInButton.setBackgroundColor(lightGrayColor);
            mEmailSignInButton.setEnabled(false);
            // chiamo il servizio per ottenere il token
            showProgress(true);
            getToken(verificationCode);
        }
    }

    private void manageTokenSuccess(/*Response<JsonObject> result*/) {
        /*if (result != null && result.getResult() != null && !(result.getResult()).has("ERROR")) {
            Gson gson = new Gson();
            MainToken tok = gson.fromJson(result.getResult().toString(), MainToken.class);
            Token token = tok.d;*/
            String tokenString = Constants.TOKEN;/*token.getToken();*/
            if (tokenString != null && tokenString.length() > 0) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("token", tokenString);
                editor.putString("matricola", mUsernameView.getText().toString());
                editor.putBoolean("showTutorial", true);
                editor.apply();
                intentToMainActivity();
            } else {
                showDialog("Errore nell'autenticazione");
            }
        /*}
        else {
            showDialog("Errore nell'autenticazione");
        }*/
    }

    /*private void manageValidationSuccess(Response<JsonObject> result){
        if (result != null && result.getResult() != null && !(result.getResult()).has("ERROR")) {
            Gson gson = new Gson();
            MainValidation validation = gson.fromJson(result.getResult().toString(), MainValidation.class);
            Validation val = validation.d;
            if (val != null && val.getMatricola().equals(mUsernameView.getText().toString())) {
                mEmailSignInButton.setBackgroundColor(lightGrayColor);
                mEmailSignInButton.setEnabled(false);

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("verified", true);
                editor.apply();

                showProgress(false);
                Utils.showStatusMessage("Utente verificato", "Controlla la tua email personale per continuare il processo di autenticazione", grayColor, whiteColor, mailIcon, lightGrayColor, getApplicationContext());
            } else {
                showDialog("Errore nell'autenticazione");
            }
        }
        else {
            showDialog("Errore nell'autenticazione");
        }
    }*/



    private void manageValidationSuccess(String result){
        if (result != null) {
            /*Validation val = new Validation();
            val.setMatricola(Constants.MATRICOLA);*/
            //if (val != null && val.getMatricola().equals(mUsernameView.getText().toString())) {
            if(result.equals(inserted)) {

                mEmailSignInButton.setBackgroundColor(lightGrayColor);
                mEmailSignInButton.setEnabled(false);

                //apro le SharedPreferences per impostare le informazioni di default
                //per l'utente di default dell'applicazione
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("verified", true);
                editor.putString("token", Constants.TOKEN);
                editor.putString("uname", Constants.MATRICOLA);
                editor.putString("nome", Constants.NOME);
                editor.putString("cognome", Constants.COGNOME);
                editor.putString("societa", Constants.SOCIETA);
                editor.putString("pdsnr", Constants.PDSNR);
                editor.putString("zausw", Constants.ZAUSW);

                editor.putBoolean("showTutorial", true);
                editor.apply();
                editor.apply();

                authenticated = true;
                Log.d("LoginActivity","authenticated = "+authenticated);
                Log.d("LoginActivity","verified = "+verified);

                showProgress(false);
                Utils.showStatusMessage("Utente verificato", "Controlla la tua email personale per continuare il processo di autenticazione", grayColor, whiteColor, mailIcon, lightGrayColor, getApplicationContext());


                //@g.rucco: inserito da me (non riesce a ritornare nella onCreate()
                // disattivo il pulsante per ottenere il verificationCode se già l'ho richiesto
                //if(verified == true){
                    mEmailSignInButton.setBackgroundColor(lightGrayColor);
                    mEmailSignInButton.setEnabled(false);
                //}

                mLoginFormView = findViewById(R.id.login_form);
                mProgressView = findViewById(R.id.login_progress);

                //if(authenticated) {
                    Log.d("LoginActivity","Sto per lanciare la MainActivity");
                    intentToMainActivity();
                //}

            } else {
                showDialog("Errore nell'autenticazione");
            }
        } else {
            showDialog("Errore nell'autenticazione");
        }
    }


    private void manageErrors(/*Response<JsonObject> result*/){
        //if (result != null && result.getResult() != null && !(result.getResult()).has("ERROR")) {
           /* Gson gson = new Gson();
            MainError err = gson.fromJson(result.getResult().toString(), MainError.class);
            if (err.error != null) {
                showDialog(err.error.getMessage().getValue());
            } else {*/
                showDialog("Errore nell'autenticazione");
            //}
        /*} else {
            showDialog("Errore nell'autenticazione");
        }*/
    }

    private void startMockServer(){
        Context ctx = getApplicationContext();
        if(Config.isMock()){
            try {
                if(MockServer.getInstance(ctx) == null){
                    new MockServer(ctx);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean verified = settings.getBoolean("verified", false);
        // disattivo il pulsante per ottenere il verificationCode se già l'ho richiesto
        if(verified == true){
            mEmailSignInButton.setBackgroundColor(lightGrayColor);
            mEmailSignInButton.setEnabled(false);
        }

        getDataFromIntent();

    }

}

