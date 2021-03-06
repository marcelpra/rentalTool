package com.presenter;

import com.RentalTool;
import com.models.UserModel;
import com.vaadin.ui.*;
import com.views.LoginView;

public class LoginPresenter implements LoginView.LoginViewListener {
    private UserModel model;
    private LoginView view;

    public LoginPresenter(UserModel model, LoginView view) {
        this.model = model;
        this.view = view;

        view.addListener(this);
    }

    @Override
    public void buttonClick(String username, String password) {

        // process login
        Boolean loginSuccess = model.login(username, password);

        System.out.println("login result: " + loginSuccess);

        // check login result
        if (!loginSuccess) {
            // get error message
            view.setDisplay(model.getErrorMsg());
        } else {
            // navigate to user view
            UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME);
        }
    }
}