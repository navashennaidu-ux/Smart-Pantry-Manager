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
import com.example.smartpantrymanager.utils.QuantityFormatter;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        // Find views
        TextView tvRecipeName =
                findViewById(R.id.tvRecipeDetailName);

        TextView tvIngredients =
                findViewById(R.id.tvRecipeIngredients);

        TextView tvMethod =
                findViewById(R.id.tvRecipeMethod);

        Button btnBack =
                findViewById(R.id.btnBack);

        // Connect to SQLite database
        DatabaseHelper databaseHelper =
                new DatabaseHelper(this);

        // Get the recipe ID sent from
        // SuggestedRecipesActivity
        int recipeId =
                getIntent().getIntExtra(
                        "recipe_id",
                        -1
                );

        // Make sure a valid recipe ID was supplied
        if (recipeId == -1) {

            Toast.makeText(
                    this,
                    "Recipe not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Retrieve the selected recipe
        Recipe recipe =
                databaseHelper.getRecipe(recipeId);

        if (recipe == null) {

            Toast.makeText(
                    this,
                    "Recipe not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Display recipe name
        tvRecipeName.setText(
                recipe.getName()
        );

        // Display preparation method
        tvMethod.setText(
                recipe.getInstructions()
        );

        // Retrieve the ingredients required
        // for this recipe
        List<RecipeIngredient> ingredients =
                databaseHelper.getRecipeIngredients(
                        recipeId
                );

        StringBuilder ingredientText =
                new StringBuilder();

        // Build the ingredient list
        for (RecipeIngredient ingredient :
                ingredients) {

            ingredientText
                    .append("• ")
                    .append(
                            ingredient
                                    .getIngredientName()
                    )
                    .append(" - ")
                    .append(
                            QuantityFormatter.format(
                                    ingredient
                                            .getRequiredQuantity()
                            )
                    )
                    .append(" ")
                    .append(
                            ingredient
                                    .getUnit()
                    )
                    .append("\n");
        }

        // Display the complete ingredient list
        tvIngredients.setText(
                ingredientText.toString()
        );

        // Return to the Suggested Recipes screen
        btnBack.setOnClickListener(
                v -> finish()
        );
    }
}