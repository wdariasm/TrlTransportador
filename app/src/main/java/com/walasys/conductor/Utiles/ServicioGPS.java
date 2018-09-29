package com.walasys.conductor.Utiles;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import com.walasys.conductor.Servicios.webServicesConductor;
import com.walasys.conductor.Utiles.Modelos.Conductor;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;


public class ServicioGPS extends Service implements LocationListener {

    LocationManager locationManager;
    String provider;
    long MINUTOS = 60;
    long MINUTOS_GPS = 20000;
    boolean ban = true;
    Thread t_gps;
    boolean banThread = true;
    int TIEMPO_ESPERA = 25;
    boolean isGPSEnabled;

    int TIEMPOBUSQUEDA = 5000;
    int DISTANCIA = 0;

    static public LatLng location;
    static public boolean banServicio = false;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        t_gps = new Thread(new Runnable() {
            @Override
            public void run() {
                while (banThread) {
                    try {
                        Thread.sleep(MINUTOS_GPS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ban = true;
                }
            }
        });
        t_gps.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Servicio Finalizado");
        banServicio = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        banThread = false;
        try {
            t_gps = null;
        } catch (Exception e) {
            System.out.println("Error thread gps");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Servicio iniciado");
        banServicio = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return Service.START_NOT_STICKY;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPOBUSQUEDA, DISTANCIA, this);
                initHilo();
            } else {
                //mostarAlertDeConfiguracion();
                onDestroy();
            }
        }
        return Service.START_NOT_STICKY;
    }


    private void initHilo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TIEMPO_ESPERA * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if (location == null) {
                    //stopUsingGPS();
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager = null;
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        locationManager.getBestProvider(criteria, false);

                        if (ActivityCompat.checkSelfPermission(ServicioGPS.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ServicioGPS.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIEMPOBUSQUEDA, DISTANCIA, ServicioGPS.this);
                        System.out.println("POR RED -> -> ->");
                    }
                }
            }
        }).start();
    }

    private String getIMEI(){
        TelephonyManager mngr = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            this.location = new LatLng(location.getLatitude(),location.getLongitude());
            System.out.println("SERVICIO: "+location.toString());
            if(ban) {
                webServicesConductor s = new webServicesConductor(getApplicationContext());
                String token = General.getToken(getApplicationContext());
                s.putPosicion(getIMEI(),String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),token);
                ban = false;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
