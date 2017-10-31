package com.presenter;

import com.models.UserModel;
import com.views.CreateUserView;

public class UserPresenter implements CreateUserView.UserViewListener {

    private UserModel model;
    private CreateUserView view;

    public UserPresenter(UserModel model, CreateUserView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void buttonClick(UserModel userModel) {

        // create user
        boolean ok = model.createUser(userModel);

        // get error and success message to display them in view
        if (!ok) {
            view.setErrorMsg(model.getErrorMsg());
        } else {
            view.setSuccessMsg(model.getSuccessMsg());
        }
    }
}