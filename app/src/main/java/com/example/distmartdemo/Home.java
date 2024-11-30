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

public class Home extends AppCompatActivity {
    TextView mNombre;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ImageView iconDisccount, iconHome, iconProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Configurar padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        mNombre = findViewById(R.id.nombre);
        iconDisccount = findViewById(R.id.icon_disccount);
        iconProfile = findViewById(R.id.icon_profile);

        // Configurar eventos de clic
        iconDisccount.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Promociones.class);
            startActivity(intent);
        });

        iconProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Perfil.class);
            startActivity(intent);
        });

        // Cargar el nombre del usuario después de inicializar mAuth
        cargarNombreUsuario();
    }

    private void cargarNombreUsuario() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("Usuarios").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nombre = document.getString("nombre");
                                mNombre.setText(nombre);
                            } else {
                                Toast.makeText(Home.this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Home.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            // Redirigir al Login si es necesario
            startActivity(new Intent(Home.this, MainActivity.class));
            finish();
        }
    }
}