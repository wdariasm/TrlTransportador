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
 * Created by wdariasm on 16/01/2018.
 */

public class AdaptadorListaParadas extends ArrayAdapter {

    Activity context;
    ArrayList<JSONObject> listaDatos;

    public static ViewPager viewPager;
    General gn;

    public AdaptadorListaParadas(Activity context, ArrayList<JSONObject> l) {
        super(context, R.layout.layout_item_paradas,l);
        this.context = context;
        this.listaDatos = l;
        gn = new General(context,null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View getView(final int position, View convertView, ViewGroup parent){
        View item = convertView;
        //item = context.getLayoutInflater().inflate(R.layout.layout_item_turno, null);
        item = context.getLayoutInflater().inflate(R.layout.layout_item_paradas, null);
        TextView lblDireccion = (TextView) item.findViewById(R.id.lblDireccion);
        TextView lblValorParada = (TextView) item.findViewById(R.id.lblValorParada);

        try {
            lblDireccion.setText(listaDatos.get(position).getString("prDireccion"));
            lblValorParada.setText(" $"+gn.formatearNumero(listaDatos.get(position).getDouble("prValorCliente")));

        }catch(Exception ex){
        }

        return item;
    }

}
