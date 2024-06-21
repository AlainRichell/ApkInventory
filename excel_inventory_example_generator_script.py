import pandas as pd
import random

# Numero de equipos generados
num_rows = 200

# Cantidades mínimas y máximas de software por cada equipo
cant_smpe = 1
cant_spe = 5

# Lista de fabricantes para cada categoría
motherboard_manufacturers = ["ASUS", "Gigabyte", "MSI", "ASRock", "Biostar"]
processor_manufacturers = ["Intel", "AMD"]
video_manufacturers = ["NVIDIA", "AMD", "Intel"]
scanner_manufacturers = ["Epson", "Canon", "HP"]
printer_manufacturers = ["HP", "Canon", "Brother"]

# Lista de nombres de software reales
software_names_list = ["Microsoft Office", "Adobe Photoshop", "AutoCAD", "Visual Studio", "Eclipse", 
                       "Google Chrome", "Mozilla Firefox", "Slack", "Skype", "Zoom", 
                       "Spotify", "VLC Media Player", "Dropbox", "OneDrive", "Notepad++", 
                       "WinRAR", "7-Zip", "Git", "Node.js", "Python"]

# Function to generate random hardware data
def generate_hardware_data(num_rows):
    hwid = [f"HW{str(i).zfill(4)}" for i in range(1, num_rows+1)]
    motherboards = [f"{random.choice(motherboard_manufacturers)}_{random.choice('ABCDEFGHIJKLMNOPQRSTUVWXYZ')}_{random.randint(1, 999)}" for _ in range(num_rows)]
    processors = [f"{random.choice(processor_manufacturers)}_{random.choice('ABCDEFGHIJKLMNOPQRSTUVWXYZ')}_{random.randint(1, 999)}" for _ in range(num_rows)]
    ram = [f"{random.choice([4, 8, 16, 32, 64])}GB" for _ in range(num_rows)]
    storage = [f"{random.choice([256, 512, 1024, 2048])}GB SSD" for _ in range(num_rows)]
    video = [f"{random.choice(video_manufacturers)}_{random.choice('ABCDEFGHIJKLMNOPQRSTUVWXYZ')}_{random.randint(1, 999)}" for _ in range(num_rows)]
    scanners = [f"{random.choice(scanner_manufacturers)}_{random.choice('ABCDEFGHIJKLMNOPQRSTUVWXYZ')}_{random.randint(1, 999)}" if random.random() > 0.25 else None for _ in range(num_rows)]
    printers = [f"{random.choice(printer_manufacturers)}_{random.choice('ABCDEFGHIJKLMNOPQRSTUVWXYZ')}_{random.randint(1, 999)}" if random.random() > 0.25 else None for _ in range(num_rows)]
    
    hardware_data = {
        "HWID": hwid,
        "Motherboard": motherboards,
        "Procesador": processors,
        "RAM": ram,
        "Almacenamiento": storage,
        "Video": video,
        "Scanner": scanners,
        "Impresoras": printers
    }
    
    return pd.DataFrame(hardware_data)

# Function to generate random software data
def generate_software_data(num_rows, hwid_list):
    software_data = {"HWID": [], "Nombre": [], "Version": []}
    
    for hwid in hwid_list:
        num_softwares = random.randint(cant_smpe,cant_spe)
        for _ in range(num_softwares):
            software_data["HWID"].append(hwid)
            software_data["Nombre"].append(random.choice(software_names_list))
            software_data["Version"].append(f"{random.randint(1, 10)}.{random.randint(0, 9)}")
    
    return pd.DataFrame(software_data)

# Generate data
hardware_df = generate_hardware_data(num_rows)
software_df = generate_software_data(num_rows, hardware_df["HWID"].tolist())

# Save to Excel file
with pd.ExcelWriter('inventario_hardware_software.xlsx') as writer:
    hardware_df.to_excel(writer, sheet_name='Hardware', index=False)
    software_df.to_excel(writer, sheet_name='Software', index=False)
