package com.walasys.conductor.Utiles.Notificaciones;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.walasys.conductor.Servicios.webServicesConductor;
import com.walasys.conductor.Utiles.General;
import com.walasys.conductor.Utiles.Modelos.Conductor;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.io.IOException;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService  {
    private static final String TAG = "MyFirebaseIIDService";
    Activity context;
    General gn;

    public  MyFirebaseInstanceIDService(Activity context){
        this.context = context;
        gn = new General(context,null);
    }


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {



                    Conductor con = gn.cargarConductor();
                    webServicesConductor us = new webServicesConductor(context);
                    JSONObject obj = us.putIdPush(gn.getIMEI(),token,con.token);

                    if (obj != null){
                        System.out.println("RESPUESTA PUSH --- " + obj.toString());
                    }

                    msg = "Device registered, registration ID=" + token;


                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.i("RegisterActivity", "Error: " + msg);
                }
                Log.i("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }
        }.execute(null, null, null);
    }
}
