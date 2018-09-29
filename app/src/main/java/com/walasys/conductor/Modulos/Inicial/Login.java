package com.walasys.conductor.Modulos.Inicial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.walasys.conductor.Modulos.Servicios.Principal;
import com.walasys.conductor.R;
import com.walasys.conductor.Servicios.webServicesLogin;
import com.walasys.conductor.Utiles.General;
import com.walasys.conductor.Utiles.Modelos.Conductor;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtPass;
    General gn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
        gn = new General(this,null);

        try{
            Bundle ex = getIntent().getExtras();
            if(ex.getBoolean("ban")){
                gn.cerrarSesion();
            }
        }catch(Exception ec){}
    }

    private void initComponent(){
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPass = (EditText) findViewById(R.id.txtPass);
    }

    private boolean validarCampos(){
        if(txtUsuario.getText().toString().trim().compareTo("") == 0){
            Toast.makeText(this,"Debe digitar un usuario.",Toast.LENGTH_SHORT).show();
            return false;
    }
        if(txtPass.getText().toString().trim().compareTo("") == 0){
            Toast.makeText(this,"Debe digitar una contraseña.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void recordarPass(View v){
        Intent i = new Intent(Login.this, RecordarPass.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    public void login(View v){
        if(validarCampos()){
            gn.initCargando("Iniciando...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesLogin sc = new webServicesLogin(Login.this);
                    String imei = gn.getIMEI();
                    final JSONObject j = sc.login(imei,txtUsuario.getText().toString().trim(),txtPass.getText().toString().trim());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                gn.finishCargando();
                                if(j == null){
                                    gn.mostrarDialog("Aviso",sc.mensaje,true);
                                }else{
                                    Conductor con = new Conductor();
                                    con.usuario = txtUsuario.getText().toString().trim();
                                    con.pass = txtPass.getText().toString().trim();
                                    getDataUsuario(j.getString("token"),con);
                                }
                            }catch(Exception ex){}
                        }
                    });
                }
            }).start();
        }
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
                    final webServicesLogin sc = new webServicesLogin(Login.this);
                    final JSONObject obj = sc.getDataUsuario(idSusuario,con.token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (obj == null) {
                                    Toast.makeText(Login.this,"Datos del usuario no encontrados",Toast.LENGTH_LONG).show();
                                }else{
                                    con.idUsuario = idSusuario;
                                    con.idConductor = obj.getString("ConductorId");
                                    con.Email = obj.getString("Email");
                                    con.Nombre = obj.getString("Nombre");
                                    con.Permisos = obj.getString("permisos");
                                    gn.guardarConductor(con);
                                    Intent i = new Intent(Login.this, Principal.class);
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

    public void irConfigServidor(View v){
        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        dialog1.setContentView(R.layout.content_config_servidor);

        final EditText txtRuta = (EditText) dialog1.findViewById(R.id.txtRuta);
        Button btnGuardar = (Button) dialog1.findViewById(R.id.btnGuardar);

        txtRuta.setText(gn.cargarServidor());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtRuta.getText().toString().compareTo("") == 0){
                    Toast.makeText(Login.this,"Debe digitar una ruta valida",Toast.LENGTH_LONG).show();
                }else{
                    gn.actualizarServidor(txtRuta.getText().toString().trim());
                    Toast.makeText(Login.this,"Guardado correctamente",Toast.LENGTH_LONG).show();
                    dialog1.dismiss();
                }
            }
        });

        dialog1.show();
    }
}
