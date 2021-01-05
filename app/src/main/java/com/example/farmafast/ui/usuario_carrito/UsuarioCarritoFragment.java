package com.example.farmafast.ui.usuario_carrito;

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

public class UsuarioCarritoFragment extends Fragment {

    private UsuarioCarritoViewModel usuarioCarritoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usuarioCarritoViewModel =
                ViewModelProviders.of(this).get(UsuarioCarritoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_usuario_carrito, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        usuarioCarritoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}