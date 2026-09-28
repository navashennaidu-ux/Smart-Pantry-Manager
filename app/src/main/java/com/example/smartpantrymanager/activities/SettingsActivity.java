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

public class SettingsActivity
        extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Spinner spinnerUnitPreference;

    private SharedPreferences preferences;

    private final String[] unitOptions = {
            "Metric",
            "Standard"
    };

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_settings);

        switchExpiryAlerts =
                findViewById(
                        R.id.switchExpiryAlerts);

        spinnerUnitPreference =
                findViewById(
                        R.id.spinnerUnitPreference);

        Button btnSave =
                findViewById(
                        R.id.btnSaveSettings);

        Button btnPantry =
                findViewById(
                        R.id.btnPantry);

        Button btnRecipes =
                findViewById(
                        R.id.btnRecipes);

        preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_item,
                        unitOptions);

        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        spinnerUnitPreference
                .setAdapter(adapter);

        loadSettings();

        btnSave.setOnClickListener(
                v -> saveSettings());

        btnPantry.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            PantryActivity.class);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();
        });

        btnRecipes.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            SuggestedRecipesActivity.class);

            startActivity(intent);
        });
    }

    private void loadSettings() {

        boolean alertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts",
                        true);

        String unitPreference =
                preferences.getString(
                        "unit_preference",
                        "Metric");

        switchExpiryAlerts.setChecked(
                alertsEnabled);

        if ("Standard".equals(
                unitPreference)) {

            spinnerUnitPreference
                    .setSelection(1);

        } else {

            spinnerUnitPreference
                    .setSelection(0);
        }
    }

    private void saveSettings() {

        SharedPreferences.Editor editor =
                preferences.edit();

        editor.putBoolean(
                "expiry_alerts",
                switchExpiryAlerts
                        .isChecked());

        editor.putString(
                "unit_preference",
                spinnerUnitPreference
                        .getSelectedItem()
                        .toString());

        editor.apply();

        Toast.makeText(
                        this,
                        "Settings saved",
                        Toast.LENGTH_SHORT)
                .show();
    }
}