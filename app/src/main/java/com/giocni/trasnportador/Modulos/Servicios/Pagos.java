package com.giocni.trasnportador.Modulos.Servicios;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.giocni.trasnportador.R;
import com.giocni.trasnportador.Servicios.webServicesPagos;
import com.giocni.trasnportador.Utiles.DatePicker;
import com.giocni.trasnportador.Utiles.General;
import com.giocni.trasnportador.Utiles.Modelos.Conductor;

import org.json.JSONArray;
import org.json.JSONObject;

public class Pagos extends AppCompatActivity {

    General gn;
    LinearLayout mRevealView;
    TextView lblConductor,lblPlaca;
    TextView lblServicio,lblEfectivo,lblDebito,lblCredito,lblTotal;
    EditText txtFechaInicial,txtFechaFinal;
    LinearLayout divInfoPagos,divCargando;
    ProgressBar pb;
    TextView lblInfo;

    DatePicker fechaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);
        initComponent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gn = new General(this,null);
        initFechas();
        cargarCartera();
        //datos COnductor
        gn.getDatosConductor(lblConductor,lblPlaca);
    }

    private void initComponent(){
        divInfoPagos = (LinearLayout) findViewById(R.id.divInfoPagos);
        divCargando = (LinearLayout) findViewById(R.id.divCargando);
        pb = (ProgressBar) findViewById(R.id.pb);
        lblInfo = (TextView) findViewById(R.id.lblInfo);
        lblConductor = (TextView) findViewById(R.id.lblConductor);
        lblPlaca = (TextView) findViewById(R.id.lblPlaca);
        mRevealView = (LinearLayout) findViewById(R.id.layout_menu_principal);
        mRevealView.setVisibility(View.INVISIBLE);
        txtFechaInicial = (EditText) findViewById(R.id.txtFechaInicial);
        txtFechaFinal = (EditText) findViewById(R.id.txtFechaFinal);
        lblServicio = (TextView) findViewById(R.id.lblServicio);
        lblEfectivo = (TextView) findViewById(R.id.lblEfectivo);
        lblDebito = (TextView) findViewById(R.id.lblDebito);
        lblCredito = (TextView) findViewById(R.id.lblCredito);
        lblTotal = (TextView) findViewById(R.id.lblTotal);

    }

    private void initFechas(){
        txtFechaInicial.setText(gn.fechaActual());
        txtFechaFinal.setText(gn.fechaActual());

        txtFechaInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFecha(view);
            }
        });
        txtFechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFecha(view);
            }
        });
        txtFechaInicial.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                cargarCartera();
            }
        });
        txtFechaFinal.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                cargarCartera();
            }
        });
    }

    public void cargarCartera(){
        divCargando.setVisibility(View.VISIBLE);
        divInfoPagos.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        lblInfo.setText("Cargando datos...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                }catch (Exception e){}
                Conductor con = gn.cargarConductor();
                final webServicesPagos sc = new webServicesPagos(Pagos.this);
                String fechaIni = gn.formatearFecha(txtFechaInicial.getText().toString(),"yyyy-MM-dd","dd/MM/yyyy");
                String fechaFin = gn.formatearFecha(txtFechaFinal.getText().toString(),"yyyy-MM-dd","dd/MM/yyyy");
                final JSONArray obj = sc.getCarteraByConductor(con.idConductor,fechaIni,fechaFin,con.token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(obj == null){
                                Toast.makeText(Pagos.this,sc.mensaje,Toast.LENGTH_LONG).show();
                            }else{
                                if(obj.length() == 0){
                                    divCargando.setVisibility(View.VISIBLE);
                                    divInfoPagos.setVisibility(View.GONE);
                                    pb.setVisibility(View.GONE);
                                    lblInfo.setText("No se encontraron resultados");
                                }else{
                                    JSONObject objCartera = obj.getJSONObject(0);
                                    lblServicio.setText(gn.formatearNumero(objCartera.getDouble("Cantidad")));
                                    lblEfectivo.setText(gn.formatearNumero(objCartera.getDouble("Efectivo")));
                                    lblCredito.setText(gn.formatearNumero(objCartera.getDouble("Credito")));
                                    lblDebito.setText(gn.formatearNumero(objCartera.getDouble("Debito")));
                                    lblTotal.setText(gn.formatearNumero(objCartera.getDouble("Total")));
                                    divCargando.setVisibility(View.GONE);
                                    divInfoPagos.setVisibility(View.VISIBLE);
                                }
                            }
                        }catch (Exception ex){}
                    }
                });
            }
        }).start();
    }

    private void mostrarFecha(View v){
        fechaDialog = new DatePicker();
        fechaDialog.txtFecha = (EditText) v;
        fechaDialog.setCancelable(false);
        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
        fechaDialog.show(fr, "Seleccione una fecha");
    }

    public void irMenu(View v){
        gn.menuPrincipal(v,mRevealView);
        gn.irMenuItem(v);
    }

    public void menuPrincipal(View v){
        gn.menuPrincipal(v,mRevealView);
    }

    public void buscarPagos(View v){

    }
}
