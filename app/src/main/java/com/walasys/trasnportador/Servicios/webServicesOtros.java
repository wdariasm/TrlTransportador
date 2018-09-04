package com.walasys.trasnportador.Servicios;

import android.app.Activity;
import android.os.StrictMode;

import com.walasys.trasnportador.Utiles.General;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Gilmar Ocampo Nieves on 29/11/2016.
 */

public class webServicesOtros {

    String hostServidor = "";
    General gn;

    public webServicesOtros(Activity context){
        gn = new General(context,null);
        hostServidor = gn.cargarServidor();
        //hostServidor = hostServidor + "/public/api";
    }

    public JSONObject getRuta(String puntoInicial, String puntoFinal)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + puntoInicial +"&destination=" + puntoFinal + "&sensor=false");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }catch(Exception ex)
        {
            //msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
