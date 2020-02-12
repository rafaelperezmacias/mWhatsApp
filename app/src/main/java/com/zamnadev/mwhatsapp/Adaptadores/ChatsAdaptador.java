package com.zamnadev.mwhatsapp.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.zamnadev.mwhatsapp.ChatActivity;
import com.zamnadev.mwhatsapp.MainActivity;
import com.zamnadev.mwhatsapp.Moldes.Mensaje;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdaptador extends RecyclerView.Adapter<ChatsAdaptador.ViewHolder> {

    private Context context;
    private ArrayList<Usuario> usuarios;
    private MainActivity mainActivity;

    private String ultimo;

    public ChatsAdaptador(Context context, ArrayList<Usuario> usuarios, MainActivity mainActivity) {
        this.context = context;
        this.usuarios = usuarios;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chats,parent,false);
        return new ChatsAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Usuario usuario = usuarios.get(position);
        holder.txtNombre.setText(usuario.getNombre());
        if (usuario.getImagen().equals("default"))
        {
            holder.imgFoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(context)
                    .load(usuario.getImagen())
                    .into(holder.imgFoto);
        }

        if (usuario.isConectado()) {
            holder.imgConectado.setVisibility(View.VISIBLE);
            holder.imgDesconectado.setVisibility(View.GONE);
        } else {
            holder.imgDesconectado.setVisibility(View.VISIBLE);
            holder.imgConectado.setVisibility(View.GONE);
        }

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id",usuario.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        ultimo = "default";

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(usuario.getId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int leer = 0;
                boolean enviado = false;
                long hora = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Mensaje msg = snapshot.getValue(Mensaje.class);
                    assert msg != null;
                    ultimo = msg.getMensaje();
                    enviado = msg.isEnviado();
                    hora = msg.getHora();
                    if (!msg.isVisto()) {
                        leer++;
                    }
                }

                if (leer == 0) {
                    holder.txtLeer.setVisibility(View.GONE);
                    holder.txtHora.setTextColor(context.getResources().getColor(R.color.negro));
                    holder.txtMensaje.setTextColor(context.getResources().getColor(R.color.gris));
                } else {
                    holder.txtLeer.setVisibility(View.VISIBLE);
                    holder.txtLeer.setText("" + leer);
                    holder.txtHora.setTextColor(context.getResources().getColor(R.color.verdeNotificacion));
                    holder.txtMensaje.setTextColor(context.getResources().getColor(R.color.negro));
                }

                holder.txtHora.setText(MostrarFecha(hora));

                switch (ultimo)
                {
                    case "default":
                        holder.txtMensaje.setText("No hay mensajes");
                        holder.txtHora.setVisibility(View.GONE);
                        break;

                    default:
                        if (ultimo.equals("")) {
                            if (!enviado) {
                                holder.txtMensaje.setText("Has enviado una foto");
                            } else {
                                holder.txtMensaje.setText("Ha enviado una foto");
                            }
                        } else {
                            holder.txtMensaje.setText(ultimo);
                        }
                        break;
                }

                ultimo = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout lytMain;
        private TextView txtNombre;
        private TextView txtMensaje;
        private CircleImageView imgFoto;
        private CircleImageView imgConectado;
        private TextView txtLeer;
        private CircleImageView imgDesconectado;
        private TextView txtHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            txtMensaje = (TextView) itemView.findViewById(R.id.txtMensaje);
            txtLeer = (TextView) itemView.findViewById(R.id.txtLeer);
            imgFoto = (CircleImageView) itemView.findViewById(R.id.imgPerfil);
            lytMain = (LinearLayout) itemView.findViewById(R.id.lytMainChat);
            imgConectado = (CircleImageView) itemView.findViewById(R.id.imgConectado);
            imgDesconectado = (CircleImageView) itemView.findViewById(R.id.imgDesconectado);
            txtHora = (TextView) itemView.findViewById(R.id.txtHora);
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