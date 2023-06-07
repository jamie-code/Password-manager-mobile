package com.example.hshmobile.ui.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shared fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}