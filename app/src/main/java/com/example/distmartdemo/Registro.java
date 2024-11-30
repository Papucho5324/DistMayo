package com.example.distmartdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText mEmail, mPassword, mPasswordConfirm, mName;
    Button mRegisterButton;
    CheckBox mTermsCheckbox;
    TextView mLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mEmail = findViewById(R.id.email_input);
        mPassword = findViewById(R.id.password_input);
        mPasswordConfirm = findViewById(R.id.password_confirm);
        mRegisterButton = findViewById(R.id.login_button);
        mName = findViewById(R.id.nombre_input);
        mTermsCheckbox = findViewById(R.id.terminos);
        mLogin = findViewById(R.id.iniciarSesion);

        mRegisterButton.setOnClickListener(v -> registro());
        mLogin.setOnClickListener(v -> finish());

    }

    public boolean isEmailValid(String email) {
        // Expresión regular para validar el formato del correo electrónico
        String expression = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void registro(){
        String nombre = mName.getText().toString();
        String correo = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmailValid(correo)) {
            Toast.makeText(this, "Por favor, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mTermsCheckbox.isChecked()) {
            Toast.makeText(this, "Por favor, acepte los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }
        crearUsuario(nombre, correo);


    }

    private void crearUsuario(final String nombre, final String correo) {
        mAuth.createUserWithEmailAndPassword(correo, mPassword.getText().toString().trim()) // Correo y contraseña correctos
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre", nombre);
                        map.put("correo", correo);

                        Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        String userId = mAuth.getCurrentUser().getUid();

                        db.collection("Usuarios").document(userId).set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Registro.this, "El usuario se almacenó correctamente", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Registro.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(Registro.this, "Error al crear el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}