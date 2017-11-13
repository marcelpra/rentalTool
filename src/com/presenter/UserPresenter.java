package com.presenter;

import com.models.UserModel;
import com.views.UserFormView;

public class UserPresenter implements UserFormView.UserViewListener {

    private UserModel model;
    private UserFormView view;

    public UserPresenter(UserModel model, UserFormView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void buttonClick(UserModel userModel) {

        Boolean ok;

        // update or create user
        if (userModel.getUserID() != null) {
            ok = userModel.updateUser();
        } else {
            ok = userModel.createUser();
        }

        // get error and success message to display them in view
        if (!ok) {
            view.setErrorMsg(model.getErrorMsg());
        } else {
            view.setSuccessMsg(model.getSuccessMsg());
        }
    }
}