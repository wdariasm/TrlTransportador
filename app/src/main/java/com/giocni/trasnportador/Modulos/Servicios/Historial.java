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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.giocni.trasnportador.R;
import com.giocni.trasnportador.Servicios.webServicesPagos;
import com.giocni.trasnportador.Servicios.webServicesServicios;
import com.giocni.trasnportador.Utiles.Adaptadores.AdaptadorListaHistorial;
import com.giocni.trasnportador.Utiles.DatePicker;
import com.giocni.trasnportador.Utiles.General;
import com.giocni.trasnportador.Utiles.Modelos.Conductor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.giocni.trasnportador.R.id.txtFechaFinal;
import static com.giocni.trasnportador.R.id.txtFechaInicial;

public class Historial extends AppCompatActivity {

    General gn;
    LinearLayout mRevealView;
    TextView lblConductor,lblPlaca;

    EditText txtFechaInicial,txtFechaFinal;
    ListView listaHistorial;
    ArrayList<JSONObject> listaHistorialData;

    DatePicker fechaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        initComponent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gn = new General(this,null);

        initFechas();
        //datos COnductor
        gn.getDatosConductor(lblConductor,lblPlaca);
    }

    protected void onResume(){
        super.onResume();
        cargarHistorial(null);
    }

    private void initComponent(){
        listaHistorial = (ListView) findViewById(R.id.listaHistorial);
        lblConductor = (TextView) findViewById(R.id.lblConductor);
        txtFechaInicial = (EditText) findViewById(R.id.txtFechaInicial);
        txtFechaFinal = (EditText) findViewById(R.id.txtFechaFinal);
        lblPlaca = (TextView) findViewById(R.id.lblPlaca);
        mRevealView = (LinearLayout) findViewById(R.id.layout_menu_principal);
        mRevealView.setVisibility(View.INVISIBLE);
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
    }

    private void mostrarFecha(View v){
        fechaDialog = new DatePicker();
        fechaDialog.txtFecha = (EditText) v;
        fechaDialog.setCancelable(false);
        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
        fechaDialog.show(fr, "Seleccione una fecha");
    }

    public void cargarHistorial(View v){
        gn.initCargando("Cargando historial...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                }catch (Exception e){}
                Conductor con = gn.cargarConductor();
                final webServicesServicios sc = new webServicesServicios(Historial.this);
                String fechaIni = gn.formatearFecha(txtFechaInicial.getText().toString(),"yyyy-MM-dd","dd/MM/yyyy");
                String fechaFin = gn.formatearFecha(txtFechaFinal.getText().toString(),"yyyy-MM-dd","dd/MM/yyyy");
                final JSONArray obj = sc.getHistorial(con.idConductor,fechaIni,fechaFin,con.token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gn.finishCargando();
                        try{
                            if(obj == null){
                                Toast.makeText(Historial.this,sc.mensaje,Toast.LENGTH_LONG).show();
                            }else{
                                listaHistorialData = new ArrayList<>();
                                for(int i = 0; i < obj.length(); i++){
                                    JSONObject objTemp = obj.getJSONObject(i);
                                    listaHistorialData.add(objTemp);
                                }
                                AdaptadorListaHistorial ad = new AdaptadorListaHistorial(Historial.this,listaHistorialData);
                                listaHistorial.setAdapter(ad);
                            }
                        }catch (Exception ex){}
                    }
                });
            }
        }).start();
    }

    public void irMenu(View v){
        gn.menuPrincipal(v,mRevealView);
        gn.irMenuItem(v);
    }

    public void menuPrincipal(View v){
        gn.menuPrincipal(v,mRevealView);
    }
}
