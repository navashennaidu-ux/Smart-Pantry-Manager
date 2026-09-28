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
import com.example.smartpantrymanager.utils.QuantityFormatter;

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

        setContentView(
                R.layout.activity_add_edit_ingredient
        );

        databaseHelper =
                new DatabaseHelper(this);

        // -----------------------------------------
        // Connect Java variables to XML views
        // -----------------------------------------

        tvFormTitle =
                findViewById(
                        R.id.tvFormTitle
                );

        etIngredientName =
                findViewById(
                        R.id.etIngredientName
                );

        etQuantity =
                findViewById(
                        R.id.etQuantity
                );

        spinnerUnit =
                findViewById(
                        R.id.spinnerUnit
                );

        etExpiry =
                findViewById(
                        R.id.etExpiry
                );

        Button btnSaveIngredient =
                findViewById(
                        R.id.btnSaveIngredient
                );

        Button btnCancel =
                findViewById(
                        R.id.btnCancel
                );

        // -----------------------------------------
        // Measurement unit spinner
        // -----------------------------------------

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        unitAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(
                unitAdapter
        );

        // -----------------------------------------
        // Expiry date picker
        // -----------------------------------------

        // Prevent manual typing because the user
        // selects the date using the calendar.
        etExpiry.setFocusable(false);
        etExpiry.setClickable(true);

        etExpiry.setOnClickListener(
                v -> showDatePicker()
        );

        // -----------------------------------------
        // Determine Add or Edit mode
        // -----------------------------------------

        itemId =
                getIntent().getIntExtra(
                        "item_id",
                        -1
                );

        if (itemId != -1) {

            loadExistingItem();

        } else {

            tvFormTitle.setText(
                    "Add Ingredient"
            );
        }

        // -----------------------------------------
        // Save button
        // -----------------------------------------

        btnSaveIngredient.setOnClickListener(
                v -> saveIngredient()
        );

        // -----------------------------------------
        // Cancel button
        // -----------------------------------------

        btnCancel.setOnClickListener(
                v -> finish()
        );
    }

    /**
     * Loads an existing pantry item when
     * the user selects Edit.
     */
    private void loadExistingItem() {

        PantryItem item =
                databaseHelper.getPantryItem(
                        itemId
                );

        if (item == null) {

            Toast.makeText(
                    this,
                    "Ingredient could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        tvFormTitle.setText(
                "Edit Ingredient"
        );

        // Existing ingredient name
        etIngredientName.setText(
                item.getIngredientName()
        );

        // Existing quantity.
        //
        // QuantityFormatter removes unnecessary
        // decimal zeros:
        //
        // 4.0  -> 4
        // 2.0  -> 2
        // 3.5  -> 3.5
        etQuantity.setText(
                QuantityFormatter.format(
                        item.getQuantity()
                )
        );

        // Existing expiry date
        etExpiry.setText(
                item.getExpiryDate() == null
                        ? ""
                        : item.getExpiryDate()
        );

        // Select the existing measurement unit
        for (int i = 0;
             i < units.length;
             i++) {

            if (units[i].equalsIgnoreCase(
                    item.getUnit())) {

                spinnerUnit.setSelection(i);

                break;
            }
        }
    }

    /**
     * Opens the Android calendar for selecting
     * an expiry date.
     */
    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        // -----------------------------------------
        // If editing an existing item, open the
        // calendar on its existing expiry date.
        // -----------------------------------------

        String existingDate =
                etExpiry
                        .getText()
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
                        dateFormat.parse(
                                existingDate
                        );

                if (parsedDate != null) {

                    calendar.setTime(
                            parsedDate
                    );
                }

            } catch (ParseException ignored) {

                // If the existing value cannot
                // be parsed, use today's date.
            }
        }

        int year =
                calendar.get(
                        Calendar.YEAR
                );

        int month =
                calendar.get(
                        Calendar.MONTH
                );

        int day =
                calendar.get(
                        Calendar.DAY_OF_MONTH
                );

        // -----------------------------------------
        // Create DatePickerDialog
        // -----------------------------------------

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

                            etExpiry.setText(
                                    selectedDate
                            );

                            etExpiry.setError(
                                    null
                            );
                        },

                        year,
                        month,
                        day
                );

        // -----------------------------------------
        // Prevent selection of past dates
        // -----------------------------------------

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
     * Validates the form and then adds or
     * updates the ingredient in SQLite.
     */
    private void saveIngredient() {

        // -----------------------------------------
        // Read entered information
        // -----------------------------------------

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

        // -----------------------------------------
        // Ingredient name validation
        // -----------------------------------------

        if (ingredientName.isEmpty()) {

            etIngredientName.setError(
                    "Ingredient name is required."
            );

            etIngredientName.requestFocus();

            return;
        }

        // Ingredient must contain at least
        // one alphabetic character.
        if (!ingredientName.matches(
                ".*[a-zA-Z].*")) {

            etIngredientName.setError(
                    "Enter a valid ingredient name."
            );

            etIngredientName.requestFocus();

            return;
        }

        // -----------------------------------------
        // Quantity validation
        // -----------------------------------------

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

        // -----------------------------------------
        // Expiry date validation
        // -----------------------------------------

        // Expiry is optional.
        // If supplied, it must be today
        // or a future date.
        if (!expiryDate.isEmpty()
                && !isValidDate(
                expiryDate)) {

            etExpiry.setError(
                    "Enter a valid expiry date that is today or later (YYYY-MM-DD)."
            );

            return;
        }

        // -----------------------------------------
        // Add new ingredient
        // -----------------------------------------

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

            // -------------------------------------
            // Update existing ingredient
            // -------------------------------------

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

        // Return to Pantry screen
        finish();
    }

    /**
     * Validates an expiry date.
     *
     * The date must:
     *
     * 1. Use YYYY-MM-DD.
     * 2. Be a real calendar date.
     * 3. Be today or a future date.
     */
    private boolean isValidDate(
            String date) {

        // -----------------------------------------
        // Check format
        // -----------------------------------------

        if (!date.matches(
                "\\d{4}-\\d{2}-\\d{2}")) {

            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        // Prevent dates such as:
        // 2026-02-31
        dateFormat.setLenient(false);

        try {

            Date enteredDate =
                    dateFormat.parse(
                            date
                    );

            if (enteredDate == null) {

                return false;
            }

            // -------------------------------------
            // Get today's date without time
            // -------------------------------------

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

            // Valid if the expiry date is today
            // or sometime in the future.
            return !enteredDate.before(
                    today.getTime()
            );

        } catch (ParseException e) {

            return false;
        }
    }
}