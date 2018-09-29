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
 * Created by wdariasm on 19/01/2018.
 */

public class AdaptadorListaResponsables extends ArrayAdapter {

    Activity context;
    ArrayList<JSONObject> listaDatos;

    public static ViewPager viewPager;
    General gn;

    public AdaptadorListaResponsables(Activity context, ArrayList<JSONObject> l) {
        super(context, R.layout.layout_item_responsables,l);
        this.context = context;
        this.listaDatos = l;
        gn = new General(context,null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View getView(final int position, View convertView, ViewGroup parent){
        View item = convertView;
        //item = context.getLayoutInflater().inflate(R.layout.layout_item_turno, null);
        item = context.getLayoutInflater().inflate(R.layout.layout_item_responsables, null);
        TextView lblResponsable = (TextView) item.findViewById(R.id.lblResponsable);
        TextView lblTelefono = (TextView) item.findViewById(R.id.lblTelefono);
        TextView lblNota = (TextView) item.findViewById(R.id.lblNota);

        try {
            lblResponsable.setText(listaDatos.get(position).getString("scNombre"));
            lblTelefono.setText(listaDatos.get(position).getString("scTelefono"));
            lblNota.setText( " " + listaDatos.get(position).getString("scNota"));


        }catch(Exception ex){
        }

        return item;
    }

}
