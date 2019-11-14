package com.gae.scaffolder.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences; //

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Felipe Echanique on 08/06/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMPlugin";
    private final String PREFERENCE_FILE_KEY = "NativeStorage"; //
    private final String KEY_CONFIG = "alerta-config"; //
    SharedPreferences pref; //
    public static final String inputFormat = "HH:mm";

    private Date dateCompareOne;
    private Date dateCompareTwo;

    String pivot_name;
    String reason_id;
    String created;
    String createdUTC;

    private Map<String, Object> messageReceived = new HashMap<>();
    private String compareStringOne;
    private String compareStringTwo;

    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.GERMANY);

    Map<String, Object> data = new HashMap<>();

    public void onNewToken(String token) {

        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "\tNotification Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "\tNotification Message: " + remoteMessage.getNotification().getBody());
        }

        data.put("wasTapped", false);

        if (remoteMessage.getNotification() != null) {
            data.put("title", remoteMessage.getNotification().getTitle());
            data.put("body", remoteMessage.getNotification().getBody());
        }

        for (String key : remoteMessage.getData().keySet()) {
            Object value = remoteMessage.getData().get(key);
            Log.e(TAG, "\tKey: " + key + " Value: " + value);
            data.put(key, value);
        }

        this.messageReceived = data;

        sendNotification(this);
    }

    public void sendNotification(Context context) {

        pref = context.getSharedPreferences( //
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE); //

        // SharedPreferences.Editor editor = pref.edit();
        // editor.putString(KEY_CONFIG,
        // "[{\"id\":32,\"reasons\":[255],\"alarm\":true,\"enable\":true,\"start\":\"00:00\",\"end\":\"23:00\",\"user\":4,\"pivots\":[32,39]},{\"id\":2,\"reasons\":[243,246,247,255],\"alarm\":true,\"enable\":true,\"start\":\"00:00:00\",\"end\":\"23:00:00\",\"user\":4,\"pivots\":[40,39]},{\"id\":3,\"reasons\":[243,246,247],\"alarm\":true,\"enable\":true,\"start\":\"00:00:00\",\"end\":\"23:00:00\",\"user\":4,\"pivots\":[40,38,39]},{\"id\":4,\"reasons\":[242,244],\"alarm\":true,\"enable\":true,\"start\":\"00:00:00\",\"end\":\"23:00:00\",\"user\":4,\"pivots\":[44]}]");
        // editor.commit();

        if (pref.contains(KEY_CONFIG) && !pref.getString(KEY_CONFIG, "").equals("")) {

            String str = pref.getString(this.KEY_CONFIG, "");

            // se ele nao tiver fazendo o unescape sozinho ja é um problema
            // tem a questao das {sua string aqui} tambem que pode fazer funcionar

            Log.e("STRING DA CONFIG", str);
            str = str.replace("\\\"", "'");
            str = str.substring(1, str.length() - 1);
            Log.e("STRING DA EDITADA", str);

            try {

                JSONArray res = new JSONArray(str);

                if (res.length() == 0) {
                    return;
                }

                String length = "" + res.length();

                Log.e("LENGTH", length);

                String pivot_id = this.messageReceived.get("pivot_id").toString();
                this.reason_id = this.messageReceived.get("painel_stream_reason").toString();
                this.created = this.messageReceived.get("painel_stream_created").toString();
                this.pivot_name = this.messageReceived.get("pivot_name").toString();

                Log.e("PIVOTS / REASONS", pivot_id + " / " + this.reason_id);

                for (int i = 0; i < res.length(); i++) {

                    Log.e("LENGTH", length);
                    Object obj = res.get(i);

                    JSONObject json = new JSONObject(obj.toString());

                    Log.e("OBJ:", json.toString());

                    Log.e("PIVOTS", json.getString("pivots"));
                    Log.e("REASONS", json.getString("reasons"));

                    if (json.getString("pivots").contains(pivot_id)
                            && json.getString("reasons").contains(this.reason_id)) {

                        Log.e("STATUS DO IF", "TRUE");
                        Log.e("ENABLE ", json.getString("enable"));
                        if (json.getString("enable") == "true") {

                            this.compareStringOne = json.getString("start");
                            this.compareStringOne = this.compareStringOne.substring(0, this.compareStringOne.length() - 3);

                            this.compareStringTwo = json.getString("end");
                            this.compareStringTwo = this.compareStringTwo.substring(0, this.compareStringTwo.length() - 3);

                            if (canBeAlert(this.compareStringOne, this.compareStringTwo, this.created)) {
                                Log.e("ALARM", "ALARM");
                                alarm();
                            } else {
                                Log.e("FORA DO RANGE?", "FORA DO RANGE?");
                            }
                            break;
                        } else {
                            return;
                        }

                    }

                }

            } catch (Exception e) {
                Log.e("ERROR: ", "" + e);
            }

        }

    }

    private Boolean canBeAlert(String start, String end, String event) {

        // String start = "22:00";
        // String end = "03:00";
        // String event = "2019-11-13T01:01:12.651Z"; // utc
        Log.e("START ", start);
        Log.e("END ", end);

        DateTime eventTimeUTC = DateTime.parse(event);
        DateTime eventTimeLOCAL = new DateTime(eventTimeUTC, DateTimeZone.forID(TimeZone.getDefault().getID()));
        //DateTime eventTimeLOCAL = new DateTime(eventTimeUTC, TimeZone.getDefault().getID());

        // LocalDateTime eventTimeLOCAL =
        // LocalDateTime.ofInstant(eventTimeUTC.toInstant(), now.getOffset());
        Log.e("UTC EVENT", eventTimeUTC.toString());
        Log.e("LOCAL EVENT", eventTimeLOCAL.toString());
        this.createdUTC = eventTimeLOCAL.toString();

        System.err.println("Timezone");
        System.err.println(TimeZone.getDefault().getID());

        // extraindo as horas e minutos do start
        String[] start_array = start.split(":", 0);
        float start_hour = Integer.parseInt(start_array[0]) + Integer.parseInt(start_array[1]) / 60; // Transforma os
                                                                                                     // minutos em horas

        // extraindo as horas e minutos do end
        String[] end_array = end.split(":", 0);
        float end_hour = Integer.parseInt(end_array[0]) + Integer.parseInt(end_array[1]) / 60; // Transforma os minutos
                                                                                               // em horas

        Log.e("START HOUR", Float.toString(start_hour) );
        Log.e("END HOUR", Float.toString(end_hour));
        Log.e("CONTA PARA JOGAR OS MINUTOS NAS HORAS: ", Integer.toString((int) ((start_hour - (int) start_hour) * 60)));
        // extraindo a diferenca de horas entre os dois horarios
        float hours_diff = 0;
        if (start_hour < end_hour)
            hours_diff = end_hour - start_hour;
        else
            hours_diff = (24 - start_hour) + end_hour;

        // Encontra o datetime do start antes do shift
        DateTime todayStartZonedTime = DateTime.now(); // pega horario local
        todayStartZonedTime = todayStartZonedTime.withHourOfDay((int) start_hour); // substitui as horas
        todayStartZonedTime = todayStartZonedTime.withMinuteOfHour((int) ((start_hour - (int) start_hour) * 60)); // substitui
                                                                                                                  // os
                                                                                                                  // minutos
        DateTime yesterdayStartZonedTime = todayStartZonedTime.minusDays(1);

        // Encontra o datetime do end antes do shift e baseado na diferenca de data
        DateTime todayEndZonedTime = todayStartZonedTime; // pega horario local
        todayEndZonedTime = todayEndZonedTime.plusHours((int) hours_diff); // substitui as horas
        todayEndZonedTime = todayEndZonedTime.plusMinutes((int) ((hours_diff - (int) hours_diff) * 60)); // substitui os
                                                                                                         // minutos
        DateTime yesterdayEndZonedTime = todayEndZonedTime.minusDays(1);

        Log.e("Range Today ", todayStartZonedTime.toString() + " -> " + todayEndZonedTime.toString());
        Log.e("Range Yesterday: ", yesterdayStartZonedTime.toString() + " -> " + yesterdayEndZonedTime.toString());

        if ((eventTimeLOCAL.isAfter(todayStartZonedTime) && eventTimeLOCAL.isBefore(todayEndZonedTime))
                || (eventTimeLOCAL.isAfter(yesterdayStartZonedTime)
                        && eventTimeLOCAL.isBefore(yesterdayEndZonedTime))) {
            return true;
        } else {
            return false;
        }

    }

    public void alarm() {

        Log.e("ALARM", "ALARM");

        Intent alarmIntent = new Intent(this, AlertActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        alarmIntent.putExtra("pivot_name", this.pivot_name);
        alarmIntent.putExtra("reason_id", this.reason_id);
        alarmIntent.putExtra("created", this.createdUTC);

        this.startActivity(alarmIntent);

    }

}
