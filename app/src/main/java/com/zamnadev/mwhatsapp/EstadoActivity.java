package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.Moldes.Estado;
import com.zamnadev.mwhatsapp.Moldes.Usuario;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class EstadoActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int contador = 0;
    long tiempoPresionado = 0L;
    long limite = 500L;

    private StoriesProgressView storiesProgressView;
    private ImageView imgFoto, imgFotoPerfil;
    private TextView txtNombre;

    private List<String> imagenes;
    private List<String> idEstados;
    private String idUsuario;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tiempoPresionado = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long ahora = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limite < ahora - tiempoPresionado;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado);

        storiesProgressView = findViewById(R.id.estados);
        imgFoto = findViewById(R.id.imgFoto);
        imgFotoPerfil = findViewById(R.id.imgFotoEstado);
        txtNombre = findViewById(R.id.txtNombre);

        idUsuario = getIntent().getStringExtra("id");

        View atras = findViewById(R.id.atras);
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        atras.setOnTouchListener(onTouchListener);

        View saltar = findViewById(R.id.saltar);
        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
        saltar.setOnTouchListener(onTouchListener);

        obtenerEstados();
        informacion();
    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext())
                .load(imagenes.get(++contador))
                .into(imgFoto);
    }

    @Override
    public void onPrev() {
        if (contador - 1 < 0) return;
        Glide.with(getApplicationContext())
                .load(imagenes.get(--contador))
                .into(imgFoto);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    private void obtenerEstados() {
        imagenes = new ArrayList<>();
        idEstados = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Estados")
                .child(idUsuario);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 imagenes.clear();
                 idEstados.clear();
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     Estado estado = snapshot.getValue(Estado.class);
                     long tiempo = System.currentTimeMillis();
                     if (tiempo > estado.getTimepoInicio() && tiempo < estado.getTiempoFin()) {
                         imagenes.add(estado.getImagen());
                         idEstados.add(estado.getId());
                     }
                 }

                 storiesProgressView.setStoriesCount(imagenes.size());
                 storiesProgressView.setStoryDuration(5000L);
                 storiesProgressView.setStoriesListener(EstadoActivity.this);
                 storiesProgressView.startStories(contador);

                Glide.with(getApplicationContext()).load(imagenes.get(contador))
                        .into(imgFoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void informacion() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(idUsuario);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                txtNombre.setText(usuario.getNombre());
                if (usuario.getImagen().equals("default")) {
                    imgFotoPerfil.setImageResource(R.drawable.ic_account_circle_black_24dp);
                } else {
                    Glide.with(getApplicationContext())
                            .load(usuario.getImagen())
                            .into(imgFotoPerfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
