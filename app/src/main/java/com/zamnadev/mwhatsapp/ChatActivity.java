package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.Adaptadores.AdaptadorMensajes;
import com.zamnadev.mwhatsapp.Moldes.Mensaje;
import com.zamnadev.mwhatsapp.Moldes.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String idUsuario;
    private EditText txtMensaje;
    private FirebaseUser firebaseUser;

    private ArrayList<Mensaje> mensajeArrayList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerview);

        idUsuario = getIntent().getStringExtra("id");
        mensajeArrayList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txtMensaje = findViewById(R.id.txtMensaje);
        final ImageView imgEnviar = findViewById(R.id.imgEnviar);

        final TextView txtNombre = findViewById(R.id.txtNombre);
        final TextView txtConexion = findViewById(R.id.txtConexion);
        final CircleImageView imgFoto = findViewById(R.id.imgFoto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(idUsuario);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                txtNombre.setText("" + usuario.getNombre());

                txtConexion.setText("Conectado");

                if (usuario.getImagen().equals("default")) {
                    imgFoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
                } else {
                    Glide.with(getApplicationContext())
                            .load(usuario.getImagen())
                            .into(imgFoto);
                }

                verMensajes(usuario.getImagen());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0 ) {
                    imgEnviar.setVisibility(View.VISIBLE);
                } else {
                    imgEnviar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });
    }

    private void verMensajes(final String img) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(firebaseUser.getUid())
                .child(idUsuario);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mensajeArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mensaje mensaje = snapshot.getValue(Mensaje.class);
                    mensajeArrayList.add(mensaje);
                }

                AdaptadorMensajes adaptadorMensajes = new AdaptadorMensajes(getApplicationContext(),mensajeArrayList,img);
                recyclerView.setAdapter(adaptadorMensajes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarMensaje() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("mensaje",txtMensaje.getText().toString());
        hashMap.put("hora", ServerValue.TIMESTAMP);
        hashMap.put("enviado",true);
        hashMap.put("visto",false);
        hashMap.put("tipo",1);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(firebaseUser.getUid())
                .child(idUsuario);

        reference.push().updateChildren(hashMap);

        hashMap.put("enviado",false);

        reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(idUsuario)
                .child(firebaseUser.getUid());

        reference.push().updateChildren(hashMap);

        actualizarListaChats();

        txtMensaje.setText("");
    }

    private void actualizarListaChats() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",idUsuario);
        hashMap.put("hora", ServerValue.TIMESTAMP);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(firebaseUser.getUid())
                .child(idUsuario);

        reference.updateChildren(hashMap);

        hashMap.put("id",firebaseUser.getUid());
        hashMap.put("hora", ServerValue.TIMESTAMP);

        reference = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(idUsuario)
                .child(firebaseUser.getUid());

        reference.updateChildren(hashMap);
    }

}
