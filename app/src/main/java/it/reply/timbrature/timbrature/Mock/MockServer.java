package it.reply.timbrature.timbrature.Mock;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonElement;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import it.reply.timbrature.timbrature.Network.NetworkManager;
import it.reply.timbrature.timbrature.Utils.Config;
import stacksmashers.smp30connectionlib.utils.AssetsUtils;

/**
 //  MockServer
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class MockServer {

    private static MockServer server;
    private Context ctx;
    private int SERVER_PORT;
    private String appID;
    private AsyncHttpServer httpServer;
    private String root = "/sap/opu/odata/sap/Z_SWFM_GATEWAY_ESS_SRV_01/";

    private Type type;

    public MockServer(Context ctx) throws Exception {
        // Setup server
        this.ctx = ctx;
        appID = Config.appID(ctx);
        SERVER_PORT = Integer.valueOf(Config.serverPort(ctx));
        start();
    }

    public static MockServer getInstance(Context ctx) throws Exception {
        if(server == null) {
            server = new MockServer(ctx);
        }
        return server;
    }

    private void start() throws Exception {
        httpServer = new AsyncHttpServer();
        httpServer.listen(SERVER_PORT);
        configure();
    }

    private void configure() throws Exception {
        // services - response configuration
        //configureLogin(200);
        configureTokenService();
        configureTimbratureService();
        //configureXCSRFTokenService();
    }

    private void configureTokenService(){

        String suffix = "qualcosa_token";

        try {
            suffix = suffix + URLEncoder.encode("('000000000')", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String url = Uri.parse(root + suffix).buildUpon().build().toString();


        httpServer.get(url, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                try {
                    response.code(200);

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });
    }

    private void configureTimbratureService(){

        String url = root + "TimbratureCoordinateSet";


        httpServer.get(url + "?$filter", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                try {
                    response.code(200);

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });

        httpServer.get(url, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                try {
                    InputStream fis = ctx.getResources().getAssets().open("TimbratureCoordinate(GET).json");
                    JsonElement el = AssetsUtils.loadJSONFromAsset(fis);
                    response.code(200);
                    result = el.toString();

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });

        httpServer.post(url, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                try {
                    // credenziali scadute
                    response.code(401);

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });

    }

    /*
    private void configureLogin(final int responseCode) throws Exception {
        httpServer.get("/odata/applications/latest/" + appID, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                response.code(responseCode);
                response.send("response");
            }
        });

        httpServer.post("/odata/applications/latest/" + appID + "/Connections", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Headers headers = response.getHeaders();
                headers.set("Set-Cookie", "X-SMP-APPCID=" + Config.xSMPAppCId(ctx) + ";");
                response.code(200);
                response.send("ok");
            }
        });
    }
    */

    /*
    private void configureXCSRFTokenService(){
        httpServer.get("/" + Config.appID(ctx) + "/?$format=json", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                try {
                    response.code(401);

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });
    }
    */

}
