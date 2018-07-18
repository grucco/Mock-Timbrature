package it.reply.timbrature.timbrature.SupportClasses;

import java.util.ArrayList;

import it.reply.timbrature.timbrature.DataSet.Timbratura;

/**
 //  Timbrature
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class Timbrature {

    private ArrayList<Timbratura> entrate;
    private ArrayList<Timbratura> uscite;

    public ArrayList<Timbratura> getEntrate() {
        return entrate;
    }

    public void setEntrate(ArrayList<Timbratura> entrate) {
        this.entrate = entrate;
    }

    public ArrayList<Timbratura> getUscite() {
        return uscite;
    }

    public void setUscite(ArrayList<Timbratura> uscite) {
        this.uscite = uscite;
    }

}
