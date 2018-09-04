package com.walasys.trasnportador.Servicios;

import android.app.Activity;
import android.os.StrictMode;

import com.walasys.trasnportador.Utiles.General;

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

public class webServicesLogin {

    public String mensaje;
    String hostServidor = "";
    General gn;

    public webServicesLogin(Activity context){
        gn = new General(context,null);
        hostServidor = gn.cargarServidor();
        //hostServidor = hostServidor + "/public/api";
    }

    public JSONObject login(String imei,String usuario, String pass)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //URL url = new URL(hostServidor+"/usuario/autenticar");
            URL url = new URL(hostServidor+"/login/conductor");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email",usuario);
            jsonParam.put("password",pass);
            jsonParam.put("imei",imei);

            byte[] postDataBytes = jsonParam.toString().getBytes("UTF-8");
            connection.getOutputStream().write(postDataBytes);

            BufferedReader rd;
            String jsonText;
            JSONObject json;

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Servicio no encontrado";
                    return null;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    rd = new BufferedReader(new InputStreamReader(connection.getErrorStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);
                    mensaje = json.getString("error");
                    connection.disconnect();
                    return null;
            }

        }catch(Exception ex)
        {
            mensaje = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject recodarPass(String email)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/usuario/recordar");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email",email);

            byte[] postDataBytes = jsonParam.toString().getBytes("UTF-8");
            connection.getOutputStream().write(postDataBytes);

            BufferedReader rd;
            String jsonText;
            JSONObject json;

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Servicio no encontrado";
                    return null;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    rd = new BufferedReader(new InputStreamReader(connection.getErrorStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);
                    mensaje = json.getString("error");
                    connection.disconnect();
                    return null;
            }

        }catch(Exception ex)
        {
            mensaje = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject cerrarSesion(String idCondutor,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/usuario/"+ idCondutor +"/cerrarConductor");
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
                    mensaje = "Credenciales no validas";
                    return null;
            }

        }catch(Exception ex)
        {
            mensaje = "Error: "+ex.getMessage();
        }
        return null;
    }


    public JSONObject getDataUsuario(String idUsuario,String token)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(hostServidor+"/usuario/"+idUsuario);
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
                    mensaje = "Credenciales no validas";
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
