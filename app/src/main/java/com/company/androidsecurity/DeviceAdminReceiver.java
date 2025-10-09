package com.company.androidsecurity;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdminReceiver extends DeviceAdminReceiver {
    public static final String ACTION_WIPE = "ACTION_WIPE";
    public static final String ACTION_QUARANTINE = "ACTION_QUARANTINE";

    /**
     * Método estático para ejecutar acciones de seguridad críticas (Wipe, Quarantine).
     */
    public static void performSecurityAction(Context context, String action) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, DeviceAdminReceiver.class);

        if (!dpm.isAdminActive(componentName)) {
            Toast.makeText(context, "El Administrador de Dispositivo NO está activo. No se puede ejecutar " + action, Toast.LENGTH_LONG).show();
            return;
        }

        switch (action) {
            case ACTION_WIPE:
                // PELIGRO: Esto ejecuta el borrado de fábrica. Solo para pruebas.
                Toast.makeText(context, "Ejecutando Borrado de Fábrica (WIPE DATA)...", Toast.LENGTH_LONG).show();
                dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE); // Incluye borrar almacenamiento externo
                break;
            case ACTION_QUARANTINE:
                // Simulación de Cuarentena: Bloqueo inmediato de la pantalla.
                Toast.makeText(context, "Dispositivo puesto en Cuarentena (Bloqueo Forzado)", Toast.LENGTH_LONG).show();
                dpm.lockNow();
                break;
            default:
                Toast.makeText(context, "Acción de seguridad desconocida: " + action, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "Administrador de Dispositivo Activado. Funciones de control habilitadas.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "Administrador de Dispositivo Desactivado. Funciones críticas perdidas.", Toast.LENGTH_LONG).show();
    }
}
