package it.reply.timbrature.timbrature.prototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.reply.timbrature.timbrature.Utils.Constants;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by g.rucco on 22/01/2018.
 */

public class PrototypingInterceptor implements Interceptor {

    private Context context;
    private SharedPreferences settings;
    private static final String TAG = "PrototypingInterceptor";


    public PrototypingInterceptor(Context context) {
        this.context = context;
    }


    public String readFromFile(String fileName) throws IOException {

        InputStream inputStream = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonString = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            jsonString.append(line);
        }

        //DEBUG
        //Log.d(TAG,"Timbrature ottenute con la GET: "+jsonString.toString());

        return jsonString.toString();

    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        final String url = chain.request().url().toString();
        switch (url) {

            case Constants.BASE_URL+Constants.MATRICOLA/*"verification_code"*//*Constants.VAL_CODE_ENDPOINT*/: {

                //IMPLEMENTARE IL CONTROLLO DEL VERIFICATION CODE !!!
                //BISOGNA VERIFICARE SE LA MATRICOLA INSERITA CORRISPONDE A QUELLA MEMORIZZATA

                final ResponseBody res_body = ResponseBody.create(MediaType.parse("application/json"), readFromFile(Constants.EMPL));
                final Response res = new Response
                        .Builder()
                        .body(res_body)
                        .request(chain.request()).message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .build();
                return res;
            }


            case Constants.BASE_URL+Constants.TIMBR_GET_ENDPOINT: {

                //NB: occorre specificare un altro file di preferenze per le timbrature
                //altrimenti queste verranno cancellate al momento del logout
                settings = context.getSharedPreferences("timbrSettings",0);

                Headers head = null;
                Headers.Builder headers = new Headers.Builder();


                //PER CANCELLARE CIO' CHE E' MEMORIZZATO NELLE SHAREDPREFERENCES
                /*
                SharedPreferences.Editor editor = settings.edit();
                editor.putStringSet("timbrature", null);
                editor.apply();
                */


                //provo ad aggiungere le timbrature ottenute con la POST tramite header HTTP
                Set<String> timbrature = settings.getStringSet("timbrature", null);
                if(timbrature != null) {

                    Iterator iterator = timbrature.iterator();
                    String timbratura = null;
                    while (iterator.hasNext()) {
                        timbratura = iterator.next().toString();
                        Log.d(TAG, "Timbratura_POST: "+timbratura);
                        headers.add(timbratura);
                    }
                    head = headers.build();
                }

                final ResponseBody res_body = ResponseBody.create(MediaType.parse("application/json"), readFromFile(Constants.TIMBRATURE_GET));
                Response res = null;
                if(head != null) {
                    /*final Response */res = new Response
                            .Builder()
                            .body(res_body)
                            .headers(head)
                            .request(chain.request()).message("OK")
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .build();
                } else {
                    /*final Response */res = new Response
                            .Builder()
                            .body(res_body)
                            .request(chain.request()).message("OK")
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .build();
                }

                return res;

            }


            case Constants.BASE_URL+Constants.TIMBR_POST_ENDPOINT: {

                String timbraturaSent =  requestBodyToString(chain.request().body());
                Log.d(TAG, "Timbratura ricevuta: "+timbraturaSent);

                settings = context.getSharedPreferences("timbrSettings",0);

                SharedPreferences.Editor editor = settings.edit();

                Set<String> timbrature = settings.getStringSet("timbrature", null);

                //NB: gli oggetti prelevati dalle SharedPreferences non possono essere modificati
                //pertanto occorre fare una copia dell'oggetto prelevato, modificarla
                //e sostituirla all'oggetto precedentemente memorizzato
                Set<String> timbsToSave = new HashSet<String>();
                if(timbrature != null) {
                    timbsToSave.addAll(timbrature);
                }

                timbsToSave.add(timbraturaSent);
                editor.putStringSet("timbrature", timbsToSave);


                editor.apply();
                Log.d(TAG, "Timbratura salvata con successo");


                final ResponseBody r = ResponseBody.create(MediaType.parse("application/json"), timbraturaSent/*readFromFile(Constants.TIMBRATURE_POST)*/);
                final Response res = new Response
                        .Builder()
                        .body(r)
                        .request(chain.request()).message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .build();
                return res;

            }

        }

        return chain.proceed(chain.request());

    }

    public static String requestBodyToString(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readUtf8();
    }
}
