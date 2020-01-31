package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    private TextInputLayout lytNombre;
    private TextInputLayout lytCorreo;
    private TextInputLayout lytPassword;
    private TextInputEditText txtNombre;
    private TextInputEditText txtCorreo;
    private TextInputEditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        lytNombre = findViewById(R.id.lytNombre);
        lytCorreo = findViewById(R.id.lytCorreo);
        lytPassword = findViewById(R.id.lytPassword);
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);

        ((Button) findViewById(R.id.btnRegistrar))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!validarNombre() | !validarPassword() | !validarCorreo()) {
                            return;
                        }

                        final FirebaseAuth auth = FirebaseAuth.getInstance();

                        final ProgressDialog progressDialog = new ProgressDialog(Registro.this);
                        progressDialog.setMessage("Insertando datos");
                        progressDialog.show();

                        auth.createUserWithEmailAndPassword(txtCorreo.getText().toString(),txtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = auth.getCurrentUser();

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id",firebaseUser.getUid());
                                        hashMap.put("correo",txtCorreo.getText().toString().trim());
                                        hashMap.put("nombre",txtNombre.getText().toString().trim());
                                        hashMap.put("hora", ServerValue.TIMESTAMP);
                                        hashMap.put("imagen", "default");

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                                                .child(firebaseUser.getUid());

                                        reference.updateChildren(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Registro.this, "Registro exitos", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(Registro.this,Login.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(Registro.this, "Error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(Registro.this, "Error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Registro.this,Login.class));
        finish();
    }

    private boolean validarNombre(){

        if (txtNombre.getText().toString().isEmpty()) {
            lytNombre.setError("Campo vacio");
            return false;
        }

        lytNombre.setError(null);
        return true;
    }

    private boolean validarCorreo(){

        if (txtCorreo.getText().toString().isEmpty()) {
            lytCorreo.setError("Campo vacio");
            return false;
        }

        lytCorreo.setError(null);
        return true;
    }

    private boolean validarPassword(){

        if (txtPassword.getText().toString().isEmpty()) {
            lytPassword.setError("Campo vacio");
            return false;
        }

        if (txtPassword.getText().toString().length() < 8) {
            lytPassword.setError("Error, caracteres minimos 8");
            return false;
        }

        lytPassword.setError(null);
        return true;
    }
}
