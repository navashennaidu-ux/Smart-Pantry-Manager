package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.utils.ExpiryUtils;
import com.example.smartpantrymanager.utils.QuantityFormatter;

import java.util.List;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private static final String PREFS_NAME =
            "SmartPantrySettings";

    private static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts";

    private List<PantryItem> pantryItems;
    private final OnPantryItemListener listener;

    public interface OnPantryItemListener {

        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    public PantryAdapter(
            List<PantryItem> pantryItems,
            OnPantryItemListener listener) {

        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_pantry,
                                parent,
                                false
                        );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item =
                pantryItems.get(position);

        // -----------------------------
        // Ingredient name
        // -----------------------------

        holder.tvIngredientName.setText(
                item.getIngredientName()
        );

        // -----------------------------
        // Quantity
        // -----------------------------

        holder.tvQuantity.setText(
                QuantityFormatter.format(
                        item.getQuantity()
                )
                        + " "
                        + item.getUnit()
        );

        // -----------------------------
        // Expiry information
        // -----------------------------

        String expiryDisplay =
                ExpiryUtils.getExpiryDisplay(
                        item.getExpiryDate()
                );

        // Read the user's saved expiry-alert setting.
        Context context =
                holder.itemView.getContext();

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        KEY_EXPIRY_ALERTS,
                        true
                );

        // Add a warning symbol only when:
        // 1. Expiry alerts are enabled.
        // 2. The ingredient expires within 7 days.
        if (expiryAlertsEnabled
                && ExpiryUtils.isExpiringSoon(
                item.getExpiryDate())) {

            holder.tvExpiry.setText(
                    "⚠ " + expiryDisplay
            );

        } else {

            holder.tvExpiry.setText(
                    expiryDisplay
            );
        }

        // -----------------------------
        // Edit button
        // -----------------------------

        holder.btnEdit.setOnClickListener(
                v -> listener.onEdit(item)
        );

        // -----------------------------
        // Delete button
        // -----------------------------

        holder.btnDelete.setOnClickListener(
                v -> listener.onDelete(item)
        );
    }

    @Override
    public int getItemCount() {

        if (pantryItems == null) {
            return 0;
        }

        return pantryItems.size();
    }

    public void setPantryItems(
            List<PantryItem> pantryItems) {

        this.pantryItems = pantryItems;

        notifyDataSetChanged();
    }

    static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvQuantity;
        TextView tvExpiry;

        Button btnEdit;
        Button btnDelete;

        public PantryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvIngredientName =
                    itemView.findViewById(
                            R.id.tvIngredientName
                    );

            tvQuantity =
                    itemView.findViewById(
                            R.id.tvQuantity
                    );

            tvExpiry =
                    itemView.findViewById(
                            R.id.tvExpiry
                    );

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEdit
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }
}