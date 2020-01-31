package com.zamnadev.mwhatsapp.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zamnadev.mwhatsapp.ChatActivity;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuariosAdaptador extends RecyclerView.Adapter<UsuariosAdaptador.ViewHolder> {

    private Context context;
    private ArrayList<Usuario> usuarios;

    public UsuariosAdaptador(Context context, ArrayList<Usuario> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuario,parent,false);
        return new UsuariosAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Usuario usuario = usuarios.get(position);

        if (usuario.getImagen().equals("default")) {
            holder.imgFoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(context)
                    .load(usuario.getImagen())
                    .into(holder.imgFoto);
        }

        holder.txtNombre.setText(usuario.getNombre());
        holder.txtCorreo.setText(usuario.getCorreo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id",usuario.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNombre;
        private TextView txtCorreo;
        private CircleImageView imgFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            txtCorreo = (TextView) itemView.findViewById(R.id.txtCorreo);
            imgFoto = (CircleImageView) itemView.findViewById(R.id.imgFoto);
        }
    }
}
