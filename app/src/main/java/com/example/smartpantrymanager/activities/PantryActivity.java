package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class PantryActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemListener {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private RecyclerView recyclerPantry;
    private android.widget.TextView tvEmptyPantry;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // Initialise database
        databaseHelper = new DatabaseHelper(this);

        // Find views
        recyclerPantry = findViewById(R.id.recyclerPantry);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set up RecyclerView
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        pantryAdapter = new PantryAdapter(pantryItems, this);
        recyclerPantry.setAdapter(pantryAdapter);

        // Add Ingredient button
        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> {
            Intent intent =
                    new Intent(PantryActivity.this,
                            AddEditIngredientActivity.class);

            startActivity(intent);
        });

        // Highlight Pantry in bottom navigation
        bottomNavigation.setSelectedItemId(R.id.nav_pantry);

        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // Already on Pantry
            if (itemId == R.id.nav_pantry) {
                return true;
            }

            // Open Suggested Recipes
            if (itemId == R.id.nav_recipes) {
                Intent intent =
                        new Intent(PantryActivity.this,
                                SuggestedRecipesActivity.class);

                startActivity(intent);
                return true;
            }

            // Open Settings
            if (itemId == R.id.nav_settings) {
                Intent intent =
                        new Intent(PantryActivity.this,
                                SettingsActivity.class);

                startActivity(intent);
                return true;
            }

            return false;
        });

        refreshPantry();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && pantryAdapter != null) {
            refreshPantry();
        }

        // Ensure Pantry is highlighted when returning to this screen
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_pantry);
        }
    }

    private void refreshPantry() {

        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        pantryAdapter.setPantryItems(pantryItems);

        if (pantryItems.isEmpty()) {

            tvEmptyPantry.setVisibility(View.VISIBLE);
            recyclerPantry.setVisibility(View.GONE);

        } else {

            tvEmptyPantry.setVisibility(View.GONE);
            recyclerPantry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {

        Intent intent =
                new Intent(PantryActivity.this,
                        AddEditIngredientActivity.class);

        intent.putExtra("item_id", item.getId());

        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getIngredientName()
                                + "?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {

                    databaseHelper.deletePantryItem(item.getId());

                    refreshPantry();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}