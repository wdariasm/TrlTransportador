package com.walasys.conductor.Modulos.Inicial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.walasys.conductor.Modulos.Servicios.Principal;
import com.walasys.conductor.R;
import com.walasys.conductor.Servicios.webServicesLogin;
import com.walasys.conductor.Utiles.General;
import com.walasys.conductor.Utiles.Modelos.Conductor;

import org.json.JSONObject;

import java.util.ArrayList;

public class Splash extends AppCompatActivity {

    LinearLayout divInfo;
    TextView lblInfo;
    ProgressBar pb;
    ArrayList<String> listaPermisos;
    long TIEMPO_INICIO = 3500;
    General gn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initComponent();
        gn = new General(this,null);
        listaPermisos = new ArrayList<>();
        hilonicio();
    }

    private void initComponent(){
        divInfo = (LinearLayout) findViewById(R.id.divInfo);
        lblInfo = (TextView) findViewById(R.id.lblInfo);
        pb = (ProgressBar) findViewById(R.id.pb);
    }

    /***
     * METODOS
     */

    private void hilonicio(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(TIEMPO_INICIO);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(verificarPermisos()){
                                lblInfo.setText("Validando sesión...");
                                if(gn.sesion()){
                                    validarSesion();
                                }else{
                                    irLogin();
                                }
                            }else{
                                pedirPermisos(null);
                            }
                        }
                    });
                }catch(Exception e){}
            }
        }).start();
    }

    private void irLogin(){
        Intent i = new Intent(Splash.this,Login.class);
        startActivity(i);
        finish();
    }

    public void pedirPermisos(View v){
        divInfo.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        lblInfo.setText("Verificando permisos...");
        String[] strLista = arrayToStringArray(listaPermisos);
        ActivityCompat.requestPermissions(Splash.this,strLista,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        boolean ban = true;
        for(int i = 0; i < grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                ban = false;
            }
        }
        if(ban){
            lblInfo.setText("Validando sesión...");
            if(gn.sesion()){
                validarSesion();
            }else{
                irLogin();
            }
        }else{
            divInfo.setVisibility(View.VISIBLE);
            lblInfo.setText("");
            pb.setVisibility(View.GONE);
        }
    }

    private void validarSesion(){
        if(gn.sesion()){
            Intent i = new Intent(Splash.this, Principal.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }else{
            irLogin();
        }
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                final webServicesLogin sc = new webServicesLogin(Splash.this);
                final Conductor con = gn.cargarConductor();
                String imei = gn.getIMEI();
                final JSONObject j = sc.login(imei,con.usuario,con.pass);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(j == null){
                                Toast.makeText(Splash.this,"Aviso, "+sc.mensaje,Toast.LENGTH_LONG).show();
                                irLogin();
                            }else{
                                getDataUsuario(j.getString("token"),con);
                            }
                        }catch(Exception ex){
                            System.out.println("ERROR: " + ex.getMessage());
                        }
                    }
                });
            }
        }).start();
        */
    }

    private void getDataUsuario(final String token,final Conductor con){
        try {
            String json64Usuario = token.split("\\.")[1];
            con.token = "Bearer " + token;
            byte[] byteData = Base64.decode(json64Usuario, Base64.URL_SAFE);
            String str = new String(byteData, "UTF-8");
            JSONObject obj = new JSONObject(str);
            final String idSusuario = obj.getJSONObject("user").getString("IdUsuario");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesLogin sc = new webServicesLogin(Splash.this);
                    final JSONObject obj = sc.getDataUsuario(idSusuario,con.token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (obj == null) {
                                    Toast.makeText(Splash.this,"Datos del usuario no encontrados",Toast.LENGTH_LONG).show();
                                }else{
                                    con.idConductor = obj.getString("ConductorId");
                                    con.Email = obj.getString("Email");
                                    con.Nombre = obj.getString("Nombre");
                                    con.Permisos = obj.getString("permisos");
                                    gn.guardarConductor(con);
                                    Intent i = new Intent(Splash.this, Principal.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(i);
                                    finish();
                                }
                            }catch (Exception e){}
                        }
                    });
                }
            }).start();
        }catch (Exception ex){}
    }

    private boolean verificarPermisos(){
        boolean ban = true;
        listaPermisos = new ArrayList<>();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.READ_PHONE_STATE);
            ban = false;
        }
        if ((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ban = false;
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ban = false;
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            listaPermisos.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            ban = false;
        }
        return ban;
    }

    private String[] arrayToStringArray(ArrayList<String> listaPermisos){
        String[] listaNueva = new String[listaPermisos.size()];
        for(int i = 0; i < listaPermisos.size(); i ++){
            listaNueva[i] = listaPermisos.get(i);
        }
        return listaNueva;
    }
}
