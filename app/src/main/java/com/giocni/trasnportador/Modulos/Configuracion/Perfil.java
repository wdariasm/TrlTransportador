package com.giocni.trasnportador.Modulos.Configuracion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.giocni.trasnportador.Modulos.Servicios.Principal;
import com.giocni.trasnportador.R;
import com.giocni.trasnportador.Servicios.webServicesConductor;
import com.giocni.trasnportador.Servicios.webServicesServicios;
import com.giocni.trasnportador.Utiles.General;
import com.giocni.trasnportador.Utiles.Modelos.Conductor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class Perfil extends AppCompatActivity {

    General gn;
    EditText txtNombre,txtEmail,txtTelefono1,txtTelefono2,txtTelefono3,txtEscolaridad,txtObservacion;
    ImageView mimageView;
    JSONObject objConductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        initComponent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gn = new General(this,null);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    protected void onStart(){
        super.onStart();
        cargarDatos();
    }

    private void initComponent(){
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtTelefono1 = (EditText) findViewById(R.id.txtTelefono1);
        txtTelefono2 = (EditText) findViewById(R.id.txtTelefono2);
        txtTelefono3 = (EditText) findViewById(R.id.txtTelefono3);
        txtEscolaridad = (EditText) findViewById(R.id.txtEscolaridad);
        txtObservacion = (EditText) findViewById(R.id.txtObservacion);
        mimageView = (ImageView) findViewById(R.id.imgFoto);


    }

    private void setFoto(Bitmap mbitmap){
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 300, 300, mpaint);// Round Image Corner 100 100 100 100
        mimageView.setImageBitmap(imageRounded);
    }

    private void mapearDatos() throws JSONException {
        txtNombre.setText(objConductor.getString("Nombre"));
        txtEmail.setText(objConductor.getString("Email"));
        txtTelefono1.setText(objConductor.getString("TelefonoPpal"));
        txtTelefono2.setText(objConductor.getString("TelefonoDos"));
        txtTelefono3.setText(objConductor.getString("TelefonoTres"));
        txtEscolaridad.setText(objConductor.getString("Escolaridad"));
        txtObservacion.setText(objConductor.getString("Observacion"));
        getFoto(objConductor.getString("RutaImg"));
    }

    private void getFoto(final String rutaFoto){
        final String PATH = Environment.getExternalStorageDirectory().getPath()+"/TRL/Media/Perfil/";
        File f = new File(PATH);
        if(!f.exists())
            f.mkdirs();
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL newurl = null;
                Bitmap mIcon_val = null;
                try {
                    newurl = new URL(rutaFoto);
                    mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(PATH + "perfil.png"));
                    mIcon_val.compress(Bitmap.CompressFormat.PNG, 85, os);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Bitmap ftax = mIcon_val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setFoto(ftax);
                        }catch(Exception ex){
                            //Toast.makeText(context,"Error, " +ex.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void cargarDatos(){
        gn.initCargando("Cargando datos del perfil...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final webServicesConductor sc = new webServicesConductor(Perfil.this);
                Conductor con = gn.cargarConductor();
                final JSONObject obj = sc.getDatosConductor(con.idConductor, con.token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            gn.finishCargando();
                            if (obj == null) {
                                Toast.makeText(Perfil.this, sc.mensaje, Toast.LENGTH_LONG).show();
                            } else {
                                objConductor = obj;
                                mapearDatos();
                            }
                        } catch (Exception ex) {
                        }
                    }
                });
            }
        }).start();
    }

    public void actualizarDatos(View v){
        if(validar()){
            gn.initCargando("Guardando...");
            setearData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final webServicesConductor sc = new webServicesConductor(Perfil.this);
                    Conductor con = gn.cargarConductor();
                    final JSONObject obj = sc.putConductor(objConductor,con.idConductor, con.token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                gn.finishCargando();
                                if (obj == null) {
                                    Toast.makeText(Perfil.this, sc.mensaje, Toast.LENGTH_LONG).show();
                                } else {
                                    if(obj.getInt("request") == 1){
                                        Toast.makeText(Perfil.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                        onStart();
                                    }else{
                                        gn.mostrarDialog("Aviso",obj.getString("message"),false);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void setearData(){
        try{
            objConductor.put("Nombre",txtNombre.getText().toString());
            objConductor.put("Email",txtEmail.getText().toString());
            objConductor.put("TelefonoPpal",txtTelefono1.getText().toString());
            objConductor.put("TelefonoDos",txtTelefono2.getText().toString());
            objConductor.put("TelefonoTres",txtTelefono3.getText().toString());
            objConductor.put("Escolaridad",txtEscolaridad.getText().toString());
            objConductor.put("Observacion",txtObservacion.getText().toString());
        }catch (Exception ex){}
    }

    private boolean validar(){

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
