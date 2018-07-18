package it.reply.timbrature.timbrature.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.PopupView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.data.IPopup;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;

import it.reply.timbrature.timbrature.R;

/**
 //  CustomDecoration
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class CustomDecoration extends CdvDecorationDefault {

    public CustomDecoration(Context context) {
        super(context);
    }

    @Override
    public EventView getEventView(final IEvent event, Rect eventBound, int hourHeight,
                                  int seperateHeight) {
        final EventView eventView =
                super.getEventView(event, eventBound, hourHeight, seperateHeight);

        MyEvent ev = (MyEvent) event;

        // hide event name
        TextView textEventName = (TextView) eventView.findViewById(com.framgia.library.calendardayview.R.id.item_event_name);
        textEventName.setTextSize(ev.getTextSize());
        textEventName.setTextColor(ev.getTextColor());
        textEventName.setTypeface(null, Typeface.BOLD);
        textEventName.setVisibility(View.VISIBLE);

        RelativeLayout mEventHeader = (RelativeLayout) eventView.findViewById(com.framgia.library.calendardayview.R.id.item_event_header);

        // hide event header
        TextView textHeader1 = (TextView) eventView.findViewById(com.framgia.library.calendardayview.R.id.item_event_header_text1);
        TextView textHeader2 = (TextView) eventView.findViewById(com.framgia.library.calendardayview.R.id.item_event_header_text2);

        //textHeader1.setVisibility(View.GONE);
        textHeader2.setVisibility(View.GONE);

        textHeader1.setTextSize(12.0f);
        textHeader1.setGravity(Gravity.BOTTOM);
        mEventHeader.setGravity(Gravity.BOTTOM | Gravity.END);
        mEventHeader.setPadding(mEventHeader.getPaddingLeft(), mEventHeader.getPaddingTop(), mEventHeader.getPaddingRight() - 2, 2);
        textHeader1.setText(ev.getDescription());
        textHeader1.setTextColor(ev.getDescriptionColor());

        LinearLayout mEventContent = (LinearLayout) eventView.findViewById(com.framgia.library.calendardayview.R.id.item_event_content);
        mEventContent.setBackgroundColor(ev.getColor());

        mEventContent.setAlpha(ev.getAlpha());

        return eventView;
    }

    @Override
    public PopupView getPopupView(final IPopup popup, Rect eventBound, int hourHeight,
                                  int seperateH) {
        PopupView popupView = super.getPopupView(popup, eventBound, hourHeight, seperateH);
        // popupView.show();
        CardView cardView = (CardView) popupView.findViewById(com.framgia.library.calendardayview.R.id.cardview);
        TextView textQuote = (TextView) popupView.findViewById(com.framgia.library.calendardayview.R.id.quote);
        TextView textTitle = (TextView) popupView.findViewById(com.framgia.library.calendardayview.R.id.title);
        ImageView imvEnd = (ImageView) popupView.findViewById(com.framgia.library.calendardayview.R.id.imv_end);

        // do something with views

        return popupView;
    }
}