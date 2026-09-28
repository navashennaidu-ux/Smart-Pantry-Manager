package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    public static List<Recipe> findMatchingRecipes(
            DatabaseHelper databaseHelper) {

        List<Recipe> matchingRecipes =
                new ArrayList<>();

        List<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        List<PantryItem> pantry =
                databaseHelper.getAllPantryItems();

        for (Recipe recipe : allRecipes) {

            List<RecipeIngredient> requiredIngredients =
                    databaseHelper.getRecipeIngredients(
                            recipe.getId());

            boolean canMakeRecipe = true;

            for (RecipeIngredient required :
                    requiredIngredients) {

                double availableQuantity =
                        getAvailableQuantity(
                                pantry,
                                required);

                double neededQuantity =
                        convertToBaseUnit(
                                required.getRequiredQuantity(),
                                required.getUnit());

                if (availableQuantity < neededQuantity) {

                    canMakeRecipe = false;
                    break;
                }
            }

            if (canMakeRecipe &&
                    !requiredIngredients.isEmpty()) {

                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }

    private static double getAvailableQuantity(
            List<PantryItem> pantry,
            RecipeIngredient required) {

        double total = 0;

        String requiredName =
                normalizeIngredientName(
                        required.getIngredientName());

        String requiredUnitType =
                getUnitType(required.getUnit());

        for (PantryItem pantryItem : pantry) {

            String pantryName =
                    normalizeIngredientName(
                            pantryItem.getIngredientName());

            String pantryUnitType =
                    getUnitType(
                            pantryItem.getUnit());

            if (pantryName.equals(requiredName) &&
                    pantryUnitType.equals(requiredUnitType)) {

                total += convertToBaseUnit(
                        pantryItem.getQuantity(),
                        pantryItem.getUnit());
            }
        }

        return total;
    }

    public static String normalizeIngredientName(
            String name) {

        if (name == null) {
            return "";
        }

        String result =
                name.trim()
                        .toLowerCase(Locale.ROOT);

        // Remove unnecessary spaces
        result = result.replaceAll("\\s+", " ");

        // Some common plural forms
        if (result.endsWith("atoes")) {
            // potatoes -> potato
            result =
                    result.substring(
                            0,
                            result.length() - 2);
        }
        else if (result.endsWith("oes")) {
            // tomatoes -> tomato
            result =
                    result.substring(
                            0,
                            result.length() - 2);
        }
        else if (result.endsWith("ies") &&
                result.length() > 3) {

            result =
                    result.substring(
                            0,
                            result.length() - 3)
                            + "y";
        }
        else if (result.endsWith("s") &&
                !result.endsWith("ss") &&
                result.length() > 3) {

            result =
                    result.substring(
                            0,
                            result.length() - 1);
        }

        return result;
    }

    public static double convertToBaseUnit(
            double quantity,
            String unit) {

        if (unit == null) {
            return quantity;
        }

        String normalized =
                unit.trim()
                        .toLowerCase(Locale.ROOT);

        switch (normalized) {

            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;

            case "g":
            case "gram":
            case "grams":
                return quantity;

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity * 1000;

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return quantity;

            case "each":
            case "item":
            case "items":
            case "piece":
            case "pieces":
            case "unit":
            case "units":
                return quantity;

            default:
                return quantity;
        }
    }

    public static String getUnitType(
            String unit) {

        if (unit == null) {
            return "unknown";
        }

        String normalized =
                unit.trim()
                        .toLowerCase(Locale.ROOT);

        switch (normalized) {

            case "kg":
            case "kilogram":
            case "kilograms":
            case "g":
            case "gram":
            case "grams":
                return "mass";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "volume";

            case "each":
            case "item":
            case "items":
            case "piece":
            case "pieces":
            case "unit":
            case "units":
                return "count";

            default:
                return normalized;
        }
    }
}