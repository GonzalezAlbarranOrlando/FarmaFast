package com.example.farmafast.ui.repartidor_pedido_actual;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farmafast.R;

public class RepartidorPedidoActualFragment extends Fragment {

    private RepartidorPedidoActualViewModel mViewModel;

    public static RepartidorPedidoActualFragment newInstance() {
        return new RepartidorPedidoActualFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repartidor_pedido_actual, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RepartidorPedidoActualViewModel.class);
        // TODO: Use the ViewModel
    }

}