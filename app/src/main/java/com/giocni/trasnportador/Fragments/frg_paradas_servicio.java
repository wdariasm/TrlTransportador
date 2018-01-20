package com.giocni.trasnportador.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;


import com.giocni.trasnportador.Modulos.Servicios.Historial;
import com.giocni.trasnportador.Modulos.Servicios.Principal;
import com.giocni.trasnportador.R;
import com.giocni.trasnportador.Servicios.webServicesServicios;
import com.giocni.trasnportador.Utiles.Adaptadores.AdaptadorListaHistorial;
import com.giocni.trasnportador.Utiles.Adaptadores.AdaptadorListaParadas;
import com.giocni.trasnportador.Utiles.Adaptadores.AdaptadorListaResponsables;
import com.giocni.trasnportador.Utiles.General;
import com.giocni.trasnportador.Utiles.Modelos.Conductor;
import com.giocni.trasnportador.Utiles.Modelos.ConfiguracionApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wdariasm on 15/01/2018.
 */

public class frg_paradas_servicio extends Fragment {

    Context context;
    View rootView;
    RelativeLayout divParada;
    LinearLayout divSelecccionar;

    Activity mActivity;
    public JSONObject objServicio = null;
    General gn;

    ArrayList<JSONObject> listaParadaData;
    ArrayList<JSONObject> listaResponsableData;
    ListView listaParada;
    ListView listaResponsables;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frg_paradas_servicio, container, false);
            initComponent();
            mActivity = getActivity();
            gn = new General(mActivity,null);


        }
        return rootView;
    }

    private void initComponent(){
        divParada = (RelativeLayout) rootView.findViewById(R.id.divParada);
        divSelecccionar = (LinearLayout) rootView.findViewById(R.id.divSelecccionar);
        listaParada = (ListView) rootView.findViewById(R.id.listaParadas);
        listaResponsables = (ListView) rootView.findViewById(R.id.listaResponsables);
    }


    public void servicioNoSeleccionado(){
        divParada.setVisibility(View.GONE);
        divSelecccionar.setVisibility(View.VISIBLE);
        objServicio = null;
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible && objServicio != null) {
            if(divParada.getVisibility() == View.GONE){
                divParada.setVisibility(View.VISIBLE);
                divSelecccionar.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void ActualizarDatos(){

        try {

            final JSONArray responsables = objServicio.getJSONArray("Contactos");
            listaResponsableData = new ArrayList<>();
            for(int i = 0; i < responsables.length(); i++){
                JSONObject objTemp = responsables.getJSONObject(i);
                listaResponsableData.add(objTemp);
            }
            AdaptadorListaResponsables ad2 = new AdaptadorListaResponsables(mActivity, listaResponsableData);
            listaResponsables.setAdapter(ad2);



            final JSONArray paradas = objServicio.getJSONArray("Paradas");
            listaParadaData = new ArrayList<>();
            for(int i = 0; i < paradas.length(); i++){
                JSONObject objTemp = paradas.getJSONObject(i);
                listaParadaData.add(objTemp);
            }
            AdaptadorListaParadas ad = new AdaptadorListaParadas(mActivity, listaParadaData);
            listaParada.setAdapter(ad);




        }catch (Exception ex){
            System.out.println("error -- " + ex.getMessage());
        }

    }
}
