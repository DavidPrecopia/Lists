package com.example.david.lists.view.common;

import android.app.Application;

public abstract class ViewModelBase {

    protected final Application application;

    protected ViewModelBase(Application application) {
        this.application = application;
    }


    protected String getStringRes(int resId) {
        return application.getString(resId);
    }

    protected String getStringRes(int resId, Object object) {
        return application.getString(resId, object);
    }
}
