package com.zamnadev.mwhatsapp.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zamnadev.mwhatsapp.Moldes.Mensaje;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorMensajes extends RecyclerView.Adapter<AdaptadorMensajes.ViewHolder> {

    private Context context;
    private ArrayList<Mensaje> mensajeArrayList;
    private String img;

    private final int MENSAJE_DERECHA = 1;
    private final int MENSAJE_IZQUIERDA = 2;

    public AdaptadorMensajes(Context context, ArrayList<Mensaje> mensajeArrayList, String img) {
        this.context = context;
        this.mensajeArrayList = mensajeArrayList;
        this.img = img;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MENSAJE_DERECHA) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_derecha,parent,false);
            return new AdaptadorMensajes.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_izquierda,parent,false);
            return new AdaptadorMensajes.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = mensajeArrayList.get(position);

        holder.txtMensaje.setText(mensaje.getMensaje());

        if (img.equals("default")) {
            holder.imgFoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(context)
                    .load(img)
                    .into(holder.imgFoto);
        }

        if (mensaje.getTipo() == 2) {
            holder.imgMensaje.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(mensaje.getImagen())
                    .into(holder.imgMensaje);
        }

        holder.imgMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO dialogo

            }
        });
    }

    @Override
    public int getItemCount() {
        return mensajeArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = mensajeArrayList.get(position);
        if (mensaje.isEnviado()) {
            return MENSAJE_DERECHA;
        } else {
            return MENSAJE_IZQUIERDA;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMensaje;
        private CircleImageView imgFoto;
        private ImageView imgMensaje;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            imgMensaje = itemView.findViewById(R.id.imgChat);
        }
    }
}
