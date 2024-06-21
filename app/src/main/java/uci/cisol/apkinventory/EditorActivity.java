package uci.cisol.apkinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import uci.cisol.apkinventory.data.ItemContract;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentItemUri;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private ImageView mItemImageView;
    public Bitmap mItemBitmap;
    public FloatingActionButton fab;
    private boolean mItemHasChanged = false;
    private Uri selectedImage = null;
    private static final int PICK_EXCEL_FILE_REQUEST = 1001;
    private static final int PICK_DATABASE_LOCATION_REQUEST = 1002;
    private Uri selectedExcelUri;
    private boolean Validacion = false;
    private String databasePath;

    private final View.OnTouchListener mTouchListener = (view, motionEvent) -> {
        mItemHasChanged = true;
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_item_name);
        mDescriptionEditText = findViewById(R.id.edit_item_description);
        fab = findViewById(R.id.floatingActionButton);
        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        fab.setOnTouchListener(mTouchListener);
        mItemBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.image_prompt)).getBitmap();
    }

    private void saveItem() {
        if (Validacion){

            String nameString = mNameEditText.getText().toString().trim();
            String descriptionString = mDescriptionEditText.getText().toString().trim();
            String imageUri = (selectedImage == null) ? "null" : selectedImage.toString();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mItemBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] photo = baos.toByteArray();
            ContentValues values = new ContentValues();
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, nameString);
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION, descriptionString);
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE, photo);
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_URI, imageUri);
            values.put(ItemContract.ItemEntry.COLUMN_ITEM_TAG1, databasePath);

            if (mCurrentItemUri == null) {
                Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_item_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_item_successful), Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Seleccione una base de datos primero", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
        }
        NavUtils.navigateUpFromSameTask(EditorActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete_entry:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (mItemHasChanged)
                    showUnsavedChangesDialog();
                else
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION,
                ItemContract.ItemEntry.COLUMN_ITEM_IMAGE,
                ItemContract.ItemEntry.COLUMN_ITEM_URI};

        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int descriptionColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION);
            int imageColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE);
            int uriColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_URI);

            String name = data.getString(nameColumnIndex);
            String description = data.getString(descriptionColumnIndex);
            byte[] photo = data.getBlob(imageColumnIndex);
            String imageURI = data.getString(uriColumnIndex);

            ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);

            mNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            mItemImageView.setImageBitmap(theImage);
            mItemBitmap = theImage;
            selectedImage = (imageURI.equals("null")) ? null : Uri.parse(imageURI);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Bitmap tempItemBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.image_prompt)).getBitmap();
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mItemImageView.setImageBitmap(tempItemBitmap);
        selectedImage = null;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, (dialog, id) -> deleteItem());
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.return_dialog_msg);
        builder.setPositiveButton(R.string.discard, (dialog, id) -> finish());
        builder.setNegativeButton(R.string.edit, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void importarBaseDatosExcel(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, PICK_EXCEL_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_FILE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                selectedExcelUri = data.getData();
                procesarExcel();
            }
        } else if (requestCode == PICK_DATABASE_LOCATION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    File selectedDatabaseFile = new File(uri.getPath());
                }
            }
        }
    }

    private void procesarExcel() {
        if (selectedExcelUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedExcelUri);
                if (inputStream != null) {

                    String fileName = getFileName(selectedExcelUri);
                    String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                    String extension = fileName.substring(fileName.lastIndexOf('.'));
                    File localDir = getExternalFilesDir(null);

                    if (localDir != null) {
                        File localFile = new File(localDir, fileName);
                        int fileIndex = 1;

                        while (localFile.exists()) {
                            fileName = baseName + "_" + fileIndex + extension;
                            localFile = new File(localDir, fileName);
                            fileIndex++;
                        }

                        localFile.createNewFile();
                        try (FileOutputStream outputStream = new FileOutputStream(localFile)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                        inputStream.close();
                        String relativePath = localFile.getAbsolutePath();
                        databasePath = localFile.getAbsolutePath();
                        mNameEditText.setText(fileName.replace(extension, ""));
                        Validacion = true;
                        Toast.makeText(this, "Archivo guardado en: " + relativePath, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al acceder a la memoria interna", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
