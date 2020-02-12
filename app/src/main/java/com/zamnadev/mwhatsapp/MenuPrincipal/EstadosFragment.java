package com.zamnadev.mwhatsapp.MenuPrincipal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.Adaptadores.EstadosAdaptador;
import com.zamnadev.mwhatsapp.AddEstadoActivity;
import com.zamnadev.mwhatsapp.MainActivity;
import com.zamnadev.mwhatsapp.Moldes.Estado;
import com.zamnadev.mwhatsapp.Moldes.ListaChats;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

public class EstadosFragment extends Fragment {

    private RecyclerView recyclerView;
    private EstadosAdaptador adaptador;
    private ArrayList<Estado> estados;
    private MainActivity mainActivity;

    public EstadosFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estados, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        estados = new ArrayList<>();
        adaptador = new EstadosAdaptador(getContext(),estados);
        recyclerView.setAdapter(adaptador);

        ((Button) view.findViewById(R.id.btnIngresar))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getContext(), AddEstadoActivity.class));
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        leerEstados();
    }

    public void leerEstados() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Estados");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long tiempo = System.currentTimeMillis();
                estados.clear();
                for (ListaChats chats : mainActivity.getListaChats()) {
                    int contadorHistorias = 0;
                    Estado estado = null;
                    for (DataSnapshot snapshot : dataSnapshot.child(chats.getId()).getChildren()) {
                        estado = snapshot.getValue(Estado.class);
                        if (tiempo > estado.getTimepoInicio() && tiempo < estado.getTiempoFin()) {
                            contadorHistorias++;
                        }
                    }
                    if (contadorHistorias > 0) {
                        estados.add(estado);
                    }
                }
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
