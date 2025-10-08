# Android Security Analyzer

Una aplicación completa para analizar y proteger dispositivos Android contra accesos remotos no autorizados.

## 🏗️ Estructura del Proyecto

\\\
AndroidSecurityAnalyzer/
├── .github/workflows/          # GitHub Actions para CI/CD
├── backend/                    # Scripts Python de análisis
├── mobile-app/                 # Aplicación Android
└── dashboard/                  # Dashboard web (próximamente)
\\\

## 🚀 Características

- 🔍 **Detección de apps de acceso remoto** (TeamViewer, AnyDesk, etc.)
- ⚡ **Análisis de configuraciones de seguridad**
- 🛡️ **Acciones de remediación automáticas**
- 📊 **Reportes de seguridad detallados**
- ☁️ **Integración con Microsoft Intune**

## 📋 Requisitos

### Para el backend:
- Python 3.9+
- ADB (Android Debug Bridge)
- Cuenta Microsoft Azure para Graph API

### Para la app móvil:
- Android Studio
- Dispositivo Android con depuración USB habilitada

## 🛠️ Instalación

1. Clona el repositorio:
\\\ash
git clone <tu-repositorio>
cd AndroidSecurityAnalyzer
\\\

2. Configura el backend:
\\\ash
cd backend
pip install -r requirements.txt
\\\

3. Configura las variables de entorno:
\\\ash
export TENANT_ID=tu_tenant_id
export CLIENT_ID=tu_client_id  
export CLIENT_SECRET=tu_client_secret
\\\

## 🎯 Uso

### Análisis de dispositivo:
\\\ash
python backend/device_analyzer.py --device <device_id> --action analyze
\\\

### Aislamiento remoto:
\\\ash
python backend/device_analyzer.py --device <device_id> --action quarantine
\\\

## 📞 Soporte

Para problemas o preguntas, abre un issue en el repositorio.
