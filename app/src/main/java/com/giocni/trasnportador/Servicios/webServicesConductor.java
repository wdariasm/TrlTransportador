package com.giocni.trasnportador.Servicios;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;

import com.giocni.trasnportador.Utiles.General;

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
 * Created by Gilmar Ocampo Nieves on 29/11/2016.
 */

public class webServicesConductor {
    public String mensaje;
    String hostServidor = "";
    General gn;

    public webServicesConductor(Activity context){
        gn = new General(context,null);
        hostServidor = gn.cargarServidor();
        //hostServidor = hostServidor + "/public/api";
    }

    public webServicesConductor(Context context){
        hostServidor = General.cargarServidor(context);
        //hostServidor = hostServidor + "/public/api";
    }

    public JSONObject getDatosConductor(String id,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/conductor/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("GET");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Servicio no encontrado";
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);

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

    public JSONObject putIdPush(String imei, String idPush,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/gps/"+imei);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("PUT");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("gpKey",idPush);

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
                    JSONObject json = new JSONObject(jsonText);

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

    public JSONObject putPosicion(String imei, String latitud,String longitud,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/gps/posicion/"+imei);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("PUT");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("gpLatitud",latitud);
            jsonParam.put("gpLongitud",longitud);

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
                    JSONObject json = new JSONObject(jsonText);

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

    public JSONObject putConductor(JSONObject jsonParam,String id,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/conductor/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("PUT");
            connection.connect();

            byte[] postDataBytes = jsonParam.toString().getBytes("UTF-8");
            connection.getOutputStream().write(postDataBytes);

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Servicio no encontrado";
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);

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

    public JSONArray getServiciosByConductor(String id, String opcion,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/conductor/" + id + "/servicios/" + opcion);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("GET");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Servicio no encontrado";
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    System.out.println("CONSULTA:"+jsonText);
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
