package com.example.distmartdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Promociones extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private OfferAdapter adapter;
    private List<DocumentSnapshot> offerList = new ArrayList<>();
    private EditText titleInput, descriptionInput;
    private Button addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promociones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.offers_recycler_view);
        titleInput = findViewById(R.id.offer_title_input);
        descriptionInput = findViewById(R.id.offer_description_input);
        addButton = findViewById(R.id.add_offer_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfferAdapter(offerList, new OfferAdapter.OnItemClickListener() {
            @Override
            public void onEdit(DocumentSnapshot document) {
                editOffer(document);
            }

            @Override
            public void onDelete(DocumentSnapshot document) {
                deleteOffer(document);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        addButton.setOnClickListener(v -> addOffer());
        loadOffers();


    }
    private void loadOffers() {
        db.collection("Offers").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error al cargar ofertas", Toast.LENGTH_SHORT).show();
                return;
            }
            offerList.clear();
            if (queryDocumentSnapshots != null) {
                offerList.addAll(queryDocumentSnapshots.getDocuments());
                Log.d("LoadOffers", "Number of offers: " + offerList.size());
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void addOffer() {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> offer = new HashMap<>();
        offer.put("title", title);
        offer.put("description", description);

        db.collection("Offers").add(offer)
                .addOnSuccessListener(documentReference -> {
                    titleInput.setText("");
                    descriptionInput.setText("");
                    Toast.makeText(this, "Oferta agregada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al agregar oferta", Toast.LENGTH_SHORT).show());
    }

    private void editOffer(DocumentSnapshot document) {
        titleInput.setText(document.getString("title"));
        descriptionInput.setText(document.getString("description"));

        addButton.setText("Actualizar");
        addButton.setOnClickListener(v -> {
            updateOffer(document);
        });
    }

    private void updateOffer(DocumentSnapshot document) {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();

        db.collection("Offers").document(document.getId())
                .update("title", title, "description", description)
                .addOnSuccessListener(aVoid -> {
                    addButton.setText("Agregar oferta");
                    titleInput.setText("");
                    descriptionInput.setText("");
                    Toast.makeText(this, "Oferta actualizada", Toast.LENGTH_SHORT).show();
                    addButton.setOnClickListener(v -> addOffer());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar oferta", Toast.LENGTH_SHORT).show());
    }

    private void deleteOffer(DocumentSnapshot document) {
        db.collection("Offers").document(document.getId()).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Oferta eliminada", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar oferta", Toast.LENGTH_SHORT).show());
    }
}