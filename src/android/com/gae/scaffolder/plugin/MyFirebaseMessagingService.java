package com.gae.scaffolder.plugin;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.Map;
import android.os.Vibrator;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences; //

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by Felipe Echanique on 08/06/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMPlugin";
    private final String PREFERENCE_FILE_KEY = "NativeStorage"; //
    private final String KEY = "alerta-config"; //

    public static final String inputFormat = "HH:mm";

    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;

    private String compareStringOne;
    private String compareStringTwo;

    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat);


    SharedPreferences pref; //
    SharedPreferences.Editor editor; //
    Vibrator vibrator;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New token: " + token);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase
     *                      Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(this, "teste");
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    public void sendNotification(Context context, String messageBody) {

        pref = context.getSharedPreferences( //
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE); //
        //SharedPreferences.Editor editor = pref.edit();
        
        if (pref.contains(KEY)){

            String res = pref.getString(this.KEY, "");
            res = res.substring(1, res.length()-1);

            Log.e("RESULTADO INICIAL: ", res);

            if(res.contains("NA")){
                return ;
            } else {
                unserialize(res);
                compareDates();
            }

        }

        
    }

    private void compareDates(){
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        date = parseDate(hour + ":" + minute);

        Log.e("Horario agora", date.toString());

        dateCompareOne = parseDate(compareStringOne);
        dateCompareTwo = parseDate(compareStringTwo);

        Log.e("inicio2", dateCompareOne.toString());
        Log.e("fim2", dateCompareTwo.toString());


        if ( dateCompareOne.before( date ) && dateCompareTwo.after(date)) {
            Log.e("PASSOU PELA COMPARAÇÃO", "!!!");
            alarm();
        } else {
            Log.e("Fora do range", "!!");
        }
    }

    private Date parseDate(String date) {

        

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private void unserialize(String str){
        String[] parts = str.split("-");
        this.compareStringOne = parts[0];
        this.compareStringTwo = parts[1];

        Log.e("inicio", this.compareStringOne);
        Log.e("fim", this.compareStringTwo);
    }

    public void alarm() {

        Intent alarmIntent = new Intent(this, AlertActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(alarmIntent);
    }
}

