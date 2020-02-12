package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddEstadoActivity extends AppCompatActivity {

    private Uri imagenUri;
    private String url;
    private StorageReference storageReference;
    private StorageTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estado);

        url = "";

        storageReference = FirebaseStorage.getInstance().getReference("Estados");

        CropImage.activity()
                .setAspectRatio(9,16)
                .start(AddEstadoActivity.this);
    }

    private void publicarHistoria() {
        final ProgressDialog progressDialog = new ProgressDialog(AddEstadoActivity.this);
        progressDialog.setMessage("Subiendo");
        progressDialog.show();

        if (imagenUri != null) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." +obtnerExtension(imagenUri));
            task = reference.putFile(imagenUri);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri tmpUri = task.getResult();
                        assert tmpUri != null;
                        String url = tmpUri.toString();

                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Estados")
                                .child(id);

                        String idEstado = ref.push().getKey();
                        long tiempoFinal = System.currentTimeMillis() + 86400000;
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imagen",url);
                        hashMap.put("timepoInicio", ServerValue.TIMESTAMP);
                        hashMap.put("tiempoFin",tiempoFinal);
                        hashMap.put("id",idEstado);
                        hashMap.put("idUsuario",id);

                        ref.child(idEstado).setValue(hashMap);
                        progressDialog.dismiss();

                        Toast.makeText(AddEstadoActivity.this, "Estado subido con exito", Toast.LENGTH_SHORT).show();

                        finish();

                    } else {
                        Toast.makeText(AddEstadoActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddEstadoActivity.this, "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            });
        } else {
            Toast.makeText(AddEstadoActivity.this, "No ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }
    }

    private String obtnerExtension(Uri uri) {
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            imagenUri = result.getUri();

            publicarHistoria();
        } else {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
