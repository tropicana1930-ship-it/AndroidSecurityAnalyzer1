# Integración fase 1

La fase 1 suministrada en `AndroidSecurityAnalyzer_fase1.zip` contiene un backend Python y una aplicación Android Kotlin independiente en `mobile-app/`. La integración preserva el repositorio anterior en la rama `master` hasta revisar y fusionar el PR.

## Seguridad
Las operaciones de bloqueo, retiro y borrado de dispositivos se ejecutan únicamente en equipos gestionados por Microsoft Intune y con autorización. `CLIENT_SECRET` debe guardarse solo en un servidor seguro.

## Pruebas
Ejecutar `pip install -r backend/requirements.txt && cd backend && pytest`. Para el APK, revisar el workflow `android-build.yml` de GitHub Actions.
