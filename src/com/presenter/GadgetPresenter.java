package com.presenter;

import com.models.GadgetModel;
import com.views.GadgetFormView;

public class GadgetPresenter implements GadgetFormView.GadgetViewListener {

    private GadgetModel model;
    private GadgetFormView view;

    public GadgetPresenter(GadgetModel model, GadgetFormView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void buttonClick(GadgetModel gadgetModel) {

        Boolean ok;

        // update or create user
        if (gadgetModel.getGadgetID() != null) {
            ok = gadgetModel.updateGadget();
        } else {
            ok = gadgetModel.addGadget();
        }

        // get error and success message to display them in view
        if (!ok) {
            view.setErrorMsg(model.getErrorMsg());
        } else {
            view.setSuccessMsg(model.getSuccessMsg());
        }
    }
}