package com.walasys.conductor.Servicios;

import android.app.Activity;
import android.os.StrictMode;

import com.walasys.conductor.Utiles.General;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Gilmar Ocampo Nieves on 1/12/2016.
 */

public class webServicesPagos {
    public String mensaje;
    String hostServidor = "";
    General gn;

    public webServicesPagos(Activity context){
        gn = new General(context,null);
        hostServidor = gn.cargarServidor();
        //hostServidor = hostServidor + "/public/api";
    }

    public JSONArray getCarteraByConductor(String idConductor, String fechaInicial, String fechaFinal, String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/cartera");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("POST");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id",idConductor);
            jsonParam.put("fecha",fechaInicial);
            jsonParam.put("fechafin",fechaFinal);

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            printout.writeBytes(jsonParam.toString());
            printout.flush ();
            printout.close ();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Servicio no encontrado";
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONArray json = new JSONArray(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            mensaje = "Error: "+ex.getMessage();
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
