package com.example.seguimientonutricional.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Date> mDate;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mDate = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Date> getDate(){
        return mDate;
    }

    public void setDate(Date date){

        mDate.setValue(date);
    }
}