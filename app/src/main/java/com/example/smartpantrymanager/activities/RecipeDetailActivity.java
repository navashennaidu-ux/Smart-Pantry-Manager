package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.List;

public class RecipeDetailActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipe_detail);

        TextView tvRecipeName =
                findViewById(
                        R.id.tvRecipeDetailName);

        TextView tvIngredients =
                findViewById(
                        R.id.tvRecipeIngredients);

        TextView tvMethod =
                findViewById(
                        R.id.tvRecipeMethod);

        Button btnBack =
                findViewById(
                        R.id.btnBack);

        DatabaseHelper databaseHelper =
                new DatabaseHelper(this);

        int recipeId =
                getIntent()
                        .getIntExtra(
                                "recipe_id",
                                -1);

        if (recipeId == -1) {

            Toast.makeText(
                            this,
                            "Recipe not found",
                            Toast.LENGTH_SHORT)
                    .show();

            finish();

            return;
        }

        Recipe recipe =
                databaseHelper
                        .getRecipe(recipeId);

        if (recipe == null) {

            Toast.makeText(
                            this,
                            "Recipe not found",
                            Toast.LENGTH_SHORT)
                    .show();

            finish();

            return;
        }

        tvRecipeName.setText(
                recipe.getName());

        tvMethod.setText(
                recipe.getInstructions());

        List<RecipeIngredient> ingredients =
                databaseHelper
                        .getRecipeIngredients(
                                recipeId);

        StringBuilder ingredientText =
                new StringBuilder();

        for (RecipeIngredient ingredient :
                ingredients) {

            ingredientText
                    .append("• ")
                    .append(
                            ingredient
                                    .getIngredientName())
                    .append(" - ")
                    .append(
                            ingredient
                                    .getRequiredQuantity())
                    .append(" ")
                    .append(
                            ingredient
                                    .getUnit())
                    .append("\n");
        }

        tvIngredients.setText(
                ingredientText.toString());

        btnBack.setOnClickListener(
                v -> finish());
    }
}