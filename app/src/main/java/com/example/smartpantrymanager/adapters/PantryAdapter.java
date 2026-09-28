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

import java.util.List;

public class PantryAdapter
        extends RecyclerView.Adapter<
        PantryAdapter.PantryViewHolder> {

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
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_pantry,
                                parent,
                                false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item =
                pantryItems.get(position);

        holder.tvIngredientName.setText(
                item.getIngredientName());

        holder.tvQuantity.setText(
                item.getQuantity() + " " +
                        item.getUnit());

        if (item.getExpiryDate() == null ||
                item.getExpiryDate().trim().isEmpty()) {

            holder.tvExpiry.setText(
                    "Expiry: Not provided");

        } else {

            holder.tvExpiry.setText(
                    "Expiry: " +
                            item.getExpiryDate());
        }

        holder.btnEdit.setOnClickListener(
                v -> listener.onEdit(item));

        holder.btnDelete.setOnClickListener(
                v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {

        return pantryItems == null
                ? 0
                : pantryItems.size();
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
                            R.id.tvIngredientName);

            tvQuantity =
                    itemView.findViewById(
                            R.id.tvQuantity);

            tvExpiry =
                    itemView.findViewById(
                            R.id.tvExpiry);

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEdit);

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete);
        }
    }
}