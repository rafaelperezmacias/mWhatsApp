package com.zamnadev.mwhatsapp.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamnadev.mwhatsapp.EstadoActivity;
import com.zamnadev.mwhatsapp.Moldes.Estado;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

public class EstadosAdaptador extends RecyclerView.Adapter<EstadosAdaptador.ViewHolder> {

    private Context context;
    private ArrayList<Estado> estados;

    public EstadosAdaptador(Context context, ArrayList<Estado> historias) {
        this.context = context;
        this.estados = historias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_estado,parent,false);
        return new EstadosAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Estado estado = estados.get(position);

        info(holder,estado.getIdUsuario(),position);

        verHistoria(holder,estado.getIdUsuario());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EstadoActivity.class);
                intent.putExtra("id",estado.getIdUsuario());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return estados.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgHistoria;
        private ImageView imgHistoriaAdd;
        private ImageView imgHistoriaVista;
        private TextView txtUsuario;
        private TextView txtUsuarioAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHistoria = (ImageView) itemView.findViewById(R.id.imgHistoria);
            imgHistoriaAdd = (ImageView) itemView.findViewById(R.id.imgHistoriaPlus);
            imgHistoriaVista = (ImageView) itemView.findViewById(R.id.imgHistoriaVista);
            txtUsuario = (TextView) itemView.findViewById(R.id.txtHistoriasNombre);
            txtUsuarioAdd = (TextView) itemView.findViewById(R.id.txtHistoriaAdd);
        }
    }

    private void info(final ViewHolder viewHolder, String idUsuario, final int pos) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(idUsuario);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                if (usuario.getImagen().equals("default")) {
                    viewHolder.imgHistoriaVista.setImageResource(R.drawable.ic_account_circle_black_24dp);
                } else {
                    Glide.with(context)
                            .load(usuario.getImagen())
                            .into(viewHolder.imgHistoria);
                }

                Glide.with(context)
                        .load(usuario.getImagen())
                        .into(viewHolder.imgHistoriaVista);

                viewHolder.txtUsuario.setText(usuario.getNombre());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verHistoria(final ViewHolder viewHolder, String idUsuario) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Estados")
                .child(idUsuario);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < snapshot.getValue(Estado.class).getTiempoFin()) {
                        i++;
                    }
                }

                if (i > 0) {
                    viewHolder.imgHistoria.setVisibility(View.VISIBLE);
                    viewHolder.imgHistoriaVista.setVisibility(View.GONE);
                } else {
                    viewHolder.imgHistoria.setVisibility(View.GONE);
                    viewHolder.imgHistoriaVista.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
