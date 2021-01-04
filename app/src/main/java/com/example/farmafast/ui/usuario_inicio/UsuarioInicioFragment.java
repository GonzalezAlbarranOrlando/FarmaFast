package com.example.farmafast.ui.usuario_inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.farmafast.R;

public class UsuarioInicioFragment extends Fragment {

    private UsuarioInicioViewModel usuarioInicioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usuarioInicioViewModel =
                ViewModelProviders.of(this).get(UsuarioInicioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_usuario_inicio, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        usuarioInicioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}