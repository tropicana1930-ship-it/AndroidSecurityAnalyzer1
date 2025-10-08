import requests
import os
from msal import ConfidentialClientApplication

class DeviceManagementAPI:
    def __init__(self):
        self.tenant_id = os.getenv('TENANT_ID')
        self.client_id = os.getenv('CLIENT_ID')
        self.client_secret = os.getenv('CLIENT_SECRET')
        self.scope = ["https://graph.microsoft.com/.default"]
        self.authority = f"https://login.microsoftonline.com/{self.tenant_id}"
        
    def get_access_token(self):
        try:
            app = ConfidentialClientApplication(
                self.client_id,
                authority=self.authority,
                client_credential=self.client_secret,
            )
            result = app.acquire_token_for_client(scopes=self.scope)
            
            if "access_token" in result:
                return result["access_token"]
            else:
                print(f"Error getting token: {result.get('error_description', 'Unknown error')}")
                return None
                
        except Exception as e:
            print(f"Exception getting token: {str(e)}")
            return None
    
    def get_managed_devices(self):
        token = self.get_access_token()
        if not token:
            return []
            
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        
        url = "https://graph.microsoft.com/v1.0/deviceManagement/managedDevices"
        response = requests.get(url, headers=headers)
        
        if response.status_code == 200:
            return response.json().get('value', [])
        else:
            print(f"Error getting devices: {response.status_code} - {response.text}")
            return []
    
    def quarantine_device(self, device_id):
        token = self.get_access_token()
        if not token:
            return False
            
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        
        # Para dispositivos Microsoft Intune
        url = f"https://graph.microsoft.com/v1.0/deviceManagement/managedDevices/{device_id}/disable"
        response = requests.post(url, headers=headers)
        
        return response.status_code == 200
    
    def wipe_device(self, device_id):
        token = self.get_access_token()
        if not token:
            return False
            
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        
        url = f"https://graph.microsoft.com/v1.0/deviceManagement/managedDevices/{device_id}/wipe"
        body = {
            "keepEnrollmentData": False,
            "keepUserData": False,
            "macOsUnlockCode": None
        }
        response = requests.post(url, headers=headers, json=body)
        
        return response.status_code == 200

if __name__ == "__main__":
    api = DeviceManagementAPI()
    devices = api.get_managed_devices()
    print(f"Found {len(devices)} managed devices")
