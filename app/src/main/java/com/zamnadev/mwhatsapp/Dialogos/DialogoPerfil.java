package com.zamnadev.mwhatsapp.Dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.MainActivity;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.R;

public class DialogoPerfil extends DialogFragment {

    private MainActivity mainActivity;

    public DialogoPerfil(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_perfil,null);
        builder.setView(view);

        final TextView txtNombre = view.findViewById(R.id.txtNombre);
        final ImageView imgFoto = view.findViewById(R.id.imgPerfil);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                assert usuario != null;
                txtNombre.setText(usuario.getNombre());
                if (usuario.getImagen().equals("default"))
                {
                    imgFoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
                } else {
                    Glide.with(getActivity().getApplicationContext())
                            .load(usuario.getImagen())
                            .into(imgFoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ((Button) view.findViewById(R.id.btnSalir))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
