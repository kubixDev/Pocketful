package com.kubixdev.pocketful.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kubixdev.pocketful.R;
import com.kubixdev.pocketful.models.PaymentItem;
import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private final List<PaymentItem> items = new ArrayList<>();

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentItem item = items.get(position);
        holder.textViewDescription.setText(item.getDescription());
        holder.textViewPrice.setText(String.valueOf(item.getAmount()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(PaymentItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDescription, textViewPrice;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.operationNameTextView);
            textViewPrice = itemView.findViewById(R.id.priceTextView);
        }
    }
}