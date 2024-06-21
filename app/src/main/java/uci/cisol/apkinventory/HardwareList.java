package uci.cisol.apkinventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Activity;
import android.net.Uri;
import android.app.ProgressDialog;

public class HardwareList extends AppCompatActivity {

    private HardwareAdapter adapter;
    private final List<HardwareItem> hardwareItemList = new ArrayList<>();
    private List<HardwareItem> filteredList;
    private static String FILE_PATH;
    private static final int FILTER_REQUEST_CODE = 1;
    private static final int CREATE_FILE_REQUEST_CODE = 2;
    private FloatingActionButton openFilterButton;
    private FloatingActionButton exportToExcelButton;
    private FloatingActionButton generateChartButton;
    private boolean isFabMenuOpen = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_list);
        FloatingActionButton mainFab = findViewById(R.id.main_fab);
        openFilterButton = findViewById(R.id.open_filter_button);
        exportToExcelButton = findViewById(R.id.export_to_excel_button);
        generateChartButton = findViewById(R.id.generate_chart_button);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_hardware);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HardwareAdapter(hardwareItemList, this::onHardwareItemClick);
        recyclerView.setAdapter(adapter);
        FILE_PATH = getIntent().getStringExtra("databasePath");
        String databaseName = getIntent().getStringExtra("databaseName");
        setTitle("Inventario de la base: " + databaseName);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Exportando a Excel...");
        progressDialog.setCancelable(false);

        mainFab.setOnClickListener(v -> toggleFabMenu());

        openFilterButton.setOnClickListener(v -> {
            Intent intent = new Intent(HardwareList.this, FilterActivity.class);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        exportToExcelButton.setOnClickListener(v -> {
            createFile();
        });

        generateChartButton.setOnClickListener(v -> generateChart());

        loadHardwareData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK) {
            applyFilters(
                    data.getStringExtra("filterMotherboard"),
                    data.getStringExtra("filterProcesador"),
                    data.getStringExtra("filterRam"),
                    data.getStringExtra("filterRamOperator"),
                    data.getStringExtra("filterAlmacenamiento"),
                    data.getStringExtra("filterAlmacenamientoOperator"),
                    data.getStringExtra("filterVideo"),
                    data.getStringExtra("filterScanner"),
                    data.getStringExtra("filterImpresoras")
            );
        } else if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                exportToExcel(uri);
            }
        }
    }

    private void applyFilters(String motherboard, String procesador, String ram, String ramOperator, String almacenamiento, String almacenamientoOperator, String video, String scanner, String impresoras) {
        filteredList = new ArrayList<>();
        for (HardwareItem item : hardwareItemList) {
            boolean matches = true;

            if (!motherboard.isEmpty() && !item.getMotherboard().toLowerCase().contains(motherboard.toLowerCase())) {
                matches = false;
            }

            if (!procesador.isEmpty() && !item.getProcesador().toLowerCase().contains(procesador.toLowerCase())) {
                matches = false;
            }

            if (!ram.isEmpty()) {
                int itemRam = Integer.parseInt(item.getRam());
                int filterRam = Integer.parseInt(ram);
                switch (ramOperator) {
                    case "Igual":
                        if (itemRam != filterRam) matches = false;
                        break;
                    case "Mayor que":
                        if (itemRam <= filterRam) matches = false;
                        break;
                    case "Menor que":
                        if (itemRam >= filterRam) matches = false;
                        break;
                }
            }

            if (!almacenamiento.isEmpty()) {
                int itemAlmacenamiento = Integer.parseInt(item.getAlmacenamiento());
                int filterAlmacenamiento = Integer.parseInt(almacenamiento);
                switch (almacenamientoOperator) {
                    case "Igual":
                        if (itemAlmacenamiento != filterAlmacenamiento) matches = false;
                        break;
                    case "Mayor que":
                        if (itemAlmacenamiento <= filterAlmacenamiento) matches = false;
                        break;
                    case "Menor que":
                        if (itemAlmacenamiento >= filterAlmacenamiento) matches = false;
                        break;
                }
            }

            if (!video.isEmpty() && !item.getVideo().toLowerCase().contains(video.toLowerCase())) {
                matches = false;
            }

            if (!scanner.isEmpty() && !item.getScanner().toLowerCase().contains(scanner.toLowerCase())) {
                matches = false;
            }

            if (!impresoras.isEmpty() && !item.getImpresoras().toLowerCase().contains(impresoras.toLowerCase())) {
                matches = false;
            }

            if (matches) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private void toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else {
            openFabMenu();
        }
    }

    private void openFabMenu() {
        isFabMenuOpen = true;
        showFab(openFilterButton);
        showFab(exportToExcelButton);
        showFab(generateChartButton);
    }

    private void closeFabMenu() {
        isFabMenuOpen = false;
        hideFab(openFilterButton);
        hideFab(exportToExcelButton);
        hideFab(generateChartButton);
    }

    private void showFab(FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationY(fab.getHeight());
        fab.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(200)
                .setListener(null);
    }

    private void hideFab(final FloatingActionButton fab) {
        fab.animate()
                .alpha(0f)
                .translationY(fab.getHeight())
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(View.GONE);
                    }
                });
    }

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "Inventario.xlsx");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    private void exportToExcel(Uri uri) {
        if (filteredList == null || filteredList.isEmpty()) {
            filteredList = hardwareItemList;
        }

        progressDialog.show();  // Mostrar el diálogo de progreso

        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            Workbook workbook = new XSSFWorkbook();
            Sheet hardwareSheet = workbook.createSheet("Hardware");
            Sheet softwareSheet = workbook.createSheet("Software");

            // Crear encabezados para la hoja de Hardware
            Row hardwareHeader = hardwareSheet.createRow(0);
            hardwareHeader.createCell(0).setCellValue("HWID");
            hardwareHeader.createCell(1).setCellValue("Motherboard");
            hardwareHeader.createCell(2).setCellValue("Procesador");
            hardwareHeader.createCell(3).setCellValue("RAM");
            hardwareHeader.createCell(4).setCellValue("Almacenamiento");
            hardwareHeader.createCell(5).setCellValue("Video");
            hardwareHeader.createCell(6).setCellValue("Scanner");
            hardwareHeader.createCell(7).setCellValue("Impresoras");

            // Llenar datos de la hoja de Hardware
            int hardwareRowNum = 1;
            for (HardwareItem item : filteredList) {
                Row row = hardwareSheet.createRow(hardwareRowNum++);
                row.createCell(0).setCellValue(item.getHwid());
                row.createCell(1).setCellValue(item.getMotherboard());
                row.createCell(2).setCellValue(item.getProcesador());
                row.createCell(3).setCellValue(item.getRam());
                row.createCell(4).setCellValue(item.getAlmacenamiento());
                row.createCell(5).setCellValue(item.getVideo());
                row.createCell(6).setCellValue(item.getScanner());
                row.createCell(7).setCellValue(item.getImpresoras());
            }

            // Crear encabezados para la hoja de Software
            Row softwareHeader = softwareSheet.createRow(0);
            softwareHeader.createCell(0).setCellValue("HWID");
            softwareHeader.createCell(1).setCellValue("Nombre");
            softwareHeader.createCell(2).setCellValue("Version");

            // Llenar datos de la hoja de Software
            int softwareRowNum = 1;
            for (HardwareItem hardwareItem : filteredList) {
                List<SoftwareItem> softwareItems = loadSoftwareData(hardwareItem.getHwid());
                for (SoftwareItem softwareItem : softwareItems) {
                    Row row = softwareSheet.createRow(softwareRowNum++);
                    row.createCell(0).setCellValue(hardwareItem.getHwid());
                    row.createCell(1).setCellValue(softwareItem.getNombre().replace("Nombre: ", ""));
                    row.createCell(2).setCellValue(softwareItem.getVersion().replace("Versión: ", ""));
                }
            }

            // Guardar el archivo Excel
            try (OutputStream fos = getContentResolver().openOutputStream(uri)) {
                if (fos != null) {
                    workbook.write(fos);
                    workbook.close();
                    runOnUiThread(() -> {
                        progressDialog.dismiss();  // Ocultar el diálogo de progreso
                        Toast.makeText(this, "Archivo exportado exitosamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();  // Ocultar el diálogo de progreso
                        Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();  // Ocultar el diálogo de progreso
                    Toast.makeText(this, "Error al exportar el archivo", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void generateChart() {
        Intent intent = new Intent(HardwareList.this, GraphicsActivity.class);
        intent.putExtra("databasePath", FILE_PATH);
        startActivity(intent);
    }


    private void onHardwareItemClick(HardwareItem item) {
        Intent intent = new Intent(this, SoftwareList.class);
        intent.putExtra("HWID", item.getHwid());
        intent.putExtra("databasePath", FILE_PATH);
        startActivity(intent);
    }

    private void loadHardwareData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue; // Skip header row
                    }

                    HardwareItem item = new HardwareItem(
                            getCellStringValue(row.getCell(0)),
                            getCellStringValue(row.getCell(1)),
                            getCellStringValue(row.getCell(2)),
                            getCellStringValue(row.getCell(3)),
                            getCellStringValue(row.getCell(4)),
                            getCellStringValue(row.getCell(5)),
                            getCellStringValue(row.getCell(6)),
                            getCellStringValue(row.getCell(7))
                    );

                    hardwareItemList.add(item);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar la base de datos", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private List<SoftwareItem> loadSoftwareData(String hwid) {
        List<SoftwareItem> softwareItemList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(1);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                String rowHwid = getCellStringValue(row.getCell(0));
                if (!rowHwid.equals(hwid)) {
                    continue; // Skip if HWID doesn't match
                }

                SoftwareItem item = new SoftwareItem(
                        getCellStringValue(row.getCell(1)),
                        getCellStringValue(row.getCell(2))
                );

                softwareItemList.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error loading software data", Toast.LENGTH_SHORT).show());
        }
        return softwareItemList;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
