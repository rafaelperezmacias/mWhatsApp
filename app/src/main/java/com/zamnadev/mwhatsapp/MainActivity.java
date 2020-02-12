package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zamnadev.mwhatsapp.Dialogos.DialogoMenuConfiguracion;
import com.zamnadev.mwhatsapp.MenuPrincipal.AdaptadorMenu;
import com.zamnadev.mwhatsapp.MenuPrincipal.ChatsFragment;
import com.zamnadev.mwhatsapp.MenuPrincipal.EstadosFragment;
import com.zamnadev.mwhatsapp.MenuPrincipal.UsuariosFragment;
import com.zamnadev.mwhatsapp.Moldes.Estado;
import com.zamnadev.mwhatsapp.Moldes.ListaChats;
import com.zamnadev.mwhatsapp.Moldes.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String[] PERMISOS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final int CODIGO_PERMISOS = 12;

    private DatabaseReference usuarioRef;
    private FirebaseUser firebaseUser;
    private ArrayList<ListaChats> listaChats;

    private EstadosFragment estadosFragment;
    private ChatsFragment chatsFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        listaChats = new ArrayList<>();

        estadosFragment = new EstadosFragment(getMe());
        chatsFragment = new ChatsFragment(getMe(),getApplicationContext());

        AdaptadorMenu adaptadorMenu = new AdaptadorMenu(getSupportFragmentManager());
        adaptadorMenu.addFragment(chatsFragment,"CHATS");
        adaptadorMenu.addFragment(estadosFragment,"ESTADOS");
        adaptadorMenu.addFragment(new UsuariosFragment(),"USUARIOS");

        viewPager.setAdapter(adaptadorMenu);
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usuarioRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                SharedPreferences sharedPreferences = getSharedPreferences("Usuario",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nombre",usuario.getNombre());
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query refListaChats = FirebaseDatabase.getInstance().getReference("ListaChats")
                .child(firebaseUser.getUid())
                .orderByChild("fecha");

        refListaChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaChats.clear();
                Log.e("d",dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    ListaChats chats = snapshot.getValue(ListaChats.class);
                    listaChats.add(chats);
                }
                chatsFragment.MostrarChats();
                estadosFragment.leerEstados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ((FloatingActionButton) findViewById(R.id.btnSettings))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogoMenuConfiguracion dialogoMenuConfiguracion = new DialogoMenuConfiguracion(getMe());
                        dialogoMenuConfiguracion.show(getSupportFragmentManager(),"DialogoMenuConfiguracion");
                    }
                });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens")
                                .child(firebaseUser.getUid())
                                .child("token");

                        reference.setValue(token);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menuCerrarSesion) {

            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            FirebaseAuth.getInstance().signOut();
            return true;
        }

        return false;
    }

    public MainActivity getMe(){
        return this;
    }

    public void cargarImagen() {
        if(verificarPermisos()) {
            proceso();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,PERMISOS,CODIGO_PERMISOS);
        }
    }

    private boolean verificarPermisos() {
        for (String permiso : PERMISOS) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,permiso) != PackageManager.PERMISSION_GRANTED) {
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
            } else {
                Snackbar.make(findViewById(R.id.main),"Error de permisos",Snackbar.LENGTH_LONG)
                        .setAction("Activar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.zamnadev.mwhasapptest"));
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    private void proceso() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri uri = result.getUri();

            if (uri != null) {

                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Subiendo...");
                progressDialog.show();

                final StorageReference storageReference = FirebaseStorage.getInstance().getReference("ImagenesPerfil")
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

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("imagen", url);

                            usuarioRef.updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Actualizacion exitosa", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(MainActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            } else {
                Toast.makeText(MainActivity.this, "Ninguna imagen seleccionada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String obtnerExtension(Uri uri) {
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    public ArrayList<ListaChats> getListaChats() {
        return listaChats;
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("conectado",true);
        hashMap.put("hora", ServerValue.TIMESTAMP);
        usuarioRef.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("conectado",false);
        hashMap.put("hora", ServerValue.TIMESTAMP);
        usuarioRef.updateChildren(hashMap);
    }
}
