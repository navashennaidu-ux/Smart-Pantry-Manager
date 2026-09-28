package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.List;

public class PantryActivity
        extends AppCompatActivity
        implements PantryAdapter.OnPantryItemListener {

    private RecyclerView recyclerPantry;
    private TextView tvEmptyPantry;

    private PantryAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_pantry);

        databaseHelper =
                new DatabaseHelper(this);

        recyclerPantry =
                findViewById(
                        R.id.recyclerPantry);

        tvEmptyPantry =
                findViewById(
                        R.id.tvEmptyPantry);

        Button btnAddIngredient =
                findViewById(
                        R.id.btnAddIngredient);

        Button btnRecipes =
                findViewById(
                        R.id.btnRecipes);

        Button btnSettings =
                findViewById(
                        R.id.btnSettings);

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this));

        adapter =
                new PantryAdapter(
                        databaseHelper.getAllPantryItems(),
                        this);

        recyclerPantry.setAdapter(adapter);

        btnAddIngredient.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PantryActivity.this,
                            AddEditIngredientActivity.class);

            startActivity(intent);
        });

        btnRecipes.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PantryActivity.this,
                            SuggestedRecipesActivity.class);

            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PantryActivity.this,
                            SettingsActivity.class);

            startActivity(intent);
        });

        refreshPantry();
    }

    @Override
    protected void onResume() {

        super.onResume();

        refreshPantry();
    }

    private void refreshPantry() {

        List<PantryItem> items =
                databaseHelper.getAllPantryItems();

        adapter.setPantryItems(items);

        if (items.isEmpty()) {

            tvEmptyPantry.setVisibility(
                    View.VISIBLE);

            recyclerPantry.setVisibility(
                    View.GONE);

        } else {

            tvEmptyPantry.setVisibility(
                    View.GONE);

            recyclerPantry.setVisibility(
                    View.VISIBLE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {

        Intent intent =
                new Intent(
                        this,
                        AddEditIngredientActivity.class);

        intent.putExtra(
                "item_id",
                item.getId());

        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Delete " +
                                item.getIngredientName() +
                                " from your pantry?")
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper
                                    .deletePantryItem(
                                            item.getId());

                            refreshPantry();
                        })
                .setNegativeButton(
                        "Cancel",
                        null)
                .show();
    }
}