package com.example.farmafast.ui.usuario_carrito;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UsuarioCarritoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UsuarioCarritoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is usuario_carrito fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}