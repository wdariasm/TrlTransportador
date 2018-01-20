package com.giocni.trasnportador.Utiles.Adaptadores;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giocni.trasnportador.R;
import com.giocni.trasnportador.Utiles.General;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gilmar Ocampo Nieves on 10/07/2016.
 */
public class AdaptadorListaMisServicios extends ArrayAdapter {

    Activity context;
    ArrayList<JSONObject> listaDatos;

    public static ViewPager viewPager;
    General gn;

    public AdaptadorListaMisServicios(Activity context, ArrayList<JSONObject> l) {
        super(context, R.layout.layout_item_servicios,l);
        this.context = context;
        this.listaDatos = l;
        gn = new General(context,null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View getView(final int position, View convertView, ViewGroup parent){
        View item = convertView;
        //item = context.getLayoutInflater().inflate(R.layout.layout_item_turno, null);
        item = context.getLayoutInflater().inflate(R.layout.layout_item_servicios, null);

        TextView lblCliente = (TextView) item.findViewById(R.id.lblCliente);
        TextView lblTelefonoCliente = (TextView) item.findViewById(R.id.lblTelefonoCliente);
        TextView lblContrato = (TextView) item.findViewById(R.id.lblContrato);
        TextView lblFecha = (TextView) item.findViewById(R.id.lblFecha);
        TextView lblHora = (TextView) item.findViewById(R.id.lblHora);
        TextView lblEstado = (TextView) item.findViewById(R.id.lblEstado);
        TextView lblPrecio = (TextView) item.findViewById(R.id.lblPrecio);
        TextView lblServicio = (TextView) item.findViewById(R.id.lblServicio);

        try {

            lblCliente.setText(listaDatos.get(position).getString("Responsable"));
            lblTelefonoCliente.setText(listaDatos.get(position).getString("Telefono"));
            lblContrato.setText("Contrato #"+listaDatos.get(position).getString("ContratoId"));
            lblFecha.setText(listaDatos.get(position).getString("FechaServicio"));
            lblHora.setText(gn.formatearHora(listaDatos.get(position).getString("FechaServicio")+" "+listaDatos.get(position).getString("Hora")));
            lblEstado.setText(listaDatos.get(position).getString("Estado"));
            lblPrecio.setText("$ "+listaDatos.get(position).getString("ValorTotal"));
            lblServicio.setText(listaDatos.get(position).getString("IdServicio"));
        }catch(Exception ex){
        }

        return item;
    }
}
