package com.example.distmartdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {
    private List<DocumentSnapshot> offerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(DocumentSnapshot document);
        void onDelete(DocumentSnapshot document);
    }

    public OfferAdapter(List<DocumentSnapshot> offerList, OnItemClickListener listener) {
        this.offerList = offerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        DocumentSnapshot document = offerList.get(position);
        holder.title.setText(document.getString("title"));
        holder.description.setText(document.getString("description"));

        holder.editButton.setOnClickListener(v -> listener.onEdit(document));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(document));
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        Button editButton, deleteButton;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.offer_title);
            description = itemView.findViewById(R.id.offer_description);
            editButton = itemView.findViewById(R.id.edit_offer_button);
            deleteButton = itemView.findViewById(R.id.delete_offer_button);
        }
    }
}
