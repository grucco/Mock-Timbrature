package it.reply.timbrature.timbrature.common;

import android.content.Context;


import java.io.IOException;

import it.reply.timbrature.timbrature.ErrorSet.UserOfflineException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by vinayaprasadn on 22/4/17.
 */

public class ErrorHandlingInterceptor implements Interceptor {

    private Context context;

    public ErrorHandlingInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(!Utils.isUserOnline(context))
            throw new UserOfflineException();
         return chain.proceed(chain.request());
    }

}
