package it.reply.timbrature.timbrature.model;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.reply.timbrature.timbrature.DataSet.Employee;
import it.reply.timbrature.timbrature.DataSet.MainTimbratura;
import it.reply.timbrature.timbrature.DataSet.Timbratura;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by g.rucco on 19/01/2018.
 */

public interface TimbratureService {

    @GET("{verification_code}")
    Call<Employee> getVerificationCode(@Path("verification_code") String verification_code);

    @GET("timbratureCoordinateGET")
    Call<MainTimbratura> getTimbrature();

    //@GET("timbratureCoordinateGET")
    //Call<MainTimbratura> getTimbratureWithDate();

    @POST("timbratureCoordinatePOST")
    Call<Timbratura> sendTimbratura(@Body JsonObject timbratura);

}
