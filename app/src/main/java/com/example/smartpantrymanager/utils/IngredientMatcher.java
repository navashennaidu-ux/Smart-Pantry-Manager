package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    /**
     * Returns only recipes for which the pantry contains
     * EVERY required ingredient in a sufficient quantity.
     */
    public static List<Recipe> findMatchingRecipes(
            DatabaseHelper databaseHelper) {

        List<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        List<Recipe> matchingRecipes =
                new ArrayList<>();

        for (Recipe recipe : allRecipes) {

            List<RecipeIngredient> requiredIngredients =
                    databaseHelper.getRecipeIngredients(
                            recipe.getId()
                    );

            boolean completeMatch = true;

            for (RecipeIngredient required :
                    requiredIngredients) {

                if (!hasEnoughIngredient(
                        required,
                        pantryItems)) {

                    completeMatch = false;
                    break;
                }
            }

            if (completeMatch) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }

    /**
     * Checks whether the pantry contains enough of
     * one particular recipe ingredient.
     *
     * Multiple pantry entries for the same ingredient
     * are added together.
     */
    private static boolean hasEnoughIngredient(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        String requiredName =
                normalizeIngredientName(
                        required.getIngredientName()
                );

        String requiredUnitType =
                getUnitType(required.getUnit());

        double requiredQuantity =
                convertToBaseUnit(
                        required.getRequiredQuantity(),
                        required.getUnit()
                );

        double availableQuantity = 0;

        for (PantryItem pantryItem :
                pantryItems) {

            String pantryName =
                    normalizeIngredientName(
                            pantryItem.getIngredientName()
                    );

            // Ingredient names must match
            if (!requiredName.equals(pantryName)) {
                continue;
            }

            String pantryUnitType =
                    getUnitType(
                            pantryItem.getUnit()
                    );

            // Units must belong to the same category.
            // For example, grams cannot satisfy millilitres.
            if (!requiredUnitType.equals(
                    pantryUnitType)) {
                continue;
            }

            availableQuantity +=
                    convertToBaseUnit(
                            pantryItem.getQuantity(),
                            pantryItem.getUnit()
                    );
        }

        return availableQuantity >=
                requiredQuantity;
    }

    /**
     * Normalises ingredient names so simple singular
     * and plural variations can be matched.
     *
     * Examples:
     * Eggs     -> egg
     * Tomatoes -> tomato
     * Potatoes -> potato
     * Berries  -> berry
     */
    private static String normalizeIngredientName(
            String ingredientName) {

        if (ingredientName == null) {
            return "";
        }

        String name =
                ingredientName
                        .toLowerCase(Locale.ROOT)
                        .trim()
                        .replaceAll("\\s+", " ");

        // Specific common pantry plurals
        if (name.equals("tomatoes")) {
            return "tomato";
        }

        if (name.equals("potatoes")) {
            return "potato";
        }

        // Example: berries -> berry
        if (name.endsWith("ies")
                && name.length() > 3) {

            return name.substring(
                    0,
                    name.length() - 3
            ) + "y";
        }

        // Example:
        // eggs -> egg
        // bananas -> banana
        // apples -> apple
        //
        // Avoid removing the final s from words
        // that naturally end in ss.
        if (name.endsWith("s")
                && !name.endsWith("ss")
                && name.length() > 1) {

            return name.substring(
                    0,
                    name.length() - 1
            );
        }

        return name;
    }

    /**
     * Converts compatible measurements into a common
     * base unit.
     *
     * Mass   -> grams
     * Volume -> millilitres
     * Count  -> individual items
     */
    private static double convertToBaseUnit(
            double quantity,
            String unit) {

        String normalizedUnit =
                normalizeUnit(unit);

        switch (normalizedUnit) {

            case "kg":
                return quantity * 1000.0;

            case "g":
                return quantity;

            case "l":
                return quantity * 1000.0;

            case "ml":
                return quantity;

            case "each":
                return quantity;

            default:
                return quantity;
        }
    }

    /**
     * Identifies whether a measurement represents
     * mass, volume or a count.
     */
    private static String getUnitType(
            String unit) {

        String normalizedUnit =
                normalizeUnit(unit);

        switch (normalizedUnit) {

            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
                return "volume";

            case "each":
                return "count";

            default:
                return "unknown";
        }
    }

    /**
     * Normalises common unit names.
     */
    private static String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String normalized =
                unit.toLowerCase(Locale.ROOT)
                        .trim();

        switch (normalized) {

            // Grams
            case "gram":
            case "grams":
                return "g";

            // Kilograms
            case "kilogram":
            case "kilograms":
                return "kg";

            // Millilitres
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return "ml";

            // Litres
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return "l";

            // Countable items
            case "item":
            case "items":
            case "piece":
            case "pieces":
            case "unit":
            case "units":
                return "each";

            default:
                return normalized;
        }
    }}