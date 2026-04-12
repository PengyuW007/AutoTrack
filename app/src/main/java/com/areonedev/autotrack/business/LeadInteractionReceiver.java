package com.areonedev.autotrack.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Notification;

import java.util.Date;

public class LeadInteractionReceiver extends BroadcastReceiver {
    private static final String TAG = "LeadReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = null;
        String interactionType = "";

        // 1. Handle Incoming Call
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                interactionType = "Call";
            }
        }

        // 2. Handle Incoming SMS
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    incomingNumber = sms.getOriginatingAddress();
                    interactionType = "SMS";
                }
            }
        }

        // 3. Process the interaction if a number was captured
        if (incomingNumber != null) {
            processInteraction(incomingNumber, interactionType);
        }
    }

    private void processInteraction(String phoneNumber, String type) {
        AccessLeads accessLeads = new AccessLeads();
        // Use your existing method to find the lead
        Lead lead = accessLeads.getLeadByPhone(phoneNumber);

        if (lead != null) {
            // Create a descriptive title: "Call from John Doe"
            String title = type + " from " + lead.getLeadFirstName() + " " + lead.getLeadLastName();
            Notification notification = new Notification(title, new Date());

            // Save to database via the new AccessNotifications class
            AccessNotifications accessNotifications = new AccessNotifications();
            accessNotifications.insertNotification(notification);

            Log.d(TAG, "Logged " + type + " for lead: " + lead.getLeadFirstName());
        } else {
            Log.d(TAG, "Interaction from unknown number: " + phoneNumber);
        }
    }
}