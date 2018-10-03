package com.walasys.conductor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.walasys.conductor.Modulos.Servicios.Principal;
import com.walasys.conductor.R;
import com.walasys.conductor.Servicios.webServicesConductor;
import com.walasys.conductor.Utiles.Adaptadores.AdaptadorListaMisServicios;
import com.walasys.conductor.Utiles.General;
import com.walasys.conductor.Utiles.Modelos.Conductor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class frg_mis_servicios extends Fragment {

    View rootView;
    Context context;
    Activity mActivity;
    ArrayList<JSONObject> listaServiciosData;
    ListView listaServicios;
    General gn;

    private static final String TAG = "Mis servicios";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frg_mis_servicios, container, false);
            mActivity = getActivity();
            gn = new General(mActivity,null);
            Fabric.with(mActivity, new Crashlytics());
            initComponent();

            //lista
            listaServicios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ((Principal) mActivity).irDetalleServicio(listaServiciosData.get(i));
                }
            });
        }
        return rootView;
    }

    private void initComponent(){
        listaServicios = (ListView) rootView.findViewById(R.id.listaServicios);
    }

    public void onResume(){
        super.onResume();
        cargarServicios();
    }

    public void actualzarDatosIraTab(final String id,final int tab){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Conductor con = gn.cargarConductor();
                final webServicesConductor sc = new webServicesConductor(mActivity);
                final JSONArray obj = sc.getServiciosByConductor(con.idConductor,"ACTIVO",con.token);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(obj == null){
                                Toast.makeText(context,sc.mensaje,Toast.LENGTH_LONG).show();
                            }else{
                                listaServiciosData = new ArrayList();
                                JSONObject objBuscado = null;
                                for(int i = 0; i < obj.length(); i++){
                                    listaServiciosData.add(obj.getJSONObject(i));
                                    if(obj.getJSONObject(i).getString("IdServicio").compareTo(id) == 0){
                                        objBuscado = obj.getJSONObject(i);
                                    }
                                }
                                AdaptadorListaMisServicios ad = new AdaptadorListaMisServicios(mActivity,listaServiciosData);
                                listaServicios.setAdapter(ad);

                                ((Principal) mActivity).actualiazrTab(tab,objBuscado);

                            }
                        }catch (Exception ex){}
                    }
                });
            }
        }).start();
    }

    public void cargarServicios(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Conductor con = gn.cargarConductor();
                final webServicesConductor sc = new webServicesConductor(mActivity);
                final JSONArray obj = sc.getServiciosByConductor(con.idConductor,"ACTIVO",con.token);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(obj == null){
                                Toast.makeText(context,sc.mensaje,Toast.LENGTH_LONG).show();
                            }else{
                                listaServiciosData = new ArrayList();
                                for(int i = 0; i < obj.length(); i++){
                                    listaServiciosData.add(obj.getJSONObject(i));
                                }
                                AdaptadorListaMisServicios ad = new AdaptadorListaMisServicios(mActivity,listaServiciosData);
                                listaServicios.setAdapter(ad);
                            }
                        }catch (Exception ex){
                            Crashlytics.log(1, TAG, "Error cargar servcios");
                            Crashlytics.logException(ex);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
