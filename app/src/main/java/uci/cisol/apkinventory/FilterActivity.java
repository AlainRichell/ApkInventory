package uci.cisol.apkinventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FilterActivity extends Activity {

    private EditText filterMotherboard;
    private EditText filterProcesador;
    private EditText filterRam;
    private Spinner filterRamOperator;
    private EditText filterAlmacenamiento;
    private Spinner filterAlmacenamientoOperator;
    private EditText filterVideo;
    private EditText filterScanner;
    private EditText filterImpresoras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        filterMotherboard = findViewById(R.id.filter_motherboard);
        filterProcesador = findViewById(R.id.filter_procesador);
        filterRam = findViewById(R.id.filter_ram);
        filterRamOperator = findViewById(R.id.filter_ram_operator);
        filterAlmacenamiento = findViewById(R.id.filter_almacenamiento);
        filterAlmacenamientoOperator = findViewById(R.id.filter_almacenamiento_operator);
        filterVideo = findViewById(R.id.filter_video);
        filterScanner = findViewById(R.id.filter_scanner);
        filterImpresoras = findViewById(R.id.filter_impresoras);
        Button applyFiltersButton = findViewById(R.id.apply_filters_button);

        applyFiltersButton.setOnClickListener(v -> applyFilters());
    }

    private void applyFilters() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("filterMotherboard", filterMotherboard.getText().toString());
        resultIntent.putExtra("filterProcesador", filterProcesador.getText().toString());
        resultIntent.putExtra("filterRam", filterRam.getText().toString());
        resultIntent.putExtra("filterRamOperator", filterRamOperator.getSelectedItem().toString());
        resultIntent.putExtra("filterAlmacenamiento", filterAlmacenamiento.getText().toString());
        resultIntent.putExtra("filterAlmacenamientoOperator", filterAlmacenamientoOperator.getSelectedItem().toString());
        resultIntent.putExtra("filterVideo", filterVideo.getText().toString());
        resultIntent.putExtra("filterScanner", filterScanner.getText().toString());
        resultIntent.putExtra("filterImpresoras", filterImpresoras.getText().toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
