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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.Adaptadores.ChatsAdaptador;
import com.zamnadev.mwhatsapp.MainActivity;
import com.zamnadev.mwhatsapp.Moldes.ListaChats;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private MainActivity mainActivity;
    private Context context;
    private ArrayList<Usuario> usuarios;
    private RecyclerView recyclerView;

    public ChatsFragment(MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;
        usuarios = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats,container,false);

        recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        MostrarChats();

        return view;
    }

    public void MostrarChats()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Usuarios");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    for (ListaChats chats : mainActivity.getListaChats())
                    {
                        assert usuario != null;
                        if (usuario.getId().equals(chats.getId()))
                        {
                            usuarios.add(usuario);
                            break;
                        }
                    }
                }

                ChatsAdaptador adaptador = new ChatsAdaptador(context,acomodarLista(),mainActivity);
                recyclerView.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Usuario> acomodarLista()
    {
        ArrayList<Usuario> tmp = new ArrayList<>();

        for (ListaChats chats : mainActivity.getListaChats())
        {
            for (Usuario usuario : usuarios)
            {
                if (chats.getId().equals(usuario.getId()))
                {
                    tmp.add(usuario);
                }
            }
        }

        return tmp;
    }
}
