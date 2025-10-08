#!/usr/bin/env python3
import subprocess
import json
import sys
import argparse
import os
import time
from DeviceManagementAPI import DeviceManagementAPI

class AndroidDeviceAnalyzer:
    def __init__(self, device_id):
        self.device_id = device_id
        self.microsoft_api = DeviceManagementAPI()
        
    def run_adb_command(self, command):
        \"\"\"Ejecuta comandos ADB de forma segura\"\"\"
        try:
            full_command = ['adb', '-s', self.device_id] + command
            result = subprocess.run(full_command, capture_output=True, text=True, timeout=30)
            return result.stdout.strip()
        except subprocess.TimeoutExpired:
            print(f\"Timeout ejecutando comando: {command}\")
            return \"\"
        except Exception as e:
            print(f\"Error ejecutando comando: {e}\")
            return \"\"
    
    def check_adb_connection(self):
        \"\"\"Verifica que el dispositivo está conectado via ADB\"\"\"
        print(\"🔌 Verificando conexión ADB...\")
        devices_output = subprocess.run(['adb', 'devices'], capture_output=True, text=True)
        
        if self.device_id in devices_output.stdout:
            print(\"✅ Dispositivo conectado correctamente\")
            return True
        else:
            print(\"❌ Dispositivo no encontrado. Conecta el dispositivo y habilita depuración USB.\")
            print(\"Dispositivos disponibles:\")
            print(devices_output.stdout)
            return False
    
    def analyze_remote_access_apps(self):
        \"\"\"Analiza aplicaciones de acceso remoto\"\"\"
        print(\"🔍 Analizando aplicaciones de acceso remoto...\")
        
        # Obtener todas las aplicaciones instaladas
        packages = self.run_adb_command(['shell', 'pm', 'list', 'packages', '-f'])
        
        remote_access_indicators = [
            'teamviewer', 'anydesk', 'rustdesk', 'vnc', 'rdp',
            'remote', 'screenmirror', 'screenshare', 'control',
            'supremo', 'logmein', 'gotomypc', 'remotepc'
        ]
        
        suspicious_apps = []
        for line in packages.split('\n'):
            if line.startswith('package:'):
                package_line = line.lower()
                for indicator in remote_access_indicators:
                    if indicator in package_line:
                        app_info = {
                            'package': line.replace('package:', '').strip(),
                            'threat_type': 'remote_access',
                            'indicator': indicator
                        }
                        suspicious_apps.append(app_info)
                        print(f\"⚠️  Detectado: {app_info['package']}\")
        
        return suspicious_apps
    
    def check_developer_options(self):
        \"\"\"Verifica configuraciones de desarrollador\"\"\"
        print(\"🔍 Verificando configuraciones de desarrollador...\")
        
        usb_debugging = self.run_adb_command(['shell', 'settings get global adb_enabled'])
        unknown_sources = self.run_adb_command(['shell', 'settings get secure install_non_market_apps'])
        
        issues = []
        if usb_debugging.strip() == '1':
            issues.append('USB Debugging habilitado')
        if unknown_sources.strip() == '1':
            issues.append('Instalación de fuentes desconocidas habilitada')
            
        return issues
    
    def analyze_running_services(self):
        \"\"\"Analiza servicios en ejecución\"\"\"
        print(\"🔍 Analizando servicios en ejecución...\")
        
        services = self.run_adb_command(['shell', 'dumpsys activity services'])
        suspicious_services = []
        
        # Buscar servicios sospechosos
        suspicious_keywords = ['remote', 'control', 'access', 'admin']
        for keyword in suspicious_keywords:
            if keyword in services.lower():
                suspicious_services.append(f\"Servicio con keyword: {keyword}\")
        
        return suspicious_services
    
    def take_remediation_actions(self, threats):
        \"\"\"Toma acciones de remediación basadas en amenazas detectadas\"\"\"
        print(\"🛡️  Aplicando acciones de remediación...\")
        
        for threat in threats:
            if threat['threat_type'] == 'remote_access':
                # Extraer nombre del paquete
                package_full = threat['package']
                package_name = package_full.split('=')[-1] if '=' in package_full else package_full
                
                print(f\"🚫 Intentando desinstalar: {package_name}\")
                
                # Forzar detención de la app
                self.run_adb_command(['shell', 'am force-stop', package_name])
                
                # Intentar desinstalar
                result = self.run_adb_command(['shell', 'pm uninstall --user 0', package_name])
                if 'Success' in result:
                    print(f\"✅ Desinstalado: {package_name}\")
                else:
                    # Si no se puede desinstalar, al menos deshabilitar
                    print(f\"⚠️  No se pudo desinstalar, intentando deshabilitar: {package_name}\")
                    disable_result = self.run_adb_command(['shell', 'pm disable-user --user 0', package_name])
                    if 'disabled' in disable_result.lower():
                        print(f\"✅ Deshabilitado: {package_name}\")
                    else:
                        print(f\"❌ No se pudo deshabilitar: {package_name}\")
    
    def generate_report(self, threats, developer_issues, running_services):
        \"\"\"Genera reporte completo\"\"\"
        report = {
            'device_id': self.device_id,
            'timestamp': time.strftime('%Y-%m-%d %H:%M:%S'),
            'threats_detected': len(threats),
            'suspicious_apps': threats,
            'developer_issues': developer_issues,
            'suspicious_services': running_services,
            'security_score': self.calculate_security_score(threats, developer_issues),
            'recommendations': self.generate_recommendations(threats, developer_issues)
        }
        
        # Guardar reporte
        report_filename = f'report_{self.device_id}.json'
        with open(report_filename, 'w', encoding='utf-8') as f:
            json.dump(report, f, indent=2, ensure_ascii=False)
        
        print(f\"📊 Reporte guardado como: {report_filename}\")
        return report
    
    def calculate_security_score(self, threats, issues):
        \"\"\"Calcula puntuación de seguridad (0-100)\"\"\"
        base_score = 100
        
        # Penalizar por apps sospechosas
        base_score -= len(threats) * 10
        
        # Penalizar por issues de desarrollador
        base_score -= len(issues) * 15
        
        return max(0, base_score)
    
    def generate_recommendations(self, threats, issues):
        \"\"\"Genera recomendaciones basadas en los hallazgos\"\"\"
        recommendations = []
        
        if threats:
            recommendations.append(\"Desinstalar aplicaciones de acceso remoto no autorizadas\")
        if issues:
            recommendations.append(\"Deshabilitar opciones de desarrollador en dispositivos de producción\")
        if 'USB Debugging habilitado' in issues:
            recommendations.append(\"Deshabilitar depuración USB para mejorar la seguridad\")
        if 'Instalación de fuentes desconocidas habilitada' in issues:
            recommendations.append(\"Deshabilitar instalación desde fuentes desconocidas\")
        
        if not recommendations:
            recommendations.append(\"Mantener actualizado el sistema operativo\")
            recommendations.append(\"Revisar permisos de aplicaciones regularmente\")
        
        return recommendations

def main():
    parser = argparse.ArgumentParser(description='Android Device Security Analyzer')
    parser.add_argument('--device', required=True, help='Device ID to analyze')
    parser.add_argument('--action', choices=['analyze', 'quarantine', 'wipe'], default='analyze')
    
    args = parser.parse_args()
    
    analyzer = AndroidDeviceAnalyzer(args.device)
    
    # Verificar conexión primero
    if not analyzer.check_adb_connection():
        sys.exit(1)
    
    if args.action == 'analyze':
        # Ejecutar análisis completo
        print(\"🚀 Iniciando análisis de seguridad completo...\\n\")
        
        threats = analyzer.analyze_remote_access_apps()
        developer_issues = analyzer.check_developer_options()
        running_services = analyzer.analyze_running_services()
        
        # Generar reporte
        report = analyzer.generate_report(threats, developer_issues, running_services)
        
        print(\"\\n\" + \"=\"*60)
        print(\"📊 REPORTE DE SEGURIDAD COMPLETO\")
        print(\"=\"*60)
        print(f\"Dispositivo: {report['device_id']}\")
        print(f\"Fecha: {report['timestamp']}\")
        print(f\"Puntuación de seguridad: {report['security_score']}/100\")
        print(f\"Amenazas detectadas: {report['threats_detected']}\")
        print(f\"Problemas de configuración: {len(report['developer_issues'])}\")
        
        if report['threats_detected'] > 0:
            print(\"\\n❌ APLICACIONES SOSPECHOSAS:\")
            for app in report['suspicious_apps']:
                print(f\"  - {app['package']} (Tipo: {app['threat_type']})\")
        
        if report['developer_issues']:
            print(\"\\n⚠️  PROBLEMAS DE CONFIGURACIÓN:\")
            for issue in report['developer_issues']:
                print(f\"  - {issue}\")
        
        print(\"\\n💡 RECOMENDACIONES:\")
        for rec in report['recommendations']:
            print(f\"  - {rec}\")
        
        # Tomar acciones automáticas si hay amenazas
        if threats:
            print(\"\\n\" + \"-\"*60)
            response = input(\"¿Deseas aplicar acciones de remediación automáticas? (s/n): \")
            if response.lower() == 's':
                analyzer.take_remediation_actions(threats)
            else:
                print(\"Acciones de remediación omitidas.\")
            
    elif args.action == 'quarantine':
        print(\"🚧 Aislando dispositivo via Microsoft Intune...\")
        success = analyzer.microsoft_api.quarantine_device(args.device)
        if success:
            print(\"✅ Dispositivo aislado correctamente\")
        else:
            print(\"❌ Error aislando dispositivo\")
            
    elif args.action == 'wipe':
        print(\"🗑️  Iniciando limpieza remota del dispositivo...\")
        confirm = input(\"¿ESTÁS SEGURO? Esto borrará TODOS los datos. Escribe 'CONFIRMAR' para continuar: \")
        if confirm == 'CONFIRMAR':
            success = analyzer.microsoft_api.wipe_device(args.device)
            if success:
                print(\"✅ Dispositivo limpiado correctamente\")
            else:
                print(\"❌ Error limpiando dispositivo\")
        else:
            print(\"❌ Limpieza cancelada\")

if __name__ == \"__main__\":
    main()
