package it.reply.timbrature.timbrature.model;


import it.reply.timbrature.timbrature.ErrorSet.NetworkError;

/**
 * Created by g.rucco on 22/01/2018
 */

public interface NetworkCallBack<T> {
    void onSuccess(T response);
    void onError(Throwable t);
    void onErrorResponse(NetworkError networkError);
}
