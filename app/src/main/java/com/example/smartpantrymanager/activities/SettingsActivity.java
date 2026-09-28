package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_UNIT_PREFERENCE = "unit_preference";

    private Switch switchExpiryAlerts;
    private Spinner spinnerUnitPreference;
    private BottomNavigationView bottomNavigation;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Find views
        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        spinnerUnitPreference = findViewById(R.id.spinnerUnitPreference);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        Button btnSaveSettings =
                findViewById(R.id.btnSaveSettings);

        // Open SharedPreferences
        sharedPreferences =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Measurement options
        String[] measurementOptions = {
                "Metric",
                "Standard"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        measurementOptions
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnitPreference.setAdapter(spinnerAdapter);

        // Load previously saved settings
        loadSettings();

        // Save settings
        btnSaveSettings.setOnClickListener(v -> saveSettings());

        // Highlight Settings
        bottomNavigation.setSelectedItemId(R.id.nav_settings);

        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // Open Pantry
            if (itemId == R.id.nav_pantry) {

                Intent intent =
                        new Intent(
                                SettingsActivity.this,
                                PantryActivity.class
                        );

                startActivity(intent);
                return true;
            }

            // Open Recipes
            if (itemId == R.id.nav_recipes) {

                Intent intent =
                        new Intent(
                                SettingsActivity.this,
                                SuggestedRecipesActivity.class
                        );

                startActivity(intent);
                return true;
            }

            // Already on Settings
            if (itemId == R.id.nav_settings) {
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Make sure Settings stays selected
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_settings);
        }
    }

    private void loadSettings() {

        boolean expiryAlerts =
                sharedPreferences.getBoolean(
                        KEY_EXPIRY_ALERTS,
                        true
                );

        String unitPreference =
                sharedPreferences.getString(
                        KEY_UNIT_PREFERENCE,
                        "Metric"
                );

        switchExpiryAlerts.setChecked(expiryAlerts);

        if ("Standard".equals(unitPreference)) {
            spinnerUnitPreference.setSelection(1);
        } else {
            spinnerUnitPreference.setSelection(0);
        }
    }

    private void saveSettings() {

        boolean expiryAlerts =
                switchExpiryAlerts.isChecked();

        String unitPreference =
                spinnerUnitPreference
                        .getSelectedItem()
                        .toString();

        sharedPreferences
                .edit()
                .putBoolean(
                        KEY_EXPIRY_ALERTS,
                        expiryAlerts
                )
                .putString(
                        KEY_UNIT_PREFERENCE,
                        unitPreference
                )
                .apply();

        Toast.makeText(
                this,
                "Settings saved",
                Toast.LENGTH_SHORT
        ).show();
    }
}