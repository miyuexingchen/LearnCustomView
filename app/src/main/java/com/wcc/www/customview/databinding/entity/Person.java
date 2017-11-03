package com.wcc.www.customview.databinding.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;

import com.wcc.www.customview.BR;

/**
 * Created by 王晨晨 on 2017/10/31.
 */

public class Person extends BaseObservable {

    public String firstname;
    public String lastname;
    //    public boolean isFired = false;
    public ObservableBoolean isFired;
    public String avatar;
    public
    @Bindable
    String address;

    public Person(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
        isFired = new ObservableBoolean(false);
    }

    public Person(String firstname, String lastname, boolean isFired) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.isFired = new ObservableBoolean(isFired);
    }

    @Bindable
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        notifyPropertyChanged(BR.firstname);
    }

    @Bindable
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        notifyPropertyChanged(BR.lastname);
    }

    @Bindable
    public ObservableBoolean getIsFired() {
        return isFired;
    }

    public void setFired(boolean fired) {
        isFired.set(fired);
        notifyPropertyChanged(BR.isFired);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
