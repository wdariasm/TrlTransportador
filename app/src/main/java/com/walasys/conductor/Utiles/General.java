package com.walasys.conductor.Utiles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walasys.conductor.Modulos.Configuracion.Configuracion;
import com.walasys.conductor.Modulos.Inicial.Login;
import com.walasys.conductor.Modulos.Servicios.Historial;
import com.walasys.conductor.Modulos.Servicios.Pagos;
import com.walasys.conductor.Modulos.Servicios.Principal;
import com.walasys.conductor.R;
import com.walasys.conductor.Servicios.webServicesConductor;
import com.walasys.conductor.Servicios.webServicesLogin;
import com.walasys.conductor.Utiles.Modelos.Conductor;
import com.walasys.conductor.Utiles.Modelos.ConfiguracionApp;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Gilmar Ocampo Nieves on 24/11/2016.
 */

public class General {

    public static Activity mActivity;

    private Activity context;
    private ProgressDialog dialogCargando;
    AlertDialog al;
    boolean banMenu = true;

    public General(Activity context,View v){
        this.context = context;
        if(v != null){

        }
    }

    public void menuPrincipal(View view,final View mRevealView){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = (view.getTop());
            int startradius=0;
            int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
            Animator animator = ViewAnimationUtils.createCircularReveal(mRevealView,cx, cy, startradius, endradius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(300);
            int reverse_startradius = Math.max(mRevealView.getWidth(),mRevealView.getHeight());
            int reverse_endradius=0;
            Animator animate = ViewAnimationUtils.createCircularReveal(mRevealView,cx,cy,reverse_startradius,reverse_endradius);
            if(banMenu){
                mRevealView.setVisibility(View.VISIBLE);
                animator.start();
                banMenu = false;
            }else{
                mRevealView.setVisibility(View.VISIBLE);
                // to hide layout on animation end
                animate.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRevealView.setVisibility(View.INVISIBLE);
                        banMenu = true;
                    }
                });
                animate.start();
            }
        }else{
            final Dialog dialog1 = new Dialog(context);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
            dialog1.setContentView(R.layout.layout_menu_principal);

            LinearLayout div_servicios = (LinearLayout) dialog1.findViewById(R.id.div_servicios);
            LinearLayout div_historial = (LinearLayout) dialog1.findViewById(R.id.div_historial);
            LinearLayout div_pagos = (LinearLayout) dialog1.findViewById(R.id.div_pagos);
            LinearLayout div_ajustes = (LinearLayout) dialog1.findViewById(R.id.div_ajustes);
            LinearLayout div_salir = (LinearLayout) dialog1.findViewById(R.id.div_salir);
            TextView lblConductor = (TextView) dialog1.findViewById(R.id.lblConductor);
            TextView lblPlaca = (TextView) dialog1.findViewById(R.id.lblPlaca);

            Conductor con = cargarConductor();
            lblConductor.setText(con.Nombre);
            lblPlaca.setText(con.CdPlaca);

            div_servicios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irMenuItem(view);
                    dialog1.dismiss();
                }
            });
            div_historial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irMenuItem(view);
                    dialog1.dismiss();
                }
            });
            div_pagos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irMenuItem(view);
                    dialog1.dismiss();
                }
            });
            div_ajustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irMenuItem(view);
                    dialog1.dismiss();
                }
            });
            div_salir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irMenuItem(view);
                    dialog1.dismiss();
                }
            });
            dialog1.show();
        }
    }

    public void irMenuItem(View v){
        Intent i;
        switch (v.getId()){
            case R.id.div_servicios:
                i = new Intent(context,Principal.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
                break;

            case R.id.div_historial:
                i = new Intent(context,Historial.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
                break;

            case R.id.div_ajustes:
                i = new Intent(context,Configuracion.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
                break;

            case R.id.div_pagos:
                i = new Intent(context,Pagos.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
                break;

            case R.id.div_salir:
                ConfiguracionApp configApp = cargarConfiguracion();
                if(configApp.cuadroConfirmacion){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Aviso");
                    alertDialog.setMessage("Desea realizar la acción?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            cerrarSesion();
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
                    cerrarSesion();
                }

                break;
        }
    }

    public void cerrarSesion(){
        initCargando("Cerrando sesión...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final webServicesLogin sc = new webServicesLogin(context);
                Conductor con = cargarConductor();
                final JSONObject j = sc.cerrarSesion(con.idConductor,con.token);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            finishCargando();
                            quitarCuenta();
                            Intent i = new Intent(context,Login.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);

                            try {
                                Intent in = new Intent(context,ServicioGPS.class);
                                context.stopService(in);
                            }catch(Exception ex){}
                        }catch(Exception ex){}
                    }
                });
            }
        }).start();
    }

    public void mostrarDialog(String titulo,String mensaje,boolean cancelable){
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(titulo);
        b.setMessage(mensaje);
        b.setCancelable(cancelable);
        b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog al = b.create();
        al.show();
    }

    public void initCargando(String mensaje){
        dialogCargando = new ProgressDialog(context);
        dialogCargando.setMessage(mensaje);
        dialogCargando.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogCargando.setCancelable(true);
        dialogCargando.show();
    }

    public void finishCargando(){
        dialogCargando.dismiss();
    }


    public boolean validarGPSActivo(){
        boolean ban = true;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        if (provider != null && !provider.equals("")) {
            Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {
                mostarAlertDeConfiguracion();
                ban = false;
            }else
                ban = true;
        }else
            ban = false;
        return ban;
    }

    public List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void mostarAlertDeConfiguracion() {
        if(al == null){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Configuración del GPS");
            alertDialog.setMessage("Debes activar el GPS para continuar.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            al = alertDialog.create();
            al.show();
        }
    }

    public void actualizarServidor(String servidor){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("rutaServidor", servidor);
        editor.commit();
    }

    public String cargarServidor(){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        //return prefs.getString("rutaServidor", "http://192.168.0.22/public/api");
        return prefs.getString("rutaServidor", "http://app.trl.com.co/public/api");
    }

    public static String cargarServidor(Context context){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        //return prefs.getString("rutaServidor", "http://192.168.0.22/public/api");
        return prefs.getString("rutaServidor", "http://app.trl.com.co/public/api");
    }

    public void guardarConfiguracion(ConfiguracionApp con){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("cfg_cuadroConfirm", con.cuadroConfirmacion);
        editor.commit();
    }

    public ConfiguracionApp cargarConfiguracion(){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        ConfiguracionApp c = new ConfiguracionApp();
        c.cuadroConfirmacion = prefs.getBoolean("cfg_cuadroConfirm", true);
        return c;
    }

    public void guardarConductor(Conductor con){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", con.token);
        editor.putString("usuario", con.usuario);
        editor.putString("idUsuario", con.idUsuario);
        editor.putString("pass", con.pass);
        editor.putString("permisos", con.Permisos);
        editor.putString("idConductor", con.idConductor);
        editor.putString("email", con.Email);
        editor.putString("Nombre", con.Nombre);
        editor.putString("placa", con.CdPlaca);
        editor.putBoolean("cuenta", true);
        editor.commit();
    }

    public Conductor cargarConductor(){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        Conductor c = new Conductor();
        c.token = prefs.getString("token", null);
        c.usuario = prefs.getString("usuario", null);
        c.idUsuario = prefs.getString("idUsuario", null);
        c.pass = prefs.getString("pass", null);
        c.Permisos = prefs.getString("permisos", null);
        c.idConductor = prefs.getString("idConductor", null);
        c.Email = prefs.getString("Email", null);
        c.Nombre = prefs.getString("Nombre", null);
        c.CdPlaca = prefs.getString("placa", null);
        return c;
    }

    public boolean sesion(){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        return prefs.getBoolean("cuenta", false);
    }

    public void quitarCuenta(){
        SharedPreferences prefs = context.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", null);
        editor.putString("usuario", null);
        editor.putString("pass", null);
        editor.putString("placa", null);
        editor.putBoolean("cuenta", false);
        editor.putBoolean("cfg_cuadroConfirm", true);
        editor.commit();
    }

    public static String getToken(Context con){
        SharedPreferences prefs = con.getSharedPreferences("tlrConductor", Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    public String getIMEI(){
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    public Drawable getDrawable(Activity context, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getDrawable(id);
        }
    }

    public void determinarBotonActualizar(String estadoActual, TextView btnConfirmar, TextView btnCancelar){
        btnCancelar.setText("Cancelar");
        if(estadoActual.compareTo("ASIGNADO") == 0){
            btnConfirmar.setText("Confirmar");
            btnCancelar.setText("Rechazar");
        }else{
            if(estadoActual.compareTo("CONFIRMADO") == 0){
                btnConfirmar.setText("Desplazandome");
            }else{
                if(estadoActual.compareTo("DESPLAZAMIENTO A SITIO") == 0){
                    btnConfirmar.setText("En sitio");
                }else{
                    if(estadoActual.compareTo("EN SITIO") == 0){
                        btnConfirmar.setText("En ruta");
                    }else{
                        if(estadoActual.compareTo("EN RUTA") == 0){
                            btnConfirmar.setText("Finalizar");
                            btnCancelar.setVisibility(View.GONE);
                        }else{
                            btnConfirmar.setText("Finalizar");
                            btnCancelar.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    public String determinarEstadoServicio(String estadoActual){
        String nuevoEstado = "CONFIRMADO";
        if(estadoActual.compareTo("ASIGNADO") == 0){
            nuevoEstado = "CONFIRMADO";
        }else{
            if(estadoActual.compareTo("CONFIRMADO") == 0){
                nuevoEstado = "DESPLAZAMIENTO A SITIO";
            }else{
                if(estadoActual.compareTo("DESPLAZAMIENTO A SITIO") == 0){
                    nuevoEstado = "EN SITIO";
                }else{
                    if(estadoActual.compareTo("EN SITIO") == 0){
                        nuevoEstado = "EN RUTA";
                    }else{
                        if(estadoActual.compareTo("EN RUTA") == 0){
                            nuevoEstado = "FINALIZADO";
                        }else
                            nuevoEstado = "FINALIZADO";
                    }
                }
            }
        }
        return nuevoEstado;
    }

    public void getDatosConductor(final TextView lblConductor,final TextView lblPlaca){
        final Conductor con = cargarConductor();
        if(con.CdPlaca == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final webServicesConductor sc = new webServicesConductor(context);
                    final JSONObject obj = sc.getDatosConductor(con.idConductor, con.token);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (obj != null) {
                                    con.CdPlaca = obj.getString("CdPlaca");
                                    guardarConductor(con);
                                    lblConductor.setText(con.Nombre);
                                    lblPlaca.setText(con.CdPlaca);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    });
                }
            }).start();
        }else{
            lblConductor.setText(con.Nombre);
            lblPlaca.setText(con.CdPlaca);
        }
    }

    public String fechaActual(){
        final Calendar c= Calendar.getInstance();
        String fecha = "";
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        String me = String.valueOf((mes + 1));
        String di = String.valueOf(dia);
        if((mes + 1) < 10){
            me = "0" +  String.valueOf((mes + 1));
        }
        if((dia + 1) < 10){
            di = "0" +  String.valueOf(dia);
        }
        fecha = anio + "-" + me + "-" + di;
        return fecha;
    }

    public String  formatearFecha(String fecha,String formatoViejo,String formatoNuevo){
        try {
            Date date = new SimpleDateFormat(formatoViejo).parse(fecha);
            return new SimpleDateFormat(formatoNuevo).format(date);
        }catch(Exception ec){
            return null;
        }
    }

    public String formatearNumero(Double numero){
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        return formatea.format(numero);
    }

    public String formatearHora(String fe){
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fe);
            return new SimpleDateFormat("hh:mm a").format(date);
        }catch(Exception ex){}
        return null;
    }
}
