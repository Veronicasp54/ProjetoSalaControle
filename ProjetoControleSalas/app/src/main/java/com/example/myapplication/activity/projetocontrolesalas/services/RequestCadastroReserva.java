package com.example.myapplication.activity.projetocontrolesalas.services;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestCadastroReserva extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... strings) {

        String urlWS = "http:/192.168.15.6:8080/ReservaDeSala/rest/reserva/cadastrar/";

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", "secret");
            conn.setRequestProperty("novaReserva", strings[0]);
            conn.setConnectTimeout(6000);

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            System.out.println("Resultado da Reserva: " + result.toString());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}