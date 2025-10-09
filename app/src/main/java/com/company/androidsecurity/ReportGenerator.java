package com.company.androidsecurity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReportGenerator {
    private static final String TAG = "ReportGenerator";
    private static final String CHANNEL_ID = "security_analyzer_channel";
    private final Context context;

    public ReportGenerator(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * Genera una notificación local y envía el reporte al backend.
     * @param result Resultado del análisis.
     */
    public void generateReportAndNotify(ThreatDetector.AnalysisResult result) {
        // 1. Enviar el reporte a través de la red (Asíncrono)
        NetworkReporter reporter = new NetworkReporter();
        reporter.sendReport(result);
        Log.i(TAG, "Reporte enviado para device_id: " + result.device_id);

        // 2. Generar notificación local para el usuario
        String title;
        String message;

        if (result.malicious_packages.isEmpty()) {
            title = "Escaneo Completo: ¡Dispositivo Seguro!";
            message = "Nivel de amenaza BAJO. Total de apps revisadas: " + result.package_count;
        } else {
            title = "¡ALERTA DE SEGURIDAD! Amenaza Detectada.";
            message = "Nivel: " + result.threat_level + ". Amenazas encontradas: " + result.malicious_packages.size() + ". Revisar backend.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_lock) // Ícono genérico de bloqueo
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    /**
     * Crea el canal de notificación para Android 8.0 (Oreo) y superior.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Security Channel";
            String description = "Notificaciones de escaneo de seguridad";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
