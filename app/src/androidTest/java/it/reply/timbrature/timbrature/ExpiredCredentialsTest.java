package it.reply.timbrature.timbrature;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.koushikdutta.ion.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import it.reply.timbrature.timbrature.DataSet.Timbratura;
import stacksmashers.smp30connectionlib.delegate.ODataHttpClientCallback;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.smp.ODataHttpClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by Y2J on 09/05/17.
 */

@RunWith(JUnit4.class)
public class ExpiredCredentialsTest {

    private int SERVER_PORT = 5555;
    private AsyncHttpServer httpServer;
    private String baseUrl = "http://localhost:" + SERVER_PORT;

    private String xsmpappcid = "xxxx-xxxx-xxxx-xxxx";
    private String username = "username";
    private String password = "password";

    private Type type;


    @Before
    public void setUp() {
        // Setup server
        httpServer = new AsyncHttpServer();
        httpServer.listen(SERVER_PORT);
    }

    private ODataHttpClient getTestClient() throws SmpExceptionInvalidInput {
        ODataHttpClient oDataHttpClient = new ODataHttpClient(getContext(), xsmpappcid, username, password);
        type = new TypeToken<ArrayList<Timbratura>>() {
        }.getType();
        return oDataHttpClient;
    }

    @After
    public void tearDown() throws Exception {
        httpServer.stop();
        AsyncServer.getDefault().stop();
    }

    @Test
    public void fetchODataEntitySetError() throws Exception {
        // Setup mock server
        httpServer.get("/test", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                response.code(401);
                response.send("error");
            }
        });

        // Setup test client
        final Semaphore semaphore = new Semaphore(0);
        ODataHttpClient oDataHttpClient = getTestClient();
        oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
            @Override
            public void onErrorCallback(Exception ex, Response response) {
                if (response != null && response.getHeaders().code() == 401){
                    System.out.println("I did it!!!");
                    assertTrue(true);
                }
                else{
                    fail();
                }
            }

            @Override
            public void onFetchEntitySuccessCallback(Object result) {
                fail();
            }

            @Override
            public void onFetchEntitySetSuccessCallback(List result) {
                fail();
            }
        });

        oDataHttpClient.fetchODataEntitySet(baseUrl + "/test", type);
        semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS);
    }


    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

}

