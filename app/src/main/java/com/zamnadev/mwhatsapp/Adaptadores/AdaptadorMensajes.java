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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Mensaje mensaje = mensajeArrayList.get(position);

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

        holder.txtHora.setText(MostrarFecha(mensaje.getHora()));

        if (mensaje.isEnviado()) {
            if (mensaje.isVisto()) {
                holder.txtVisto.setText("Visto");
            } else {
                holder.txtVisto.setText("Enviado");
            }
        }

        if (position == mensajeArrayList.size()-1) {
            holder.txtVisto.setVisibility(View.VISIBLE);
        } else {
            holder.txtVisto.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mensaje.isEnviado()) {
                    if (holder.txtHora.getVisibility() == View.VISIBLE) {
                        holder.txtHora.setVisibility(View.GONE);
                    } else {
                        holder.txtHora.setVisibility(View.VISIBLE);
                    }
                }
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
        private TextView txtHora;
        private TextView txtVisto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            imgMensaje = itemView.findViewById(R.id.imgChat);
            txtHora = (TextView) itemView.findViewById(R.id.txtHora);
            txtVisto = (TextView) itemView.findViewById(R.id.txtVisto);
        }
    }

    private String MostrarFecha(Long hora)
    {
        Calendar calendar = Calendar.getInstance();
        int anoA = calendar.get(Calendar.YEAR);
        int mesA = calendar.get(Calendar.MONTH);
        int diaA = calendar.get(Calendar.DAY_OF_MONTH);

        Date d = new Date(hora);
        SimpleDateFormat simpleDateFormat;

        StringTokenizer tokenizer = new StringTokenizer(d.toString(),"-");
        int ano = Integer.parseInt(tokenizer.nextToken());
        int mes = Integer.parseInt(tokenizer.nextToken());
        int dia = Integer.parseInt(tokenizer.nextToken());

        if (ano == anoA && (mesA + 1) == mes && diaA == dia)
        {
            d = new Date(hora);
            simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return simpleDateFormat.format(d);
        } else
        {
            d = new Date(hora);
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return simpleDateFormat.format(d);
        }
    }
}
