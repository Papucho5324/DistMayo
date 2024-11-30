package com.example.distmartdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText editTextName, editTextEmail, editTextPhone;
    private Button buttonUpdate;
    private ImageView icon_disccount, icon_home;
    private String userId;
    TextView mNombre, mCorreo, mTelefono;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Referencias a los elementos de la UI
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        mNombre = findViewById(R.id.textViewName);
        mCorreo = findViewById(R.id.textViewEmail);
        mTelefono = findViewById(R.id.textViewPhone);

        icon_disccount = findViewById(R.id.icon_disccount);
        icon_home = findViewById(R.id.icon_home);

        // Configurar los íconos de navegación
        icon_disccount.setOnClickListener(v -> {
            startActivity(new Intent(Perfil.this, Promociones.class));
        });

        icon_home.setOnClickListener(v -> {
            startActivity(new Intent(Perfil.this, Home.class));
        });

        // Obtener el usuario actual
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Obtener el UID del usuario actual
            loadUserData(); // Cargar datos del usuario desde Firestore
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar la actividad si no hay usuario autenticado
        }

        // Configurar el botón para actualizar los datos
        buttonUpdate.setOnClickListener(v -> updateUserData());
    }

    private void loadUserData() {
        DocumentReference docRef = firestore.collection("Usuarios").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Mostrar los datos actuales en los campos de texto
                mNombre.setText(documentSnapshot.getString("nombre"));
                mCorreo.setText(documentSnapshot.getString("correo"));
                mTelefono.setText(documentSnapshot.getString("phone"));
            } else {
                Toast.makeText(Perfil.this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(Perfil.this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserData() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos actualizados
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", name);
        user.put("correo", email);
        user.put("phone", phone);

        // Sobrescribir el documento en Firestore
        firestore.collection("Usuarios").document(userId)
                .set(user) // `set` sobrescribe todo el documento
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Perfil.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Perfil.this, "Error al actualizar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
