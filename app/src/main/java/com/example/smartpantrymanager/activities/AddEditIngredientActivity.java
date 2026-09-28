package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

public class AddEditIngredientActivity
        extends AppCompatActivity {

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
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_ingredient);

        databaseHelper =
                new DatabaseHelper(this);

        tvFormTitle =
                findViewById(
                        R.id.tvFormTitle);

        etIngredientName =
                findViewById(
                        R.id.etIngredientName);

        etQuantity =
                findViewById(
                        R.id.etQuantity);

        etExpiry =
                findViewById(
                        R.id.etExpiry);

        spinnerUnit =
                findViewById(
                        R.id.spinnerUnit);

        Button btnSave =
                findViewById(
                        R.id.btnSaveIngredient);

        Button btnCancel =
                findViewById(
                        R.id.btnCancel);

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_item,
                        units);

        unitAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        spinnerUnit.setAdapter(
                unitAdapter);

        itemId =
                getIntent()
                        .getIntExtra(
                                "item_id",
                                -1);

        if (itemId != -1) {

            loadExistingItem();
        }

        btnSave.setOnClickListener(v ->
                saveIngredient());

        btnCancel.setOnClickListener(v ->
                finish());
    }

    private void loadExistingItem() {

        PantryItem item =
                databaseHelper
                        .getPantryItem(itemId);

        if (item == null) {
            return;
        }

        tvFormTitle.setText(
                "Edit Ingredient");

        etIngredientName.setText(
                item.getIngredientName());

        etQuantity.setText(
                String.valueOf(
                        item.getQuantity()));

        etExpiry.setText(
                item.getExpiryDate());

        for (int i = 0;
             i < units.length;
             i++) {

            if (units[i]
                    .equalsIgnoreCase(
                            item.getUnit())) {

                spinnerUnit.setSelection(i);
                break;
            }
        }
    }

    private void saveIngredient() {

        String name =
                etIngredientName
                        .getText()
                        .toString()
                        .trim();

        String quantityText =
                etQuantity
                        .getText()
                        .toString()
                        .trim();

        String expiry =
                etExpiry
                        .getText()
                        .toString()
                        .trim();

        String unit =
                spinnerUnit
                        .getSelectedItem()
                        .toString();

        // Validation
        if (name.isEmpty()) {

            etIngredientName.setError(
                    "Ingredient name is required");

            etIngredientName.requestFocus();

            return;
        }

        if (quantityText.isEmpty()) {

            etQuantity.setError(
                    "Quantity is required");

            etQuantity.requestFocus();

            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(
                            quantityText);

        } catch (NumberFormatException e) {

            etQuantity.setError(
                    "Enter a valid quantity");

            return;
        }

        if (quantity <= 0) {

            etQuantity.setError(
                    "Quantity must be greater than zero");

            return;
        }

        if (!expiry.isEmpty() &&
                !expiry.matches(
                        "\\d{4}-\\d{2}-\\d{2}")) {

            etExpiry.setError(
                    "Use YYYY-MM-DD format");

            return;
        }

        if (itemId == -1) {

            PantryItem item =
                    new PantryItem(
                            name,
                            quantity,
                            unit,
                            expiry);

            long result =
                    databaseHelper
                            .addPantryItem(item);

            if (result != -1) {

                Toast.makeText(
                                this,
                                "Ingredient added",
                                Toast.LENGTH_SHORT)
                        .show();

                finish();

            } else {

                Toast.makeText(
                                this,
                                "Unable to add ingredient",
                                Toast.LENGTH_SHORT)
                        .show();
            }

        } else {

            PantryItem item =
                    new PantryItem(
                            itemId,
                            name,
                            quantity,
                            unit,
                            expiry);

            int rows =
                    databaseHelper
                            .updatePantryItem(item);

            if (rows > 0) {

                Toast.makeText(
                                this,
                                "Ingredient updated",
                                Toast.LENGTH_SHORT)
                        .show();

                finish();

            } else {

                Toast.makeText(
                                this,
                                "Unable to update ingredient",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}