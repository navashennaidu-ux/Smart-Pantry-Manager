package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.utils.IngredientMatcher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;
    private RecyclerView recyclerRecipes;
    private TextView tvNoRecipes;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        // Initialise database
        databaseHelper = new DatabaseHelper(this);

        // Find views
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set up RecyclerView
        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Get recipes that strictly match pantry contents
        List<Recipe> matchingRecipes =
                IngredientMatcher.findMatchingRecipes(databaseHelper);

        recipeAdapter =
                new RecipeAdapter(matchingRecipes, this);

        recyclerRecipes.setAdapter(recipeAdapter);

        // Highlight Recipes in bottom navigation
        bottomNavigation.setSelectedItemId(R.id.nav_recipes);

        // Handle bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // Open Pantry
            if (itemId == R.id.nav_pantry) {

                Intent intent =
                        new Intent(
                                SuggestedRecipesActivity.this,
                                PantryActivity.class
                        );

                startActivity(intent);
                return true;
            }

            // Already on Recipes
            if (itemId == R.id.nav_recipes) {
                return true;
            }

            // Open Settings
            if (itemId == R.id.nav_settings) {

                Intent intent =
                        new Intent(
                                SuggestedRecipesActivity.this,
                                SettingsActivity.class
                        );

                startActivity(intent);
                return true;
            }

            return false;
        });

        refreshRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recipeAdapter != null) {
            refreshRecipes();
        }

        // Ensure Recipes remains highlighted
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_recipes);
        }
    }

    private void refreshRecipes() {

        List<Recipe> matchingRecipes =
                IngredientMatcher.findMatchingRecipes(databaseHelper);

        recipeAdapter.setRecipes(matchingRecipes);

        if (matchingRecipes.isEmpty()) {

            tvNoRecipes.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);

        } else {

            tvNoRecipes.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        Intent intent =
                new Intent(
                        SuggestedRecipesActivity.this,
                        RecipeDetailActivity.class
                );

        intent.putExtra("recipe_id", recipe.getId());

        startActivity(intent);
    }
}