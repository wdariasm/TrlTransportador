package com.walasys.trasnportador.Utiles.Notificaciones;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.walasys.trasnportador.Modulos.Inicial.Login;
import com.walasys.trasnportador.Modulos.Servicios.Principal;
import com.walasys.trasnportador.R;
import com.walasys.trasnportador.Utiles.General;
import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * Created by giocni on 1/2/15.
 */
public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static MediaPlayer player;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    LocalBroadcastManager broadcaster;
    public static final String TAG = "GCMNotificationIntentService";

    public boolean actualizarActivity(String filtro) {
        Intent broadcastIntent = new Intent(filtro);
        //broadcastIntent.putExtra("mensaje",mensaje);
        return broadcaster.sendBroadcast(broadcastIntent);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            try {
                System.out.println("Completed work @ " + SystemClock.elapsedRealtime());
                sendNotification(extras);
                System.out.println("Received: " + extras.toString());
            }catch(Exception e){
                System.out.println("EXCEPTION NOTIFICACION " + e.getMessage());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void sendNotification(Bundle msg) {
        System.out.println("Preparing to send notification...: " + msg);

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, Principal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int ban = Integer.parseInt(msg.getString("std"));
        try{
            switch (ban){
                case 1:
                    intent = new Intent(this, Principal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    actualizarActivity("nuevoServicio");
                    break;
                case 1000:
                    Intent i = new Intent(this,Login.class);
                    i.putExtra("ban",true);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    //General gn = new General(General.mActivity,null);
                    //gn.cerrarSesion();
                    break;
            }
        }catch(Exception ex){
            System.out.println("ERROR NOTIIIII " + ex.getMessage());
        }

        if(ban != 1000){
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentTitle(msg.getString("title"))
                    .setContentText(msg.getString("msg"))
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentIntent(pIntent);
            //.setSmallIcon(R.drawable.ti)
            Notification notification = builder.getNotification();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;

            mNotificationManager.notify(0, notification);
            System.out.println("Notification sent successfully.");
        }
    }

}
