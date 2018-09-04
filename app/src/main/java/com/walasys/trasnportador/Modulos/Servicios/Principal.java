package com.walasys.trasnportador.Modulos.Servicios;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.walasys.trasnportador.Fragments.frg_detalle_servicio;
import com.walasys.trasnportador.Fragments.frg_mis_servicios;
import com.walasys.trasnportador.Fragments.frg_paradas_servicio;
import com.walasys.trasnportador.Fragments.frg_ubicacion_servicio;
import com.walasys.trasnportador.R;
import com.walasys.trasnportador.Servicios.webServicesServicios;
import com.walasys.trasnportador.Utiles.Adaptadores.AdaptadorListaParadas;
import com.walasys.trasnportador.Utiles.General;
import com.walasys.trasnportador.Utiles.Modelos.Conductor;
import com.walasys.trasnportador.Utiles.Notificaciones.initNotificacion;
import com.walasys.trasnportador.Utiles.ServicioGPS;
import com.walasys.trasnportador.Utiles.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Principal extends AppCompatActivity{

    General gn;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayout mRevealView;
    TextView lblConductor,lblPlaca;
    boolean banMenu = true;
    BroadcastReceiver receptorNotificacion;
    LocalBroadcastManager mLocalBroadcastManager;

    //fragmentos
    frg_mis_servicios frgMisServicios;
    frg_detalle_servicio frgDetalleServicio;
    frg_ubicacion_servicio frgUbicacionServicio;
    frg_paradas_servicio frgParadasServicio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        initComponent();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gn = new General(this,null);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        receptorNotificacion = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("nuevoServicio")) {
                    try{
                        frgMisServicios.cargarServicios();
                    }catch (Exception ex){}
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("nuevoServicio");
        mLocalBroadcastManager.registerReceiver(receptorNotificacion, filter);

        //CONFIG TABS
        configViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //setIconTabs();

        //initPUSH
        new initNotificacion(this).initPush();

        //datos COnductor
        gn.getDatosConductor(lblConductor,lblPlaca);

        General.mActivity = this;
    }

    public void irMenu(View v){
        gn.menuPrincipal(v,mRevealView);
        gn.irMenuItem(v);
    }

    public void menuPrincipal(View v){
        gn.menuPrincipal(v,mRevealView);
    }

    protected void onResume(){
        super.onResume();
        //INIT SERVCICIO GPS
        if(gn.validarGPSActivo()){
            Intent i = new Intent(getBaseContext(), ServicioGPS.class);
            startService(i);
        }
    }

    private void initComponent(){
        lblConductor = (TextView) findViewById(R.id.lblConductor);
        lblPlaca = (TextView) findViewById(R.id.lblPlaca);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mRevealView = (LinearLayout) findViewById(R.id.layout_menu_principal);
        mRevealView.setVisibility(View.INVISIBLE);
    }

    public void irDetalleServicio(JSONObject obj){
        frgDetalleServicio.objServicio = obj;
        frgUbicacionServicio.objServicio = obj;
        frgParadasServicio.objServicio = obj;
        try {
            cargarHistorial(null, obj.getString("IdServicio"));
        }catch(Exception ec){}

        viewPager.setCurrentItem(1);
    }


    //CONFIGURACION DE LOS TAB
    private void setIconTabs(){
        tabLayout.getTabAt(0).setIcon(gn.getDrawable(this,R.drawable.servicio_ser));
        tabLayout.getTabAt(1).setIcon(gn.getDrawable(this,R.drawable.detalle_ser));
        tabLayout.getTabAt(2).setIcon(gn.getDrawable(this,R.drawable.detalle_ser));
        tabLayout.getTabAt(3).setIcon(gn.getDrawable(this,R.drawable.ubicacion_ser));
    }

    private void configViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        frgMisServicios = new frg_mis_servicios();
        frgDetalleServicio = new frg_detalle_servicio();
        frgUbicacionServicio = new frg_ubicacion_servicio();
        frgParadasServicio = new frg_paradas_servicio();
        adapter.addFragment(frgMisServicios, "Servicios");
        adapter.addFragment(frgDetalleServicio, "Detalle");
        adapter.addFragment(frgParadasServicio, "Notas");
        adapter.addFragment(frgUbicacionServicio, "Ubicación");
        viewPager.setAdapter(adapter);

    }

    public void actualiazrTab(int tab,JSONObject obj){
        try {
            if(obj == null){
                frgDetalleServicio.servicioNoSeleccionado();
                frgParadasServicio.servicioNoSeleccionado();
                frgUbicacionServicio.servicioNoSeleccionado();
            }

            frgDetalleServicio.objServicio = obj;
            frgUbicacionServicio.objServicio = obj;
            frgParadasServicio.objServicio = obj;
            frgDetalleServicio.actualizarBtn();
            frgUbicacionServicio.actualizarBtn();

            if(obj != null){
                frgDetalleServicio.lblEstado.setText(obj.getString("Estado"));
                frgUbicacionServicio.lblEstado.setText(obj.getString("Estado"));
            }

        }catch(Exception ec){}
    }

    public void PregCancelarServicio(String estado,final String id,final String idCliente,final int tab){
        if(estado.compareTo("ASIGNADO") == 0){
            //RECHAZAR
            try{
                final Conductor con = gn.cargarConductor();
                gn.initCargando("Rechazando...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final webServicesServicios sc = new webServicesServicios(Principal.this);
                        final JSONObject obj = sc.deleteServicio(id, con.token);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    gn.finishCargando();
                                    if (obj == null) {
                                        Toast.makeText(Principal.this, sc.mensaje, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Principal.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                        //actualizar datos
                                        frgMisServicios.actualzarDatosIraTab(id,tab);
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        });
                    }
                }).start();
            }catch (Exception ec){}
        }else{
            //CACELAR
            mostrarMotivosCancelar(id,tab,idCliente);
        }
    }

    public void cargarHistorial(View v, final String idServicio){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Conductor con = gn.cargarConductor();
                final webServicesServicios sc = new webServicesServicios(Principal.this);

                final JSONObject obj = sc.getServicioById(idServicio,con.token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            if(obj == null){
                                Toast.makeText(Principal.this,sc.mensaje,Toast.LENGTH_LONG).show();
                            }else{
                                frgParadasServicio.objServicio = obj;
                                frgParadasServicio.ActualizarDatos();
                            }
                        }catch (Exception ex){
                            System.out.println("error -- " + ex.getMessage());
                        }
                    }
                });
            }
        }).start();
    }

    private void mostrarMotivosCancelar(final String idServicio,final int tab,final String idCliente){
        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        dialog1.setContentView(R.layout.content_motivos_cancelar);

        final RadioGroup radioGroup = (RadioGroup) dialog1.findViewById(R.id.radioGroup);
        final LinearLayout divBotones = (LinearLayout) dialog1.findViewById(R.id.divBotones);
        Button btnCancelar = (Button) dialog1.findViewById(R.id.btnCancelar);
        Button btnConfirmar = (Button) dialog1.findViewById(R.id.btnConfirmar);
        final ProgressBar pb = (ProgressBar) dialog1.findViewById(R.id.pb);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Conductor con = gn.cargarConductor();
                final webServicesServicios sc = new webServicesServicios(Principal.this);
                final JSONArray obj = sc.getMotivosCancelar("CONDUCTOR",con.token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            pb.setVisibility(View.GONE);
                            if(obj != null){
                                RadioButton btn;
                                if(obj.length() > 0) {
                                    for (int i = 0; i < obj.length(); i++) {
                                        btn = new RadioButton(Principal.this);
                                        btn.setText(obj.getJSONObject(i).getString("mtDescripcion"));
                                        btn.setTag(obj.getJSONObject(i).getString("IdMotivo"));
                                        radioGroup.addView(btn);
                                    }
                                    divBotones.setVisibility(View.VISIBLE);
                                }
                            }
                        }catch (Exception ex){}
                    }
                });
            }
        }).start();

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton btn = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                if(btn == null)
                    Toast.makeText(Principal.this,"Debe seleccionar un motivo",Toast.LENGTH_LONG).show();
                else{
                    dialog1.dismiss();
                    cancelarServicio(tab,idServicio,idCliente,btn.getTag().toString());
                }
            }
        });

        dialog1.show();
    }

    private void cancelarServicio(final int tab,final String idServicio,String idCliente,String idMotivo){
        try{
            final JSONObject objSend = new JSONObject();
            final Conductor con = gn.cargarConductor();
            objSend.put("Cliente",idCliente);
            objSend.put("Motivo",idMotivo);
            objSend.put("Conductor",con.idConductor);
            objSend.put("Estado","CANCELADO");
            gn.initCargando("Cancelando...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesServicios sc = new webServicesServicios(Principal.this);
                    final JSONObject obj = sc.cancelarServicio(objSend, idServicio, con.token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                gn.finishCargando();
                                if (obj == null) {
                                    Toast.makeText(Principal.this, sc.mensaje, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Principal.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                    //actualizar datos
                                    frgMisServicios.actualzarDatosIraTab(idServicio,tab);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    });
                }
            }).start();
        }catch (Exception ec){}
    }

    public void confirmarServicio(JSONObject objServicio,final int tab){
        try {
            final Conductor con = gn.cargarConductor();
            final String idServicio = objServicio.getString("IdServicio");
            final JSONObject objUpdate = new JSONObject();
            objUpdate.put("Estado", "CONFIRMADO");
            objUpdate.put("Email", "NO");
            objUpdate.put("ClienteId", objServicio.getString("ClienteId"));
            objUpdate.put("Responsable", objServicio.getString("Responsable"));
            objUpdate.put("Conductor", con.idConductor);
            gn.initCargando("Confirmando...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesServicios sc = new webServicesServicios(Principal.this);
                    final JSONObject obj = sc.putServicioConfirmar(objUpdate, idServicio, con.token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                gn.finishCargando();
                                if (obj == null) {
                                    Toast.makeText(Principal.this, sc.mensaje, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Principal.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                    //actualizar datos
                                    //actualizar datos
                                    frgMisServicios.actualzarDatosIraTab(idServicio,tab);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    });
                }
            }).start();
        }catch(Exception ex){}
    }

    public void cambiarEstadoServicio(String estadoActual,final String idServicio,final int tab){
        try{
            final String nuevoEstado = gn.determinarEstadoServicio(estadoActual);
            final Conductor con = gn.cargarConductor();
            gn.initCargando("Confirmando...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesServicios sc = new webServicesServicios(Principal.this);
                    final JSONObject obj = sc.putServicioEstado(nuevoEstado, idServicio, con.token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                gn.finishCargando();
                                if (obj == null) {
                                    Toast.makeText(Principal.this, sc.mensaje, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Principal.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                    //actualizar datos
                                    frgMisServicios.actualzarDatosIraTab(idServicio,tab);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    });
                }
            }).start();
        }catch (Exception ec){}
    }
}
