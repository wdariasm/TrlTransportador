package com.walasys.conductor.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walasys.conductor.Modulos.Servicios.Principal;
import com.walasys.conductor.R;
import com.walasys.conductor.Servicios.webServicesServicios;
import com.walasys.conductor.Utiles.General;
import com.walasys.conductor.Utiles.Modelos.Conductor;
import com.walasys.conductor.Utiles.Modelos.ConfiguracionApp;

import org.json.JSONObject;

public class frg_detalle_servicio extends Fragment {

    TextView lblServicio,lblContrato,lblFecha,lblHora,lblResponsable,lblTelefono;
    TextView lblDireccionOrigen,lblDireccionDestino,lblPasajeros,lblPrecio,lblFormaPago;
    public TextView lblEstado;
    TextView btnConfirmar,btnCancelar;

    View rootView;
    RelativeLayout divInfo;
    LinearLayout divSelecccionar;

    Context context;
    Activity mActivity;
    public JSONObject objServicio = null;
    ConfiguracionApp configApp;
    General gn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.frg_detalle_servicio, container, false);
            initComponent();
            mActivity = getActivity();
            gn = new General(mActivity,null);
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
                ((Principal) mActivity).confirmarServicio(objServicio,1);
            }else{
                ((Principal) mActivity).cambiarEstadoServicio(estadoActual,objServicio.getString("IdServicio"),1);
            }
        }catch (Exception ex){}
    }

    private void btnCancelar(){
        try{
            String estadoActual = objServicio.getString("Estado");
            ((Principal) mActivity).PregCancelarServicio(estadoActual,objServicio.getString("IdServicio"),objServicio.getString("ClienteId"),1);
        }catch (Exception ex){}
    }

    private void initComponent(){
        divInfo = (RelativeLayout) rootView.findViewById(R.id.divInfo);
        divSelecccionar = (LinearLayout) rootView.findViewById(R.id.divSelecccionar);
        lblServicio = (TextView) rootView.findViewById(R.id.lblServicio);
        lblContrato = (TextView) rootView.findViewById(R.id.lblContrato);
        lblFecha = (TextView) rootView.findViewById(R.id.lblFecha);
        lblHora = (TextView) rootView.findViewById(R.id.lblHora);
        lblResponsable = (TextView) rootView.findViewById(R.id.lblResponsable);
        lblTelefono = (TextView) rootView.findViewById(R.id.lblTelefono);
        lblDireccionOrigen = (TextView) rootView.findViewById(R.id.lblDireccionOrigen);
        lblDireccionDestino = (TextView) rootView.findViewById(R.id.lblDireccionDestino);
        lblPasajeros = (TextView) rootView.findViewById(R.id.lblPasajeros);
        lblPrecio = (TextView) rootView.findViewById(R.id.lblPrecio);
        lblFormaPago = (TextView) rootView.findViewById(R.id.lblFormaPago);
        lblEstado = (TextView) rootView.findViewById(R.id.lblEstado);

        btnConfirmar = (TextView) rootView.findViewById(R.id.btnConfirmar);
        btnCancelar = (TextView) rootView.findViewById(R.id.btnCancelar);
    }

    public void servicioNoSeleccionado(){
        divInfo.setVisibility(View.GONE);
        divSelecccionar.setVisibility(View.VISIBLE);
        objServicio = null;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && objServicio != null) {
            if(divInfo.getVisibility() == View.GONE){
                divInfo.setVisibility(View.VISIBLE);
                divSelecccionar.setVisibility(View.GONE);
            }
            //CARGAR DATOS
            try {
                lblServicio.setText(objServicio.getString("IdServicio"));
                lblContrato.setText(objServicio.getString("ContratoId"));
                lblFecha.setText(objServicio.getString("FechaServicio"));
                lblHora.setText(gn.formatearHora(objServicio.getString("FechaServicio")+" "+objServicio.getString("Hora")));
                lblResponsable.setText(objServicio.getString("Responsable"));
                lblTelefono.setText(objServicio.getString("Telefono"));
                lblDireccionOrigen.setText(objServicio.getString("DireccionOrigen"));
                lblDireccionDestino.setText(objServicio.getString("DireccionDestino"));
                lblPasajeros.setText(objServicio.getString("NumPasajeros"));
                lblPrecio.setText(gn.formatearNumero(objServicio.getDouble("ValorTotal")));
                lblFormaPago.setText(objServicio.getString("FormaPago"));
                lblEstado.setText(objServicio.getString("Estado"));

                gn.determinarBotonActualizar(objServicio.getString("Estado"),btnConfirmar,btnCancelar);
            }catch(Exception ex){}
        }
    }

    public void actualizarBtn(){
        try {
            gn.determinarBotonActualizar(objServicio.getString("Estado"), btnConfirmar, btnCancelar);
        }catch (Exception e){}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
