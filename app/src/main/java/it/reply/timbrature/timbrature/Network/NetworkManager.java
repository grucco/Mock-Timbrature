package it.reply.timbrature.timbrature.Network;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import it.reply.timbrature.timbrature.Utils.Config;
import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;

/**
 //  NetworkManager
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class NetworkManager {

    // LOGIN STATUS CODES
    public final static int UNDEFINED_ERROR = 0;
    public final static int LOGIN_ERROR = 1;
    public final static int REGISTRATION_ERROR = 2;
    public final static int NETWORK_ERROR = 1;
    public final static int CONNECTION_SUCCESS = 1;
    ///////////////////////////////////////////////

    public static String timbratureService(Context ctx){
        return Config.baseURL(ctx) + "/sap/opu/odata/sap/Z_SWFM_GATEWAY_ESS_SRV_01/TimbratureCoordinateSet";
    }

    public static String xCSRFTokenService(Context ctx){
        return Config.baseURL(ctx) + "/sap/opu/odata/sap/Z_SWFM_GATEWAY_ESS_SRV_01/";
    }

    public static String tokenService(Context ctx){
        return Config.baseURL(ctx) + "/sap/opu/odata/sap/Z_SWFM_GATEWAY_ESS_SRV_01/TimbratureTokenSet";
    }

    public static String validationCodeService(Context ctx){
        return Config.baseURL(ctx) + "/sap/opu/odata/sap/Z_SWFM_GATEWAY_ESS_SRV_01/TimbratureVerCodeSet";
    }

    public static List buildResult(JsonObject raw, Type type) {
        if(raw != null) {
            JsonArray jsonArray = raw.get("d").getAsJsonObject().get("results").getAsJsonArray();
            return new Gson().fromJson(jsonArray, type);
        }
        else return null;
    }

    public static TypeHttpProtocol getURLProtocol(String url) throws Exception {

        TypeHttpProtocol result = null;

        try {
            URL u = new URL(url);
            if (u.getProtocol().equalsIgnoreCase(TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP.getValue())) {
                result = TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP;
            }

            if (u.getProtocol().equalsIgnoreCase(TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS.getValue())) {
                result = TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS;
            }
        } catch (MalformedURLException ex) {
            throw new Exception("ERROR_TYPE_MALFORMED_SERVICE_URL");
        }

        return result;
    }

}
