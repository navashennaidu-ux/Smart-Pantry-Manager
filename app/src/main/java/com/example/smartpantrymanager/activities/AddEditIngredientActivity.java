package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etExpiry;
    private Spinner spinnerUnit;
    private TextView tvFormTitle;

    private DatabaseHelper databaseHelper;

    private int itemId = -1;

    private final String[] units = {
            "each",
            "g",
            "kg",
            "ml",
            "L"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        // Find views
        tvFormTitle = findViewById(R.id.tvFormTitle);
        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        etExpiry = findViewById(R.id.etExpiry);

        Button btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);

        Button btnCancel =
                findViewById(R.id.btnCancel);

        // Set up unit spinner
        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

        // Check whether we are adding or editing
        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId != -1) {
            loadExistingItem();
        } else {
            tvFormTitle.setText("Add Ingredient");
        }

        // Save button
        btnSaveIngredient.setOnClickListener(v -> saveIngredient());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadExistingItem() {

        PantryItem item =
                databaseHelper.getPantryItem(itemId);

        if (item == null) {
            Toast.makeText(
                    this,
                    "Ingredient could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        tvFormTitle.setText("Edit Ingredient");

        etIngredientName.setText(item.getIngredientName());

        etQuantity.setText(
                String.valueOf(item.getQuantity())
        );

        etExpiry.setText(
                item.getExpiryDate() == null
                        ? ""
                        : item.getExpiryDate()
        );

        // Select existing unit
        for (int i = 0; i < units.length; i++) {

            if (units[i].equalsIgnoreCase(item.getUnit())) {
                spinnerUnit.setSelection(i);
                break;
            }
        }
    }

    private void saveIngredient() {

        // Remove unnecessary spaces from beginning/end
        String ingredientName =
                etIngredientName.getText()
                        .toString()
                        .trim();

        String quantityText =
                etQuantity.getText()
                        .toString()
                        .trim();

        String unit =
                spinnerUnit.getSelectedItem()
                        .toString();

        String expiryDate =
                etExpiry.getText()
                        .toString()
                        .trim();

        // -----------------------------
        // Ingredient name validation
        // -----------------------------

        if (ingredientName.isEmpty()) {

            etIngredientName.setError(
                    "Ingredient name is required."
            );

            etIngredientName.requestFocus();
            return;
        }

        if (!ingredientName.matches(".*[a-zA-Z].*")) {

            etIngredientName.setError(
                    "Enter a valid ingredient name."
            );

            etIngredientName.requestFocus();
            return;
        }

        // -----------------------------
        // Quantity validation
        // -----------------------------

        if (quantityText.isEmpty()) {

            etQuantity.setError(
                    "Quantity is required."
            );

            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {

            quantity = Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            etQuantity.setError(
                    "Enter a valid quantity."
            );

            etQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {

            etQuantity.setError(
                    "Quantity must be greater than 0."
            );

            etQuantity.requestFocus();
            return;
        }

        if (quantity > 1000000) {

            etQuantity.setError(
                    "Quantity is too large."
            );

            etQuantity.requestFocus();
            return;
        }

        // -----------------------------
        // Expiry date validation
        // -----------------------------

        if (!expiryDate.isEmpty()
                && !isValidDate(expiryDate)) {

            etExpiry.setError(
                    "Use a valid date in YYYY-MM-DD format."
            );

            etExpiry.requestFocus();
            return;
        }

        // -----------------------------
        // Save to SQLite
        // -----------------------------

        if (itemId == -1) {

            // Add new pantry item
            PantryItem newItem =
                    new PantryItem(
                            ingredientName,
                            quantity,
                            unit,
                            expiryDate
                    );

            databaseHelper.addPantryItem(newItem);

            Toast.makeText(
                    this,
                    "Ingredient added successfully.",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // Update existing pantry item
            PantryItem updatedItem =
                    new PantryItem(
                            itemId,
                            ingredientName,
                            quantity,
                            unit,
                            expiryDate
                    );

            databaseHelper.updatePantryItem(updatedItem);

            Toast.makeText(
                    this,
                    "Ingredient updated successfully.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    /**
     * Checks both the required YYYY-MM-DD format
     * and whether the date actually exists.
     *
     * For example:
     * 2026-10-15 = valid
     * 2026-02-31 = invalid
     */
    private boolean isValidDate(String date) {

        // Must follow YYYY-MM-DD
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        // Reject impossible dates such as 2026-02-31
        dateFormat.setLenient(false);

        try {

            Date enteredDate = dateFormat.parse(date);

            // Get today's date without the current time
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Reject dates before today
            return enteredDate != null
                    && !enteredDate.before(today.getTime());

        } catch (ParseException e) {

            return false;
        }
    }}