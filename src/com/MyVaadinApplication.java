package com;

import com.models.UserModel;
import com.presenter.LoginPresenter;
import com.presenter.UserListPresenter;
import com.presenter.UserPresenter;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.views.CreateUserViewImpl;
import com.views.ListUserViewImpl;
import com.views.LoginView;
import com.views.LoginViewImpl;

public class MyVaadinApplication extends UI {

    Navigator navigator;

    static final String LOGIN = "login";
    static final String CREATE_USER = "createUser";

    @Override
    public void init(VaadinRequest request) {

        navigator = new Navigator(this, this);

        UserModel model = new UserModel();
        LoginViewImpl view = new LoginViewImpl();

        new LoginPresenter(model, view);

        // TODO think about a ChangeUserView as we coudl use it for updating and creating an User
        CreateUserViewImpl viewl = new CreateUserViewImpl();

        new UserPresenter(model, viewl);

        ListUserViewImpl view2 = new ListUserViewImpl();
        new UserListPresenter(model, view2);

        navigator.addView("", view);
        navigator.addView("createUser", viewl);
        navigator.addView("listUsers", view2);

        // I can switch now between view by adding /#!user to the url to get to the create user thing
//        navigator.addProvider(new ViewProvider() {
//            @Override
//            public String getViewName(String s) {
//                return s;
//            }
//
//            @Override
//            public View getView(String s) {
//                System.out.println("navigation called with param: " + s);
//                if (s.equals(CREATE_USER)) {
//                    System.out.println("navigation routed to create user: " + s);
//                    return viewl;
//                } else {
//                    return view;
//                }
//            }
//        });

        // Config for Login

//        setContent(view);

        // Config for creating User

//        setContent(view);
    }


}

//public class Login implements View {
//
//    public void init() {
//        UserModel model = new UserModel();
//        LoginViewImpl view = new LoginViewImpl();
//
//        new LoginPresenter(model, view);
//        setContent(view);
//    }
//}
