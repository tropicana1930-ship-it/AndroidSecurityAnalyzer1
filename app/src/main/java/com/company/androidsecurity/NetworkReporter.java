package com.company.androidsecurity;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkReporter {
    private static final String TAG = "NetworkReporter";
    
    // URL para acceder al servidor Flask que corre en tu PC (http://10.0.2.2:5000)
    private static final String REPORT_ENDPOINT = "http://10.0.2.2:5000/api/security_report";
    
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Envía el objeto de resultado del análisis al backend en formato JSON.
     * @param reportData Objeto con el resultado del análisis.
     */
    public void sendReport(ThreatDetector.AnalysisResult reportData) {
        // La operación de red debe ejecutarse en un hilo separado
        new Thread(() -> {
            try {
                // 1. Convertir el objeto Java a JSON
                String jsonReport = gson.toJson(reportData);

                // 2. Construir la Petición POST
                RequestBody body = RequestBody.create(jsonReport, JSON);
                Request request = new Request.Builder()
                        .url(REPORT_ENDPOINT)
                        .post(body)
                        .build();

                // 3. Ejecutar la petición y manejar la respuesta
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Reporte enviado exitosamente. Código: " + response.code());
                    } else {
                        Log.e(TAG, "Fallo al enviar el reporte. Código: " + response.code() + ", Mensaje: " + response.body().string());
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error de red al enviar el reporte: " + e.getMessage());
            }
        }).start();
    }
}
