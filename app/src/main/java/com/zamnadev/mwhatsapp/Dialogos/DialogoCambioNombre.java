package com.zamnadev.mwhatsapp.Dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zamnadev.mwhatsapp.MainActivity;
import com.zamnadev.mwhatsapp.R;

import java.util.HashMap;

public class DialogoCambioNombre extends DialogFragment {

    private MainActivity mainActivity;

    public DialogoCambioNombre(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_cambio_nombre,null);
        builder.setView(view);

        final TextInputLayout lytNombre = view.findViewById(R.id.lytNombre);
        final TextInputEditText txtNombre = view.findViewById(R.id.txtNombre);

        ((Button) view.findViewById(R.id.btnAceptar))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (txtNombre.getText().toString().isEmpty()) {
                            lytNombre.setError("Rellene el campo");
                            return;
                        }

                        lytNombre.setError(null);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("nombre",txtNombre.getText().toString().trim());
                        hashMap.put("busqueda",txtNombre.getText().toString().trim().toUpperCase());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Actualizacion exitosa", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }
                        });

                    }
                });

        ((Button) view.findViewById(R.id.btnCancelar))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });

        return builder.create();
    }

}
