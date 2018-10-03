package com.walasys.conductor.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.walasys.conductor.Modulos.Servicios.Principal;
import com.walasys.conductor.R;
import com.walasys.conductor.Servicios.webServicesOtros;
import com.walasys.conductor.Utiles.General;
import com.walasys.conductor.Utiles.Modelos.ConfiguracionApp;
import com.walasys.conductor.Utiles.ServicioGPS;
import com.walasys.conductor.Utiles.WorkaroundMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class frg_ubicacion_servicio extends Fragment implements OnMapReadyCallback {

    public TextView lblEstado;
    RelativeLayout divEstados;
    TextView btnConfirmar,btnCancelar;

    View rootView;
    Context context;
    Activity mActivity;
    ConfiguracionApp configApp;
    General gn;
    List<LatLng> listaPos;
    ArrayList<Polyline> listaRuta;
    GoogleMap mMap;
    Marker markerMyPos = null;
    Marker markerOrigen = null;
    Marker markerDestino = null;
    public JSONObject objServicio = null;
    private static final String TAG = "Ubicación";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frg_ubicacion_servicio, container, false);
            initComponent();
            mActivity = getActivity();
            gn = new General(mActivity,null);
            Fabric.with(mActivity, new Crashlytics());
            //eventos
            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    configApp = gn.cargarConfiguracion();
                    if(configApp.cuadroConfirmacion){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Aviso");
                        alertDialog.setMessage("Desea realizar la acción?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                btnConfirmar();
                            }
                        });
                        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog al = alertDialog.create();
                        al.show();
                    }else{
                        btnConfirmar();
                    }
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    configApp = gn.cargarConfiguracion();
                    if(configApp.cuadroConfirmacion){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Aviso");
                        alertDialog.setMessage("Desea realizar la acción?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                btnCancelar();
                            }
                        });
                        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog al = alertDialog.create();
                        al.show();
                    }else{
                        btnCancelar();
                    }
                }
            });
        }
        return rootView;
    }

    private void btnConfirmar(){
        try{
            String estadoActual = objServicio.getString("Estado");
            if(estadoActual.compareTo("ASIGNADO") == 0){
                ((Principal) mActivity).confirmarServicio(objServicio,2);
            }else{
                ((Principal) mActivity).cambiarEstadoServicio(estadoActual,objServicio.getString("IdServicio"),2);
            }
        }catch (Exception ex){}
    }

    private void btnCancelar(){
        try{
            String estadoActual = objServicio.getString("Estado");
            ((Principal) mActivity).PregCancelarServicio(estadoActual,objServicio.getString("IdServicio"),objServicio.getString("ClienteId"),2);
        }catch (Exception ex){}
    }

    public void servicioNoSeleccionado(){
        divEstados.setVisibility(View.GONE);
        markerOrigen.remove();
        markerDestino.remove();
        for(int i = 0; i < listaRuta.size(); i++){
            listaRuta.get(i).remove();
        }
        mostrarUbicacion();
        moverCamara(mMap, new LatLng(ServicioGPS.location.latitude, ServicioGPS.location.longitude), 14.5f);
        objServicio = null;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && objServicio != null) {
            try {
                divEstados.setVisibility(View.VISIBLE);
                getJsonRutas();
                lblEstado.setText(objServicio.getString("Estado"));

                gn.determinarBotonActualizar(objServicio.getString("Estado"),btnConfirmar,btnCancelar);
            }catch(Exception ex){
                Crashlytics.log(2, TAG, "Error obtener ubicación del servicio");
                Crashlytics.logException(ex);
            }
        }
    }

    public void actualizarBtn(){
        try {
            gn.determinarBotonActualizar(objServicio.getString("Estado"), btnConfirmar, btnCancelar);
        }catch (Exception e){}
    }

    private void initComponent(){
        try {
            divEstados = (RelativeLayout) rootView.findViewById(R.id.divEstados);
            lblEstado = (TextView) rootView.findViewById(R.id.lblEstado);
            btnConfirmar = (TextView) rootView.findViewById(R.id.btnConfirmar);
            btnCancelar = (TextView) rootView.findViewById(R.id.btnCancelar);

            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    //mScrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
        }catch (Exception e){
            System.out.println("error polylineas: "+e.getMessage());
            Crashlytics.log(2, TAG, "InitComponent Ubicación servicio");
            Crashlytics.logException(e);
        }
    }

    private void getJsonRutas()
    {
        try {
            webServicesOtros ruta = new webServicesOtros(mActivity);

            String posOri = objServicio.getString("LatOrigen") + "," + objServicio.getString("LngOrigen");
            String posDes = objServicio.getString("LatDestino") + "," + objServicio.getString("LngDestino");
            JSONObject jsonRutas = ruta.getRuta(posOri,posDes);
            JSONArray routesArray = null;
            routesArray = jsonRutas.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            JSONObject poly = route.getJSONObject("overview_polyline");
            String polyline = poly.getString("points");
            listaPos = gn.decodePoly(polyline);
            trazarRuta();
        } catch (JSONException e) {
            Crashlytics.log(2, TAG, "Error obtener ubicación del servicio");
            Crashlytics.logException(e);
            System.out.println("error polylineas: "+e.getMessage());
        }

    }

    public void trazarRuta()
    {
        listaRuta = new ArrayList<Polyline>();
        for (int i = 0; i < listaPos.size() - 1; i++) {
            LatLng src = listaPos.get(i);
            LatLng dest = listaPos.get(i + 1);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),new LatLng(dest.latitude,dest.longitude))
                    .width(10).color(Color.parseColor("#73B9FF")).geodesic(true));
            listaRuta.add(line);
        }
        try {
            LatLngBounds.Builder bounds;
            bounds = new LatLngBounds.Builder();
            LatLng posOri = new LatLng(objServicio.getDouble("LatOrigen"), objServicio.getDouble("LngOrigen"));
            LatLng posDes = new LatLng(objServicio.getDouble("LatDestino"), objServicio.getDouble("LngDestino"));
            marcarOrigenDestino(posOri,posDes);
            bounds.include(posOri);
            bounds.include(posDes);
            //bounds.include(ServicioGPS.location);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
        }catch (Exception ex){
            Crashlytics.log(2, TAG, "Trazar Ruta");
            Crashlytics.logException(ex);
        }
    }

    private void marcarOrigenDestino(LatLng posOri,LatLng porDes){
        if(markerOrigen != null)
            markerOrigen.remove();
        markerOrigen = mMap.addMarker(new MarkerOptions()
                .position(posOri)
                .title("Origen")
                .icon(getMarkerIcon("#83C743")));

        if(markerDestino != null)
            markerDestino.remove();
        markerDestino = mMap.addMarker(new MarkerOptions()
                .position(porDes)
                .title("Destino")
                .icon(getMarkerIcon("#FB7064")));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //mMap.setMyLocationEnabled(true);
        //hilo para ubicar
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(ServicioGPS.location == null) {
                        Thread.sleep(1000);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarUbicacion();
                            if(objServicio == null) {
                                moverCamara(mMap, new LatLng(ServicioGPS.location.latitude, ServicioGPS.location.longitude), 14.5f);
                            }else{
                                getJsonRutas();
                            }
                        }
                    });
                }catch (Exception e){
                    Crashlytics.log(2, TAG, "onMapReady");
                    Crashlytics.logException(e);
                }
            }
        }).start();
    }

    public void mostrarUbicacion()
    {
        if(markerMyPos != null)
            markerMyPos.remove();
        markerMyPos = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(ServicioGPS.location.latitude, ServicioGPS.location.longitude))
                .title("Tu posición")
                .icon(getMarkerIcon("#73B9FF")));
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.yo)));
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void moverCamara(GoogleMap m,LatLng pos,float zoom)
    {
        m.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
