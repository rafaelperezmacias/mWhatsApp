package com.zamnadev.mwhatsapp.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zamnadev.mwhatsapp.Moldes.Mensaje;
import com.zamnadev.mwhatsapp.R;

import java.util.ArrayList;

public class AdaptadorMensajes extends RecyclerView.Adapter<AdaptadorMensajes.ViewHolder> {

    private Context context;
    private ArrayList<Mensaje> mensajeArrayList;
    private String img;

    private final int MENSAJE_DERECHA = 1;
    private final int MENSAJE_IZQUIERDA = 1;

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
    }

    @Override
    public int getItemCount() {
        return mensajeArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        if (mensajeArrayList.get(position).isEnviado()) {
            return MENSAJE_DERECHA;
        } else {
            return MENSAJE_IZQUIERDA;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMensaje;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }
    }
}
