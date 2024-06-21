package uci.cisol.apkinventory;

public class HardwareItem {
    private final String hwid;
    private final String motherboard;
    private final String procesador;
    private final String ram;
    private final String almacenamiento;
    private final String video;
    private final String scanner;
    private final String impresoras;

    public HardwareItem(String hwid, String motherboard, String procesador, String ram,
                        String almacenamiento, String video, String scanner, String impresoras) {
        this.hwid = hwid;
        this.motherboard = motherboard;
        this.procesador = procesador;
        this.ram = ram;
        this.almacenamiento = almacenamiento;
        this.video = video;
        this.scanner = scanner;
        this.impresoras = impresoras;
    }

    public String getHwid() {
        return hwid;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public String getProcesador() {
        return procesador;
    }

    public String getRam() {
        return ram;
    }

    public String getAlmacenamiento() {
        return almacenamiento;
    }

    public String getVideo() {
        return video;
    }

    public String getScanner() {
        return scanner;
    }

    public String getImpresoras() {
        return impresoras;
    }
}
