package com.company.androidsecurity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class DeviceAnalyzerService extends IntentService {
    private static final String TAG = "AnalyzerService";
    private static final String ACTION_ANALYZE = "com.company.androidsecurity.action.ANALYZE";

    public DeviceAnalyzerService() {
        super("DeviceAnalyzerService");
    }

    /**
     * Helper para iniciar el servicio de análisis.
     */
    public static void startActionAnalyze(Context context) {
        Intent intent = new Intent(context, DeviceAnalyzerService.class);
        intent.setAction(ACTION_ANALYZE);
        // Usamos startService ya que IntentService está obsoleto pero funciona con este nivel de SDK.
        context.startService(intent); 
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ANALYZE.equals(action)) {
                handleActionAnalyze();
            }
        }
    }

    /**
     * La lógica principal del escaneo, detección y reporte.
     */
    private void handleActionAnalyze() {
        Log.i(TAG, "Iniciando análisis del dispositivo...");

        // 1. Obtener ID Único del Dispositivo
        // Utilizamos ANDROID_ID como un identificador único.
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // 2. Escaneo de Paquetes
        SecurityScanner scanner = new SecurityScanner(this);
        List<String> installedPackages = scanner.scanInstalledPackages();

        // 3. Detección de Amenazas
        ThreatDetector detector = new ThreatDetector();
        ThreatDetector.AnalysisResult result = detector.analyze(deviceId, installedPackages);

        // 4. Reporte y Notificación
        ReportGenerator reportGenerator = new ReportGenerator(this);
        reportGenerator.generateReportAndNotify(result);

        Log.i(TAG, "Análisis completado para Device ID: " + deviceId);
    }
}
