package com.presenter;

import com.models.ReservationModel;
import com.models.UserModel;
import com.views.ReservationFormView;
import com.views.ReservationFormViewImpl;
import com.views.UserFormView;

public class ReservationPresenter implements ReservationFormView.ReservationViewListener {

    private ReservationModel model;
    private ReservationFormView view;

    public ReservationPresenter(ReservationModel model, ReservationFormView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void buttonClick(ReservationModel reservationModel) {

        Boolean ok;

        // update or create user
        if (reservationModel.getReservationId() != null) {
            ok = reservationModel.updateReservation();
        } else {
            ok = reservationModel.createReservation();
        }

        // get error and success message to display them in view
        if (!ok) {
            view.setErrorMsg(model.getErrorMsg());
        } else {
            view.setSuccessMsg(model.getSuccessMsg());
        }
    }
}