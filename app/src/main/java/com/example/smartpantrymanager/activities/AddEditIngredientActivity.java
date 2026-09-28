package com.example.smartpantrymanager.activities;

import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        // -------------------------------------------------
        // Expiry date picker
        // -------------------------------------------------

        // Prevent manual typing.
        // The user selects the expiry date from the calendar.
        etExpiry.setFocusable(false);
        etExpiry.setClickable(true);

        etExpiry.setOnClickListener(v -> showDatePicker());

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

    /**
     * Loads an existing pantry item when the user
     * chooses to edit an ingredient.
     */
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

        etIngredientName.setText(
                item.getIngredientName()
        );

        etQuantity.setText(
                String.valueOf(item.getQuantity())
        );

        etExpiry.setText(
                item.getExpiryDate() == null
                        ? ""
                        : item.getExpiryDate()
        );

        // Select the existing measurement unit
        for (int i = 0; i < units.length; i++) {

            if (units[i].equalsIgnoreCase(item.getUnit())) {

                spinnerUnit.setSelection(i);
                break;
            }
        }
    }

    /**
     * Opens a calendar so that the user can select
     * an expiry date instead of manually typing one.
     */
    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        // If an existing valid expiry date is present,
        // open the calendar on that date.
        String existingDate =
                etExpiry.getText()
                        .toString()
                        .trim();

        if (!existingDate.isEmpty()) {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            try {

                Date parsedDate =
                        dateFormat.parse(existingDate);

                if (parsedDate != null) {
                    calendar.setTime(parsedDate);
                }

            } catch (ParseException ignored) {
                // If the stored value cannot be parsed,
                // the calendar simply opens on today's date.
            }
        }

        int year =
                calendar.get(Calendar.YEAR);

        int month =
                calendar.get(Calendar.MONTH);

        int day =
                calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view,
                         selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            String selectedDate =
                                    String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d-%02d",
                                            selectedYear,
                                            selectedMonth + 1,
                                            selectedDay
                                    );

                            etExpiry.setText(selectedDate);
                            etExpiry.setError(null);
                        },
                        year,
                        month,
                        day
                );

        // Prevent the user from selecting a date
        // before today.
        Calendar today =
                Calendar.getInstance();

        today.set(
                Calendar.HOUR_OF_DAY,
                0
        );

        today.set(
                Calendar.MINUTE,
                0
        );

        today.set(
                Calendar.SECOND,
                0
        );

        today.set(
                Calendar.MILLISECOND,
                0
        );

        datePickerDialog
                .getDatePicker()
                .setMinDate(
                        today.getTimeInMillis()
                );

        datePickerDialog.show();
    }

    /**
     * Validates the entered information and then
     * adds or updates the pantry item in SQLite.
     */
    private void saveIngredient() {

        String ingredientName =
                etIngredientName
                        .getText()
                        .toString()
                        .trim();

        String quantityText =
                etQuantity
                        .getText()
                        .toString()
                        .trim();

        String unit =
                spinnerUnit
                        .getSelectedItem()
                        .toString();

        String expiryDate =
                etExpiry
                        .getText()
                        .toString()
                        .trim();

        // -------------------------------------------------
        // Ingredient name validation
        // -------------------------------------------------

        if (ingredientName.isEmpty()) {

            etIngredientName.setError(
                    "Ingredient name is required."
            );

            etIngredientName.requestFocus();
            return;
        }

        // Ingredient must contain at least one letter
        if (!ingredientName.matches(".*[a-zA-Z].*")) {

            etIngredientName.setError(
                    "Enter a valid ingredient name."
            );

            etIngredientName.requestFocus();
            return;
        }

        // -------------------------------------------------
        // Quantity validation
        // -------------------------------------------------

        if (quantityText.isEmpty()) {

            etQuantity.setError(
                    "Quantity is required."
            );

            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(
                            quantityText
                    );

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

        // -------------------------------------------------
        // Expiry date validation
        // -------------------------------------------------

        // Expiry date is optional.
        // If supplied, it must be valid and cannot be
        // earlier than today's date.
        if (!expiryDate.isEmpty()
                && !isValidDate(expiryDate)) {

            etExpiry.setError(
                    "Enter a valid expiry date that is today or later (YYYY-MM-DD)."
            );

            return;
        }

        // -------------------------------------------------
        // Add or update SQLite record
        // -------------------------------------------------

        if (itemId == -1) {

            PantryItem newItem =
                    new PantryItem(
                            ingredientName,
                            quantity,
                            unit,
                            expiryDate
                    );

            databaseHelper.addPantryItem(
                    newItem
            );

            Toast.makeText(
                    this,
                    "Ingredient added successfully.",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            PantryItem updatedItem =
                    new PantryItem(
                            itemId,
                            ingredientName,
                            quantity,
                            unit,
                            expiryDate
                    );

            databaseHelper.updatePantryItem(
                    updatedItem
            );

            Toast.makeText(
                    this,
                    "Ingredient updated successfully.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    /**
     * Checks that an expiry date:
     *
     * 1. Uses YYYY-MM-DD format.
     * 2. Is a real calendar date.
     * 3. Is today or a future date.
     */
    private boolean isValidDate(String date) {

        // Required format
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

            Date enteredDate =
                    dateFormat.parse(date);

            if (enteredDate == null) {
                return false;
            }

            Calendar today =
                    Calendar.getInstance();

            // Remove current time so that today's date
            // remains valid.
            today.set(
                    Calendar.HOUR_OF_DAY,
                    0
            );

            today.set(
                    Calendar.MINUTE,
                    0
            );

            today.set(
                    Calendar.SECOND,
                    0
            );

            today.set(
                    Calendar.MILLISECOND,
                    0
            );

            return !enteredDate.before(
                    today.getTime()
            );

        } catch (ParseException e) {

            return false;
        }
    }
}