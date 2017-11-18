package com.views;

import com.models.ReservationModel;
import com.vaadin.navigator.View;

public interface ReservationFormView extends View {

    public void setErrorMsg(String value);

    public void setSuccessMsg(String value);

    interface ReservationViewListener {
        void buttonClick(ReservationModel userModel);
    }
    public void addListener(ReservationFormView.ReservationViewListener listener);
}
