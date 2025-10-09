package com.company.androidsecurity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    private TextView statusTextView;
    private TextView adminStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de componentes de UI
        Button scanButton = findViewById(R.id.btn_scan);
        Button wipeTestButton = findViewById(R.id.btn_wipe_test);
        statusTextView = findViewById(R.id.tv_status);
        adminStatusTextView = findViewById(R.id.tv_admin_status);

        // Inicialización de la gestión de políticas de dispositivo
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceAdminReceiver.class);

        // Listener para Iniciar Escaneo
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia el servicio de análisis en segundo plano
                if (isAdminActive()) {
                    statusTextView.setText("Iniciando escaneo en segundo plano...");
                    DeviceAnalyzerService.startActionAnalyze(MainActivity.this);
                } else {
                    promptEnableAdmin();
                }
            }
        });

        // Listener para Probar Borrado (Wipe)
        wipeTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    // Llama a la función estática de DeviceAdminReceiver para simular el borrado
                    statusTextView.setText("¡ADVERTENCIA! Intentando Wipe/Borrado de Fábrica...");
                    DeviceAdminReceiver.performSecurityAction(MainActivity.this, DeviceAdminReceiver.ACTION_WIPE);
                } else {
                    promptEnableAdmin();
                }
            }
        });

        // Actualiza el estado al iniciar
        updateAdminStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdminStatus();
    }

    /**
     * Comprueba si el Administrador de Dispositivo está activo.
     */
    private boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(componentName);
    }

    /**
     * Muestra el diálogo para activar el administrador.
     */
    private void promptEnableAdmin() {
        Toast.makeText(this, "Debe activar el Administrador de Dispositivo primero.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_description));
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }

    /**
     * Actualiza el TextView del estado del Administrador.
     */
    private void updateAdminStatus() {
        if (isAdminActive()) {
            adminStatusTextView.setText("Estado Admin: Activo");
            adminStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            adminStatusTextView.setText("Estado Admin: No Activado. Debe activarlo para el escaneo.");
            adminStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Administrador de Dispositivo Activado.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Activación del Administrador Cancelada.", Toast.LENGTH_SHORT).show();
            }
            updateAdminStatus();
        }
    }
}
