package com.areonedev.autotrack.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Notification;
import java.util.Date;

public class LeadInteractionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AccessLeads accessLeads = new AccessLeads();
        String incomingNumber = null;
        String type = "";

        // 1. Detect Incoming Call
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                type = "Call";
            }
        }

        // 2. Detect Incoming SMS
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                SmsMessage[] msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    incomingNumber = msgs[i].getOriginatingAddress();
                    type = "SMS";
                }
            }
        }

        // 3. Match with Lead and Save Notification
        if (incomingNumber != null) {
            // You need to implement this method in AccessLeads to search by phone
            Lead lead = accessLeads.getLeadByPhone(incomingNumber);

            if (lead != null) {
                String title = type + " from " + lead.getLeadFirstName() + " " + lead.getLeadLastName();
                Notification note = new Notification(title, new Date());

                // Save to your SQLite database
                accessLeads.saveNotification(note);
            }
        }
    }
}