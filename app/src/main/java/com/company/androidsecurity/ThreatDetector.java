package com.company.androidsecurity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ThreatDetector {
    private static final String TAG = "ThreatDetector";

    /**
     * Clase interna para estructurar el resultado del análisis (usada por Gson para el JSON).
     */
    public static class AnalysisResult {
        public String device_id;
        public String threat_level; // LOW, MEDIUM, HIGH
        public int package_count;
        public List<String> malicious_packages;
        public String timestamp;
    }

    /**
     * Simulación de una lista negra (blacklist) de paquetes.
     */
    private static final List<String> BLACKLIST = List.of(
            "com.evil.malware",
            "net.phishing.spyware",
            "com.android.badtool",
            "com.example.riskyapp"
    );

    /**
     * Analiza la lista de paquetes instalados contra la lista negra simulada.
     * @param deviceId ID único del dispositivo.
     * @param installedPackages Lista de nombres de paquetes instalados por el usuario.
     * @return Objeto AnalysisResult con los resultados.
     */
    public AnalysisResult analyze(String deviceId, List<String> installedPackages) {
        List<String> detectedThreats = new ArrayList<>();

        for (String packageName : installedPackages) {
            if (BLACKLIST.contains(packageName)) {
                detectedThreats.add(packageName);
            }
        }

        // Detección de Amenaza Aleatoria para pruebas (1 en 10 escaneos)
        if (new Random().nextInt(10) == 0) {
            detectedThreats.add("com.simulated.randomthreat");
        }

        String threatLevel;
        if (detectedThreats.isEmpty()) {
            threatLevel = "LOW";
        } else if (detectedThreats.size() < 3) {
            threatLevel = "MEDIUM";
        } else {
            threatLevel = "HIGH";
        }

        Log.d(TAG, "Detección completada. Nivel de amenaza: " + threatLevel);

        AnalysisResult result = new AnalysisResult();
        result.device_id = deviceId;
        result.threat_level = threatLevel;
        result.package_count = installedPackages.size();
        result.malicious_packages = detectedThreats;
        result.timestamp = String.valueOf(System.currentTimeMillis());

        return result;
    }
}
