# Smart Pantry Manager

Smart Pantry Manager is an Android application developed in Java for the Mobile App Development 700 project.

The application allows users to maintain a digital pantry, record ingredient quantities and expiry dates, and receive recipe suggestions based strictly on the ingredients and quantities currently available.

## Project Purpose

The purpose of Smart Pantry Manager is to help users keep track of food ingredients they already have and identify recipes that can be prepared without requiring missing ingredients.

Instead of displaying partial recipe matches, the application checks every required ingredient before suggesting a recipe. A recipe is only displayed when the pantry contains all required ingredients in sufficient quantities.

## Technologies Used

- Java
- Android Studio
- Android SDK
- SQLite
- SQLiteOpenHelper
- RecyclerView
- Material Components
- SharedPreferences
- Git
- GitHub

The application logic is written in Java.

## Main Features

### Pantry Management

Users can:

- Add pantry ingredients
- View stored ingredients
- Edit existing ingredients
- Delete ingredients
- Record ingredient quantities
- Select measurement units
- Add optional expiry dates

Pantry information is stored in a local SQLite database and remains available after the application is closed and reopened.

### Input Validation

The ingredient form validates user input before information is stored.

Validation includes:

- Ingredient name is required
- Ingredient name must contain valid alphabetic characters
- Quantity is required
- Quantity must be greater than zero
- Excessively large quantities are rejected
- Expiry date is optional
- Expiry dates cannot be in the past
- Invalid calendar dates are rejected

A DatePicker is provided for selecting expiry dates.

### Expiry Tracking

Pantry items with expiry dates display the remaining number of days.

Examples include:

- Expires today
- Expires tomorrow
- Expires in 5 days
- 30 days remaining

Ingredients expiring within seven days can display an expiry warning when expiry alerts are enabled in Settings.

### Recipe Database

The SQLite database contains 20 predefined recipes.

Each recipe contains:

- Recipe name
- Required ingredients
- Required quantities
- Measurement units
- Preparation instructions

Recipe information is stored using separate recipe and recipe-ingredient tables.

### Strict Recipe Matching

Recipe suggestions use strict ingredient matching.

A recipe is displayed only when:

1. Every required ingredient exists in the pantry.
2. The available quantity is equal to or greater than the required quantity.
3. The pantry and recipe measurement units are compatible.

Partial recipe matches are excluded.

For example, if a Cheese Omelette requires:

- 2 eggs
- 30 ml milk
- 50 g cheese

the recipe will not appear if the user only has eggs and milk.

It will also not appear if the user has less than 50 g of cheese.

### Unit Conversion

The recipe matching system supports compatible unit conversion.

Examples:

- 1 kg = 1000 g
- 1 L = 1000 ml

Count-based units are also normalised for matching.

### Ingredient Name Normalisation

Simple singular and plural ingredient names are normalised during recipe matching.

Examples:

- Egg / Eggs
- Tomato / Tomatoes
- Potato / Potatoes
- Berry / Berries

This allows normal pantry naming variations to match the recipe database.

### Suggested Recipes

The Suggested Recipes screen displays only recipes that can currently be prepared using the pantry contents.

When no recipes match, the application displays a clear message informing the user that additional ingredients are required.

### Recipe Details

Users can open a matching recipe to view:

- Recipe name
- Required ingredients
- Required quantities
- Preparation method

### Settings

The Settings screen allows users to:

- Enable or disable expiring-soon warnings
- Save a preferred measurement-system setting

Settings are persisted using Android SharedPreferences.

## Application Screens

The application contains the following main screens:

1. Pantry
2. Add/Edit Ingredient
3. Suggested Recipes
4. Recipe Detail
5. Settings

Activities communicate using Android Intents.

A Material bottom navigation component provides navigation between Pantry, Recipes and Settings.

## Database Design

Smart Pantry Manager uses SQLite through `SQLiteOpenHelper`.

The database contains three main tables.

### pantry_items

Stores user pantry information.

Main fields:

- id
- ingredient_name
- quantity
- unit
- expiry_date

### recipes

Stores predefined recipe information.

Main fields:

- id
- recipe_name
- instructions

### recipe_ingredients

Stores the ingredients required by each recipe.

Main fields:

- id
- recipe_id
- ingredient_name
- required_quantity
- unit

The `recipe_id` field associates recipe ingredients with their corresponding recipe.

## Why SQLite Was Selected

SQLite was selected because Smart Pantry Manager primarily requires structured local data storage.

It provides several advantages for this application:

- Works offline
- Requires no external server
- Is integrated into Android
- Supports relational data
- Supports CRUD operations
- Provides persistent local storage
- Is suitable for pantry and recipe information

The relational structure also allows recipes and their required ingredients to be stored separately while remaining linked through recipe IDs.

## Application Architecture

The project separates responsibilities into packages.

### activities

Contains Android screens and user interaction logic.

### adapters

Contains RecyclerView adapters used to display pantry items and recipes.

### database

Contains the SQLite database helper and database operations.

### models

Contains Java model classes representing pantry items, recipes and recipe ingredients.

### utils

Contains reusable application logic including:

- Ingredient matching
- Quantity formatting
- Expiry calculations

This structure improves readability and separates database, interface and business logic.

## Recipe Matching Example

Assume the pantry contains:

- Eggs: 2 each
- Milk: 1 L
- Cheese: 40 g

Cheese Omelette will not be suggested because 50 g of cheese is required.

If the cheese quantity is changed to 60 g, Cheese Omelette becomes available.

The same example demonstrates unit conversion because the recipe requires only 30 ml of milk while the pantry contains 1 L.

## Testing

The application was manually tested for:

- Adding pantry ingredients
- Editing pantry ingredients
- Deleting pantry ingredients
- SQLite persistence
- Input validation
- Date validation
- DatePicker operation
- Expiry calculations
- Expiry warning preferences
- Empty pantry feedback
- Zero matching recipe feedback
- Strict recipe matching
- Insufficient quantity rejection
- Singular and plural ingredient matching
- Gram/kilogram conversion
- Millilitre/litre conversion
- Recipe detail navigation
- Bottom navigation
- Settings persistence

## Running the Project

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise.
4. Select an Android emulator or compatible Android device.
5. Build the project.
6. Run the application.

The project was tested using an Android emulator running API 34.

## Version Control

Git and GitHub are used for version control.

Development changes are recorded through meaningful commits covering functionality, validation, recipe matching, navigation, expiry handling and interface improvements.

## Author

Navashen

## Academic Project

This project was developed as part of the Mobile App Development 700 written assessment.