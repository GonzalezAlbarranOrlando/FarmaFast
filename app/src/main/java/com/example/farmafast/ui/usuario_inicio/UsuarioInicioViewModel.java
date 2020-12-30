package com.example.farmafast.ui.usuario_inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UsuarioInicioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UsuarioInicioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is usuario_inicio fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}