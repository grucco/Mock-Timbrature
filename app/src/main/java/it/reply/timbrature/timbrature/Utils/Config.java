package it.reply.timbrature.timbrature.Utils;

import android.content.Context;

import it.reply.timbrature.timbrature.BuildConfig;
import it.reply.timbrature.timbrature.R;

/**
 //  Config
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class Config {

    public static String baseURL(Context ctx){
        return ctx.getResources().getString(R.string.service_root) + ":" + serverPort(ctx);
    }

    public static boolean isMock(){
        return BuildConfig.BUILD_TYPE.equals("mock");
    }

    public static String appID(Context ctx){
        return ctx.getResources().getString(R.string.appid);
    }

    public static String serverPort(Context ctx){
        return ctx.getResources().getString(R.string.server_port);
    }

    public static String technicalUsername(Context ctx){
        return ctx.getResources().getString(R.string.technical_username);
    }

    public static String technicalPassword(Context ctx){
        return ctx.getResources().getString(R.string.technical_password);
    }

    public static boolean isTesting(Context ctx){
        if(!BuildConfig.BUILD_TYPE.equals("release")){
            return true;
        }
        else{
            return false;
        }
    }

    public static String latitude(Context ctx){
        return ctx.getResources().getString(R.string.fake_latitude);
    }

    public static String longitude(Context ctx){
        return ctx.getResources().getString(R.string.fake_longitude);
    }

    public static String address(Context ctx){
        return ctx.getResources().getString(R.string.fake_address);
    }
}
