package uci.cisol.apkinventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoftwareList extends AppCompatActivity {

    private SoftwareAdapter adapter;
    private final List<SoftwareItem> softwareItemList = new ArrayList<>();
    private static String FILE_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_software);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SoftwareAdapter(softwareItemList);
        recyclerView.setAdapter(adapter);

        FILE_PATH = getIntent().getStringExtra("databasePath");
        String hwid = getIntent().getStringExtra("HWID");
        setTitle("Lista de softwares de: " + hwid);
        loadSoftwareData(hwid);
    }

    private void loadSoftwareData(String hwid) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(1);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
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
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading software data", Toast.LENGTH_SHORT).show();
        }
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
