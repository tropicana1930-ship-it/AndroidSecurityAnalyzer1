package com.company.androidsecurity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SecurityScanner {
    private static final String TAG = "SecurityScanner";
    private final Context context;

    public SecurityScanner(Context context) {
        this.context = context;
    }

    /**
     * Escanea e identifica todas las aplicaciones instaladas por el usuario.
     * @return Lista de nombres de paquetes (Strings) de aplicaciones de usuario.
     */
    public List<String> scanInstalledPackages() {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> userPackages = new ArrayList<>();

        for (ApplicationInfo appInfo : installedApps) {
            // Filtra las apps de sistema
            boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            if (!isSystemApp) {
                userPackages.add(appInfo.packageName);
            }
        }
        Log.d(TAG, "Escaneo de paquetes completado. Paquetes de usuario: " + userPackages.size());
        return userPackages;
    }
}
