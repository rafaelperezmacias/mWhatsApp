package com.zamnadev.mwhatsapp.MenuPrincipal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.Adaptadores.UsuariosAdaptador;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

public class UsuariosFragment extends Fragment {

    private ArrayList<Usuario> usuarioArrayList;
    private UsuariosAdaptador usuariosAdaptador;

    public UsuariosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        usuarioArrayList = new ArrayList<>();

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usuariosAdaptador = new UsuariosAdaptador(getContext(),usuarioArrayList);
        recyclerView.setAdapter(usuariosAdaptador);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarioArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    if (!usuario.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        usuarioArrayList.add(usuario);
                    }
                }

                usuariosAdaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
