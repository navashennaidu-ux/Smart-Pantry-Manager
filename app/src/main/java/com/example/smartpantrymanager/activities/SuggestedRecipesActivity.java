package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.utils.IngredientMatcher;

import java.util.List;

public class SuggestedRecipesActivity
        extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerRecipes;
    private TextView tvNoRecipes;

    private DatabaseHelper databaseHelper;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_suggested_recipes);

        databaseHelper =
                new DatabaseHelper(this);

        recyclerRecipes =
                findViewById(
                        R.id.recyclerRecipes);

        tvNoRecipes =
                findViewById(
                        R.id.tvNoRecipes);

        Button btnPantry =
                findViewById(
                        R.id.btnPantry);

        Button btnSettings =
                findViewById(
                        R.id.btnSettings);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this));

        adapter =
                new RecipeAdapter(
                        IngredientMatcher
                                .findMatchingRecipes(
                                        databaseHelper),
                        this);

        recyclerRecipes.setAdapter(adapter);

        btnPantry.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SuggestedRecipesActivity.this,
                            PantryActivity.class);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();
        });

        btnSettings.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SuggestedRecipesActivity.this,
                            SettingsActivity.class);

            startActivity(intent);
        });

        refreshRecipes();
    }

    @Override
    protected void onResume() {

        super.onResume();

        refreshRecipes();
    }

    private void refreshRecipes() {

        List<Recipe> matches =
                IngredientMatcher
                        .findMatchingRecipes(
                                databaseHelper);

        adapter.setRecipes(matches);

        if (matches.isEmpty()) {

            tvNoRecipes.setVisibility(
                    View.VISIBLE);

            recyclerRecipes.setVisibility(
                    View.GONE);

        } else {

            tvNoRecipes.setVisibility(
                    View.GONE);

            recyclerRecipes.setVisibility(
                    View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(
            Recipe recipe) {

        Intent intent =
                new Intent(
                        this,
                        RecipeDetailActivity.class);

        intent.putExtra(
                "recipe_id",
                recipe.getId());

        startActivity(intent);
    }
}