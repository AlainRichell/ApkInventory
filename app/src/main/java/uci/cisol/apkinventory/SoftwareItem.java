package uci.cisol.apkinventory;

public class SoftwareItem {
    private final String nombre;
    private final String version;

    public SoftwareItem(String nombre, String version) {
        this.nombre = "Nombre: " + nombre;
        this.version = "Versión: " + version;
    }

    public String getNombre() {
        return nombre;
    }

    public String getVersion() {
        return version;
    }
}
