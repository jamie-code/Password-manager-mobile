package com.example.hshmobile.ui.genpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PwdGenViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PwdGenViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is genpassword fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}