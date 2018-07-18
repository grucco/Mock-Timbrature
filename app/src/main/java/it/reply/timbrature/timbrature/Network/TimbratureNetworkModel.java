package it.reply.timbrature.timbrature.Network;


import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it.reply.timbrature.timbrature.DataSet.Employee;
import it.reply.timbrature.timbrature.DataSet.MainTimbratura;
import it.reply.timbrature.timbrature.DataSet.Timbratura;
import it.reply.timbrature.timbrature.ErrorSet.ErrorParser;
import it.reply.timbrature.timbrature.ErrorSet.NetworkErrorType;
import it.reply.timbrature.timbrature.model.NetworkCallBack;
import it.reply.timbrature.timbrature.model.TimbratureService;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by g.rucco on 19/01/2018.
 */

public class TimbratureNetworkModel {

    private TimbratureService timbratureService;

    public TimbratureNetworkModel(TimbratureService timbService) {
        this.timbratureService = timbService;
    }

    public TimbratureNetworkModel() {
        super();
    }

    public void getVerificationCode(final String mat, final NetworkCallBack networkCallBack) {

        timbratureService.getVerificationCode(mat).enqueue(new Callback<Employee>() {

            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {

                if (response.isSuccessful()) {
                    Log.d("getVerificationCode()","sono nella onResponse() con successo");
                    Employee verCode = response.body();
                    networkCallBack.onSuccess(verCode);
                } else {
                    Log.d("getVerificationCode()","sono nella onResponse() con errore");
                    final NetworkErrorType networkError = ErrorParser.getErrorType(response);
                    networkCallBack.onErrorResponse(networkError);
                }

            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.d("getVerificationCode()","sono nella onFailure()");
                networkCallBack.onError(t);
            }

        });

    }


    public void getTimbrature(final NetworkCallBack networkCallback) {

        timbratureService.getTimbrature().enqueue(new Callback<MainTimbratura>() {

            @Override
            public void onResponse(Call<MainTimbratura> call, Response<MainTimbratura> response) {

                if(response.isSuccessful()) {
                    Log.d("getTimbrature()","sono nella onResponse() con successo");
                    MainTimbratura listaTimbrature = response.body();

                    //aggiungo qui le timbrature che immetto con la POST
                    Headers headers = response.headers();
                    ArrayList<String> timbrs = new ArrayList<>();
                    if(headers != null) {
                        String timbratura = null;
                        for(int i=0; i<headers.size(); i++) {
                            //la reale timbratura si ottiene da tale composizione
                            timbratura = headers.name(i)+":"+headers.value(i);
                            Log.d("TimbratureNetworkModel", "Timbratura "+(i+1)+": "+timbratura);
                            timbrs.add(timbratura);
                            listaTimbrature.getResultData().getResults().add(jsonString2Timbratura(timbratura));
                        }
                    }

                    networkCallback.onSuccess(listaTimbrature);
                } else {
                    Log.d("getTimbrature()","sono nella onResponse() con errore");
                    final NetworkErrorType networkError = ErrorParser.getErrorType(response);
                    networkCallback.onErrorResponse(networkError);
                }

            }

            @Override
            public void onFailure(Call<MainTimbratura> call, Throwable t) {
                Log.d("getTimbrature()","sono nella onFailure()");
                networkCallback.onError(t);
            }

        });

    }

    public void sendTimbratura(JsonObject t, final NetworkCallBack net) {

        timbratureService.sendTimbratura(t).enqueue(new Callback<Timbratura>() {
            @Override
            public void onResponse(Call<Timbratura> call, Response<Timbratura> response) {

                if(response.isSuccessful()) {
                    Log.d("sendTimbratura()", "sono nella onResponse() con successo");
                    Timbratura timbratura = response.body();
                    //Log.d("sendTimbratura()", "timbratura inviata: "+timbratura.toString());
                    net.onSuccess(timbratura);
                } else {
                    Log.d("sendTimbratura()","sono nella onResponse() con errore");
                    final NetworkErrorType networkError = ErrorParser.getErrorType(response);
                    net.onErrorResponse(networkError);
                }
            }

            @Override
            public void onFailure(Call<Timbratura> call, Throwable t) {
                Log.d("sendTimbratura()","sono nella onFailure()");
                net.onError(t);
            }
        });
    }


    //@g.rucco
    //function that builds a Timbratura object from a JSON string
    public Timbratura jsonString2Timbratura(String timbString) {

        Timbratura t = new Timbratura();

        //field counter (used in switch-case)
        int count=0;

        //field value
        String value = null;

        //parto dall'indice delle doppie apici che chiudono la stringa "Satza"
        int i=7;

        int j=0;

        //fintantochè non arriviamo all'ultimo campo della trimbratura
        while(count != 15) {

            //caso valore di un campo
            if(timbString.charAt(i)=='"' && timbString.charAt(i+1)==':' && timbString.charAt(i+2)=='"') {

                //allora il carattere in posizione (i+3) è il primo carattere del valore di un campo
                j=i+3;

                StringBuffer val = new StringBuffer();
                if(count==14) {
                    //la stringa-timbratura termina con "}
                    while(!(timbString.charAt(j)=='"' && timbString.charAt(j+1)=='}')) {
                        val.append(timbString.charAt(j));
                        j++;
                    }

                } else {
                    while(!(timbString.charAt(j)=='"' && timbString.charAt(j+1)==',' && timbString.charAt(j+2)=='"')) {
                        val.append(timbString.charAt(j));
                        j++;
                    }
                }

                value = val.toString();
                i=j+3;

                switch(count) {

                    case 0:
                        t.setSatza(value);
                        break;

                    case 1:
                        t.setLatitudine(value);
                        break;

                    case 2:
                        t.setLongitudine(value);
                        break;

                    case 3:
                        t.setVia(value);
                        break;

                    case 4:
                        t.setNome(value);
                        break;

                    case 5:
                        t.setCognome(value);
                        break;

                    case 6:
                        t.setUname(value);
                        break;

                    case 7:
                        t.setTipotext(value);
                        break;

                    case 8:
                        t.setSocieta(value);
                        break;

                    case 9:
                        t.setZausw(value);
                        break;

                    case 10:
                        t.setPdsnr(value);
                        break;

                    case 11:
                        t.setLink(value);
                        break;

                    case 12:
                        t.setPdcUsrup(value);
                        break;

                    case 13:
                        t.setLdate(value);
                        break;

                    case 14:
                        t.setLtime(value);
                        break;

                }

                count++;

            } else {
                //altrimenti abbiamo beccato il nome di un campo (che non ci interessa)
                i++;
            }

        }

        return t;

    }

}
