package com;

import com.models.UserModel;
import com.presenter.LoginPresenter;
import com.presenter.UserListPresenter;
import com.presenter.UserPresenter;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.views.UserFormViewImpl;
import com.views.ListUserViewImpl;
import com.views.LoginViewImpl;

public class RentalTool extends UI {

    public static final String LOGIN = "login";
    public static final String HOME = "";
    public static final String CREATE_USER = "createUser";
    public static final String LIST_USERS = "listUsers";

    @Override
    public void init(VaadinRequest request) {

        String msgs = request.getParameter("14");
        System.out.println(msgs);

        Navigator navigator = new Navigator(this, this);

        // load models
        UserModel userModel = new UserModel();

        // load views
        LoginViewImpl loginView = new LoginViewImpl();
        UserFormViewImpl createUserView = new UserFormViewImpl();
        ListUserViewImpl listUserView = new ListUserViewImpl();

        // TODO think about a ChangeUserView as we could use it for updating and creating an User
        // load presenter classes
        new LoginPresenter(userModel, loginView);
        new UserPresenter(userModel, createUserView);
        new UserListPresenter(userModel, listUserView);

        navigator.addView(HOME, loginView);
        navigator.addView(CREATE_USER,  createUserView);
        navigator.addView(LIST_USERS, listUserView);
    }
}