package it.reply.timbrature.timbrature;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;
import com.github.sundeepk.compactcalendarview.*;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.io.IOException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.reply.timbrature.timbrature.DataSet.MainTimbratura;
import it.reply.timbrature.timbrature.DataSet.Timbratura;
import it.reply.timbrature.timbrature.ErrorSet.ErrorParser;
import it.reply.timbrature.timbrature.ErrorSet.MainError;
import it.reply.timbrature.timbrature.ErrorSet.NetworkError;
import it.reply.timbrature.timbrature.ErrorSet.NetworkErrorType;
import it.reply.timbrature.timbrature.Mock.MockServer;
import it.reply.timbrature.timbrature.Network.NetworkManager;
import it.reply.timbrature.timbrature.Network.TimbratureNetworkModel;
import it.reply.timbrature.timbrature.SupportClasses.Timbrature;
import it.reply.timbrature.timbrature.SupportClasses.TimbratureModule;
import it.reply.timbrature.timbrature.Utils.Config;
import it.reply.timbrature.timbrature.Utils.CustomDecoration;
import it.reply.timbrature.timbrature.Utils.Utils;
import it.reply.timbrature.timbrature.Utils.MyEvent;
import it.reply.timbrature.timbrature.common.App;
import it.reply.timbrature.timbrature.model.NetworkCallBack;
import it.reply.timbrature.timbrature.view.MainView;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

// GPS
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;


import javax.inject.Inject;

import static java.lang.Integer.parseInt;

/**
//  MainActivity
//  Timbrature
//
//  Created by Raffaele Forgione @ Syskoplan Reply.
//  Copyright © 2017 Acea. All rights reserved.
*/

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String TAG = "MainActivity";

    private static final int TIME_ADJUST = 20;

    @Inject
    TimbratureNetworkModel timbratureNetworkModel;

    // INTERFACCIA
    private CompactCalendarView compactCalendarView;
    private Date dataSelezionata;
    private BottomSheetBehavior mBottomSheetBehavior;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.MonthView)
    TextView monthLabel;
    @BindView(R2.id.YearView)
    TextView yearLabel;
    @BindView(R2.id.ShowCalendar)
    TextView dayLabel;
    @BindView(R2.id.DateView)
    TextView selectedDate;
    @BindView(R2.id.ServiceMessage)
    TextView serviceMessage;
    @BindView(R2.id.EventPlanner)
    ScrollView eventPlanner;
    @BindView(R2.id.dayView)
    CalendarDayView dayView;
    @BindView(R2.id.BadgeImage)
    ImageView badgeImage;
    @BindView(R2.id.fab)
    FloatingActionButton fab;
    @BindView(R2.id.mainHUD)
    ProgressBar hud;
    @BindView(R2.id.swiperefresh)
    SwipeRefreshLayout refresh;
    @BindView(R2.id.bottom_sheet)
    View bottomSheet;
    @BindView(R2.id.AddressText)
    TextView addressText;
    @BindView(R2.id.TipoText)
    TextView tipoText;
    @BindView(R2.id.OrarioText)
    TextView orarioText;
    @BindView(R2.id.menuButton)
    Button menuButton;

    // GPS
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String longitude = "";
    private String latitude = "";
    private Geocoder geocoder;
    private List<Address> addresses;
    private String address = "";

    // Timbrature
    private ArrayList<Timbratura> timbratureScaricate;

    // Altro
    private boolean isToday = true;
    private boolean isDownloadingTimbrature = false;
    private SharedPreferences settings;

    private int whiteColor;
    private int grayColor;
    private int lightGrayColor;
    private Drawable warnIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //g.rucco
        //startMockServer();

        //necessario per inizializzare correttamente i vari oggetti che lavorano
        //per effettuare le richieste e ricevere le risposte
        ((App) getApplication()).getAppComponent()
                .plus(new TimbratureModule(this))
                .inject(this);

        // GPS
        geocoder = new Geocoder(this, Locale.ITALIAN);
        configureLocationManager();

        // UI
        setSupportActionBar(toolbar);
        configureBottomSheet();
        serviceMessage.setText("");

        // dialog
        lightGrayColor = ContextCompat.getColor(getApplicationContext(), R.color.superLightGrayColor);
        whiteColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        warnIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_warn);
        grayColor = ContextCompat.getColor(getApplicationContext(), R.color.darkGrayColor);

        refreshDayEvents();

        eventPlanner.setVisibility(View.INVISIBLE);
        monthLabel.setVisibility(View.INVISIBLE);
        yearLabel.setVisibility(View.INVISIBLE);

        dataSelezionata = Utils.getZeroTimeDate(new Date());
        monthLabel.setText(Utils.monthOfYear(Calendar.getInstance().get(Calendar.MONTH)));
        yearLabel.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        dayLabel.setText(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));

        createCalendar();

        // scarico le timbrature
        showProgress(true);
        timbratureScaricate = new ArrayList<>();
        getTimbratureFromService();

        makeTimbratura(); //se va tutto bene lancia TimbraturaActivity

    }

    @Override
    @OnClick(R2.id.ShowCalendar)
    public void submit() {
        if (!compactCalendarView.isAnimating()) {
            if (compactCalendarView.getVisibility() == View.VISIBLE) {
                monthLabel.setVisibility(View.INVISIBLE);
                yearLabel.setVisibility(View.INVISIBLE);
                hideCalendar();
            } else {
                monthLabel.setVisibility(View.VISIBLE);
                yearLabel.setVisibility(View.VISIBLE);
                showCalendar();
            }
        }
    }

    @Override
    @OnClick(R2.id.menuButton)
    public void showMenu() {
        PopupMenu popup = new PopupMenu(MainActivity.this, menuButton);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Esci")) {
                    logout();
                }
                return true;
            }
        });
        popup.show();//showing popup menu
    }


    @Override
    @OnClick(R2.id.closeBottom)
    public void closeBottomSheet(){
        mBottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setVisibility(View.INVISIBLE);
    }

    private void logout() {
        settings = getApplicationContext().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }

    protected void createCalendar() {

        Log.d(TAG, "sono nella createCalendar()");

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.ITALIAN);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                Log.d(TAG, "sono nella onDayClick()");

                // collasso la bottomsheet
                mBottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);

                bottomSheet.setVisibility(View.INVISIBLE);

                // aggiorno il timesheet
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                //Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateClicked);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                //int year = cal.get(Calendar.YEAR);
                Date today = Utils.getZeroTimeDate(new Date());
                Date chosenDate = Utils.getZeroTimeDate(dateClicked);
                dataSelezionata = chosenDate;
                dayLabel.setText(String.valueOf(day));
                String dayString = String.valueOf(day);
                if (today.compareTo(dateClicked) == 0) {
                    selectedDate.setText("Oggi");
                    selectedDate.append("\n" + dayString + " " + Utils.monthOfYearForShort(month));
                    fab.setVisibility(View.VISIBLE);
                    isToday = true;
                } else {
                    selectedDate.setText(dayString);
                    //monthLabel.setText(Constants.MONTHS[month]);
                    //yearLabel.setText(String.valueOf(year));
                    selectedDate.append("\n" + Utils.monthOfYearForShort(month));
                    //selectedDate.setText(String.valueOf(day) + "/n" + monthsForShort[month]);
                    fab.setVisibility(View.INVISIBLE);
                    isToday = false;
                }
                compactCalendarView.setVisibility(View.INVISIBLE);
                monthLabel.setVisibility(View.INVISIBLE);
                yearLabel.setVisibility(View.INVISIBLE);

                showProgress(true);
                if (events != null && events.size() > 0) {
                    showTimbrature(timbratureScaricate);
                    showProgress(false);
                } else {
                    showProgress(true);
                    getTimbratureFromServiceWithDate(dateClicked); //DA MOCKARE
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                Calendar todayCal = Calendar.getInstance();
                cal.setTime(firstDayOfNewMonth);
                todayCal.setTime(today);
                int month = cal.get(Calendar.MONTH);
                int todayMonth = todayCal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                int todayYear = todayCal.get(Calendar.YEAR);
                monthLabel.setText(Utils.monthOfYear(month));
                yearLabel.setText(String.valueOf(year));

                /*
                if (todayYear == year && todayMonth >= month) {
                    // non fare nulla
                } else {
                    // disattivare i giorni da calendario
                }
                */
            }
        });
    }

    private void configureLocationManager() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                // riporta l'utente alla schermata delle impostazioni del GPS di Android
                // Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                // startActivity(gpsIntent);

                // cancello le informazioni sulla posizione
                latitude = "";
                longitude = "";
                address = "";
            }
        };
        // CONTROLLO DI VERSIONE PER I PERMESSI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET},
                        10);
                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    } catch (SecurityException e) {
                        // let the user know there is a problem with the gps
                    }
                }
        }
    }


    private void makeTimbratura() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // se ha acquisito correttamente le coordinate e ha ricavato l'indirizzo allora passo all'activity per la timbratura
                if (missingDataForTimbratura()) {
                    Snackbar.make(view, "Attenzione, impossibile trovare la posizione del dispositivo", Snackbar.LENGTH_LONG)
                            .show();


                    //showDialog("Attenzione", "Impossibile trovare la posizione del dispositivo", warnIcon);
                } else {
                    Date oggi = Utils.getZeroTimeDate(new Date());
                    if (dataSelezionata.compareTo(oggi) == 0) {
                        // permetto di effettuare la timbratura solo per la data odierna
                        Intent intent = new Intent(MainActivity.this, TimbraturaActivity.class);
                        configureTimbraturaIntent(intent);
                        startActivity(intent);
                    } else {
                        Snackbar.make(view, "Attenzione, impossibile effettuare la timbratura", Snackbar.LENGTH_LONG)
                                .show();

                        //showDialog("Attenzione", "Impossibile effettuare la timbratura", warnIcon);
                    }
                }

            }
        });
    }

    //ottiene un elenco di timbrature locale (da oggetto JSON negli assets)
    private ArrayList<Timbratura> getTimbrature() {

        String jsonString = Utils.loadJSONFromAsset("TimbratureCoordinate(GET)", getApplicationContext());

        Gson gson = new Gson();
        MainTimbratura mainTimbratura = gson.fromJson(jsonString, MainTimbratura.class);
        return mainTimbratura.d.results;
    }


    private void showTimbrature(ArrayList<Timbratura> timbrature) {
        ArrayList<IEvent> events;
        ArrayList<Timbratura> timbratureGiornoSelezionato = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ITALIAN);

        if (timbrature.size() > 0) {
            // rimuovo gli eventi precedentemente salvati nel calendario
            compactCalendarView.removeAllEvents();
        }

        for (Timbratura t : timbrature) {

            // aggiungo evento timbratura al calendario
            String ldate = t.getLdate();

            // aggiungo al calendario solo un evento per giornata
            Long date = Utils.dateInMillis(ldate);
            if (compactCalendarView.getEvents(date).size() == 0) {
                // Colori
                int EVENT_COLOR = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                Event event = new Event(EVENT_COLOR, date, "");
                compactCalendarView.addEvent(event, false);
            }

            Date timbDate = null;
            try {
                ldate = Utils.oDataDateToDate(t.getLdate());
                Date temp = format.parse(ldate);
                timbDate = Utils.getZeroTimeDate(temp);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (timbDate != null && dataSelezionata.compareTo(timbDate) == 0) {
                timbratureGiornoSelezionato.add(t);
            }
        }

        timbratureGiornoSelezionato = sortTimbrature(timbratureGiornoSelezionato);

        dayView.setDecorator(new CustomDecoration(getApplicationContext()));

        // SE CI SONO TIMBRATURE MOSTRO IL COMPONENTE, ALTRIMENTI LO NASCONDO E MOSTRO L'IMMAGINE DI SFONDO
        if (timbratureGiornoSelezionato.size() > 0) {
            events = getEventsManagingAnomalies(timbratureGiornoSelezionato);
            setEventsListener();

            dayView.setEvents(events);
            eventPlanner.setVisibility(View.VISIBLE);
            badgeImage.setVisibility(View.INVISIBLE);

            Context ctx = getApplicationContext();

            if (isToday) {
                fab.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.timbratureMainColor));
                fab.setColorFilter(ContextCompat.getColor(ctx, R.color.colorAccent));
            }

        } else {
            eventPlanner.setVisibility(View.INVISIBLE);
            badgeImage.setVisibility(View.VISIBLE);
            bottomSheet.setVisibility(View.INVISIBLE);
            serviceMessage.setText("Non esistono timbrature per il giorno selezionato.");

            Context ctx = getApplicationContext();

            if (isToday) {
                fab.setColorFilter(ContextCompat.getColor(ctx, R.color.timbratureMainColor));
                fab.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.colorAccent));
            }
        }


    }

    private void setEventsListener() {
        ((CdvDecorationDefault) (dayView.getDecoration())).setOnEventClickListener(
                new EventView.OnEventClickListener() {
                    @Override
                    public void onEventClick(EventView view, IEvent data) {
                        if (data instanceof MyEvent) {
                            // change event (ex: set event color)
                            MyEvent event = (MyEvent) data;
                            if (event.getIndirizzo() != null && event.getIndirizzo().length() > 0) {
                                // compilo il bottom sheet
                                bottomSheet.setVisibility(View.VISIBLE);
                                //addressText.setText(event.getIndirizzo() + "\n");

                                // GESTIONE LINK
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    addressText.setText(Html.fromHtml("<p>" + event.getIndirizzo() + "</p><a href=\"" + event.getLink() + "\">Vedi Mappa</a>", Html.FROM_HTML_MODE_COMPACT));
                                }
                                else{
                                    addressText.setText(Html.fromHtml("<p>" + event.getIndirizzo() + "</p><a href=\"" + event.getLink() + "\">Vedi Mappa</a>"));
                                }
                                addressText.setMovementMethod(LinkMovementMethod.getInstance());

                                String orario = event.getDescription();
                                String eventTitle = event.getName();
                                tipoText.setText(eventTitle);
                                if(eventTitle != null && eventTitle.equals("Entrata")){
                                    String[] description = orario.split(" - ");
                                    if(description != null && description.length > 0){
                                        orario = description[0];
                                    }
                                }
                                else if(eventTitle != null && eventTitle.equals("Uscita")){

                                }
                                orarioText.setText(orario);
                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            else{
                                bottomSheet.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onEventViewClick(View view, EventView eventView, IEvent data) {
                        if (data instanceof MyEvent) {
                            // change event (ex: set event color)
                            MyEvent event = (MyEvent) data;
                            if (event.getIndirizzo() != null && event.getIndirizzo().length() > 0) {
                                // compilo il bottom sheet
                                bottomSheet.setVisibility(View.VISIBLE);
                                //addressText.setText(event.getIndirizzo() + "\n");

                                // GESTIONE LINK
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    addressText.setText(Html.fromHtml("<p>" + event.getIndirizzo() + "</p><a href=\"" + event.getLink() + "\">Vedi Mappa</a>", Html.FROM_HTML_MODE_COMPACT));
                                }
                                else{
                                    addressText.setText(Html.fromHtml("<p>" + event.getIndirizzo() + "</p><tr><a href=\"" + event.getLink() + "\">Vedi Mappa</a>"));
                                }
                                addressText.setMovementMethod(LinkMovementMethod.getInstance());

                                String orario = event.getDescription();
                                String eventTitle = event.getName();
                                tipoText.setText(eventTitle);
                                if(eventTitle != null && eventTitle.equals("Entrata")){
                                    String[] description = orario.split(" - ");
                                    if(description != null && description.length > 0){
                                        orario = description[0];
                                    }
                                }
                                orarioText.setText(orario);
                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            else{
                                bottomSheet.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }


    private MyEvent createEvent(long id, Date timbTime1, Date timbTime2, int color, String eventType, float alpha, float textSize, ColorStateList textColor) {
        Calendar timeStart = Calendar.getInstance();
        timeStart.set(Calendar.HOUR_OF_DAY, parseInt((DateFormat.format("HH", timbTime1).toString())));
        timeStart.set(Calendar.MINUTE, parseInt((DateFormat.format("mm", timbTime1).toString())));
        Calendar timeEnd = Calendar.getInstance();
        timeEnd.set(Calendar.HOUR_OF_DAY, parseInt((DateFormat.format("HH", timbTime2).toString())));
        timeEnd.set(Calendar.MINUTE, parseInt((DateFormat.format("mm", timbTime2).toString())));
        return new MyEvent(id, timeStart, timeEnd, eventType, color, alpha, textSize, textColor);
    }

    private MyEvent createEvent(long id, Calendar timbTime1, Date timbTime2, int color, String eventType, float alpha, float textSize, ColorStateList textColor) {
        Calendar timeEnd = Calendar.getInstance();
        timeEnd.set(Calendar.HOUR_OF_DAY, parseInt((DateFormat.format("HH", timbTime2).toString())));
        timeEnd.set(Calendar.MINUTE, parseInt((DateFormat.format("mm", timbTime2).toString())));
        return new MyEvent(id, timbTime1, timeEnd, eventType, color, alpha, textSize, textColor);
    }

    private MyEvent createEvent(long id, Calendar timbTime1, Calendar timbTime2, int color, String eventType, float alpha, float textSize, ColorStateList textColor) {
        return new MyEvent(id, timbTime1, timbTime2, eventType, color, alpha, textSize, textColor);
    }

    private MyEvent createEvent(long id, Date timbTime1, Calendar timbTime2, int color, String eventType, float alpha, float textSize, ColorStateList textColor) {
        Calendar timeStart = Calendar.getInstance();
        timeStart.set(Calendar.HOUR_OF_DAY, parseInt((DateFormat.format("HH", timbTime1).toString())));
        timeStart.set(Calendar.MINUTE, parseInt((DateFormat.format("mm", timbTime1).toString())));
        return new MyEvent(id, timeStart, timbTime2, eventType, color, alpha, textSize, textColor);
    }

    private String getParam(String timbData, String activityData) {
        if (activityData != "" && activityData != null) {
            return activityData;
        }
        if (timbData != null && timbData.length() > 0) {
            return timbData;
        } else {
            return "";
        }
    }

    private void configureTimbraturaIntent(Intent intent) {
        // localizzazione
        intent.putExtra("latitudine", latitude);
        intent.putExtra("longitudine", longitude);
        intent.putExtra("via", address);

        //@g.rucco: aggiunto io

        //Formatto la data e l'ora secondo quello specificato negli oggetti JSON presenti negli assets
        long epochh = System.currentTimeMillis()/1000;
        epochh = epochh*1000;
        String today = "/Date("+epochh+")/";

        /*SimpleDateFormat f = new SimpleDateFormat("HH:MM:SS", Locale.ITALIAN);
        String ora = f.format(new Date(epochh));
        Log.d(TAG, "Ora: "+ora);
        ora = "PT"+ora.substring(0, 2)+"H"+ora.substring(3, 5)+"M"+ora.substring(6, 8)+"S";
        Log.d(TAG, "Ora formattata: "+ora);*/

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdff = new SimpleDateFormat("HH:mm:ss");
        String ora = sdff.format(cal.getTime()).toString();
        ora = "PT"+ora.substring(0, 2)+"H"+ora.substring(3, 5)+"M"+ora.substring(6, 8)+"S";
        Log.d(TAG,"ORA formattata: "+ora);

        intent.putExtra("ldate", today);
        intent.putExtra("ltime", ora);
    }

    private boolean missingDataForTimbratura() {
        if (Utils.isNullOrEmpty(latitude)) {
            return true;
        }
        if (Utils.isNullOrEmpty(longitude)) {
            return true;
        }
        if (Utils.isNullOrEmpty(address)) {
            return true;
        }
        return false;
    }

    private Timbrature getEntrateUscite(ArrayList<Timbratura> giornaliere) {
        Timbrature timbrature = new Timbrature();

        ArrayList<Timbratura> entrate = new ArrayList<Timbratura>();
        ArrayList<Timbratura> uscite = new ArrayList<Timbratura>();

        for (Timbratura t : giornaliere) {
            if (t.getSatza().equals("P10")) {
                entrate.add(t);
            } else {
                uscite.add(t);
            }
        }

        // ordino le entrate per orario
        Collections.sort(entrate, new Comparator<Timbratura>() {
            public int compare(Timbratura t1, Timbratura t2) {
                return t1.getDateTime().compareTo(t2.getDateTime());
            }
        });

        // ordino le uscite per orario
        Collections.sort(uscite, new Comparator<Timbratura>() {
            public int compare(Timbratura t1, Timbratura t2) {
                return t1.getDateTime().compareTo(t2.getDateTime());
            }
        });

        timbrature.setEntrate(entrate);
        timbrature.setUscite(uscite);

        return timbrature;
    }


    private ArrayList<Timbratura> sortTimbrature(ArrayList<Timbratura> giornaliere) {
        ArrayList<Timbratura> ordinate = giornaliere;
        Collections.sort(ordinate, new Comparator<Timbratura>() {
            public int compare(Timbratura t1, Timbratura t2) {
                return t1.getDateTime().compareTo(t2.getDateTime());
            }
        });
        return ordinate;
    }


    private ArrayList getEventsManagingAnomalies(ArrayList<Timbratura> timbratureGiornoSelezionato) {

        int anomalie = 0;

        ArrayList<IEvent> events = new ArrayList();

        for (Timbratura t : timbratureGiornoSelezionato) {
            Date jobTime1 = null;
            Date jobTime2 = null;
            String time1 = "";
            String time2 = "";
            String temp = "";
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
                time1 = Utils.formatLtime(t.getLtime());
                if (t.getSatza().equals("P10")) {
                    time2 = Utils.addMinutesToTime(TIME_ADJUST, time1);
                } else {
                    temp = Utils.subtractMinutesToTime(TIME_ADJUST, time1);
                    time2 = time1;
                    time1 = temp;
                }
                jobTime1 = timeFormat.parse(time1);
                jobTime2 = timeFormat.parse(time2);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            // Colori
            Context ctx = getApplicationContext();
            int JOB_COLOR = ContextCompat.getColor(ctx, R.color.jobColor);
            int ENTRANCE_EXIT_COLOR = ContextCompat.getColor(ctx, R.color.entranceOrExitColor);
            int ANOMALY_COLOR = ContextCompat.getColor(ctx, R.color.anomalyColor);
            ColorStateList textColor = ContextCompat.getColorStateList(ctx, R.color.colorAccent);
            ColorStateList darkTextColor = ContextCompat.getColorStateList(ctx, R.color.darkGrayColor);
            float alpha = 0.6f;
            float textSize = 18.0f;

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
            MyEvent newEvent = null;
            boolean entranceExit = false;
            if (events.size() > 0) {
                MyEvent previous = (MyEvent) events.get(events.size() - 1);
                if (previous.getName().equals("Entrata") && t.getTipotext().equals("Uscita")) {
                    newEvent = createEvent(1, previous.getEndTime(), jobTime1, JOB_COLOR, "A Lavoro", alpha, textSize, textColor);
                    newEvent.setDescriptionColor(textColor);
                    if (previous.getEndTime().getTimeInMillis() >= newEvent.getEndTime().getTimeInMillis()) {
                        Calendar previousTime = previous.getStartTime();
                        Calendar endTime = (Calendar) previousTime.clone();
                        endTime.add(Calendar.MINUTE, TIME_ADJUST);
                        newEvent = createEvent(1, previousTime, endTime, ENTRANCE_EXIT_COLOR, "Entrata/Uscita", alpha, textSize, textColor);
                        newEvent.setDescription(String.format("%02d:%02d", previous.getStartTime().get(Calendar.HOUR_OF_DAY), previous.getStartTime().get(Calendar.MINUTE)) + " - " + timeFormat.format(jobTime2));
                        Log.i(TAG, "getEventsManagingAnomalies1: " + newEvent.getDescription());
                        newEvent.setDescriptionColor(darkTextColor);
                        newEvent.setLatitudine(t.getLatitudine());
                        newEvent.setLongitudine(t.getLongitudine());
                        newEvent.setIndirizzo(t.getVia());
                        newEvent.setLink(t.getLink());
                        events.remove(previous);
                        entranceExit = true;
                    }
                }
                else if (previous.getName().equals("Entrata") && t.getTipotext().equals("Entrata")) {
                    entranceExit = true;
                    anomalie++;
                }
                else if (previous.getName().equals("Uscita") && t.getTipotext().equals("Uscita")) {
                    entranceExit = true;
                    anomalie++;
                }
            }
            MyEvent timbratura;
            if (entranceExit == false) {
                timbratura = createEvent(1, jobTime1, jobTime2, ENTRANCE_EXIT_COLOR, t.getTipotext(), alpha, textSize, textColor);
                timbratura.setLatitudine(t.getLatitudine());
                timbratura.setLongitudine(t.getLongitudine());
                timbratura.setIndirizzo(t.getVia());
                timbratura.setLink(t.getLink());
                if (t.getTipotext().equals("Entrata")) {
                    timbratura.setDescriptionColor(darkTextColor);
                    timbratura.setDescription(timeFormat.format(jobTime1));
                    Log.i(TAG, "getEventsManagingAnomalies3: " + timbratura.getDescription());
                }
                else {
                    if (t.getSatza().equals("P10")) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(jobTime2);
                        cal.add(Calendar.MINUTE, -30);
                        Date fixDate = cal.getTime();
                        timbratura.setDescription(timeFormat.format(fixDate));
                    } else {
                        timbratura.setDescription(timeFormat.format(jobTime2));
                    }

                    Log.i(TAG, "getEventsManagingAnomalies4: " + timbratura.getDescription());
                    Log.i(TAG, "getEventsManagingAnomalies4: " + t.toString());
                    MyEvent previous;
                    if (events.size() > 0) {
                        previous = (MyEvent) events.get(events.size() - 1);
                        if (previous.getName().equals("Entrata")) {
                            events.remove(previous);
                            previous.setDescription(previous.getDescription().replaceAll(" ", "") + " - " + String.format("%02d:%02d", timbratura.getEndTime().get(Calendar.HOUR_OF_DAY), timbratura.getEndTime().get(Calendar.MINUTE)));
                            Log.i(TAG, "getEventsManagingAnomalies2: " + previous.getDescription());
                            events.add(previous);
                        }
                    }
                    timbratura.setDescriptionColor(textColor);
                }
                events.add(timbratura);
            }
            if (newEvent != null) {
                events.add(newEvent);
            }
        }

        if(anomalie > 0){
            serviceMessage.setText("Sono state rilevate delle anomalie a sistema.");
        }
        else{
            serviceMessage.setText("");
        }

        return events;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            if (isToday) {
                fab.setVisibility(show ? View.GONE : View.VISIBLE);
                fab.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
            }
            badgeImage.setVisibility(show ? View.GONE : View.VISIBLE);
            badgeImage.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    badgeImage.setVisibility(show ? View.GONE : View.VISIBLE);
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
            if (isToday) {
                fab.setVisibility(show ? View.GONE : View.VISIBLE);
            }
            hud.setVisibility(show ? View.VISIBLE : View.GONE);
            badgeImage.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        dayLabel.setEnabled(!show);
    }

    /*
    private void clearDataIfExpired() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("credentials", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }
    */

    //DA MOCKARE
    private void refreshDayEvents() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTimbratureFromServiceWithDate(dataSelezionata);
            }
        });
    }

    private void removeOldTimbratureForThisDate() {
        ArrayList<Timbratura> tmpArray = (ArrayList<Timbratura>) timbratureScaricate.clone();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ITALIAN);
        for (Timbratura t : tmpArray) {

            String ldate = t.getLdate();

            Date timbDate = null;
            try {
                ldate = Utils.oDataDateToDate(t.getLdate());
                Date temp = format.parse(ldate);
                timbDate = Utils.getZeroTimeDate(temp);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (timbDate != null && dataSelezionata.compareTo(timbDate) == 0) {
                timbratureScaricate.remove(t);
            }
        }
    }

    private void showTutorial() {
        settings = getApplicationContext().getSharedPreferences("settings", 0);
        boolean showTutorial = settings.getBoolean("showTutorial", false);
        if (showTutorial) {
            showMakeTimbraturaTutorial();
        }
    }

    private void showCalendarTutorial() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setFocalColour(ContextCompat.getColor(getApplicationContext(), R.color.timbratureMainColor))
                .setBackgroundColour(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                .setTarget(findViewById(R.id.ShowCalendar))
                .setPrimaryText("Mostra Calendario")
                .setSecondaryText("Scegli un giorno per visualizzarne le timbrature")
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                        //Do something such as storing a value so that this prompt is never shown again
                        settings = getApplicationContext().getSharedPreferences("settings", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("showTutorial", false);
                        editor.apply();
                    }

                    @Override
                    public void onHidePromptComplete() {
                    }
                }).show();
    }

    private void showMakeTimbraturaTutorial() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.fab))
                .setFocalColour(ContextCompat.getColor(getApplicationContext(), R.color.timbratureMainColor))
                .setBackgroundColour(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                .setPrimaryText("Invia Timbratura")
                .setSecondaryText("Marca l'entrata o l'uscita di oggi")
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                    }

                    @Override
                    public void onHidePromptComplete() {
                        showCalendarTutorial();
                    }
                }).show();
    }

    //DA MOCKARE
    @Override
    public void onResume() {
        super.onResume();

        if (!isDownloadingTimbrature) {
            showProgress(true);
            getTimbratureFromService();
            configureLocationManager();
        }

    }

    @Override
    public void onBackPressed() {
        // se il calendario è aperto lo chiudo, altrimenti esco dall'app
        if (compactCalendarView.getVisibility() == View.VISIBLE) {
            monthLabel.setVisibility(View.INVISIBLE);
            yearLabel.setVisibility(View.INVISIBLE);
            hideCalendar();
        } else {
            finish();
        }
    }

    // MESSAGE DIALOGS
    @Override
    public void showCloseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sei sicuro di voler chiudere l'app?")
                .setTitle("");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
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

    @Override
    public void hideCalendar() {
        compactCalendarView.animate()
                .alpha(0.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        compactCalendarView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void showCalendar() {
        compactCalendarView.animate()
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        compactCalendarView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void configureBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(120);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setVisibility(View.INVISIBLE);
    }

    private void startMockServer() {
        if (Config.isMock()) {
            Context ctx = getApplicationContext();
            latitude = Config.latitude(ctx);
            longitude = Config.longitude(ctx);
            address = Config.address(ctx);
            try {
                if (MockServer.getInstance(ctx) == null) {
                    new MockServer(ctx);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //DA MOCKARE
    private void getTimbratureFromService() {

        isDownloadingTimbrature = true;

        Context ctx = getApplicationContext();
        settings = ctx.getSharedPreferences("settings", 0);
        /*String tokenString = settings.getString("token", null);
        String user = Config.technicalUsername(ctx);
        String pwd = Config.technicalPassword(ctx);

        String url = Uri.parse(NetworkManager.timbratureService(getApplicationContext()))
                .buildUpon()
                .build().toString();*/

        timbratureNetworkModel.getTimbrature(new NetworkCallBack<MainTimbratura>() {

            //se la ricezione delle timbrature è andata a buon fine
            @Override
            public void onSuccess(MainTimbratura response) {

                Log.d(TAG,"sono nella onSuccess()");
                manageServiceSuccess(response);

            }

            @Override
            public void onError(Throwable t) {

                Log.d(TAG,"sono nella onError()");

                final NetworkErrorType errorType = ErrorParser.getErrorType(t);
                if(errorType==NetworkErrorType.NO_INTERNET)
                    onNoNetwork();
                else
                    displayNetworkError(errorType);
                showProgress(false);

                manageServiceErrors(errorType);

            }

            @Override
            public void onErrorResponse(NetworkError networkError) {

                Log.d(TAG,"sono nella onErrorResponse()");
                //restituisci l'errore di rete ricevuto
                displayNetworkError(networkError);
                showProgress(false);

                //manageServiceErrors(networkError);

            }
        });

        /*try {
            Ion ion = Ion.getDefault(ctx);
            ion.getCookieMiddleware().clear();
            ion.with(this)
                    .load(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Token", tokenString)
                    .basicAuthentication(user, pwd)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                manageServiceSuccess(result);
                            } catch (Exception e1) {
                                manageServiceErrors(e, result);
                                showTutorial();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            manageServiceExceptions();
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

    //DA MOCKARE ed IMPLEMENTARE COME SI DEVE !!!
    private void getTimbratureFromServiceWithDate(Date date) {

        Log.d(TAG, "Sono nella getTimbratureFromServiceWithDate()");

        //@g.rucco: aggiunto da me
        //getTimbratureFromService();


        /*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
        String dateString = df.format(date);
        String filter = "";
        if (dateString.length() > 0) {
            filter = "Ldate eq datetime'" + dateString + "T00:00:00'";
        }


        String url = Uri.parse(NetworkManager.timbratureService(getApplicationContext()))
                .buildUpon()
                .appendQueryParameter("$filter", filter)
                .build().toString();*/

        Context ctx = getApplicationContext();
        settings = ctx.getSharedPreferences("settings", 0);
        /*String tokenString = settings.getString("token", null);
        String user = Config.technicalUsername(ctx);
        String pwd = Config.technicalPassword(ctx);*/

        ArrayList<Timbratura> timbratureWithDate = new ArrayList<Timbratura>();
        long epoch;
        String timbDateString;
        Date timbDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ITALIAN);
        for(Timbratura t : timbratureScaricate) {

            //get epoch date value (long)
            epoch = Long.parseLong(t.getLdate().substring(6, t.getLdate().length()-2));
            //convert epoch to human readable date (String)
            timbDateString = sdf.format(new Date(epoch));
            try {
                //convert String date to Date
                timbDate = sdf.parse(timbDateString);
            } catch (ParseException e) {
                Log.d(TAG, "Errore nel parsing della data!!");
                e.printStackTrace();
            }
            //Log.d(TAG,"converted: "+epoch+", ldate: "+t.getLdate());
            //Log.d(TAG,timbDate);

            if(date.compareTo(timbDate)==0) {
                timbratureWithDate.add(t);
            }
        }

        manageServiceWithDateSuccess(timbratureWithDate);


        /*try {
            Ion ion = Ion.getDefault(ctx);
            ion.getCookieMiddleware().clear();
            ion.with(this)
                    .load(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Token", tokenString)
                    .basicAuthentication(user, pwd)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                manageServiceWithDateSuccess(result);
                            } catch (Exception e1) {
                                manageServiceErrors(e, result);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showProgress(false);
        }*/

    }

    private void manageServiceExceptions() {
        showProgress(false);
        isDownloadingTimbrature = false;
        showTutorial();
    }

    private void manageServiceSuccess(/*Response<JsonObject>*/MainTimbratura result) {
        //final Type type = new TypeToken<ArrayList<Timbratura>>() {
        //}.getType();
        timbratureScaricate = result.getResultData().getResults();
        for (Timbratura timbratura : timbratureScaricate) {
            if (timbratura.getTipotext().equals("Clock-in")) {
                timbratura.setTipotext("Entrata");
            }
            if (timbratura.getTipotext().equals("Clock-out")) {
                timbratura.setTipotext("Uscita");
            }

            /*
            long epoch = Long.parseLong(timbratura.getLdate().substring(6, timbratura.getLdate().length()-2));

            Log.d(TAG,"converted: "+epoch+", ldate: "+timbratura.getLdate());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String timbDate = sdf.format(new Date(epoch));
            Log.d(TAG,timbDate);
            */

        }
        showTimbrature(timbratureScaricate);
        showProgress(false);
        refresh.setRefreshing(false);
        isDownloadingTimbrature = false;
        showTutorial();
    }


    private void manageServiceWithDateSuccess(ArrayList<Timbratura> result/*Response<JsonObject> result*/) {

        Log.d(TAG, "sono nella manageServiceWithDateSuccess()");
        //final Type type = new TypeToken<ArrayList<Timbratura>>() {
        //}.getType();
        //ArrayList<Timbratura> newTimbrature = (ArrayList<Timbratura>) NetworkManager.buildResult(result.getResult(), type);
        if (result.size() == 0) {
            eventPlanner.setVisibility(View.INVISIBLE);
            badgeImage.setVisibility(View.VISIBLE);
        } else {
            removeOldTimbratureForThisDate();
            for (Timbratura t : result) {
                if (t.getTipotext().equals("Clock-in")) {
                    t.setTipotext("Entrata");
                }
                if (t.getTipotext().equals("Clock-out")) {
                    t.setTipotext("Uscita");
                }
                timbratureScaricate.add(t);
            }
            showTimbrature(timbratureScaricate);
        }
        showProgress(false);
        refresh.setRefreshing(false);

    }

    private void manageServiceErrors(NetworkError e/*Exception e, Response<JsonObject> result*/) {

        eventPlanner.setVisibility(View.INVISIBLE);
        badgeImage.setVisibility(View.VISIBLE);
        bottomSheet.setVisibility(View.INVISIBLE);
        serviceMessage.setText("");
        showProgress(false);
        refresh.setRefreshing(false);

        if(e==NetworkErrorType.NO_INTERNET)
            onNoNetwork();
        else
            displayNetworkError(e);

        //showProgress(false);

        /*if (result != null && result.getResult() != null && !(result.getResult()).has("ERROR")) {
            Gson gson = new Gson();
            MainError err = gson.fromJson(result.getResult().toString(), MainError.class);
            if (err.error != null) {
                if (err.error.getCode().equals("DB/111")) {
                    logout();
                }
                showDialog("Attenzione", err.error.getMessage().getValue(), warnIcon);
            } else {
                showDialog("Attenzione", "Errore nello scaricamento della timbratura", warnIcon);
            }
        } else if ((result != null && result.getResult() != null && ((result.getResult())).has("ERROR")) || result == null) {
            showDialog("Attenzione", "Errore nello scaricamento della timbratura", warnIcon);
        }*/
        isDownloadingTimbrature = false;
    }

    private void showDialog(String title, String subtitle, Drawable icon) {
        showProgress(false);
        Utils.showStatusMessage(title, subtitle, grayColor, whiteColor, icon, lightGrayColor, getApplicationContext());
    }

    /* -------------------------- VECCHI SERVIZI --------------------------
    // GET TIMBRATURE
    private void getTimbratureFromServiceOld() {

        isDownloadingTimbrature = true;
        // Ignore cookies during connection id retrieval
        try {
            ODataHttpClient oDataHttpClient = new ODataHttpClient(getApplicationContext(), xsmpappcid,
                    userName, password);
            oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
                @Override
                public void onErrorCallback(Exception ex, Response response) {
                    // A network error happened. Use parameters "ex" and "response" to get more details
                    eventPlanner.setVisibility(View.INVISIBLE);
                    badgeImage.setVisibility(View.VISIBLE);
                    bottomSheet.setVisibility(View.INVISIBLE);
                    serviceMessage.setText("");
                    showProgress(false);
                    refresh.setRefreshing(false);

                    if (response != null && response.getResult() != null && !((JsonObject) (response.getResult())).has("ERROR")) {
                        Gson gson = new Gson();
                        MainError err = gson.fromJson(response.getResult().toString(), MainError.class);
                        if (err.error != null) {
                            Utils.showStatusMessage(err.error.getMessage().getValue(), getApplicationContext());
                        } else {
                            Utils.showStatusMessage("Errore nello scaricamento della timbratura", getApplicationContext());
                        }
                    } else if ((response != null && response.getResult() != null && ((JsonObject) (response.getResult())).has("ERROR")) || response == null) {
                        Utils.showStatusMessage("Errore nello scaricamento della timbratura", getApplicationContext());
                    }
                    // token scaduto
                    // else if (response != null && response.getHeaders() != null && response.getHeaders().code() == 401) {
                    //    clearDataIfExpired();
                    //    moveToLogin();
                    //}

                    isDownloadingTimbrature = false;
                    showTutorial();
                }

                @Override
                public void onFetchEntitySuccessCallback(Object result) {
                    // Callback invoked for single object retrieval
                    // Currently unused
                    showProgress(false);
                    refresh.setRefreshing(false);
                    isDownloadingTimbrature = false;
                    showTutorial();
                }

                @Override
                public void onFetchEntitySetSuccessCallback(List result) {
                    // Callback invoked for object collection retrieval
                    //System.out.println("Arrivo qui!");
                    timbratureScaricate = (ArrayList<Timbratura>) result;
                    showTimbrature(timbratureScaricate);
                    showProgress(false);
                    refresh.setRefreshing(false);
                    isDownloadingTimbrature = false;
                    showTutorial();
                }
            });

            // GSON will inject json to a collection of PojoClass instances
            Type type = new TypeToken<ArrayList<Timbratura>>() {
            }.getType();
            // If everything is ok you would get a List<PojoClass> in the delegate method
            // onFetchEntitySetSuccessCallback
            oDataHttpClient.fetchODataEntitySet(NetworkManager.timbratureGETService(getApplicationContext()), type);

        } catch (SmpExceptionInvalidInput ex) {
            // INVALID PARAMETERS ARE PROVIDED
            ex.printStackTrace();
            showProgress(false);
            isDownloadingTimbrature = false;
            showTutorial();
        }
    }

    // GET TIMBRATURE
    private void getTimbratureFromServiceWithDateOld(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
        String dateString = "";
        dateString = df.format(date);
        String filter = "";
        if (dateString.length() > 0) {
            filter = "Ldate eq datetime'" + dateString + "T00:00:00'";
        }

        // Ignore cookies during connection id retrieval
        try {
            ODataHttpClient oDataHttpClient = new ODataHttpClient(getApplicationContext(), xsmpappcid,
                    userName, password);
            oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
                @Override
                public void onErrorCallback(Exception ex, Response response) {
                    // A network error happened. Use parameters "ex" and "response" to get more details
                    eventPlanner.setVisibility(View.INVISIBLE);
                    badgeImage.setVisibility(View.VISIBLE);
                    bottomSheet.setVisibility(View.INVISIBLE);
                    serviceMessage.setText("");
                    showProgress(false);
                    refresh.setRefreshing(false);

                    if (response != null && response.getResult() != null && !((JsonObject) (response.getResult())).has("ERROR")) {
                        Gson gson = new Gson();
                        MainError err = gson.fromJson(response.getResult().toString(), MainError.class);
                        if (err != null && err.error != null) {
                            Utils.showStatusMessage(err.error.getMessage().getValue(), getApplicationContext());
                        } else {
                            Utils.showStatusMessage("Errore nello scaricamento della timbratura", getApplicationContext());
                        }
                    } else if ((response != null && response.getResult() != null && ((JsonObject) (response.getResult())).has("ERROR")) || response == null) {
                        Utils.showStatusMessage("Errore nello scaricamento della timbratura", getApplicationContext());
                    }
                    // token scaduto

                    //else if (response != null && response.getHeaders() != null && response.getHeaders().code() == 401) {
                    //    clearDataIfExpired();
                    //    moveToLogin();
                    //}

                }

                @Override
                public void onFetchEntitySuccessCallback(Object result) {
                    // Callback invoked for single object retrieval
                    // Currently unused
                    showProgress(false);
                    refresh.setRefreshing(false);
                }

                @Override
                public void onFetchEntitySetSuccessCallback(List result) {
                    // Callback invoked for object collection retrieval
                    //se ci sono timbrature le aggiungo in coda
                    if (result.size() == 0) {
                        eventPlanner.setVisibility(View.INVISIBLE);
                        badgeImage.setVisibility(View.VISIBLE);
                    } else {
                        removeOldTimbratureForThisDate();
                        for (Timbratura t : (ArrayList<Timbratura>) result) {
                            timbratureScaricate.add(t);
                        }
                        showTimbrature(timbratureScaricate);
                    }
                    showProgress(false);
                    refresh.setRefreshing(false);
                }
            });

            // GSON will inject json to a collection of PojoClass instances
            Type type = new TypeToken<ArrayList<Timbratura>>() {
            }.getType();
            // If everything is ok you would get a List<PojoClass> in the delegate method
            // onFetchEntitySetSuccessCallback
            String uri = Uri.parse(NetworkManager.timbratureGETService(getApplicationContext()))
                    .buildUpon()
                    .appendQueryParameter("$filter", filter)
                    .build().toString();
            oDataHttpClient.fetchODataEntitySet(uri, type);

        } catch (SmpExceptionInvalidInput ex) {
            // INVALID PARAMETERS ARE PROVIDED
            ex.printStackTrace();
            showProgress(false);
        }
    }
    -------------------------- VECCHI SERVIZI --------------------------*/

}
