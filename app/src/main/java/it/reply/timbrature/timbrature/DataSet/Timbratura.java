package it.reply.timbrature.timbrature.DataSet;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.reply.timbrature.timbrature.Utils.Utils;

/**
 //  Timbratura
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class Timbratura {

    @SerializedName("__metadata")
    private Metadata metadata;

    @SerializedName("Pdsnr")
    private String pdsnr;

    @SerializedName("PdcUsrup")
    private String pdcUsrup;

    @SerializedName("Uname")
    private String uname;

    @SerializedName("Nome")
    private String nome;

    @SerializedName("Cognome")
    private String cognome;

    @SerializedName("Societa")
    private String societa;

    @SerializedName("Ldate")
    private String ldate;

    @SerializedName("Ltime")
    private String ltime;

    @SerializedName("Satza")
    private String satza;

    @SerializedName("Tipotext")
    private String tipotext;

    @SerializedName("Zausw")
    private String zausw;

    @SerializedName("Latitudine")
    private String latitudine;

    @SerializedName("Longitudine")
    private String longitudine;

    @SerializedName("Via")
    private String via;

    @SerializedName("Link")
    private String link;

    public Timbratura(String pdsnr, String pdcUsrup, String uname, String nome, String cognome,
                      String societa, String ldate, String ltime, String satza, String tipotext,
                      String zausw, String latitudine, String longitudine, String via, String link) {
        this.pdsnr = pdsnr;
        this.pdcUsrup = pdcUsrup;
        this.uname = uname;
        this.nome = nome;
        this.cognome = cognome;
        this.societa = societa;
        this.ldate = ldate;
        this.ltime = ltime;
        this.satza = satza;
        this.tipotext = tipotext;
        this.zausw = zausw;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.via = via;
        this.link = link;
        this.metadata = null;
    }

    public Timbratura(Metadata m, String pdsnr, String pdcUsrup, String uname, String nome, String cognome,
                      String societa, String ldate, String ltime, String satza, String tipotext,
                      String zausw, String latitudine, String longitudine, String via, String link) {
        this.pdsnr = pdsnr;
        this.pdcUsrup = pdcUsrup;
        this.uname = uname;
        this.nome = nome;
        this.cognome = cognome;
        this.societa = societa;
        this.ldate = ldate;
        this.ltime = ltime;
        this.satza = satza;
        this.tipotext = tipotext;
        this.zausw = zausw;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.via = via;
        this.link = link;
        this.metadata = m;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Timbratura() { super(); }

    public String getPdsnr() {
        return pdsnr;
    }

    public void setPdsnr(String pdsnr) {
        this.pdsnr = pdsnr;
    }

    public String getPdcUsrup() {
        return pdcUsrup;
    }

    public void setPdcUsrup(String pdcUsrup) {
        this.pdcUsrup = pdcUsrup;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getSocieta() {
        return societa;
    }

    public void setSocieta(String societa) {
        this.societa = societa;
    }

    public String getLdate() {
        return ldate;
    }

    public void setLdate(String ldate) {
        this.ldate = ldate;
    }

    public String getLtime() {
        return ltime;
    }

    public void setLtime(String ltime) {
        this.ltime = ltime;
    }

    public String getSatza() {
        return satza;
    }

    public void setSatza(String satza) {
        this.satza = satza;
    }

    public String getTipotext() {
        return tipotext;
    }

    public void setTipotext(String tipotext) {
        this.tipotext = tipotext;
    }

    public String getZausw() {
        return zausw;
    }

    public void setZausw(String zausw) {
        this.zausw = zausw;
    }

    public String getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(String latitudine) {
        this.latitudine = latitudine;
    }

    public String getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(String longitudine) {
        this.longitudine = longitudine;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getDateTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
        try {
            return timeFormat.parse(Utils.formatLtime(ltime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Timbratura{" +
                "Pdsnr='" + pdsnr + '\'' +
                ", Uname='" + uname + '\'' +
                ", Nome='" + nome + '\'' +
                ", Cognome='" + cognome + '\'' +
                ", Societa='" + societa + '\'' +
                ", Ldate='" + ldate + '\'' +
                ", Ltime='" + ltime + '\'' +
                ", Satza='" + satza + '\'' +
                ", Tipotext='" + tipotext + '\'' +
                ", Zausw='" + zausw + '\'' +
                ", Latitudine='" + latitudine + '\'' +
                ", Longitudine='" + longitudine + '\'' +
                ", Via='" + via + '\'' +
                ", Link='" + link + '\'' +
                ", PdcUsrup='" + pdcUsrup + '\'' +
                '}';
    }

}
