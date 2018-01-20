package com.giocni.trasnportador.Modulos.Inicial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.giocni.trasnportador.R;
import com.giocni.trasnportador.Servicios.webServicesLogin;
import com.giocni.trasnportador.Utiles.General;
import com.giocni.trasnportador.Utiles.Modelos.Conductor;

import org.json.JSONObject;

public class RecordarPass extends AppCompatActivity {

    EditText txtEmail;
    General gn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordar_pass);
        gn = new General(this,null);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
    }

    private boolean validarCampos(){
        if(txtEmail.getText().toString().trim().compareTo("") == 0){
            Toast.makeText(this,"Debe digitar un email.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void enviarCorreo(View v){
        if(validarCampos()){
            gn.initCargando("Enviando...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesLogin sc = new webServicesLogin(RecordarPass.this);
                    String imei = gn.getIMEI();
                    final JSONObject j = sc.recodarPass(txtEmail.getText().toString().trim());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                gn.finishCargando();
                                if(j == null){
                                    gn.mostrarDialog("Aviso",sc.mensaje,true);
                                }else{
                                    Toast.makeText(RecordarPass.this,j.getString("request"),Toast.LENGTH_SHORT).show();
                                    if(j.getString("message").compareTo("Correcto") == 0){
                                        finish();
                                    }
                                }
                            }catch(Exception ex){}
                        }
                    });
                }
            }).start();
        }
    }

    public void volver(View v){
        finish();
    }
}
