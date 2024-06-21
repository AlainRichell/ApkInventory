package uci.cisol.apkinventory;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import uci.cisol.apkinventory.databinding.ActivityGraphicsBinding;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphicsActivity extends AppCompatActivity {

    private Spinner spinnerOptions;
    private EditText editTextAttributes;
    private GraphView graphView;
    private final List<HardwareItem> hardwareItemList = new ArrayList<>();
    private final List<String> attributesList = new ArrayList<>();
    private ArrayAdapter<String> attributesAdapter;
    private static String FILE_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uci.cisol.apkinventory.databinding.ActivityGraphicsBinding binding = ActivityGraphicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle("Análisis gráfico");
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Análisis gráfico");

        FILE_PATH = getIntent().getStringExtra("databasePath");
        loadHardwareData();

        spinnerOptions = findViewById(R.id.spinner_options);
        editTextAttributes = findViewById(R.id.editText_attributes);
        Button buttonAddAttribute = findViewById(R.id.button_add_attribute);
        Button buttonGenerateChart = findViewById(R.id.button_generate_chart);
        graphView = findViewById(R.id.graphView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        attributesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attributesList);
        binding.listViewAttributes.setAdapter(attributesAdapter);

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = spinnerOptions.getSelectedItem().toString().toLowerCase();

                switch (selectedOption) {
                    case "motherboard":
                    case "procesador":
                    case "video":
                    case "scanner":
                    case "impresoras":
                        editTextAttributes.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case "ram":
                    case "almacenamiento":
                        editTextAttributes.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonAddAttribute.setOnClickListener(v -> {
            String attribute = editTextAttributes.getText().toString();
            if (!attribute.isEmpty()) {
                attributesList.add(attribute);
                attributesAdapter.notifyDataSetChanged();
                editTextAttributes.setText("");
            } else {
                Toast.makeText(this, "Por favor ingrese un atributo", Toast.LENGTH_SHORT).show();
            }
        });
        buttonGenerateChart.setOnClickListener(v -> generateBarChart());
    }

    private void loadHardwareData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue;
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

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar la base de datos", Toast.LENGTH_SHORT).show());
            }
        });
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

    private void generateBarChart() {
        String selectedOption = spinnerOptions.getSelectedItem().toString().toLowerCase();
        spinnerOptions.setEnabled(false);
        if (attributesList.isEmpty()) {
            Toast.makeText(this, "Por favor agregue al menos un atributo", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Integer> attributeCountMap = new HashMap<>();
        for (String attribute : attributesList) {
            attributeCountMap.put(attribute, 0);
        }

        for (HardwareItem item : hardwareItemList) {
            String value = "";
            boolean isNumber = false;
            switch (selectedOption) {
                case "motherboard":
                    value = item.getMotherboard().toLowerCase();
                    break;
                case "procesador":
                    value = item.getProcesador().toLowerCase();
                    break;
                case "ram":
                    value = item.getRam().toLowerCase();
                    isNumber = true;
                    break;
                case "almacenamiento":
                    value = item.getAlmacenamiento().toLowerCase();
                    isNumber = true;
                    break;
                case "video":
                    value = item.getVideo().toLowerCase();
                    break;
                case "scanner":
                    value = item.getScanner().toLowerCase();
                    break;
                case "impresoras":
                    value = item.getImpresoras().toLowerCase();
                    break;
            }

            for (String attribute : attributesList) {
                if(!isNumber){
                    if (value.toLowerCase().contains(attribute.toLowerCase())) {
                        attributeCountMap.put(attribute, attributeCountMap.get(attribute) + 1);
                    }
                }else{
                    if (value.equals(attribute.trim())){
                        attributeCountMap.put(attribute, attributeCountMap.get(attribute) + 1);
                    }
                }

            }
        }

        List<DataPoint> dataPoints = new ArrayList<>();
        int index = 1; // Empezar en 1 para evitar problemas con la coordenada 0
        for (String attribute : attributesList) {
            dataPoints.add(new DataPoint(index++, attributeCountMap.get(attribute)));
        }

        if (dataPoints.isEmpty()) {
            Toast.makeText(this, "No hay datos para mostrar en el gráfico", Toast.LENGTH_SHORT).show();
            return;
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints.toArray(new DataPoint[0]));

        series.setSpacing(50);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(attributesList.size() + 1); // Ajustar el maxX para incluir todas las barras
        graphView.getViewport().setXAxisBoundsManual(true);

        graphView.removeAllSeries();
        graphView.addSeries(series);

        graphView.getGridLabelRenderer().setNumHorizontalLabels(attributesList.size());
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Cantidad");
        graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(24);

        // Establecer el título del gráfico
        String chartTitle = selectedOption.toUpperCase() + " : " + String.join(", ", attributesList);
        graphView.setTitle(chartTitle);
        graphView.setTitleTextSize(24);
        graphView.setTitleColor(Color.BLACK);

        graphView.invalidate();
    }
}
