package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    private TextInputLayout lytPassword;
    private TextInputLayout lytCorreo;
    private TextInputEditText txtPassword;
    private TextInputEditText txtCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lytCorreo = findViewById(R.id.lytCorreo);
        lytPassword = findViewById(R.id.lytPassword);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);


        final TextView txtLeyenda = findViewById(R.id.txtLeyenda);
        txtLeyenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Registro.class));
                finish();
            }
        });


        txtCorreo.setText("rafael@gmail.com");

        ((Button) findViewById(R.id.btnIngresar))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!validarCorreo() | !validarPassword()) {
                            return;
                        }

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithEmailAndPassword(txtCorreo.getText().toString(),txtPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            startActivity(new Intent(Login.this,MainActivity.class));
                                            finish();

                                        } else {
                                            Toast.makeText(Login.this, "Usuario y/o contraseña", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

        ((TextView) findViewById(R.id.txtLeyendaVerde))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtLeyenda.callOnClick();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }
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

        lytPassword.setError(null);
        return true;
    }
}
