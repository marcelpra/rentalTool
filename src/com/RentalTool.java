package com;

import com.models.UserModel;
import com.presenter.LoginPresenter;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.views.MainViewImpl;
import com.views.LoginViewImpl;

@Theme("valo")
public class RentalTool extends UI {

    public static final String LOGIN = "login";
    public static final String HOME = "main";
    public static final String CREATE_USER = "User";
    public static final String LIST_USERS = "Users";
    public static final String CREATE_RESERVATION = "Reservation";
    public static final String LIST_RESERVATION = "Reservations";
    public static final String EDIT_PROFILE = "Profile";
    public static final String ADD_GADGET = "Gadget";
    public static final String LIST_GADGETS = "Gadgets";

    @Override
    public void init(VaadinRequest request) {

        Navigator navigator = new Navigator(this, this);

        MainViewImpl mainView = new MainViewImpl();
        LoginViewImpl loginView = new LoginViewImpl();

        UserModel userModel = new UserModel();

        new LoginPresenter(userModel, loginView);

        navigator.addView(HOME, mainView);
        navigator.addView(LOGIN, loginView);
        navigator.addView("", loginView);
        navigator.setErrorView(loginView);
    }
}