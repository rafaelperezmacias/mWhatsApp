package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zamnadev.mwhatsapp.Adaptadores.AdaptadorMensajes;
import com.zamnadev.mwhatsapp.Moldes.Mensaje;
import com.zamnadev.mwhatsapp.Moldes.Usuario;
import com.zamnadev.mwhatsapp.Notificaciones.Cliente;
import com.zamnadev.mwhatsapp.Notificaciones.Data;
import com.zamnadev.mwhatsapp.Notificaciones.Enviar;
import com.zamnadev.mwhatsapp.Notificaciones.Exito;
import com.zamnadev.mwhatsapp.Notificaciones.Mensajes;
import com.zamnadev.mwhatsapp.Notificaciones.Token;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private String idUsuario;
    private EditText txtMensaje;
    private FirebaseUser firebaseUser;

    private ArrayList<Mensaje> mensajeArrayList;
    private RecyclerView recyclerView;
    private AdaptadorMensajes adaptadorMensajes;

    private final String[] PERMISOS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final int CODIGO_PERMISOS = 12;

    private Mensajes APIMensajes;

    private ValueEventListener leerMensajes;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerview);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        APIMensajes = Cliente.getCliente("https://fcm.googleapis.com/").create(Mensajes.class);

        idUsuario = getIntent().getStringExtra("id");
        mensajeArrayList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txtMensaje = findViewById(R.id.txtMensaje);
        final ImageView imgEnviar = findViewById(R.id.imgEnviar);

        final TextView txtNombre = findViewById(R.id.txtNombre);
        final TextView txtConexion = findViewById(R.id.txtConexion);
        final CircleImageView imgFoto = findViewById(R.id.imgFoto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                txtNombre.setText("" + usuario.getNombre());

                txtConexion.setText("Conectado");

                if (usuario.getImagen().equals("default")) {
                    imgFoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
                } else {
                    Glide.with(getApplicationContext())
                            .load(usuario.getImagen())
                            .into(imgFoto);
                }

                if (!usuario.isConectado())
                {
                    Date date = new Date(usuario.getHora());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm a", Locale.getDefault());
                    txtConexion.setText("Ultima conexión: " + simpleDateFormat.format(date));
                } else {
                    txtConexion.setText("Conectado");
                }

                verMensajes(usuario.getImagen());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    imgEnviar.setVisibility(View.VISIBLE);
                } else {
                    imgEnviar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(idUsuario)
                .child(firebaseUser.getUid());

        leerMensajes = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Mensaje mensaje = snapshot.getValue(Mensaje.class);
                    assert mensaje != null;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("visto", true);
                    snapshot.getRef().updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje(txtMensaje.getText().toString(),1,"");
            }
        });

        ((ImageView) findViewById(R.id.imgEnviarImagen))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enviarImagen();
                    }
                });
    }

    private void enviarImagen() {
        if(verificarPermisos()) {
            proceso();
        } else {
            ActivityCompat.requestPermissions(ChatActivity.this,PERMISOS,CODIGO_PERMISOS);
        }
    }

    private boolean verificarPermisos() {
        for (String permiso : PERMISOS) {
            if (ActivityCompat.checkSelfPermission(ChatActivity.this,permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISOS) {
            boolean ban = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    ban = false;
                }
            }

            if (ban) {
                proceso();
            }
        }
    }

    private void proceso() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(ChatActivity.this);
    }

    private String obtnerExtension(Uri uri) {
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri uri = result.getUri();

            if (uri != null) {

                final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
                progressDialog.setMessage("Subiendo...");
                progressDialog.show();

                final StorageReference storageReference = FirebaseStorage.getInstance().getReference("ImagenesChat")
                        .child(System.currentTimeMillis() + "." + obtnerExtension(uri));
                StorageTask task = storageReference.putFile(uri);
                task.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isComplete()) {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri tmpUri = task.getResult();
                            assert tmpUri != null;
                            String url = tmpUri.toString();

                            enviarMensaje("",2,url);
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(ChatActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(ChatActivity.this, "Ninguna imagen seleccionada", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void verMensajes(final String img) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(firebaseUser.getUid())
                .child(idUsuario);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mensajeArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mensaje mensaje = snapshot.getValue(Mensaje.class);
                    mensajeArrayList.add(mensaje);
                }

                adaptadorMensajes = new AdaptadorMensajes(getApplicationContext(),mensajeArrayList,img);
                recyclerView.setAdapter(adaptadorMensajes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarMensaje(String mensaje,int tipo, String imagen) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("hora", ServerValue.TIMESTAMP);
        hashMap.put("enviado",true);
        hashMap.put("visto",false);
        hashMap.put("tipo",tipo);

        if(tipo == 2) {
            hashMap.put("imagen",imagen);
            hashMap.put("mensaje","Has enviado una foto");
        } else {
            hashMap.put("mensaje",mensaje);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(firebaseUser.getUid())
                .child(idUsuario);

        reference.push().updateChildren(hashMap);

        hashMap.put("enviado",false);
        if(tipo == 2) {
            hashMap.put("mensaje","Ha enviado una foto");
        }

        reference = FirebaseDatabase.getInstance().getReference("Chats")
                .child(idUsuario)
                .child(firebaseUser.getUid());

        reference.push().updateChildren(hashMap);

        actualizarListaChats();

        enviarNotificacion(mensaje);

        txtMensaje.setText("");
    }

    private String getNombre() {
        SharedPreferences sharedPreferences = getSharedPreferences("Usuario",MODE_PRIVATE);
        return sharedPreferences.getString("nombre","");
    }

    private void enviarNotificacion (final String mensaje) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens")
                .child(idUsuario);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                Data data = new Data("Nuevo mensaje", getNombre() + ": " + mensaje,idUsuario,firebaseUser.getUid());
                Enviar enviar = new Enviar(data,token.getToken());

                APIMensajes.enviarNotificacion(enviar)
                        .enqueue(new Callback<Exito>() {
                            @Override
                            public void onResponse(Call<Exito> call, Response<Exito> response) {
                                if (response.code() == 200) {
                                    assert response.body() != null;
                                    if (response.body().success != 1) {
                                        Log.e("NOTIFICACION","Error con la notificacion");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Exito> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void actualizarListaChats() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",idUsuario);
        hashMap.put("hora", ServerValue.TIMESTAMP);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(firebaseUser.getUid())
                .child(idUsuario);

        reference.updateChildren(hashMap);

        hashMap.put("id",firebaseUser.getUid());
        hashMap.put("hora", ServerValue.TIMESTAMP);

        reference = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(idUsuario)
                .child(firebaseUser.getUid());

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("conectado",true);
        hashMap.put("hora", ServerValue.TIMESTAMP);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("conectado",false);
        hashMap.put("hora", ServerValue.TIMESTAMP);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(leerMensajes);
    }
}
