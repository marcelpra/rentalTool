package com.views;

import com.models.GadgetModel;
import com.vaadin.navigator.View;

public interface GadgetFormView extends View {

    public void setErrorMsg(String value);

    public void setSuccessMsg(String value);

    interface GadgetViewListener {
        void buttonClick(GadgetModel userModel);
    }
    public void addListener(GadgetFormView.GadgetViewListener listener);
}
