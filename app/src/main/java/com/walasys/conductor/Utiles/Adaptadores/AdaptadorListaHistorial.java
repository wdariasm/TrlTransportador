package com.walasys.conductor.Utiles.Adaptadores;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.walasys.conductor.R;
import com.walasys.conductor.Utiles.General;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gilmar Ocampo Nieves on 10/07/2016.
 */
public class AdaptadorListaHistorial extends ArrayAdapter {

    Activity context;
    ArrayList<JSONObject> listaDatos;

    public static ViewPager viewPager;
    General gn;

    public AdaptadorListaHistorial(Activity context, ArrayList<JSONObject> l) {
        super(context, R.layout.layout_item_historial,l);
        this.context = context;
        this.listaDatos = l;
        gn = new General(context,null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View getView(final int position, View convertView, ViewGroup parent){
        View item = convertView;
        //item = context.getLayoutInflater().inflate(R.layout.layout_item_turno, null);
        item = context.getLayoutInflater().inflate(R.layout.layout_item_historial, null);

        TextView lblResponsable = (TextView) item.findViewById(R.id.lblResponsable);
        TextView lblTelefono = (TextView) item.findViewById(R.id.lblTelefono);
        TextView lblTServicio = (TextView) item.findViewById(R.id.lblTServicio);
        TextView lblTVehiculo = (TextView) item.findViewById(R.id.lblTVehiculo);
        TextView lblEstado = (TextView) item.findViewById(R.id.lblEstado);
        TextView lblFecha = (TextView) item.findViewById(R.id.lblFecha);
        TextView lblContrato = (TextView) item.findViewById(R.id.lblContrato);
        TextView lblPrecio = (TextView) item.findViewById(R.id.lblPrecio);

        try {

            lblResponsable.setText(listaDatos.get(position).getString("Responsable"));
            lblTelefono.setText(listaDatos.get(position).getString("Telefono"));
            lblTServicio.setText(listaDatos.get(position).getString("svDescripcion"));
            lblTVehiculo.setText(listaDatos.get(position).getString("DescVehiculo"));
            lblEstado.setText(listaDatos.get(position).getString("Estado"));
            lblFecha.setText(listaDatos.get(position).getString("FechaServicio") + "\n" + gn.formatearHora(listaDatos.get(position).getString("FechaServicio") + " " + listaDatos.get(position).getString("Hora")));
            lblContrato.setText(listaDatos.get(position).getString("NumeroContrato"));
            lblPrecio.setText("$"+gn.formatearNumero(listaDatos.get(position).getDouble("ValorTotal")));


        }catch(Exception ex){
        }

        return item;
    }
}
