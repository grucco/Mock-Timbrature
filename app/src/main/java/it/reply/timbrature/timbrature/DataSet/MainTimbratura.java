package it.reply.timbrature.timbrature.DataSet;

import com.google.gson.annotations.SerializedName;

/**
 //  MainTimbratura
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class MainTimbratura {

    @SerializedName("d")
    public ResultData d;

    public ResultData getResultData() {
        return d;
    }

    public void setResultData(ResultData d) {
        this.d = d;
    }

}
