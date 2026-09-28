package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String PANTRY_ID = "id";
    public static final String PANTRY_NAME = "ingredient_name";
    public static final String PANTRY_QUANTITY = "quantity";
    public static final String PANTRY_UNIT = "unit";
    public static final String PANTRY_EXPIRY = "expiry_date";

    // Recipe table
    public static final String TABLE_RECIPES = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_INSTRUCTIONS = "instructions";

    // Recipe ingredient table
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String RI_ID = "id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_NAME = "ingredient_name";
    public static final String RI_QUANTITY = "required_quantity";
    public static final String RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createPantryTable =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PANTRY_NAME + " TEXT NOT NULL, " +
                        PANTRY_QUANTITY + " REAL NOT NULL, " +
                        PANTRY_UNIT + " TEXT NOT NULL, " +
                        PANTRY_EXPIRY + " TEXT" +
                        ")";

        String createRecipesTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RECIPE_NAME + " TEXT NOT NULL, " +
                        RECIPE_INSTRUCTIONS + " TEXT NOT NULL" +
                        ")";

        String createRecipeIngredientsTable =
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RI_RECIPE_ID + " INTEGER NOT NULL, " +
                        RI_NAME + " TEXT NOT NULL, " +
                        RI_QUANTITY + " REAL NOT NULL, " +
                        RI_UNIT + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + RI_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + RECIPE_ID + ") " +
                        "ON DELETE CASCADE" +
                        ")";

        db.execSQL(createPantryTable);
        db.execSQL(createRecipesTable);
        db.execSQL(createRecipeIngredientsTable);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " +
                TABLE_RECIPE_INGREDIENTS);

        db.execSQL("DROP TABLE IF EXISTS " +
                TABLE_RECIPES);

        db.execSQL("DROP TABLE IF EXISTS " +
                TABLE_PANTRY);

        onCreate(db);
    }

    // --------------------------------------------------
    // PANTRY CRUD
    // --------------------------------------------------

    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PANTRY_NAME, item.getIngredientName());
        values.put(PANTRY_QUANTITY, item.getQuantity());
        values.put(PANTRY_UNIT, item.getUnit());
        values.put(PANTRY_EXPIRY, item.getExpiryDate());

        return db.insert(
                TABLE_PANTRY,
                null,
                values);
    }

    public List<PantryItem> getAllPantryItems() {

        List<PantryItem> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                PANTRY_NAME + " ASC");

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(PANTRY_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(PANTRY_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(PANTRY_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(PANTRY_UNIT));

                String expiry = cursor.getString(
                        cursor.getColumnIndexOrThrow(PANTRY_EXPIRY));

                PantryItem item =
                        new PantryItem(
                                id,
                                name,
                                quantity,
                                unit,
                                expiry);

                items.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return items;
    }

    public PantryItem getPantryItem(int id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                PANTRY_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        PantryItem item = null;

        if (cursor.moveToFirst()) {

            item = new PantryItem(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(PANTRY_ID)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(PANTRY_NAME)),
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(PANTRY_QUANTITY)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(PANTRY_UNIT)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(PANTRY_EXPIRY))
            );
        }

        cursor.close();

        return item;
    }

    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PANTRY_NAME, item.getIngredientName());
        values.put(PANTRY_QUANTITY, item.getQuantity());
        values.put(PANTRY_UNIT, item.getUnit());
        values.put(PANTRY_EXPIRY, item.getExpiryDate());

        return db.update(
                TABLE_PANTRY,
                values,
                PANTRY_ID + "=?",
                new String[]{
                        String.valueOf(item.getId())
                });
    }

    public int deletePantryItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                PANTRY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                });
    }

    // --------------------------------------------------
    // RECIPES
    // --------------------------------------------------

    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.query(
                        TABLE_RECIPES,
                        null,
                        null,
                        null,
                        null,
                        null,
                        RECIPE_NAME + " ASC");

        if (cursor.moveToFirst()) {

            do {

                Recipe recipe =
                        new Recipe(
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow(
                                                RECIPE_ID)),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                RECIPE_NAME)),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                RECIPE_INSTRUCTIONS))
                        );

                recipes.add(recipe);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return recipes;
    }

    public Recipe getRecipe(int recipeId) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.query(
                        TABLE_RECIPES,
                        null,
                        RECIPE_ID + "=?",
                        new String[]{
                                String.valueOf(recipeId)
                        },
                        null,
                        null,
                        null);

        Recipe recipe = null;

        if (cursor.moveToFirst()) {

            recipe =
                    new Recipe(
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            RECIPE_ID)),
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            RECIPE_NAME)),
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            RECIPE_INSTRUCTIONS))
                    );
        }

        cursor.close();

        return recipe;
    }

    public List<RecipeIngredient>
    getRecipeIngredients(int recipeId) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.query(
                        TABLE_RECIPE_INGREDIENTS,
                        null,
                        RI_RECIPE_ID + "=?",
                        new String[]{
                                String.valueOf(recipeId)
                        },
                        null,
                        null,
                        RI_NAME + " ASC");

        if (cursor.moveToFirst()) {

            do {

                RecipeIngredient ingredient =
                        new RecipeIngredient(
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow(
                                                RI_ID)),
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow(
                                                RI_RECIPE_ID)),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                RI_NAME)),
                                cursor.getDouble(
                                        cursor.getColumnIndexOrThrow(
                                                RI_QUANTITY)),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                RI_UNIT))
                        );

                ingredients.add(ingredient);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return ingredients;
    }

    // --------------------------------------------------
    // SEED RECIPES
    // --------------------------------------------------

    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String instructions) {

        ContentValues values =
                new ContentValues();

        values.put(RECIPE_NAME, name);
        values.put(RECIPE_INSTRUCTIONS,
                instructions);

        return db.insert(
                TABLE_RECIPES,
                null,
                values);
    }

    private void insertRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String name,
            double quantity,
            String unit) {

        ContentValues values =
                new ContentValues();

        values.put(RI_RECIPE_ID, recipeId);
        values.put(RI_NAME, name);
        values.put(RI_QUANTITY, quantity);
        values.put(RI_UNIT, unit);

        db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values);
    }

    private void seedRecipes(SQLiteDatabase db) {

        long recipeId;

        // 1
        recipeId = insertRecipe(
                db,
                "Scrambled Eggs",
                "1. Crack the eggs into a bowl.\n" +
                        "2. Add milk and whisk.\n" +
                        "3. Heat butter in a pan.\n" +
                        "4. Add egg mixture and stir until cooked.");

        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "milk", 30, "ml");
        insertRecipeIngredient(db, recipeId,
                "butter", 10, "g");

        // 2
        recipeId = insertRecipe(
                db,
                "Cheese Omelette",
                "1. Beat eggs and milk.\n" +
                        "2. Pour into a heated pan.\n" +
                        "3. Add cheese.\n" +
                        "4. Fold and cook until ready.");

        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "milk", 30, "ml");
        insertRecipeIngredient(db, recipeId,
                "cheese", 50, "g");

        // 3
        recipeId = insertRecipe(
                db,
                "Tomato Toast",
                "1. Toast the bread.\n" +
                        "2. Slice the tomato.\n" +
                        "3. Place tomato on toast.\n" +
                        "4. Season and serve.");

        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "tomato", 1, "each");

        // 4
        recipeId = insertRecipe(
                db,
                "Cheese Toast",
                "1. Place cheese on bread.\n" +
                        "2. Toast until golden and melted.");

        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "cheese", 50, "g");

        // 5
        recipeId = insertRecipe(
                db,
                "Egg Sandwich",
                "1. Cook the eggs.\n" +
                        "2. Place between slices of bread.\n" +
                        "3. Serve warm.");

        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");

        // 6
        recipeId = insertRecipe(
                db,
                "Banana Milkshake",
                "1. Peel banana.\n" +
                        "2. Add banana and milk to blender.\n" +
                        "3. Blend until smooth.");

        insertRecipeIngredient(db, recipeId,
                "banana", 1, "each");
        insertRecipeIngredient(db, recipeId,
                "milk", 250, "ml");

        // 7
        recipeId = insertRecipe(
                db,
                "Simple Fruit Bowl",
                "1. Chop apple and banana.\n" +
                        "2. Combine in a bowl.");

        insertRecipeIngredient(db, recipeId,
                "apple", 1, "each");
        insertRecipeIngredient(db, recipeId,
                "banana", 1, "each");

        // 8
        recipeId = insertRecipe(
                db,
                "Tomato Egg Scramble",
                "1. Chop tomato.\n" +
                        "2. Scramble eggs in a pan.\n" +
                        "3. Add tomato and cook together.");

        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "tomato", 1, "each");

        // 9
        recipeId = insertRecipe(
                db,
                "Buttered Toast",
                "1. Toast bread.\n" +
                        "2. Spread butter over warm toast.");

        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "butter", 20, "g");

        // 10
        recipeId = insertRecipe(
                db,
                "Cheesy Scrambled Eggs",
                "1. Beat eggs.\n" +
                        "2. Cook slowly in pan.\n" +
                        "3. Stir in cheese before serving.");

        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "cheese", 40, "g");
        insertRecipeIngredient(db, recipeId,
                "butter", 10, "g");

        // 11
        recipeId = insertRecipe(
                db,
                "Potato Mash",
                "1. Peel and boil potatoes.\n" +
                        "2. Drain water.\n" +
                        "3. Add milk and butter.\n" +
                        "4. Mash until smooth.");

        insertRecipeIngredient(db, recipeId,
                "potato", 3, "each");
        insertRecipeIngredient(db, recipeId,
                "milk", 100, "ml");
        insertRecipeIngredient(db, recipeId,
                "butter", 20, "g");

        // 12
        recipeId = insertRecipe(
                db,
                "Tomato Cheese Sandwich",
                "1. Slice tomato.\n" +
                        "2. Place cheese and tomato on bread.\n" +
                        "3. Close sandwich and serve.");

        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "tomato", 1, "each");
        insertRecipeIngredient(db, recipeId,
                "cheese", 40, "g");

        // 13
        recipeId = insertRecipe(
                db,
                "Rice and Egg Bowl",
                "1. Cook rice.\n" +
                        "2. Fry eggs.\n" +
                        "3. Serve eggs over cooked rice.");

        insertRecipeIngredient(db, recipeId,
                "rice", 100, "g");
        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");

        // 14
        recipeId = insertRecipe(
                db,
                "Garlic Rice",
                "1. Cook rice.\n" +
                        "2. Melt butter in pan.\n" +
                        "3. Add chopped garlic.\n" +
                        "4. Stir rice into garlic butter.");

        insertRecipeIngredient(db, recipeId,
                "rice", 100, "g");
        insertRecipeIngredient(db, recipeId,
                "garlic", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "butter", 15, "g");

        // 15
        recipeId = insertRecipe(
                db,
                "Tomato Rice",
                "1. Cook rice.\n" +
                        "2. Chop tomato.\n" +
                        "3. Cook tomato until soft.\n" +
                        "4. Mix with rice.");

        insertRecipeIngredient(db, recipeId,
                "rice", 100, "g");
        insertRecipeIngredient(db, recipeId,
                "tomato", 2, "each");

        // 16
        recipeId = insertRecipe(
                db,
                "Banana Toast",
                "1. Toast bread.\n" +
                        "2. Slice banana.\n" +
                        "3. Place banana slices on toast.");

        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "banana", 1, "each");

        // 17
        recipeId = insertRecipe(
                db,
                "Apple Banana Smoothie",
                "1. Chop fruit.\n" +
                        "2. Add fruit and milk to blender.\n" +
                        "3. Blend until smooth.");

        insertRecipeIngredient(db, recipeId,
                "apple", 1, "each");
        insertRecipeIngredient(db, recipeId,
                "banana", 1, "each");
        insertRecipeIngredient(db, recipeId,
                "milk", 200, "ml");

        // 18
        recipeId = insertRecipe(
                db,
                "Garlic Toast",
                "1. Mix garlic with butter.\n" +
                        "2. Spread onto bread.\n" +
                        "3. Toast until golden.");

        insertRecipeIngredient(db, recipeId,
                "bread", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "garlic", 1, "each");
        insertRecipeIngredient(db, recipeId,
                "butter", 20, "g");

        // 19
        recipeId = insertRecipe(
                db,
                "Cheesy Potato",
                "1. Boil potatoes until soft.\n" +
                        "2. Place cheese over potatoes.\n" +
                        "3. Heat until cheese melts.");

        insertRecipeIngredient(db, recipeId,
                "potato", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "cheese", 60, "g");

        // 20
        recipeId = insertRecipe(
                db,
                "Egg Fried Rice",
                "1. Cook rice and allow it to cool slightly.\n" +
                        "2. Scramble eggs in a pan.\n" +
                        "3. Add cooked rice.\n" +
                        "4. Stir together until hot.");

        insertRecipeIngredient(db, recipeId,
                "rice", 100, "g");
        insertRecipeIngredient(db, recipeId,
                "egg", 2, "each");
        insertRecipeIngredient(db, recipeId,
                "butter", 10, "g");
    }
}