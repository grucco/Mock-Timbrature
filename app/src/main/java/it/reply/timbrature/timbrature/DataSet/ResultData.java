package it.reply.timbrature.timbrature.DataSet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 //  ResultData
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class ResultData {

    @SerializedName("results")
    public ArrayList<Timbratura> results;

    public ArrayList<Timbratura> getResults() {
        return results;
    }

    public void setResults(ArrayList<Timbratura> results) {
        this.results = results;
    }

}
