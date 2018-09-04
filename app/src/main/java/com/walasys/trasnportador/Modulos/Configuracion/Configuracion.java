package com.walasys.trasnportador.Modulos.Configuracion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walasys.trasnportador.Modulos.Servicios.Principal;
import com.walasys.trasnportador.R;
import com.walasys.trasnportador.Utiles.General;
import com.walasys.trasnportador.Utiles.Modelos.ConfiguracionApp;

public class Configuracion extends AppCompatActivity {

    General gn;
    LinearLayout mRevealView;
    TextView lblConductor,lblPlaca;
    CheckBox cboxCuadroConfirm;
    ConfiguracionApp configApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        initComponent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gn = new General(this,null);

        //datos COnductor
        gn.getDatosConductor(lblConductor,lblPlaca);
        configApp = gn.cargarConfiguracion();
        initConfig();
    }

    private void initConfig(){
        cboxCuadroConfirm.setChecked(configApp.cuadroConfirmacion);
    }

    private void initComponent(){
        cboxCuadroConfirm = (CheckBox) findViewById(R.id.cboxCuadroConfirm);
        lblConductor = (TextView) findViewById(R.id.lblConductor);
        lblPlaca = (TextView) findViewById(R.id.lblPlaca);
        mRevealView = (LinearLayout) findViewById(R.id.layout_menu_principal);
        mRevealView.setVisibility(View.INVISIBLE);
    }

    public void irPerfil(View v){
        Intent i = new Intent(this,Perfil.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    public void clickCuadroConfirm(View v){
        CheckBox ch = (CheckBox) v;
        configApp.cuadroConfirmacion = ch.isChecked();
        gn.guardarConfiguracion(configApp);
    }

    public void irMenu(View v){
        gn.menuPrincipal(v,mRevealView);
        gn.irMenuItem(v);
    }

    public void menuPrincipal(View v){
        gn.menuPrincipal(v,mRevealView);
    }
}
