package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.utils.QuantityFormatter;

import java.util.List;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private List<PantryItem> pantryItems;
    private final OnPantryItemListener listener;

    /**
     * Interface used to send Edit and Delete actions
     * back to PantryActivity.
     */
    public interface OnPantryItemListener {

        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    /**
     * Constructor for the RecyclerView adapter.
     */
    public PantryAdapter(
            List<PantryItem> pantryItems,
            OnPantryItemListener listener) {

        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    /**
     * Creates the layout used for each pantry item.
     */
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

    /**
     * Places the pantry information into each
     * RecyclerView item.
     */
    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item =
                pantryItems.get(position);

        // Ingredient name
        holder.tvIngredientName.setText(
                item.getIngredientName()
        );

        // Quantity and measurement unit.
        // QuantityFormatter removes unnecessary
        // decimal zeros.
        //
        // Example:
        // 10.0 -> 10
        // 1.5  -> 1.5
        holder.tvQuantity.setText(
                QuantityFormatter.format(
                        item.getQuantity()
                )
                        + " "
                        + item.getUnit()
        );

        // Expiry date
        String expiryDate =
                item.getExpiryDate();

        if (expiryDate == null
                || expiryDate.trim().isEmpty()) {

            holder.tvExpiry.setText(
                    "Expiry: Not specified"
            );

        } else {

            holder.tvExpiry.setText(
                    "Expiry: " + expiryDate
            );
        }

        // Edit ingredient
        holder.btnEdit.setOnClickListener(
                v -> listener.onEdit(item)
        );

        // Delete ingredient
        holder.btnDelete.setOnClickListener(
                v -> listener.onDelete(item)
        );
    }

    /**
     * Returns the number of pantry records that
     * should be displayed.
     */
    @Override
    public int getItemCount() {

        if (pantryItems == null) {
            return 0;
        }

        return pantryItems.size();
    }

    /**
     * Replaces the RecyclerView data when the
     * pantry is refreshed.
     */
    public void setPantryItems(
            List<PantryItem> pantryItems) {

        this.pantryItems = pantryItems;

        notifyDataSetChanged();
    }

    /**
     * Holds references to the views contained
     * inside item_pantry.xml.
     */
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