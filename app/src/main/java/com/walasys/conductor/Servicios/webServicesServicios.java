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
 * Created by Gilmar Ocampo Nieves on 29/11/2016.
 */

public class webServicesServicios {
    public String mensaje;
    String hostServidor = "";
    General gn;

    public webServicesServicios(Activity context){
        gn = new General(context,null);
        hostServidor = gn.cargarServidor();
        //hostServidor = hostServidor + "/public/api";
    }

    public JSONArray getHistorial(String idConductor,String fecha,String fechafin,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/conductor");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("POST");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("fechafin",fechafin);
            jsonParam.put("fecha",fecha);
            jsonParam.put("id",idConductor);

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

    public JSONObject putServicioConfirmar(JSONObject obj,String idServicio, String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/" + idServicio );
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("PUT");
            connection.connect();

            JSONObject jsonParam = obj;

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

    public JSONObject cancelarServicio(JSONObject obj,String idServicio, String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/" + idServicio + "/cancelar");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("PUT");
            connection.connect();

            JSONObject jsonParam = obj;

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

    public JSONArray getMotivosCancelar(String modulo, String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/motivo/" + modulo );
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

    public JSONObject deleteServicio(String idServicio, String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/" + idServicio );
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("DELETE");
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

    public JSONObject putServicioEstado(String estado,String idServicio, String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/conductor/" + idServicio );
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty ("Authorization", token);
            connection.setRequestMethod("PUT");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("Estado",estado);

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

    public JSONObject getServicioById(String id,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/servicio/"+id);
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

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
