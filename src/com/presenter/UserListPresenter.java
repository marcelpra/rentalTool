package com.presenter;

import com.models.UserModel;
import com.views.ListUserView;

public class UserListPresenter implements ListUserView.ListUserViewListener {

    private UserModel model;
    private ListUserView view;

    public UserListPresenter(UserModel model, ListUserView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void buttonClick() {

    }
}