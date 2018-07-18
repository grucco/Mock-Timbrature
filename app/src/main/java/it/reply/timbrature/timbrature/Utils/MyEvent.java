package it.reply.timbrature.timbrature.Utils;

/**
//  MyEvent
//  Timbrature
//
//  Created by Raffaele Forgione @ Syskoplan Reply.
//  Copyright © 2017 Acea. All rights reserved.
*/

import android.content.res.ColorStateList;

import com.framgia.library.calendardayview.data.IEvent;
import java.util.Calendar;

public class MyEvent implements IEvent {

    private long mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private int mColor;
    private float alpha;
    private float textSize;
    private ColorStateList textColor;
    private String description;
    private ColorStateList descriptionColor;
    private String latitudine;
    private String longitudine;
    private String indirizzo;
    private String link;

    public MyEvent(long mId, Calendar mStartTime, Calendar mEndTime, String mName, int mColor, float alpha, float textSize, ColorStateList textColor) {
        this.mId = mId;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mName = mName;
        this.mColor = mColor;
        this.alpha = alpha;
        this.textSize = textSize;
        this.textColor = textColor;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public ColorStateList getTextColor(){
        return textColor;
    }

    public void setTextColor(ColorStateList textColor) {
        this.textColor = textColor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description + "  ";
    }

    public ColorStateList getDescriptionColor() {
        return descriptionColor;
    }

    public void setDescriptionColor(ColorStateList descriptionColor) {
        this.descriptionColor = descriptionColor;
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

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
