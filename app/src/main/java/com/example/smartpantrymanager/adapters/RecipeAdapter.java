package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.Recipe;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<
        RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {

        void onRecipeClick(
                Recipe recipe);
    }

    public RecipeAdapter(
            List<Recipe> recipes,
            OnRecipeClickListener listener) {

        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_recipe,
                                parent,
                                false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position) {

        Recipe recipe =
                recipes.get(position);

        holder.tvRecipeName.setText(
                recipe.getName());

        holder.btnViewRecipe
                .setOnClickListener(
                        v -> listener
                                .onRecipeClick(recipe));

        holder.itemView
                .setOnClickListener(
                        v -> listener
                                .onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {

        return recipes == null
                ? 0
                : recipes.size();
    }

    public void setRecipes(
            List<Recipe> recipes) {

        this.recipes = recipes;
        notifyDataSetChanged();
    }

    static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRecipeName;
        Button btnViewRecipe;

        public RecipeViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvRecipeName =
                    itemView.findViewById(
                            R.id.tvRecipeName);

            btnViewRecipe =
                    itemView.findViewById(
                            R.id.btnViewRecipe);
        }
    }
}