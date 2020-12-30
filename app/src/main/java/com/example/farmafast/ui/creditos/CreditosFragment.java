package com.example.farmafast.ui.creditos;

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

public class CreditosFragment extends Fragment {

    private CreditosViewModel creditosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        creditosViewModel =
                ViewModelProviders.of(this).get(CreditosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_creditos, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        creditosViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}