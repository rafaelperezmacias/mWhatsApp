package com.zamnadev.mwhatsapp.Dialogos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zamnadev.mwhatsapp.MainActivity;
import com.zamnadev.mwhatsapp.R;

public class DialogoMenuConfiguracion extends BottomSheetDialogFragment {

    private MainActivity mainActivity;

    public DialogoMenuConfiguracion(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_menu_configuracion,container);

        ((Button) view.findViewById(R.id.btnPerfil))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogoPerfil dialogoPerfil = new DialogoPerfil(mainActivity);
                        dialogoPerfil.show(mainActivity.getSupportFragmentManager(),"DialogoPerfil");
                        dismiss();
                    }
                });

        ((Button) view.findViewById(R.id.btnImagen))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mainActivity.cargarImagen();
                        dismiss();
                    }
                });

        ((Button) view.findViewById(R.id.btnNombre))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogoCambioNombre dialogoCambioNombre = new DialogoCambioNombre(mainActivity);
                        dialogoCambioNombre.show(mainActivity.getSupportFragmentManager(),"DialogoCambioNombre");
                        dismiss();
                    }
                });

        return view;
    }
}